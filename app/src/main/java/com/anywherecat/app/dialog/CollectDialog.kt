package com.anywherecat.app.dialog

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.anywherecat.app.db.CollectDao
import com.anywherecat.app.R
/**
 * @author xinwii
 * 收藏dialog，目前没用
 */
class CollectDialog (context: Context, themeResId: Int): Dialog(context, themeResId) {
    open class Builder(var context: Context){
        private var address:String=""
        private var latitude: Double = 0.0
        private var longitude: Double = 0.0
        fun create(): CollectDialog {
            val collectDialog = CollectDialog(context, R.style.AppTheme)
            collectDialog.setContentView(R.layout.dialog_collect)
            val noteEt = collectDialog.findViewById<EditText>(R.id.et_note)
            val confirmBtn = collectDialog.findViewById<Button>(R.id.btn_confirm)
            val cancelBtn = collectDialog.findViewById<Button>(R.id.btn_cancel)
            val addressTv = collectDialog.findViewById<TextView>(R.id.tv_address)
            addressTv.text = address
            var dao = CollectDao(context)
            confirmBtn.setOnClickListener {
                dao.insert(noteEt.text.toString().trim(),address,latitude,longitude)
                collectDialog.cancel()
            }
            cancelBtn.setOnClickListener { collectDialog.cancel()}
            collectDialog.window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            return collectDialog
        }
        fun setAddress(address:String):Builder{
            this.address = address
            return this
        }
        fun setLatitude(latitude: Double):Builder{
            this.latitude = latitude
            return this
        }
        fun setLongitude(longitude: Double):Builder{
            this.longitude = longitude
            return this
        }
    }
}