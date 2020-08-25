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
import java.util.*


class InsertActivity : AppCompatActivity() {
    private val url = "http://10.0.2.2:8000/api/todo"
    val http = HttpPost()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        toolbar.inflateMenu(R.menu.insert_menu)
        toolbar.setTitleTextColor(getColor(R.color.white))
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val startDt = findViewById<DatePicker>(R.id.taskStartDt)
        val startTm = findViewById<TimePicker>(R.id.taskStartTm)
        val endDt = findViewById<DatePicker>(R.id.taskEndDt)
        val endTm = findViewById<TimePicker>(R.id.taskEndTm)
        val name = findViewById<EditText>(R.id.taskNm)
        val des = findViewById<EditText>(R.id.editDes)
        val pri = findViewById<Spinner>(R.id.taskPri)
        val not = findViewById<Switch>(R.id.taskNot)
        val error1 = findViewById<TextView>(R.id.error_msg1)
        val error2 = findViewById<TextView>(R.id.error_msg2)
        val stDate: Calendar = Calendar.getInstance()
        val stTime: Calendar = Calendar.getInstance()
        val edDate: Calendar = Calendar.getInstance()
        val edTime: Calendar = Calendar.getInstance()
        val isEmpty = intent.extras?.isEmpty
        var taskStartDt: String?
        var taskEndDt: String?
        var taskStartTm: String?
        var taskEndTm: String?
        var flag: Boolean? = null

        if (!isEmpty!!) {
            flag = true

            name.setText(intent.extras?.get("taskNm").toString())
            val arrDt = intent.extras?.get("taskStDt").toString().split("/")
            startDt.updateDate(arrDt[0].toInt(),arrDt[1].toInt(),arrDt[2].toInt())

            val arrStTm = intent.extras?.get("taskStTm").toString().split(":")
            startTm.hour = arrStTm[0].toInt()
            startTm.minute = arrStTm[1].toInt()
            startTm.setIs24HourView(true)

            val arrEdTm = intent.extras?.get("taskEdTm").toString().split(":")
            endTm.hour = arrEdTm[0].toInt()
            endTm.minute = arrEdTm[1].toInt()
            endTm.setIs24HourView(true)

            des.setText(intent.extras?.get("taskDs").toString())
            pri.setSelection(intent.extras?.get("taskPri")as Int)
            not.isChecked = intent.extras?.get("taskNot") as Boolean

        } else {
            startDt.updateDate(2020, 8, 21)
            startTm.hour = 12
            startTm.minute = 0
            startTm.setIs24HourView(true)

            endDt.updateDate(2020, 8, 21)
            endTm.hour = 12 + 1
            endTm.minute = 0
            endTm.setIs24HourView(true)
        }

        toolbar.setOnMenuItemClickListener {
            if(it.itemId == R.id.checked){

                val taskNm = name.text.toString()

                val stDay = startDt.dayOfMonth
                val stMonth = startDt.month
                val stYear = startDt.year
                taskStartDt = "$stYear-$stMonth-$stDay"
                stDate.set(stYear, stMonth, stDay)

                val edDay = endDt.dayOfMonth
                val edMonth = endDt.month
                val edYear = endDt.year
                taskEndDt = "$edYear-$edMonth-$edDay"
                edDate.set(edYear, edMonth, edDay)

                val stHour = startTm.hour
                val stMin = startTm.minute
                taskStartTm = "$stHour:$stMin"
                stTime.set(stHour, stMin)

                val edHour = endTm.hour
                val edMin = endTm.minute
                taskEndTm = "$edHour:$edMin"
                edTime.set(edHour, edMin)

                val taskDes = des.text.toString()
                val taskPri = pri.selectedItemId
                val taskNot = not.isChecked


                if (taskNm.isEmpty()) {
                    error1.text = "文字を入力してください。"
                }else if (!stTime.after(edTime)) {
                    error2.text = "開始日,開始時間を確認してください。"
                }else if (!stDate.after(edDate) && stDate != edDate) {
                    error2.text = "開始日,開始時間を確認してください。"
                }else {
                    val json = JSONObject()
                    json.put("taskName", taskNm)
                    json.put("taskDes", taskDes)
                    json.put("startDate", "$taskStartDt")
                    json.put("startTime", "$taskStartTm")
                    json.put("endDate", "$taskEndDt")
                    json.put("endTime", "$taskEndTm")
                    json.put("priorityType", taskPri)
                    json.put("notifyFlag", taskNot)
                    json.put("compFlag", 0)
                    json.put("deleteFlag", 0)

                    onInsertButtonClick(json, flag)
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
        menuInflater.inflate(R.menu.insert_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun onInsertButtonClick(json: JSONObject, flag: Boolean?) = GlobalScope.launch(Dispatchers.Main) {

        withContext(Dispatchers.Default) { if (flag!!) http.httpPut1(url, json) else http.httpPost1(url, json)}?.let {
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

        fun httpPut1(url: String, json: JSONObject): Response? {

            val client = OkHttpClient()
            val mimeType: MediaType? = MediaType.parse("application/json; charset=utf-8")
            val requestBody = RequestBody.create(mimeType, json.toString())
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()
            return client.newCall(request).execute()
        }


    }

}