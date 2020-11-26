package com.nikoladrljaca.intervaltimer


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.nikoladrljaca.intervaltimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var timeLeft = 0
    private var sets = 0
    private var rest = 0

    private var isSavedTimerActivityTrue = false

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCustomToolbar()
        binding.btnStartTimer.setOnClickListener {

            val setsCheck = binding.clockLayout.etSets.text.toString().isEmpty()
            val restCheck = binding.clockLayout.etRest.text.toString().isEmpty()
            val workCheck = binding.clockLayout.etWork.text.toString().isEmpty()

            if (setsCheck || restCheck || workCheck) {
                displayToast(getString(R.string.field_empty))
            } else {
                timeLeft = binding.clockLayout.etWork.text.toString().toInt()
                sets = binding.clockLayout.etSets.text.toString().toInt()
                rest = binding.clockLayout.etRest.text.toString().toInt()

                if (timeLeft == 0) {
                    displayToast(getString(R.string.enter_work_timer))
                } else {
                    val intent = Intent(this, TimerActivity::class.java)
                    intent.putExtra(WORK_KEY, timeLeft)
                    intent.putExtra(SETS_KEY, sets)
                    intent.putExtra(REST_KEY, rest)
                    startActivity(intent)
                }
            }
        }

        binding.btnSaveTimer.setOnClickListener {
            val setsCheck = binding.clockLayout.etSets.text.toString().isEmpty()
            val restCheck = binding.clockLayout.etRest.text.toString().isEmpty()
            val workCheck = binding.clockLayout.etWork.text.toString().isEmpty()

            if (setsCheck || restCheck || workCheck) {
                displayToast(getString(R.string.field_empty))
            } else {
                val timerWork = binding.clockLayout.etWork.text.toString().toInt()
                val timerSets = binding.clockLayout.etSets.text.toString().toInt()
                val timerRest = binding.clockLayout.etRest.text.toString().toInt()

                if (timerWork == 0) {
                    displayToast(getString(R.string.enter_work_timer))
                } else {
                    showSaveTimerDialog(timerSets, timerWork, timerRest)
                }
            }
        }

        binding.fabSavedTimers.setOnClickListener {
            val savedTimersIntent = Intent(this, SavedTimersActivity::class.java)
            startActivity(savedTimersIntent)
            finish()
        }

        isSavedTimerActivityTrue = intent.getBooleanExtra(SavedTimersActivity.IS_DATA_SENT,
                                            false)

        if (isSavedTimerActivityTrue) {
            grabIntentData()
        }
    }

    fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.about_item) showInfoDialog()
        return true
    }

    private fun showInfoDialog() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

    private fun grabIntentData() {
        val intentWork = intent.getIntExtra(SavedTimersActivity.WORK_KEY_SAVED, 0)
        val intentSets = intent.getIntExtra(SavedTimersActivity.SETS_KEY_SAVED, 0)
        val intentRest = intent.getIntExtra(SavedTimersActivity.REST_KEY_SAVED, 0)

        binding.clockLayout.etSets.setText(intentSets.toString(), TextView.BufferType.EDITABLE)
        binding.clockLayout.etWork.setText(intentWork.toString(), TextView.BufferType.EDITABLE)
        binding.clockLayout.etRest.setText(intentRest.toString(), TextView.BufferType.EDITABLE)

    }

    private fun showSaveTimerDialog(sets: Int, work: Int, rest: Int) {
        val editText = EditText(this)

        val builder = AlertDialog.Builder(this)
        builder.setView(editText)
        builder.setTitle(getString(R.string.dialog_timer_name))
        builder.setMessage(getString(R.string.dialog_timer_desc))

        builder.setPositiveButton(getString(R.string.save)) {
            dialogInterface, _ ->

                if (editText.text.toString().isEmpty()){
                    displayToastLong(getString(R.string.dialog_warning))
                } else {
                    val name = editText.text.toString()

                    val shared = PreferenceManager.getDefaultSharedPreferences(this).edit()
                    val gSon = Gson()
                    val timer = Timer(sets, work, rest, name)
                    val jsonOfTimer = gSon.toJson(timer)
                    shared.putString(timer.key, jsonOfTimer)
                    shared.apply()
                    //displayToast(getString(R.string.save_success))
                    displaySnackbarLong(getString(R.string.save_success))

                    dialogInterface.dismiss()
                }
        }

        builder.setNegativeButton(getString(R.string.dialog_cancel)){
            dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        builder.create().show()
    }


    private fun setupCustomToolbar() {
        binding.toolbar.setTitleTextAppearance(this, R.style.ToolbarTextAppearance)
        setSupportActionBar(binding.toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)
            window.navigationBarColor = ContextCompat.getColor(this, R.color.primaryColor)
            
        }
    }

    private fun displayToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun displayToastLong(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun displaySnackbarLong(message: String) {
        Snackbar.make(binding.btnSaveTimer, message, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.fabSavedTimers)
            .show()
    }


    companion object {
        const val WORK_KEY = "work"
        const val SETS_KEY = "sets"
        const val REST_KEY = "rest"
    }
}