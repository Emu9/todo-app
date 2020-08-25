package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


class DescriptionActivity : AppCompatActivity() {
    private val url = "http://10.0.2.2:8000/api/todo"

    val http = HttpPost()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        toolbar.inflateMenu(R.menu.description_memu)
        toolbar.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val taskNm = findViewById<TextView>(R.id.taskNm)
        val taskStDt = findViewById<TextView>(R.id.taskStartDt)
        val taskStTm = findViewById<TextView>(R.id.taskStartTm)
        val taskEdTm = findViewById<TextView>(R.id.taskEndTm)
        val taskDs = findViewById<TextView>(R.id.taskDes)
        val taskPri  = findViewById<TextView>(R.id.taskPri)
        val taskNot  = findViewById<Switch>(R.id.taskNot)

        val text = intent.extras?.get("id")
        onParallelGetButtonClick(text as Int)

        toolbar.setOnMenuItemClickListener { it ->
            if (it.itemId == R.id.action_settings0) {
                val intent = Intent(this@DescriptionActivity, InsertActivity::class.java)
                intent.putExtra("taskNm", taskNm.text.toString())
                intent.putExtra("taskStDt", taskStDt.text.toString())
                intent.putExtra("taskStTm", taskStTm.text.toString())
                intent.putExtra("taskEdTm", taskEdTm.text.toString())
                intent.putExtra("taskDs", taskDs.text.toString())
                intent.putExtra("taskPri", taskPri.id)
                intent.putExtra("taskNot", taskNot.isChecked)
                startActivity(intent)
                finish()
//            } else if (it.itemId == R.id.action_settings1){

            } else if (it.itemId == R.id.action_settings2){
                GlobalScope.launch(Dispatchers.Main) {
                    withContext(Dispatchers.Default) { http.httpDelete1("$url/$text") }?.let {
                        val responseCode: Int = it.code()
                        println("responseCode: $responseCode")

                        if (!it.isSuccessful) {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "error!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (it.body() != null) {
                            val intent = Intent(this@DescriptionActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
            true
        }

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)

                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.description_memu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun onParallelGetButtonClick(id: Int) = GlobalScope.launch(Dispatchers.Main) {
        val taskNm = findViewById<TextView>(R.id.taskNm)
        val taskStDt = findViewById<TextView>(R.id.taskStartDt)
        val taskStTm = findViewById<TextView>(R.id.taskStartTm)
        val taskEdTm = findViewById<TextView>(R.id.taskEndTm)
        val taskDs = findViewById<TextView>(R.id.taskDes)
        val taskPri  = findViewById<TextView>(R.id.taskPri)
        val taskNot  = findViewById<Switch>(R.id.taskNot)
        val stateTexts: Array<String> = resources.getStringArray(R.array.list)

        withContext(Dispatchers.Default) { http.httpGet2("$url/$id") }.let {
            val result = Json.parse(it).asObject()
            taskNm.text = (result["data"] as JsonObject).get("taskName").asString()
            taskStDt.text = (result["data"] as JsonObject).get("startDate").asString()
            taskStTm.text = (result["data"] as JsonObject).get("startTime").asString()
            taskEdTm.text = (result["data"] as JsonObject).get("endTime").asString()
            taskDs.text = if (!result["data"].asObject().get("taskDes").isNull) (result["data"] as JsonObject).get("taskDes").asString() else null
            taskPri.text = stateTexts[(result["data"] as JsonObject).get("priorityType").asInt()]
            taskNot.showText = ((result["data"] as JsonObject).get("notifyFlag")).isBoolean
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        return true
    }

    class HttpPost {
        fun httpGet2(url: String): String? {
            val headerKey = "H-API-KEY"
            val headerValue = "AUTH_API_KEY_20181203"

            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader(headerKey, headerValue)
                .build()

            val response = client.newCall(request).execute()
            return response.body()?.string()
        }

        fun httpDelete1(url: String): Response? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .delete()
                .build()

            return client.newCall(request).execute()
        }
    }
}