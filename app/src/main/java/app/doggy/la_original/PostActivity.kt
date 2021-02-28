package app.doggy.la_original

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_post.*
import java.util.*

class PostActivity : AppCompatActivity() {

    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        submitButton.setOnClickListener {
            create(
                actualValueEditText.text.toString().toInt(),
                perceivedValueEditText.text.toString().toInt(),
                commentEditText.text.toString()
            )
            finish()
        }
    }

    private fun create(actualValue: Int, perceivedValue: Int, comment: String) {
        realm.executeTransaction {
            val record = it.createObject(Record::class.java, UUID.randomUUID().toString())
            Log.d("comment", comment)
            record.actualValue = actualValue
            record.perceivedValue = perceivedValue
            record.comment = comment
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}