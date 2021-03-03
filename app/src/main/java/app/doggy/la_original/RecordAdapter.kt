package app.doggy.la_original

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_record.view.*
import java.util.*

class RecordAdapter(
    private val context: Context,
    private var recordList: OrderedRealmCollection<Record>?,
    private val autoUpdate: Boolean
): RealmRecyclerViewAdapter<Record, RecordAdapter.RecordViewHolder>(recordList, autoUpdate) {

    override fun getItemCount(): Int = recordList?.size ?: 0

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record: Record = recordList?.get(position) ?: return

        holder.titleText.text = record.title
        holder.satisfactionText.text = record.satisfaction.toString() + "％"
        holder.amountText.text = record.amount.toString() + "円"

        if (record.iconId == 0) {
            holder.icon.setImageResource(R.drawable.ic_baseline_sentiment_very_satisfied_24)
        } else {
            holder.icon.setImageResource(record.iconId)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecordViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_record, viewGroup, false)
        return RecordViewHolder(v)
    }

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.titleText
        val satisfactionText: TextView = view.satisfactionText
        val amountText: TextView = view.amountText
        val icon: ImageView = view.recordIcon
    }

}