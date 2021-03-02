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

        val recordList = readAll()

        if (recordList.isEmpty()) {
            createDummy()
        }

        val adapter = RecordAdapter(context as Context, recordList, true)

        recordRecyclerViewInCalendar.setHasFixedSize(true)
        recordRecyclerViewInCalendar.layoutManager = LinearLayoutManager(context)
        recordRecyclerViewInCalendar.adapter = adapter

        //カレンダーの日付のクリックイベント設定
        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            Toast.makeText(context, "" + year + "-" + (month+1) + "-" + dayOfMonth, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun createDummy() {
        for (i in 0..9) {
            create("支出$i")
        }
    }

    private fun create(comment: String) {
        realm.executeTransaction {
            val item = it.createObject(Record::class.java, UUID.randomUUID().toString())
            item.comment = comment
        }
    }

    private fun readAll(): RealmResults<Record> {
        return realm.where(Record::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }
}