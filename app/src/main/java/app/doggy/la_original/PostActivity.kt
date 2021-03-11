package app.doggy.la_original

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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

    //満足度。
    private var satisfaction = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.activity_post_title)

        //アイコンを管理するリスト。
        val icons: List<CardView> = listOf(
            veryUnsatisfiedIcon,
            unsatisfiedIcon,
            neitherIcon,
            satisfiedIcon,
            verySatisfiedIcon
        )

        //アイコンにOnClickListenerを設定。
        for (i in 0 until icons.size) {
            icons[i].setOnClickListener(IconClickListener(i, icons))
        }

//        //Sliderの処理。
//        satisfiedSlider.addOnSliderTouchListener(object: Slider.OnSliderTouchListener {
//            override fun onStartTrackingTouch(slider: Slider) {
//                // Responds to when slider's touch event is being started
//            }
//
//            override fun onStopTrackingTouch(slider: Slider) {
//                // Responds to when slider's touch event is being stopped
//                satisfaction = satisfiedSlider.value.toInt()
//                selectedSatisfaction = true
//            }
//        })
//
//        satisfiedSlider.addOnChangeListener { slider, value, fromUser ->
//            // Responds to when slider's value is changed
//            satisfaction = satisfiedSlider.value.toInt()
//            selectedSatisfaction = true
//        }
//
//        satisfiedIcon.setOnClickListener {
//            satisfiedSlider.value = 100f
//            satisfaction = satisfiedSlider.value.toInt()
//            selectedSatisfaction = true
//        }
//
//        unsatisfiedIcon.setOnClickListener {
//            satisfiedSlider.value = -100f
//            satisfaction = satisfiedSlider.value.toInt()
//            selectedSatisfaction = true
//        }
//
//        satisfiedSlider.setLabelFormatter { value: Float ->
//            return@setLabelFormatter "${value.roundToInt()}%"
//        }

        //DatePickerの処置。
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH)
        val day = calender.get(Calendar.DAY_OF_MONTH)

        datePickText.setOnClickListener {

            //datePickerをインスタンス化。
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener{view,year,month,dayOfMonth ->
                    datePickText.text = getDate(year, month, dayOfMonth)
                },
                year,
                month,
                day
            )

            //datePickerを起動。
            datePickerDialog.show()

        }

        //Categoryの表示。
        //Categoryの名前。
        val names: List<String> = listOf(
                getString(R.string.category_convenience_store),
                getString(R.string.category_lunch),
                getString(R.string.category_dinner),
                getString(R.string.category_cafe)
        )

        //Categoryのアイコン。
        val iconIds: List<Int> = listOf(
                R.drawable.ic_baseline_storefront_24,
                R.drawable.ic_baseline_lunch_dining_24,
                R.drawable.ic_baseline_dinner_dining_24,
                R.drawable.ic_baseline_coffee_24
        )

        //Categoryのデータを取得。
        val categoryList = readAllCategory()

        //初回起動時にオリジナルのCategoryを生成。
        if (readAllCategory().isEmpty()) {
            createOriginalCategory(names, iconIds)
        }

        //カテゴリーの表示。
        var categoryId = ""
        var iconId = 0
        var preCard: CardView = findViewById(R.id.dummyCard)

        val adapter = CategoryAdapter(this, categoryList, object: CategoryAdapter.OnItemClickListener {
            override fun onItemClick(item: Category, card: CardView) {

//                val toastMessage = getString(R.string.toast_category_selected, item.name)
//                Toast.makeText(baseContext, toastMessage, Toast.LENGTH_SHORT).show()

                categoryId = item.id
                iconId = item.iconId

                preCard.setBackgroundResource(R.drawable.shape_rounded_corners)
                preCard.cardElevation = 6f

                card.setBackgroundResource(R.drawable.shape_rounded_corners_selected)
                card.cardElevation = 0f

                preCard = card
            }
        },true)

        //categoryRecyclerViewの設定。
        categoryRecyclerView.setHasFixedSize(true)
        categoryRecyclerView.layoutManager = GridLayoutManager(baseContext, 4)
        categoryRecyclerView.adapter = adapter

        //Recordの保存。
        saveButton.setOnClickListener {

            //データを入力させる工夫をしたい。
            if (datePickText.text.toString() == "") {
                makeSnackbar(contextView, R.string.snack_bar_date_empty)

            } else if (amountEditText.text.toString() == "") {
                makeSnackbar(contextView, R.string.snack_bar_amount_empty)

            } else if (titleEditText.text.toString() == "") {
                makeSnackbar(contextView, R.string.snack_bar_title_empty)

            } else if (satisfaction == -1) {
                makeSnackbar(contextView, R.string.snack_bar_satisfaction_empty)

            } else {
                val amount = amountEditText.text.toString().toInt()
                val title = titleEditText.text.toString()
                val comment = commentEditText.text.toString()
                val date = datePickText.text.toString()

                create(satisfaction, amount, title, comment, date, categoryId, iconId)

                finish()

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    //Categoryの追加。
    private fun createCategory(name: String, iconId: Int) {
        realm.executeTransaction {
            val category = it.createObject(Category::class.java, UUID.randomUUID().toString())
            category.name = name
            category.iconId = iconId
        }
    }

    //元から表示しておくCategoryを追加。
    private fun createOriginalCategory(names: List<String>, iconIds: List<Int>) {
        for (i in 0 until names.size) {
            createCategory(names[i], iconIds[i])
        }
    }

    //全てのCategoryを取得する。
    private fun readAllCategory(): RealmResults<Category> {
        return realm.where(Category::class.java).findAll().sort("createdAt", Sort.ASCENDING)
    }

    //Recordの追加。
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

    //日付を文字列で取得する。
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

    private fun makeSnackbar(view: View, textId: Int) {

        val snackbar = Snackbar.make(view, getText(textId), Snackbar.LENGTH_SHORT)

        snackbar.setAction("OK") {
            snackbar.dismiss()
        }

        snackbar.show()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class IconClickListener(val index: Int, val icons: List<CardView>): View.OnClickListener {
        override fun onClick(view: View) {

            //全てのアイコンを選択されていない状態にする。
            for (i in 0 until icons.size) {
                icons[i].setBackgroundResource(R.drawable.shape_round)
                icons[i].cardElevation = 6f
            }

            //押したアイコンを選択された状態にする。
            icons[index].setBackgroundResource(R.drawable.shape_round_selected)
            icons[index].cardElevation = 0f

            //satisfactionに満足度代入。
            satisfaction = index

        }
    }

}