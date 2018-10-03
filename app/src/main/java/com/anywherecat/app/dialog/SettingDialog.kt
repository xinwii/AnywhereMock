package com.anywherecat.app.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.anywherecat.app.R

/**
 * @author xinwii
 * 设置
 */
class SettingDialog(context: Context, themeResId: Int) : Dialog(context, themeResId) {
    open class Builder(var context: Context){
        private var mock = false
        private var location = false
        fun create(): SettingDialog {
            val settingDialog = SettingDialog(context, R.style.AppTheme)
            settingDialog.setContentView(R.layout.dialog_setting)
            val mockAppTv = settingDialog.findViewById<View>(R.id.mn_layout)
            val closeLocation = settingDialog.findViewById<View>(R.id.wz_layout)
            val mockFalseTv = settingDialog.findViewById<TextView>(R.id.mn_setting)
            val mockTrueTv = settingDialog.findViewById<TextView>(R.id.mn_trueView)
            val locationFalseTv = settingDialog.findViewById<TextView>(R.id.wz_setting)
            val locationTrueTv = settingDialog.findViewById<TextView>(R.id.wz_trueView)
            if(mock) {
                mockTrueTv.visibility = View.VISIBLE
                mockFalseTv.visibility = View.GONE
            }
            if(location){
                locationTrueTv.visibility = View.VISIBLE
                locationFalseTv.visibility = View.GONE
            }
            mockAppTv.setOnClickListener {   context.startActivity(Intent("android.settings.APPLICATION_DEVELOPMENT_SETTINGS"))
}
            closeLocation.setOnClickListener {  (context as Activity).startActivityForResult(Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 0)}
            settingDialog.window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            return settingDialog
        }
        fun setMock(mock:Boolean):Builder{
            this.mock  = mock
            return this
        }
        fun setLocation(location:Boolean):Builder{
            this.location = location
            return this
        }
    }
}