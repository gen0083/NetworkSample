package jp.gcreate.sample.networksample

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
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
    private val compositeDisposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        compositeDisposable.dispose()
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
            val d = Single.fromCallable { callApi() }
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
            compositeDisposable.add(d)
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