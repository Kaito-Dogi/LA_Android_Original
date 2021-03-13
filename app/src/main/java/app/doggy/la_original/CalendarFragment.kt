package app.doggy.la_original

import android.content.Context
import android.os.Bundle
import android.util.Log
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

        //Calendarをインスタンス化。
        val calendar = Calendar.getInstance()

        //今日の日付を取得。
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
        Log.d("getListToday", calendar.time.toInt().toString())

        //今日のRecordリストを表示。
        var recordList = readAtTheDay(calendar.time.toInt())
        var adapter = RecordAdapter(context as Context, recordList, object: RecordAdapter.OnItemClickListener {
            override fun onItemClick(item: Record) {

            }
        },true)
        recordRecyclerViewInCalendar.adapter = adapter

        //今日の平均満足度を表示。
        //calendarAverageText.text = "${calculateSatisfactionAverage(SimpleDateFormat("yyyy/MM/dd").format(Date()).toString())}％"
        //calendarAverageText.text = getString(R.string.text_calendar_average, calculateSatisfactionAverage(SimpleDateFormat("yyyy/MM/dd").format(Date())))

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->

            //日付を取得。
            val clickedCalendar = Calendar.getInstance()
            clickedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            Log.d("getListCalendar", clickedCalendar.time.toInt().toString())

            //その日のRecordを一覧表示。
            recordList = readAtTheDay(clickedCalendar.time.toInt())
            adapter = RecordAdapter(context as Context, recordList, object: RecordAdapter.OnItemClickListener {
                override fun onItemClick(item: Record) {

                }
            },true)
            recordRecyclerViewInCalendar.adapter = adapter

            //その日の平均満足度を表示。
            //calendarAverageText.text = getString(R.string.text_calendar_average, calculateSatisfactionAverage(date))

        }

        //calendar.dateTextAppearance

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //指定した日付のRecordを取得する。
    private fun readAtTheDay(date: Int): RealmResults<Record> {

        val recordList = realm
                .where(Record::class.java)
                .equalTo("date", date)
                .findAll()
                .sort("createdAt", Sort.DESCENDING)

        Log.d("getList", recordList.toString())

        return recordList

    }

    private fun Date.toInt(): Int {
        val dateString = SimpleDateFormat("yyyyMMdd").format(this)
        Log.d("Date.toInt", dateString)
        return dateString.toInt()
    }

//    //指定した日付の平均値を求める。
//    private fun calculateSatisfactionAverage(date: Date): Int {
//
//        //その日のRecordを取得。
//        val recordList = readAtTheDay(date)
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
//
//    }
}