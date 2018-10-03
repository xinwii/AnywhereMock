package com.anywherecat.app.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anywherecat.app.model.CollectModel
import com.anywherecat.app.MainActivity
import com.anywherecat.app.R
import com.anywherecat.app.db.CollectDao
import com.anywherecat.app.model.Constants
import com.baidu.mapapi.model.LatLng

/**
 * @author xinwii
 * 收藏
 */
class CollectAdapter: RecyclerView.Adapter<CollectAdapter.CollectViewHolder> {
    private var context: Context
    private var list:ArrayList<CollectModel>
    private var dao:CollectDao
    constructor(context: Context,list:ArrayList<CollectModel>){
        this.context = context
        this.list = list
        dao = CollectDao(context)
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CollectViewHolder {
        var holder = CollectViewHolder(LayoutInflater.from(context).inflate(R.layout.item_collect, p0, false))
        return holder
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: CollectViewHolder, position: Int) {
        holder.idTv.text = list[position].id.toString()
        holder.addrTv.text = "地址:"+list[position].address
        holder.noteTv.text = "备注:"+list[position].note
        holder.item.setOnClickListener { context.startActivity(Intent(context, MainActivity::class.java).putExtra(Constants.LATLNG,LatLng(list[position].latitude,list[position].longitude))) }
        holder.item.setOnLongClickListener{
            AlertDialog.Builder(context).setTitle("要删除这条收藏吗？").setPositiveButton("确定"){_,_->
                run {
                    dao.delete(list[position].id)
                    list.removeAt(position)
                    notifyDataSetChanged()
                }
            }.setNegativeButton("取消",null).create().show()
            return@setOnLongClickListener true
        }
    }

    inner class CollectViewHolder : RecyclerView.ViewHolder {
        var idTv:TextView
        var addrTv:TextView
        var noteTv:TextView
        var item:View
        constructor(itemView:View) : super(itemView) {
            idTv = itemView.findViewById(R.id.tv_item_id)
            addrTv = itemView.findViewById(R.id.tv_item_address)
            noteTv = itemView.findViewById(R.id.tv_item_note)
            item = itemView
        }
    }
}