package com.anywherecat.app.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anywherecat.app.MainActivity
import com.anywherecat.app.R
import com.anywherecat.app.model.Constants
import com.baidu.mapapi.search.core.PoiInfo

/**
 * @author xinwii
 * 推荐地址，poi检索
 */
class SuggestAdapter(val context: Context, private val list:ArrayList<PoiInfo>) : RecyclerView.Adapter<SuggestAdapter.CollectViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CollectViewHolder {
        return CollectViewHolder(LayoutInflater.from(context).inflate(R.layout.item_suggest, p0, false))
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: CollectViewHolder, position: Int) {
        holder.suggestTv.text = list[position].name
        holder.cityTv.text = list[position].address
        holder.item.setOnClickListener { context.startActivity(Intent(context, MainActivity::class.java).putExtra(Constants.LATLNG,list[position].location)) }
    }

    inner class CollectViewHolder : RecyclerView.ViewHolder {
        var suggestTv:TextView
        var cityTv:TextView
        var item:View
        constructor(itemView:View) : super(itemView) {
            suggestTv = itemView.findViewById(R.id.tv_item_suggest)
            cityTv = itemView.findViewById(R.id.tv_item_city)
            item = itemView
        }
    }
}