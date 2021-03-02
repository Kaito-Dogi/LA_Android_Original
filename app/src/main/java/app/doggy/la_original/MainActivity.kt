package app.doggy.la_original

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val postFab = findViewById<FloatingActionButton>(R.id.post_fab)
        postFab.setOnClickListener {
            val postIntent = Intent(baseContext, PostActivity::class.java)
            startActivity(postIntent)
        }

    }
}