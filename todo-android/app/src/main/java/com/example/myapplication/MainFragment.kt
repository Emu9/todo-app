package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.eclipsesource.json.Json
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONObject

class MainFragment : Fragment() {
    private val url = "http://10.0.2.2:8000/api/todo"

    companion object{
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onParallelGetButtonClick()
    }

    private fun onParallelGetButtonClick() = GlobalScope.launch(Dispatchers.Main) {
        val http = HttpUtil()

        withContext(Dispatchers.Default) { http.httpGet1(url) }.let {
            val result = Json.parse(it).asObject()

            val data = result.get("data").asArray()
            val tasks = arrayListOf<Task>()
            for (i in data) {
                val taskId = i.asObject()["id"].asInt()
                val taskTime = i.asObject()["startTime"].asString()
                val taskDes = i.asObject()["taskName"].asString()
                val timePri = i.asObject()["priorityType"].asInt()

                tasks.add(Task(taskId, taskTime, taskDes, timePri))
            }
            listView.adapter = TasksAdapter(tasks, itemListener)

            listView.setOnItemClickListener { _, _, _, id ->
                val taskId = id.toInt()
                val intent = Intent(activity, DescriptionActivity::class.java)
                intent.putExtra("id", tasks[taskId].id)
                startActivity(intent)
            }
            true
        }
    }

    private val itemListener = object : TaskItemListener {
        override fun onDeleteClick(task: Task): Job = GlobalScope.launch(Dispatchers.Main){

                val http = HttpUtil()
                val json = JSONObject()
                json.put("compFlag", 1)
                withContext(Dispatchers.Default){ http.httpPut1(url + "/" +task.id, json) }?.let{
                    val responseCode: Int = it.code()
                    println("responseCode: $responseCode")

                    if (!it.isSuccessful) {
                        Toast.makeText(
                            activity,
                            "error!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    if (it.body() != null) {
                        val intent = Intent(activity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                    }
                }
        }
    }

    private class TasksAdapter(private val tasks: List<Task>, private val listener: TaskItemListener): BaseAdapter() {
        override fun getCount() = tasks.size

        override fun getItem(i: Int) = tasks[i]

        override fun getItemId(i: Int) = i.toLong()

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val task = getItem(i)
            val rowView = view ?: LayoutInflater.from(viewGroup.context).inflate(R.layout.task_item, viewGroup, false)

            rowView.findViewById<TextView>(R.id.taskTime).apply {
                text = task.time
            }
            rowView.findViewById<TextView>(R.id.taskDescription).apply {
                text = task.description
            }

            rowView.findViewById<TextView>(R.id.taskPri).apply {
                text = task.priority.toString()
            }

            rowView.findViewById<ImageView>(R.id.taskCompButton).setOnClickListener {
                listener.onDeleteClick(task)
            }
            return rowView
        }
    }

    interface TaskItemListener {
        fun onDeleteClick(task: Task): Job
    }

    class HttpUtil {
        private val headerKey = "H-API-KEY"
        private val headerValue = "AUTH_API_KEY_20181203"

        fun httpGet1(url: String): String? {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .addHeader(headerKey, headerValue)
                .build()

            val response = client.newCall(request).execute()
            return response.body()?.string()
        }

        fun httpPut1(url: String, json: JSONObject):Response? {
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
