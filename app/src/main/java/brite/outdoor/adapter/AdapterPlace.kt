package brite.outdoor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import brite.outdoor.R

class AdapterPlace(var list: ArrayList<String>, var context: Context) :
    RecyclerView.Adapter<AdapterPlace.ViewHolder>() {

    private var mContext: Context? = null
    private var mArrayList: ArrayList<String>? = null

    init {
        mContext = context
        mArrayList = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPlace.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binData(position)
    }
    override fun getItemCount(): Int {
        return if (mArrayList == null) 0 else mArrayList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvContentPlace: TextView? = null

        init {
            tvContentPlace = itemView.findViewById(R.id.tvContentPlace)

        }
        fun binData(position: Int){
            tvContentPlace?.text = mArrayList?.get(position)
        }
    }
    fun update(list: ArrayList<String>?){
        list?.apply {
            mArrayList?.clear()
            mArrayList?.addAll(list)
            notifyDataSetChanged()
        }
    }
}