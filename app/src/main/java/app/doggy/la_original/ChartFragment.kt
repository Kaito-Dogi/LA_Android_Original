package app.doggy.la_original

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.Dimension
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlin.math.round

class ChartFragment : Fragment() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    //円グラフ。
    private lateinit var pieChart: PieChart

    //グラフの表示形式を管理する変数。
    private var switch = 0

    //グラフに表示するデータ。
    private val dimensions = mutableListOf<String>()
    private val values = mutableListOf<Float>()

    //出費の合計。
    private var amountSum = 0f

    //収入の合計。
    private val incomeSum = 1030000f

    //未登録の金額の合計。
    private var unregisteredSum = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pie_chart)

        changeChart(switch)

        chartSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(switch) {
                0 -> {
                    switch = 1
                    changeChart(switch)
                }

                1 -> {
                    switch = 0
                    changeChart(switch)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        changeChart(switch)
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //満足度によるグラフのデータを用意。
    private fun setSatisfactionData() {

        val recordList = realm.where(Record::class.java).findAll()

        //満足度の項目
        var satisfactionSum = 0
        var neitherSum = 0
        var unSatisfactionSum = 0

        for (i in 0 until recordList.size) {

            //全出費を求める。
            amountSum += recordList[i]?.amount as Int

            //満足度毎に、出費の合計を求める。
            when {
                recordList[i]?.satisfaction as Int > 0 -> {
                    satisfactionSum += recordList[i]?.amount as Int
                }
                recordList[i]?.satisfaction as Int == 0 -> {
                    neitherSum += recordList[i]?.amount as Int
                }
                else -> {
                    unSatisfactionSum += recordList[i]?.amount as Int
                }
            }

        }

        //データを配列にする。
        if (satisfactionSum > 0) {
            dimensions.add("満足")
            values.add(satisfactionSum.toString().toFloat())
        }
        if (neitherSum > 0) {
            dimensions.add("どちらでもない")
            values.add(neitherSum.toString().toFloat())
        }
        if (unSatisfactionSum > 0) {
            dimensions.add("不満")
            values.add(unSatisfactionSum.toString().toFloat())
        }

        //割合を求める。
        for (i in 0 until values.size) {
            values[i] = values[i] * 100 / incomeSum
            values[i] = round(values[i])
        }

        //未登録の出費を求める。
        unregisteredSum = incomeSum - amountSum
        unregisteredSum = unregisteredSum * 100f / incomeSum

        //未登録の出費をグラフのデータに加える。
        dimensions.add("未登録")
        values.add(unregisteredSum)

    }

    //カテゴリ毎のデータを用意。
    private fun setCategoryData() {

        val categoryList = realm.where(Category::class.java).findAll().sort("amountSum", Sort.DESCENDING)

        //カテゴリ毎の出費の合計を求める。
        for (i in 0 until categoryList.size) {
            if (categoryList[i]?.amountSum as Int > 0) {
                dimensions.add(categoryList[i]?.name as String)
                values.add(categoryList[i]?.amountSum.toString().toFloat())
            }
        }

        //全カテゴリの出費を求める。
        for (i in 0 until values.size) {
            amountSum += values[i]
        }

        //カテゴリ毎の割合を求める。
        for (i in 0 until values.size) {
            values[i] = values[i] * 100f / incomeSum
            values[i] = round(values[i])
        }

        //未登録の出費を求める。
        unregisteredSum = incomeSum - amountSum
        unregisteredSum = unregisteredSum * 100f / incomeSum

        //未登録の出費をグラフのデータに加える。
        dimensions.add("未登録")
        values.add(unregisteredSum)

    }

    //グラフを描画する処理。
    private fun drawChart() {

        //Entryにデータを格納。
        val entryList = mutableListOf<PieEntry>()
        for(i in values.indices){
            entryList.add(
                PieEntry(values[i], dimensions[i])
            )
        }

        //PieDataSetにデータを格納。
        val pieDataSet = PieDataSet(entryList, "expense")

        //DataSetのフォーマットを指定。
        //各項目の色を変更。
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        //グラフ内に、各項目の値を表示。
        pieDataSet.valueTextSize = 20f
        pieDataSet.valueTextColor = Color.WHITE
        pieDataSet.valueFormatter = object: ValueFormatter() {
            override fun getFormattedValue(value: Float): String{
                return "%.1f%%".format(value)
            }
        }

        //PieDataにPieDataSetを格納。
        val pieData = PieData(pieDataSet)

        //PieChartにPieDataを格納。
        pieChart.data = pieData

        //Chartのフォーマットを指定。
        //凡例を非表示。
        pieChart.legend.isEnabled = false
        //グラフのタイトルを非表示。
        pieChart.description.isEnabled = false
        //グラフ内に表示する、各項目の名前のサイズ。
        pieChart.setEntryLabelTextSize(10f)
        //グラフの中央に文字を表示。
        pieChart.centerText = amountSum.toString()
        pieChart.setCenterTextSize(20f)
        //グラフに触れなくする。
        pieChart.setTouchEnabled(false)

        //PieChart更新。
        pieChart.invalidate()

        //格納されているデータを削除。
        dimensions.clear()
        values.clear()

        //データをリセット。
        amountSum = 0f

    }

    //表示するグラフを変える処理。
    private fun changeChart(switch: Int) {
        when(switch) {
            0 -> setSatisfactionData()
            1 -> setCategoryData()
        }
        drawChart()
    }
}