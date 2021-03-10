package app.doggy.la_original

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.fragment_chart.*
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt

class ChartFragment : Fragment() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    //円グラフ。
    private lateinit var pieChart: PieChart

    //グラフの表示形式を管理する変数。
    private var chartFormat = 0

    //グラフのデータ。
    private val dimensions = mutableListOf<String>()
    private val values = mutableListOf<Float>()

    //収入の合計。
    private val incomeSum = 1030000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Legendを取得する。
        var legendList = readAllLegend(chartFormat)

        //Legendを生成。
        if (legendList.isEmpty()) {
            createLegends(chartFormat)
        }

        val adapter = LegendAdapter(context as Context, legendList, object: LegendAdapter.OnItemClickListener {
            override fun onItemClick(item: Legend) {

            }
        },true)

        //legendRecyclerViewの設定。
        legendRecyclerView.setHasFixedSize(true)
        legendRecyclerView.layoutManager = LinearLayoutManager(context as Context) //縦横表示
        legendRecyclerView.adapter = adapter

        //円グラフを関連付け。
        pieChart = view.findViewById(R.id.pie_chart)

        //Switchの処理。
        chartSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(chartFormat) {
                0 -> {
                    //グラフを切り替える。
                    chartFormat = 1

                    //グラフの表示。
                    drawChart(chartFormat)

                    //再度Legendを取得する。
                    legendList = readAllLegend(chartFormat)

                    //Legendを生成。
                    if (legendList.isEmpty()) {
                        createLegends(chartFormat)
                    }
                }

                1 -> {
                    //グラフを切り替える。
                    chartFormat = 0

                    //グラフの表示。
                    drawChart(chartFormat)

                    //再度Legendを取得する。
                    legendList = readAllLegend(chartFormat)

                    //Legendを生成。
                    if (legendList.isEmpty()) {
                        createLegends(chartFormat)
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        //グラフの表示。
        drawChart(chartFormat)

        //chartAverageText.text = getString(R.string.text_chart_average, calculateSatisfactionAverage())
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //Legendの生成。
    private fun createLegend(title: String, iconId: Int, satisfaction: Int, chartFormat: Int) {
        realm.executeTransaction {
            val legend = it.createObject(Legend::class.java, UUID.randomUUID().toString())
            legend.title = title
            legend.iconId = iconId
            legend.satisfaction = satisfaction
            legend.chartFormat = chartFormat
        }
    }

    //初回起動時にLegendを生成。
    private fun createLegends(chartFormat: Int) {

        val titleList: List<String> = listOf("絶望", "不満", "どちらでもない", "満足", "大満足")
        val iconList: List<Int> = listOf(
            R.drawable.ic_baseline_sentiment_very_dissatisfied_24,
            R.drawable.ic_baseline_sentiment_dissatisfied_24,
            R.drawable.ic_baseline_sentiment_neutral_24,
            R.drawable.ic_baseline_sentiment_satisfied_24,
            R.drawable.ic_baseline_sentiment_very_satisfied_24
        )

        when(chartFormat) {
            0 -> {
                for (i in 0..4) {
                    createLegend(titleList[i], iconList[i], i, chartFormat)
                }
            }

            1 -> {
                for (i in 0..4) {
                    createLegend(titleList[i], iconList[i], i, chartFormat)
                }
                createLegend("未登録", R.drawable.ic_baseline_savings_24, -1, chartFormat)
            }
        }
    }

    //Legendを取得。
    private fun readAllLegend(chartFormat: Int): RealmResults<Legend> {
        return realm.where(Legend::class.java).equalTo("chartFormat", chartFormat).greaterThan("ratio", 0).findAll().sort("satisfaction", Sort.DESCENDING)
    }

    //Recordを取得。
    //期間を絞り込めるようにする。
    private fun readAllRecord(): RealmResults<Record> {
        return realm.where(Record::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }

//    //平均満足度を求める処理。
//    private fun calculateSatisfactionAverage(): Int {
//
//        val recordList = readAllRecord()
//
//        //満足度の合計を求める。
//        var satisfactionSum = 0
//        for (i in 0 until recordList.size) {
//            satisfactionSum += recordList[i]?.satisfaction as Int
//        }
//
//        //平均満足度を求める。
//        var averageSatisfaction = 0f
//        if (!recordList.isEmpty()) {
//            averageSatisfaction =  satisfactionSum.toString().toFloat() / recordList.size
//        }
//
//        return averageSatisfaction.roundToInt()
//    }

    //出費の合計を返す処理。
    private fun getAmountSum(): Int {

        val recordList = readAllRecord()

        //出費の合計を求める。
        var amountSum = 0
        for (i in 0 until recordList.size) {
            amountSum += recordList[i]?.amount as Int
        }

        return amountSum
    }

    //満足度によるグラフのデータを用意。
    private fun setSatisfactionData(chartFormat: Int) {

        val recordList = readAllRecord()

        //出費の合計の変数。
        var veryUnSatisfactionSum = 0
        var unSatisfactionSum = 0
        var neitherSum = 0
        var satisfactionSum = 0
        var verySatisfactionSum = 0

        for (i in 0 until recordList.size) {
            //満足度毎に、出費の合計を求める。
            when(recordList[i]?.satisfaction) {
                0 -> veryUnSatisfactionSum += recordList[i]?.amount as Int
                1 -> unSatisfactionSum += recordList[i]?.amount as Int
                2 -> neitherSum += recordList[i]?.amount as Int
                3 -> satisfactionSum += recordList[i]?.amount as Int
                4 -> verySatisfactionSum += recordList[i]?.amount as Int
            }
        }

        //出費の合計を配列に代入する。
        if (verySatisfactionSum > 0) {
            dimensions.add("大満足")
            values.add(verySatisfactionSum.toString().toFloat())
        }

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

        if (veryUnSatisfactionSum > 0) {
            dimensions.add("絶望")
            values.add(veryUnSatisfactionSum.toString().toFloat())
        }

        //chartFormatの値によって分母を変更。
        var denominator = 0
        when(chartFormat) {
            0 -> denominator = getAmountSum()
            1 -> {
                denominator = incomeSum

                //未登録の出費を求める。
                val unregisteredSum = incomeSum - getAmountSum()

                if (unregisteredSum > 0) {
                    dimensions.add("未登録")
                    values.add(unregisteredSum.toString().toFloat())
                }
            }
        }

        //割合を求める。
        for (i in 0 until values.size) {
            values[i] = values[i] * 100f / denominator.toString().toFloat()
            values[i] = round(values[i])
        }

    }

    //グラフを描画する処理。
    private fun createChart(chartFormat: Int) {

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
                return "%.0f％".format(value)
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
        when(chartFormat) {
            0 -> pieChart.centerText = "¥${"%,d".format(getAmountSum())}"
            1 -> pieChart.centerText = "¥${"%,d".format(incomeSum)}"
        }
        pieChart.setCenterTextSize(18f)

        //グラフに触れなくする。
        pieChart.setTouchEnabled(false)

        //PieChart更新。
        pieChart.invalidate()

        //格納されているデータを削除。
        dimensions.clear()
        values.clear()

    }

    //表示するグラフを変える処理。
    private fun drawChart(chartFormat: Int) {
        setSatisfactionData(chartFormat)
        createChart(chartFormat)
    }

}