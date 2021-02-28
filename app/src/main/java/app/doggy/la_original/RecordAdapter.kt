package app.doggy.la_original

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_record_data_cell.view.*
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter(
    private val context: Context,
    private var recordList: OrderedRealmCollection<Record>?,
    private val autoUpdate: Boolean
): RealmRecyclerViewAdapter<Record, RecordAdapter.TaskViewHolder>(recordList, autoUpdate) {

    override fun getItemCount(): Int = recordList?.size ?: 0

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val record: Record = recordList?.get(position) ?: return

        holder.commentTextView.text = record.comment
        when {
            record.perceivedValue - record.actualValue > 0 -> holder.savingsTextView.setTextColor(Color.BLUE)
            record.perceivedValue - record.actualValue < 0 -> holder.savingsTextView.setTextColor(Color.RED)
            else -> holder.savingsTextView.setTextColor(Color.parseColor("#222222"))
        }
        holder.savingsTextView.text = "¥" + (record.perceivedValue - record.actualValue).toString()
        holder.dateTextView.text =
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.JAPANESE).format(record.createdAt)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_record_data_cell, viewGroup, false)
        return TaskViewHolder(v)
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val commentTextView: TextView = view.commentTextView
        val savingsTextView: TextView = view.savingsTextView
        val dateTextView: TextView = view.dateTextView
    }

}