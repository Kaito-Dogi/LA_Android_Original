package app.doggy.la_original

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_legend.view.*

class LegendAdapter(
    private val context: Context,
    private var legendList: OrderedRealmCollection<Legend>?,
    private var listener: OnItemClickListener,
    private val autoUpdate: Boolean
): RealmRecyclerViewAdapter<Legend, LegendAdapter.LegendViewHolder>(legendList, autoUpdate) {

    override fun getItemCount(): Int = legendList?.size ?: 0

    override fun onBindViewHolder(holder: LegendViewHolder, position: Int) {
        val legend: Legend = legendList?.get(position) ?: return

        holder.container.setOnClickListener{
            listener.onItemClick(legend)
        }

        holder.titleText.text = legend.title
        holder.ratioText.text = "${legend.ratio} ％"
        holder.icon.setImageResource(legend.iconId)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): LegendViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_legend, viewGroup, false)
        return LegendViewHolder(v)
    }

    class LegendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container : LinearLayout = view.legendContainer
        val titleText: TextView = view.legendTitleText
        val ratioText: TextView = view.ratioText
        val icon: ImageView = view.legendIcon
    }

    interface OnItemClickListener {
        fun onItemClick(item: Legend)
    }
}