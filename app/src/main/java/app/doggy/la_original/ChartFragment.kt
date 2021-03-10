package app.doggy.la_original

import android.content.Context
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
    private var switch = 0

    //グラフに表示するデータ。
    private val dimensions = mutableListOf<String>()
    private val values = mutableListOf<Float>()

    //収入の合計。
    private val incomeSum = 1030000f

    //未登録の金額の合計。
    private var unregisteredSum = 0f

    private val legendList: MutableList<Legend> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Legendを取得する。
        val legendList = readAllLegend(chartFormat)

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

        pieChart = view.findViewById(R.id.pie_chart)

        changeChart(switch)

        chartSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when(switch) {
                0 -> {
                    switch = 1
                    changeChart(switch)
                    //adapter.addAll(legendList)
                }

                1 -> {
                    switch = 0
                    changeChart(switch)
                    //adapter.addAll(legendList)
                }
            }
            emptyText.isVisible = realm.where(Record::class.java).findAll().size == 0 && switch == 0
        }

    }

    override fun onResume() {
        super.onResume()

        emptyText.isVisible = realm.where(Record::class.java).findAll().size == 0 && switch == 0

        changeChart(switch)

        chartAverageText.text = getString(R.string.text_chart_average, calculateSatisfactionAverage())
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun createLegend(title: String, iconId: Int, satisfaction: Int, chartFormat: Int) {
        realm.executeTransaction {
            val legend = it.createObject(Legend::class.java, UUID.randomUUID().toString())
            legend.title = title
            legend.iconId = iconId
            legend.satisfaction = satisfaction
            legend.chartFormat = chartFormat
        }
    }

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

    private fun readAllLegend(chartFormat: Int): RealmResults<Legend> {
        return realm.where(Legend::class.java).equalTo("chartFormat", chartFormat).findAll().sort("satisfaction", Sort.DESCENDING)
    }

    private fun readAllRecord(): RealmResults<Record> {
        return realm.where(Record::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }

    //平均満足度を求める処理。
    private fun calculateSatisfactionAverage(): Int {

        val recordList = readAllRecord()

        //満足度の合計を求める。
        var satisfactionSum = 0
        for (i in 0 until recordList.size) {
            satisfactionSum += recordList[i]?.satisfaction as Int
        }

        //平均満足度を求める。
        var averageSatisfaction = 0f
        if (!recordList.isEmpty()) {
            averageSatisfaction =  satisfactionSum.toString().toFloat() / recordList.size
        }

        return averageSatisfaction.roundToInt()
    }

    //出費の合計を求める処理。
    private fun calculateAmountSum(): Int {

        val recordList = readAllRecord()

        //出費の合計を求める。
        var amountSum = 0
        for (i in 0 until recordList.size) {
            amountSum += recordList[i]?.amount as Int
        }

        return amountSum
    }

    //満足度によるグラフのデータを用意。
    private fun setSatisfactionData(switch: Int) {

        val recordList = readAllRecord()

        //満足度の項目
        var satisfactionSum = 0
        var neitherSum = 0
        var unSatisfactionSum = 0

        for (i in 0 until recordList.size) {

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

        when(switch) {
            0 -> {
                //割合を求める。
                for (i in 0 until values.size) {
                    values[i] = values[i] * 100 / calculateAmountSum()
                    values[i] = round(values[i])
                }
            }

            1 -> {
                //割合を求める。
                for (i in 0 until values.size) {
                    values[i] = values[i] * 100 / incomeSum
                    values[i] = round(values[i])
                }

                //未登録の出費を求める。
                unregisteredSum = incomeSum - calculateAmountSum()
                unregisteredSum = unregisteredSum * 100f / incomeSum

                //未登録の出費をグラフのデータに加える。
                dimensions.add("未登録")
                values.add(unregisteredSum)
            }
        }

        legendList.clear()

//        for (i in 0 until dimensions.size) {
//            legendList.add(Legend(
//                dimensions[i],
//                values[i].roundToInt(),
//                R.drawable.ic_baseline_sentiment_very_satisfied_24
//            ))
//        }

    }

    //グラフを描画する処理。
    private fun drawChart(switch: Int) {

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
                return "%.0f%%".format(value)
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
        when(switch) {
            0 -> {
                if (calculateAmountSum() == 0) {
                    pieChart.centerText = ""
                } else {
                    pieChart.centerText = "¥${"%,d".format(calculateAmountSum())}"
                }
            }
            1 -> pieChart.centerText = "¥${"%,d".format(incomeSum.roundToInt())}"
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
    private fun changeChart(switch: Int) {
        setSatisfactionData(switch)
        drawChart(switch)
    }

}