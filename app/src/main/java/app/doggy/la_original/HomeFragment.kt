package app.doggy.la_original

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.*

class HomeFragment : Fragment() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recordList = readAll()

        val adapter = RecordAdapter(context as Context, recordList, true)

        recordRecyclerView.setHasFixedSize(true)
        recordRecyclerView.layoutManager = LinearLayoutManager(context)
        recordRecyclerView.adapter = adapter

    }

    override fun onResume() {
        super.onResume()

        var totalSavings = calculateTotal()

        when {
            totalSavings > 0 -> totalSavingsTextView.setTextColor(Color.BLUE)
            totalSavings < 0 -> totalSavingsTextView.setTextColor(Color.RED)
            else -> totalSavingsTextView.setTextColor(Color.parseColor("#222222"))
        }

        totalSavingsTextView.text = "¥" + totalSavings.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun readAll(): RealmResults<Record> {
        return realm.where(Record::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }

    private fun calculateTotal(): Int {
        var calculateTotal = 0
        val recordList = realm.where(Record::class.java).findAll()
        for (i in 0 until recordList.size) {
            calculateTotal += (recordList[i]?.perceivedValue as Int) - (recordList[i]?.actualValue as Int)
        }
        return calculateTotal
    }
}