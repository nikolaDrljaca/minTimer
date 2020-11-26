package com.nikoladrljaca.intervaltimer

import android.os.Parcel
import android.os.Parcelable

data class Timer(var sets: Int, var work: Int, var rest: Int, var name: String, var key: String = "") {
    init {
        counter++
        key = "key:${sets + work + rest + counter}"
    }

    fun getStats(): String {
        return "Timer: $sets | $work | $rest"
    }

    companion object{
        var counter = 0
    }
}