package app.doggy.la_original

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class CalendarFragment : Fragment() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //RecyclerViewの設定。
        recordRecyclerViewInCalendar.setHasFixedSize(true)
        recordRecyclerViewInCalendar.layoutManager = LinearLayoutManager(context)

        //今日のRecordリストを表示。
        var recordList = readAtTheDay(SimpleDateFormat("yyyy/MM/dd").format(Date()).toString())
        var adapter = RecordAdapter(context as Context, recordList, object: RecordAdapter.OnItemClickListener {
            override fun onItemClick(item: Record) {

            }
        },true)
        recordRecyclerViewInCalendar.adapter = adapter

        //今日の平均満足度を表示。
        //calendarAverageText.text = "${calculateSatisfactionAverage(SimpleDateFormat("yyyy/MM/dd").format(Date()).toString())}％"
        calendarAverageText.text = getString(R.string.text_calendar_average, calculateSatisfactionAverage(SimpleDateFormat("yyyy/MM/dd").format(Date()).toString()))

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->

            //日付を取得。
            val date = getDate(year, month, dayOfMonth)

            //その日のれcおρdリストを表示。
            recordList = readAtTheDay(date)
            adapter = RecordAdapter(context as Context, recordList, object: RecordAdapter.OnItemClickListener {
                override fun onItemClick(item: Record) {

                }
            },true)
            recordRecyclerViewInCalendar.adapter = adapter

            //その日の平均満足度を表示。
            calendarAverageText.text = "${calculateSatisfactionAverage(date)}％"

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //日付の文字列で取得する処理。
    private fun getDate(year: Int, month: Int, dayOfMonth: Int): String {

        val date: String

        if (month in 0..8 && dayOfMonth in 0..8) {
            date = "" + year + "/0" + "${month + 1}" + "/0" + dayOfMonth
        } else if (month in 0..8) {
            date = "" + year + "/0" + "${month + 1}" + "/" + dayOfMonth
        } else if (dayOfMonth in 0..8) {
            date = "" + year + "/" + "${month + 1}" + "/0" + dayOfMonth
        } else {
            date = "" + year + "/" + "${month + 1}" + "/" + dayOfMonth
        }

        return date

    }

    //指定した日付のRecordを取得する処理。
    private fun readAtTheDay(date: String): RealmResults<Record> {
        return realm
            .where(Record::class.java)
            .equalTo("date", date)
            .findAll()
            .sort("createdAt", Sort.DESCENDING)
    }

    //指定した日付の平均値を求める処理。
    private fun calculateSatisfactionAverage(date: String): Int {

        //その日のRecordを取得。
        val recordList = readAtTheDay(date)

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
}