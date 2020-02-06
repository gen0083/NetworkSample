package jp.gcreate.sample.networksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.gcreate.sample.networksample.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var binding: ActivityMainBinding
    private val okHttpClient = OkHttpClient.Builder().build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.getHtml.setOnClickListener {
            launch {
                binding.progressBar.show()
                val request = Request.Builder()
                    .url("https://android.gcreate.jp/")
                    .build()
                val result = withContext(Dispatchers.IO) {
                    okHttpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.string()
                        } else {
                            "failed/ code: ${response.code} / message: ${response.message}"
                        }
                    }
                }
                binding.textView.text = result
                binding.progressBar.hide()
            }
        }

        binding.getApi.setOnClickListener {
            launch {
                binding.progressBar.show()
                val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/todos/1")
                    .build()
                val result = withContext(Dispatchers.IO) {
                    okHttpClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.string()
                        } else {
                            "failed/ code: ${response.code} / message: ${response.message}"
                        }
                    }
                }
                binding.textView.text = result
                binding.progressBar.hide()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}
