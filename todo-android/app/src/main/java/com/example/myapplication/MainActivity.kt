package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

const val MY_REQUEST_CODE = 1
var locale: Locale? = null
var language: String? = null

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val today = findViewById<TextView>(R.id.today)
        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
        today.text = current.format(formatter)

        toolbar.also {
            it.setTitleTextColor(getColor(R.color.white))
            setSupportActionBar(it)
        }

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.contentFrame, MainFragment.newInstance())
        }.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_memu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent = Intent(this, InsertActivity::class.java)
        startActivity(intent)

        return super.onOptionsItemSelected(item)
    }
}