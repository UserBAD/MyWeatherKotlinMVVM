package com.example.myweatherkotlinmvvm.framework.ui.threads

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.example.myweatherkotlinmvvm.R
import com.example.myweatherkotlinmvvm.databinding.FragmentThreadsBinding
import com.example.myweatherkotlinmvvm.services.BoundService
import com.example.myweatherkotlinmvvm.services.MyForegroundService
import kotlinx.coroutines.*

import java.util.*
import java.util.concurrent.TimeUnit

class ThreadsFragment : Fragment(), CoroutineScope by MainScope() {
    private var _binding: FragmentThreadsBinding? = null
    private val binding get() = _binding!!

    private var isBound = false
    private var boundService: BoundService.ServiceBinder? = null

    private val testReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(
                "SERVICE", "FROM SERVICE:" +
                        " ${
                            intent?.getBooleanExtra(
                                MyForegroundService.INTENT_SERVICE_DATA,
                                false
                            )
                        }"
            )
        }
    }

    private val boundServiceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            boundService = service as BoundService.ServiceBinder
            isBound = boundService != null
            Log.i("SERVICE", "BOUND SERVICE")
            Log.i("SERVICE", "next fibonacci: ${service.nextFibonacci}")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            boundService = null
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isBound) {
            val bindServiceIntent = Intent(context, BoundService::class.java)
            activity?.bindService(
                bindServiceIntent,
                boundServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
        activity?.registerReceiver(
            testReceiver,
            IntentFilter(MyForegroundService.INTENT_ACTION_KEY)
        )
    }

    override fun onStop() {
        activity?.unregisterReceiver(testReceiver)
        if (isBound) {
            activity?.unbindService(boundServiceConnection)
        }
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThreadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            button.setOnClickListener {
                launch {
                    val resultJop = async(Dispatchers.Default) {
                        startCalculations(editText.text.toString().toInt())
                    }
                    textView.text = resultJop.await()
                    mainContainer.addView(AppCompatTextView(it.context).apply {
                        text = getString(R.string.in_main_thread)
                        textSize = resources.getDimension(R.dimen.main_container_text_size)
                    })
                }
            }
        }
        MyForegroundService.start(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    private fun startCalculations(seconds: Int): String {
        val date = Date()
        var diffInSec: Long

        do {
            val currentDate = Date()
            val diffInMs: Long = currentDate.time - date.time
            diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs)
        } while (diffInSec < seconds)

        return diffInSec.toString()
    }

    companion object {
        fun newInstance() = ThreadsFragment()
    }
}