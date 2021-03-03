package app.doggy.la_original

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val postFab = findViewById<FloatingActionButton>(R.id.postFab)
        postFab.setOnClickListener {
            val postIntent = Intent(baseContext, PostActivity::class.java)
            startActivity(postIntent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                val settingsIntent = Intent(baseContext, SettingsActivity::class.java)
                startActivity(settingsIntent)
                true
            }
            R.id.howToUse -> {
                val howToUseIntent = Intent(baseContext, HowToUseActivity::class.java)
                startActivity(howToUseIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}