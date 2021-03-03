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
import kotlinx.android.synthetic.main.item_category.view.*
import kotlinx.android.synthetic.main.item_record.view.*

class CategoryAdapter(
        private val context: Context,
        private var categoryList: OrderedRealmCollection<Category>?,
        private var listener: OnItemClickListener,
        private val autoUpdate: Boolean
): RealmRecyclerViewAdapter<Category, CategoryAdapter.CategoryViewHolder>(categoryList, autoUpdate) {

    override fun getItemCount(): Int = categoryList?.size ?: 0

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category: Category = categoryList?.get(position) ?: return

        holder.container.setOnClickListener{
            listener.onItemClick(category)
        }

        holder.nameText.text = category.name

        if (category.iconId == 0) {
            holder.icon.setImageResource(R.drawable.ic_baseline_sentiment_very_satisfied_24)
        } else {
            holder.icon.setImageResource(category.iconId)
        }

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): CategoryViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_category, viewGroup, false)
        return CategoryViewHolder(v)
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container : LinearLayout = view.categoryContainer
        val nameText: TextView = view.categoryNameText
        val icon: ImageView = view.categoryIcon
    }

    interface OnItemClickListener {
        fun onItemClick(item: Category)
    }

}