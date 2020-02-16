package jp.gcreate.sample.networksample

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.gcreate.sample.networksample.databinding.FragmentBackgroundWorkBinding
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread

class BackgroundWorkFragment : Fragment(), CoroutineScope by MainScope() {
    private lateinit var binding: FragmentBackgroundWorkBinding
    private val okHttpClient = OkHttpClient.Builder().build()
    private val request = Request.Builder()
        .url("https://jsonplaceholder.typicode.com/todos/1")
        .build()
    private val handler = Handler()

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_background_work, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAsyncTask.setOnClickListener {
            binding.textView.text = "work with AsyncTask"
            // DO NOT USE THIS WAY.
            object : AsyncTask<Unit, Unit, String>() {
                override fun doInBackground(vararg params: Unit?): String {
                    val result = callApi()
                    return "with AsyncTask: $result"
                }

                override fun onPostExecute(result: String?) {
                    binding.textView.text = result
                }
            }.execute()
        }
        binding.buttonThread.setOnClickListener {
            binding.textView.text = "work with Thread"
            thread {
                val result = callApi()
                handler.post {
                    binding.textView.text = "with Thread: $result"
                }
            }
        }
        binding.buttonRxjava.setOnClickListener {
            Single.fromCallable { callApi() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    binding.textView.text = "work with RxJava"
                }
                .subscribe({
                    binding.textView.text = "with RxJava: $it"
                }, {
                    binding.textView.text = "onError RxJava: $it"
                })
        }
        binding.buttonCoroutine.setOnClickListener {
            launch {
                binding.textView.text = "work with Coroutine"
                val result = withContext(Dispatchers.IO) { callApi() }
                binding.textView.text = "with Coroutine: $result"
            }
        }
    }

    private fun callApi(): String {
        return okHttpClient.newCall(request).execute().use { result ->
            if (result.isSuccessful) {
                result.body!!.string()
            } else {
                "error: ${result.code}/${result.message}"
            }
        }
    }
}