package jp.gcreate.sample.networksample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.gcreate.sample.networksample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.networkAccess.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NetworkAccessFragment())
                .commit()
        }
        binding.backgroundWork.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BackgroundWorkFragment())
                .commit()
        }
    }
}
