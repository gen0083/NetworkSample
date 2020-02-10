package jp.gcreate.sample.networksample

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import jp.gcreate.sample.networksample.databinding.FragmentNetworkAccessBinding
import kotlinx.coroutines.*
import okhttp3.*
import java.io.IOException

class NetworkAccessFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var binding: FragmentNetworkAccessBinding
    private lateinit var handler: Handler
    private val okHttpClient = OkHttpClient.Builder().build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_network_access, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        handler = Handler()

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
            binding.progressBar.show()
            val request = Request.Builder()
                .url("https://jsonplaceholder.typicode.com/todos/1")
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    handler.post {
                        binding.textView.text = "error: $e"
                        binding.progressBar.hide()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = if (response.isSuccessful) {
                        response.body?.string()
                    } else {
                        "failed/ code: ${response.code} / message: ${response.message}"
                    }
                    handler.post {
                        binding.textView.text = result
                        binding.progressBar.hide()
                    }
                }

            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}