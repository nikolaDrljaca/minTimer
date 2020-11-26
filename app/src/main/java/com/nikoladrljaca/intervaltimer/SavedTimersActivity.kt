package com.nikoladrljaca.intervaltimer

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nikoladrljaca.intervaltimer.databinding.ActivitySavedTimersBinding

class SavedTimersActivity : AppCompatActivity(), SavedTimersAdapter.ListClickListener {
    private lateinit var binding: ActivitySavedTimersBinding

    private val listOfTimers: ArrayList<Timer> = arrayListOf()
    private lateinit var timer: Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedTimersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //grab data needed from sharedPref and populate list
        grabDataForList()
        setupCustomToolbar()
        setupRecyclerView()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun listItemClicked(timer: Timer) {
        val intent = Intent(this, MainActivity::class.java)
        val isDataSent = true
        intent.putExtra(WORK_KEY_SAVED, timer.work)
        intent.putExtra(SETS_KEY_SAVED, timer.sets)
        intent.putExtra(REST_KEY_SAVED, timer.rest)
        intent.putExtra(IS_DATA_SENT, isDataSent)
        startActivity(intent)
        finish()
    }

    private fun grabDataForList() {
        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        val content = shared.all

        if (content.isNotEmpty()){
            val gSon = Gson()
            val contentList = content.toList()
            for (i in contentList.indices) {
                val stringForObject = contentList[i].second.toString()
                val newTimer = gSon.fromJson(stringForObject, Timer::class.java)
                listOfTimers.add(newTimer)
            }
        }
    }

    private fun setupCustomToolbar() {
        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance)
        binding.toolbar.title = getString(R.string.saved_timer_activity_title)
        setSupportActionBar(binding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
        }
    }

    private fun setupRecyclerView() {
        binding.rvSavedTimersList.layoutManager = LinearLayoutManager(this)
        binding.rvSavedTimersList.adapter = SavedTimersAdapter(listOfTimers, this, this)

        if (listOfTimers.isEmpty()) {
            binding.rvSavedTimersList.visibility = View.GONE
        } else {
            binding.tvNothingToShow.visibility = View.GONE
        }
    }

    companion object {
        const val WORK_KEY_SAVED = "workSaved"
        const val REST_KEY_SAVED = "restSaved"
        const val SETS_KEY_SAVED = "setsSaved"
        const val IS_DATA_SENT = "dataSaved"
    }
}