package app.doggy.la_original

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.item_category.*
import java.util.*

class PostActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.activity_post_title)

        /*
        Sliderの処理。
         */

        var satisfaction = 0

        satisfiedSlider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                satisfaction = satisfiedSlider.value.toInt()
            }
        })

        satisfiedSlider.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            satisfaction = satisfiedSlider.value.toInt()
        }

        /*
        DatePickerの処置。
         */

        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        datePickText.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth ->
                    datePickText.text = getDate(year, month, dayOfMonth)
                },
                year,
                month,
                day
            )

            datePickerDialog.show()

        }

        /*
        Categoryの表示。
         */

        val categoryList = readAllCategory()

        if (readAllCategory().isEmpty()) {
            createDummy()
        }

        var categoryId = ""
        var iconId = 0

        val adapter = CategoryAdapter(this, categoryList, object: CategoryAdapter.OnItemClickListener {
            override fun onItemClick(item: Category) {

                Snackbar.make(postContainer, "${item.name}" + getText(R.string.post_snack_bar), Snackbar.LENGTH_SHORT).show()

                categoryId = item.id
                iconId = item.iconId

            }
        },true)

        categoryRecyclerView.setHasFixedSize(true)
        categoryRecyclerView.layoutManager = GridLayoutManager(baseContext, 4)
        categoryRecyclerView.adapter = adapter

        submitButton.setOnClickListener {
            create(
                    satisfaction,
                    amountEditText.text.toString().toInt(),
                    titleEditText.text.toString(),
                    commentEditText.text.toString(),
                    datePickText.text.toString(),
                    categoryId,
                    iconId
            )
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun createCategory(name: String, iconId: Int) {
        realm.executeTransaction {
            val category = it.createObject(Category::class.java, UUID.randomUUID().toString())
            category.name = name
            category.iconId = iconId
        }
    }

    private fun createDummy() {
        for (i in 0..9) {
            createCategory("カテゴリー$i", R.drawable.ic_baseline_sentiment_very_satisfied_24)
        }
    }

    private fun readAllCategory(): RealmResults<Category> {
        return realm.where(Category::class.java).findAll().sort("createdAt", Sort.DESCENDING)
    }

    private fun create(
        satisfaction: Int,
        amount: Int,
        title: String,
        comment: String,
        date: String,
        categoryId: String,
        iconId: Int
    ) {
        realm.executeTransaction {
            val record = it.createObject(Record::class.java, UUID.randomUUID().toString())
            record.satisfaction = satisfaction
            record.amount = amount
            record.title = title
            record.comment = comment
            record.date = date
            record.categoryId = categoryId
            record.iconId = iconId
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
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
}