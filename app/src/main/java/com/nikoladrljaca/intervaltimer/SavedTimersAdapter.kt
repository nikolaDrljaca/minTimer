package com.nikoladrljaca.intervaltimer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.saved_timers_list_item.view.*

class SavedTimersAdapter(var listOfTimers: ArrayList<Timer>, val clickListener: ListClickListener,
val context: Context):
    RecyclerView.Adapter<SavedTimersViewHolder>() {

    interface ListClickListener{
        fun listItemClicked(timer: Timer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedTimersViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.saved_timers_list_item, parent, false)
        return SavedTimersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  listOfTimers.size
    }

    override fun onBindViewHolder(holder: SavedTimersViewHolder, position: Int) {
        listOfTimers.sortBy {
            it.name
        }
        holder.itemView.tvSavedTimerName.text = listOfTimers[position].name
        holder.itemView.tvSavedTimerStats.text = listOfTimers[position].getStats()

        holder.itemView.setOnClickListener {
            clickListener.listItemClicked(listOfTimers[position])
        }

        holder.itemView.btnDeleteItem.setOnClickListener {
            removeFromList(listOfTimers[position].key)
            listOfTimers.removeAt(position)
            notifyDataSetChanged()
        }
    }

    private fun removeFromList(key: String) {
        val shared = PreferenceManager.getDefaultSharedPreferences(context).edit()
        shared.remove(key)
        shared.apply()
    }
}

class SavedTimersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

}