package com.mirea.veremeev.l.m.practice7

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.IOException
import java.net.Socket


class MainActivity : AppCompatActivity() {
    val uiScope = CoroutineScope(Dispatchers.IO)
    var currentTime : String = ""

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var mTextView: TextView
    private val host = "time-a.nist.gov" // или time-a.nist.gov or time-b.nist.gov
    private val port = 13

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mTextView = findViewById(R.id.timeView)
    }

    //ASYNC DEPRECATED BETTER TO USE COROUTINES

    private suspend fun getTime() : String {
        var timeResult = ""
        try {
            val socket = Socket(host, port)
            val socketUtils = SocketUtils()
            val reader: BufferedReader = socketUtils.getReader(socket)
            reader.readLine()
            timeResult = reader.readLine()
            Log.d(TAG, timeResult)
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return timeResult
    }

    fun setTime() = CoroutineScope(Dispatchers.Main).launch {
        val task = uiScope.launch {
            currentTime = getTime()
        }
        task.join()
        mTextView.text = currentTime
    }
    fun onClick(view: View?) {0
        val coroutine = uiScope.launch {
            setTime()
        }

    }
}