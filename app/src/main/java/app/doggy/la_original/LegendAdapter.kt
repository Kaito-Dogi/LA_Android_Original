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
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import kotlinx.android.synthetic.main.item_legend.view.*
import kotlin.math.round
import kotlin.math.roundToInt

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
        holder.icon.setImageResource(legend.iconId)

        if (legend.ratio < 1.00f) {
            holder.ratioText.text = "${round(legend.ratio * 100) / 100}％"
        } else {
            holder.ratioText.text = "${legend.ratio.roundToInt()}％"
        }

        when(legend.satisfaction) {
            0 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#053C5E"))
            1 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#86BBD8"))
            2 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BFDBF7"))
            3 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E76B74"))
            4 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EA526F"))
            -1 -> holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#444444"))
        }

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
        val iconContainer: CardView = view.legendIconContainer
    }

    interface OnItemClickListener {
        fun onItemClick(item: Legend)
    }
}