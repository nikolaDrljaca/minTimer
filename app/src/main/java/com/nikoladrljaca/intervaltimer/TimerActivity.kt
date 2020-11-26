package com.nikoladrljaca.intervaltimer

import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.nikoladrljaca.intervaltimer.databinding.ActivityTimerBinding


class TimerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimerBinding

    private lateinit var countDownTimer: CountDownTimer
    private lateinit var getReadyCountDown: CountDownTimer
    private lateinit var restCountDownTimer: CountDownTimer

    private var countDownInterval: Long = 1000

    private var sets = 0
    private var rest = 0
    private var timeLeft = 0
    private var constTimeLeft = 0

    private var isTimerRunning: Boolean = false

    private var getReadyLong = 5000

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowColors()

        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound)

        assignPassedValues()
        defineRestTimer()
        getReadyTimer()

        binding.fabStartStop.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun pauseTimer() {
        countDownTimer.cancel()
        isTimerRunning = false
        binding.fabStartStop.setImageResource(R.drawable.ic_play_arrow_filled)
    }

    private fun assignPassedValues() {
        timeLeft = intent.getIntExtra(MainActivity.WORK_KEY, 0)
        constTimeLeft = intent.getIntExtra(MainActivity.WORK_KEY, 0)
        rest = intent.getIntExtra(MainActivity.REST_KEY, 0)
        sets = intent.getIntExtra(MainActivity.SETS_KEY, 0)
    }

    private fun setupWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer((timeLeft*1000).toLong(), countDownInterval){
            override fun onFinish() {
                sets--
                binding.tvTimerSets.text = getString(R.string.remaining_sets, sets)
                if (sets > 0) restCountDownTimer.start()
                if (sets == 0 || sets < 0) {
                    displayToast(getString(R.string.good_job))
                    finish()
                }
                binding.fabStartStop.visibility = View.INVISIBLE
                timeLeft = constTimeLeft
            }
            override fun onTick(millsUntilFinished: Long) {
                binding.tvStatus.text = resources.getString(R.string.go)
                timeLeft = millsUntilFinished.toInt() / 1000
                binding.tvTimerWork.text = (millsUntilFinished.toInt() / 1000).toString()

                if (timeLeft < 3) {
                    mediaPlayer?.start()
                }
            }
        }
        countDownTimer.start()
        isTimerRunning = true
        binding.fabStartStop.visibility = View.VISIBLE
        binding.fabStartStop.setImageResource(R.drawable.ic_outline_pause_24)
    }

    private fun defineRestTimer() {
        restCountDownTimer = object : CountDownTimer((rest*1000).toLong(), countDownInterval){
            override fun onFinish() {
                timeLeft = constTimeLeft
                //countDownTimer.start()
                startTimer()
                binding.fabStartStop.visibility = View.VISIBLE
            }

            override fun onTick(p0: Long) {
                binding.tvStatus.text = getString(R.string.rest)
                rest = p0.toInt() / 1000
                binding.tvTimerWork.text = rest.toString()
            }
        }
    }

    private fun getReadyTimer() {
        getReadyCountDown = object : CountDownTimer(getReadyLong.toLong(), countDownInterval){
            override fun onFinish() {
                startTimer()
            }

            override fun onTick(p0: Long) {
                binding.tvStatus.text = resources.getString(R.string.get_ready)
                getReadyLong = p0.toInt() / 1000
                binding.tvTimerWork.text = getReadyLong.toString()
            }
        }
        binding.tvTimerSets.text = getString(R.string.remaining_sets, sets)
        getReadyCountDown.start()
        binding.fabStartStop.visibility = View.INVISIBLE
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}