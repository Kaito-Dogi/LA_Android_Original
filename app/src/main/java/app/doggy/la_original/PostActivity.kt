package app.doggy.la_original

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_post.*
import java.util.*

class PostActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    //lateinit var preContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.activity_post_title)

        /*
        Sliderの処理。
         */

        var satisfaction = 0
        var selectedSatisfaction = false

        satisfiedSlider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being started
            }

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped
                satisfaction = satisfiedSlider.value.toInt()
                selectedSatisfaction = true
            }
        })

        satisfiedSlider.addOnChangeListener { slider, value, fromUser ->
            // Responds to when slider's value is changed
            satisfaction = satisfiedSlider.value.toInt()
            selectedSatisfaction = true
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

        val names: List<String> = listOf(
                getString(R.string.category_convenience_store),
                getString(R.string.category_lunch),
                getString(R.string.category_dinner),
                getString(R.string.category_cafe),
                getString(R.string.category_fast_food),
                getString(R.string.category_shopping),
                getString(R.string.category_ramen),
                getString(R.string.category_games),
                getString(R.string.category_music),
                getString(R.string.category_savings),
                getString(R.string.category_others),
                getString(R.string.category_others)
        )

        val iconIds: List<Int> = listOf(
                R.drawable.ic_baseline_storefront_24,
                R.drawable.ic_baseline_lunch_dining_24,
                R.drawable.ic_baseline_dinner_dining_24,
                R.drawable.ic_baseline_coffee_24,
                R.drawable.ic_baseline_fastfood_24,
                R.drawable.ic_baseline_shopping_cart_24,
                R.drawable.ic_baseline_ramen_dining_24,
                R.drawable.ic_baseline_sports_esports_24,
                R.drawable.ic_baseline_music_note_24,
                R.drawable.ic_baseline_savings_24,
                R.drawable.ic_baseline_sentiment_very_satisfied_24,
                R.drawable.ic_baseline_sentiment_very_dissatisfied_24
        )

        val categoryList = readAllCategory()

        if (readAllCategory().isEmpty()) {
            createOriginalCategory(names, iconIds)
        }

        var categoryId = ""
        var iconId = 0
        var preItem: CardView = findViewById(R.id.dummyCard)

        val adapter = CategoryAdapter(this, categoryList, object: CategoryAdapter.OnItemClickListener {
            override fun onItemClick(item: Category, card: CardView) {
                //Toast.makeText(baseContext, item.name + getText(R.string.toast_category_selected), Toast.LENGTH_SHORT).show()
                categoryId = item.id
                iconId = item.iconId

                preItem.setBackgroundResource(R.drawable.shape_rounded_corners_white)
                preItem.cardElevation = 6f

                card.setBackgroundResource(R.drawable.shape_rounded_corners_gray)
                card.cardElevation = 0f

                preItem = card
            }
        },true)

        categoryRecyclerView.setHasFixedSize(true)
        categoryRecyclerView.layoutManager = GridLayoutManager(baseContext, 4)
        categoryRecyclerView.adapter = adapter

        submitButton.setOnClickListener {

            if (datePickText.text.toString() == "") {
                Snackbar.make(postContainer, getText(R.string.snack_bar_date_empty), Snackbar.LENGTH_SHORT).show()
            } else if (amountEditText.text.toString() == "") {
                Snackbar.make(postContainer, getText(R.string.snack_bar_amount_empty), Snackbar.LENGTH_SHORT).show()
            } else if (titleEditText.text.toString() == "") {
                Snackbar.make(postContainer, getText(R.string.snack_bar_title_empty), Snackbar.LENGTH_SHORT).show()
            } else if (!selectedSatisfaction) {
                Snackbar.make(postContainer, getText(R.string.snack_bar_satisfaction_empty), Snackbar.LENGTH_SHORT).show()
            } else {
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

    private fun createOriginalCategory(names: List<String>, iconIds: List<Int>) {
        for (i in 0 until names.size) {
            createCategory(names[i], iconIds[i])
        }
    }

    private fun readAllCategory(): RealmResults<Category> {
        return realm.where(Category::class.java).findAll().sort("createdAt", Sort.ASCENDING)
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