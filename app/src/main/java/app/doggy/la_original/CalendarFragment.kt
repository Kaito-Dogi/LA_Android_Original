package app.doggy.la_original

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var recordList = readAtTheDay(SimpleDateFormat("yyyy/MM/dd").format(Date()).toString())

        var adapter = RecordAdapter(context as Context, recordList, true)

        recordRecyclerViewInCalendar.setHasFixedSize(true)

        recordRecyclerViewInCalendar.layoutManager = LinearLayoutManager(context)
        recordRecyclerViewInCalendar.adapter = adapter

        averageText.text = calculateAverageSatisfaction(SimpleDateFormat("yyyy/MM/dd").format(Date()).toString()).toString() + "％"

        /*
        Calendarの処理。
         */

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->

            val date = getDate(year, month, dayOfMonth)

            recordList = readAtTheDay(date)
            adapter = RecordAdapter(context as Context, recordList, true)
            recordRecyclerViewInCalendar.adapter = adapter

            averageText.text = calculateAverageSatisfaction(date).toString() + "％"

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

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

    private fun readAtTheDay(date: String): RealmResults<Record> {
        return realm
            .where(Record::class.java)
            .equalTo("date", date)
            .findAll()
            .sort("createdAt", Sort.DESCENDING)
    }

    private fun calculateAverageSatisfaction(date: String): Double {

        val records = realm
                .where(Record::class.java)
                .equalTo("date", date)
                .findAll()

        var averageSatisfaction = 0.0

        for (i in 0 until records.size) {
            averageSatisfaction += records[i]?.satisfaction.toString().toDouble()
        }

        if (!records.isEmpty()) {
            averageSatisfaction /= records.size
        }

        return averageSatisfaction
    }
}