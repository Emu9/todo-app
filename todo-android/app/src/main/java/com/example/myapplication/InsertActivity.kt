package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject


class InsertActivity : AppCompatActivity() {
    val url = "http://10.0.2.2:8000/api/todo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        toolbar.inflateMenu(R.menu.insert_menu)
        toolbar.setTitleTextColor(getColor(R.color.white));
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back);

        var taskStartDt: String? = null
        var taskEndDt: String? = null
        var taskStartTm: String? = null
        var taskEndTm: String? = null
        val startDt = findViewById<DatePicker>(R.id.taskStartDt)
        val startTm = findViewById<TimePicker>(R.id.taskStartTm)
        val endDt = findViewById<DatePicker>(R.id.taskEndDt)
        val endTm = findViewById<TimePicker>(R.id.taskEndTm)
        val name = findViewById<EditText>(R.id.taskNm)
        val des = findViewById<EditText>(R.id.editDes)
        val pri = findViewById<Spinner>(R.id.taskPri)
        val not = findViewById<Switch>(R.id.taskNot)


        startDt.updateDate(2020,8,21)
        startTm.hour = 12
        startTm.minute = 0
        startTm.setIs24HourView(true)

        endDt.updateDate(2020,8,21)
        endTm.hour = 12 + 1
        endTm.minute = 0
        endTm.setIs24HourView(true)


        toolbar.setOnMenuItemClickListener { it ->
            if(it.itemId == R.id.checked){

                if (name.text.toString().isEmpty()) {
                    name.error = "文字を入力してください"
                }else {

                    val taskNm = name.text.toString()

                    val stDay = startDt.dayOfMonth
                    val stMonth = startDt.month
                    val stYear = startDt.year
                    taskStartDt = "$stYear-$stMonth-$stDay"

                    val edDay = endDt.dayOfMonth
                    val edMonth = endDt.month
                    val edYear = endDt.year
                    taskEndDt = "$edYear-$edMonth-$edDay"

                    val stHour = startTm.hour
                    val stMin = startTm.minute
                    taskStartTm = "$stHour:$stMin"

                    var edHour = endTm.hour
                    var edMin = endTm.minute
                    taskEndTm = "$edHour:$edMin"

                    val taskDes = des.text.toString()
                    val taskPri = pri.selectedItemId
                    val taskNot = not.isChecked

                    val json = JSONObject()
                    json.put("taskName", "$taskNm")
                    json.put("taskDes", "$taskDes")
                    json.put("startDate", "$taskStartDt")
                    json.put("startTime", "$taskStartTm")
                    json.put("endDate", "$taskEndDt")
                    json.put("endTime", "$taskEndTm")
                    json.put("priorityType", taskPri)
                    json.put("notifyFlag", taskNot)
                    json.put("compFlag", 0)
                    json.put("deleteFlag", 0)

                    onInsertButtonClick(json)

                }
                true
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
        menuInflater.inflate(R.menu.insert_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun onInsertButtonClick(json: JSONObject) = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpPost()
        withContext(Dispatchers.Default) { http.httpPost1("$url", json) }?.let {
            val responseCode: Int = it.code()
            println("responseCode: $responseCode")

            if (!it.isSuccessful) {
                Toast.makeText(
                    this@InsertActivity,
                    "error!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (it.body() != null) {
                val intent = Intent(this@InsertActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()

            }
        }
    }

    class HttpPost {
        fun httpPost1(url: String, json: JSONObject): Response? {

            val client = OkHttpClient()
            val mimeType: MediaType? = MediaType.parse("application/json; charset=utf-8")
            val requestBody = RequestBody.create(mimeType, json.toString())
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()
            return client.newCall(request).execute()
        }
    }

}