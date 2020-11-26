package com.example.kt_lab7_1

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {
    private var rabprogress = 0
    private var turprogress = 0

    val scope = CoroutineScope(Dispatchers.Default)

    lateinit var seekBar: SeekBar
    lateinit var seekBar2: SeekBar
    lateinit var btn_start: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekBar = findViewById(R.id.seekBar)
        seekBar2 = findViewById(R.id.seekBar2)
        btn_start = findViewById(R.id.btn_start)

        rabprogress = 0;
        turprogress = 0;
        seekBar.setProgress(0);
        seekBar2.setProgress(0);
    }

    override fun onResume() {
        super.onResume()
        btn_start.setOnClickListener {
            scope.launch {

                rabprogress = 0;
                turprogress = 0;
                seekBar.setProgress(0);
                seekBar2.setProgress(0);

                runThread()
                myTask()
            }
        }
    }

    private fun runThread() {

        Thread {
            while (rabprogress <= 100 && turprogress <= 100) {
                try {
                    Thread.sleep(100)
                    rabprogress += (Math.random() * 3).toInt()
                    val msg = Message()
                    msg.what = 1
                    mHandler.sendMessage(msg)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private val mHandler = Handler { msg ->
        when (msg.what) {
            1 -> seekBar.progress = rabprogress
        }
        if (rabprogress >= 100 && turprogress < 100) {
            Toast.makeText(this@MainActivity,
                    "兔子勝利", Toast.LENGTH_SHORT).show()
            btn_start.isEnabled = true
            Thread.sleep(1000)
        }
        false
    }

    override fun onPause() {
        super.onPause()
        scope.coroutineContext.cancel()
    }

    private suspend fun myTask() {
        try {
            // 相当于onPreExecute，withContext可以在切换线程的同时进行线程同步
            withContext(Dispatchers.Main) {
                //text.text = "Start"
            }
            // 相当于doInBackground
            for (i in 1..10) {
                while (turprogress <= 100 && rabprogress < 100) {
                    try {
                        delay(100)
                        turprogress += (Math.random() * 3).toInt()
                        withContext(Dispatchers.Main) {
                            seekBar2.setProgress(turprogress);
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                // 相当于onPostExecute
                withContext(Dispatchers.Main) {
                    if (turprogress >= 100 && rabprogress < 100) {
                        Toast.makeText(this@MainActivity,
                                "烏龜勝利", Toast.LENGTH_SHORT).show()
                        btn_start.setEnabled(true)
                        Thread.sleep(1000)
                    }
                }
            }
        } catch (e: Exception) {
            // 相当于onCancelled
            Log.e(localClassName, "Cancelled", e)
        }
    }
}