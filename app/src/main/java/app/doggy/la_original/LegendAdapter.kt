package app.doggy.la_original

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_legend.view.*

class LegendAdapter(private val context: Context):
    RecyclerView.Adapter<LegendAdapter.ViewHolder>() {

    val items: MutableList<Legend> = mutableListOf()

    //importする。
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_legend, parent, false)
        return ViewHolder(view)
    }

    //importする。
    //itemsのposition番目の要素をViewHolderに表示する。
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.titleText.text = item.title
        holder.ratioText.text = item.ratio.toString()
        if (item.iconId == 0) {
            holder.icon.setImageResource(R.drawable.ic_baseline_sentiment_very_satisfied_24)
        } else {
            holder.icon.setImageResource(item.iconId)
        }
    }

    //importする。
    override fun getItemCount(): Int {
        return items.size
    }

    //Adapterにデータを登録するためのメソッド。
    fun addAll(items: List<Legend>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    //複数のViewを保持するクラス。
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val titleText: TextView = view.legendTitleText
        val ratioText: TextView = view.ratioText
        val icon: ImageView = view.legendIcon
    }
}