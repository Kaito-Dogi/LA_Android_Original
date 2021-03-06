package app.doggy.la_original

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_record.view.*

class RecordAdapter(
        private val context: Context,
        private var recordList: OrderedRealmCollection<Record>?,
        private var listener: OnItemClickListener,
        private val autoUpdate: Boolean
): RealmRecyclerViewAdapter<Record, RecordAdapter.RecordViewHolder>(recordList, autoUpdate) {

    override fun getItemCount(): Int = recordList?.size ?: 0

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record: Record = recordList?.get(position) ?: return

        holder.container.setOnClickListener{
            listener.onItemClick(record)
        }

        holder.titleText.text = record.title
        //holder.satisfactionText.text = "${record.satisfaction}％"
        holder.amountText.text = "¥${"%,d".format(record.amount)}"
        holder.icon.setImageResource(record.iconId)

        when(record.satisfaction) {
            0 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pri_700))
            1 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pri_500))
            2 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.pri_200))
            3 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.positive_200))
            4 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.positive_500))
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecordViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_record, viewGroup, false)
        return RecordViewHolder(v)
    }

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container : LinearLayout = view.recordContainer
        val titleText: TextView = view.titleText
        //val satisfactionText: TextView = view.satisfactionText
        val amountText: TextView = view.amountText
        val icon: ImageView = view.recordIcon
        val iconContainer: CardView = view.recordIconContainer
    }

    interface OnItemClickListener {
        fun onItemClick(item: Record)
    }
}