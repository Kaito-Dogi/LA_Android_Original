package app.doggy.la_original

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.Dimension
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_chart.*

class ChartFragment : Fragment() {

    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pieChart = view.findViewById(R.id.pie_chart)

        //表示用サンプルデータの作成
        var dimensions = listOf("A", "B", "C", "D")//分割円の名称(String型)
        var values = listOf(1f, 2f, 3f, 4f)//分割円の大きさ(Float型)

        drawChart(pieChart, dimensions, values)

        var switch = 0

        chartSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            // Responds to switch being checked/unchecked
            when(switch) {
                0 -> {
                    switch = 1

                    dimensions = listOf("E", "F", "G", "H")//分割円の名称(String型)
                    values = listOf(4f, 3f, 2f, 1f)//分割円の大きさ(Float型)

                    drawChart(pieChart, dimensions, values)

                }
                1 -> {
                    switch = 0

                    dimensions = listOf("A", "B", "C", "D")//分割円の名称(String型)
                    values = listOf(1f, 2f, 3f, 4f)//分割円の大きさ(Float型)

                    drawChart(pieChart, dimensions, values)

                }
            }
        }

    }

    private fun drawChart(pieChart: PieChart, dimensions: List<String>, values: List<Float>) {

        //①Entryにデータ格納
        var entryList = mutableListOf<PieEntry>()
        for(i in values.indices){
            entryList.add(
                PieEntry(values[i], dimensions[i])
            )
        }

        //②PieDataSetにデータ格納
        val pieDataSet = PieDataSet(entryList, "expense")
        //③DataSetのフォーマット指定
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        //④PieDataにPieDataSet格納
        val pieData = PieData(pieDataSet)

        //⑤PieChartにPieData格納
        pieChart.data = pieData

        //⑥Chartのフォーマット指定
        //凡例を非表示。
        pieChart.legend.isEnabled = false
        //グラフのタイトルを非表示。
        pieChart.description.isEnabled= false
        //グラフ内に、各項目の名前を表示。
        pieChart.setEntryLabelTextSize(20f)
        //グラフの中央に文字を表示。
        pieChart.centerText = "¥1,030,000"
        pieChart.setCenterTextSize(20f)
        //グラフを触れなくする。
        pieChart.setTouchEnabled(false)
        //グラフ内に、各項目の値を表示。
        pieDataSet.valueTextSize = 20f
        pieDataSet.valueTextColor = Color.WHITE
        //各項目の色を変更。
        pieDataSet.colors = listOf(resources.getColor(R.color.teal_700), resources.getColor(R.color.teal_200), resources.getColor(R.color.purple_200), resources.getColor(R.color.purple_500))

        //⑦PieChart更新
        pieChart.invalidate()

    }

}