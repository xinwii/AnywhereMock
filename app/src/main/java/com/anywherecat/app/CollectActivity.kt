package com.anywherecat.app

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.anywherecat.app.adapter.CollectAdapter
import com.anywherecat.app.db.CollectDao
import com.anywherecat.app.utils.RecycleViewDivider

/**
 * @author xinwii
 * 收藏
 */
class CollectActivity : AppCompatActivity() {
    private val recyclerView:RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv_collect)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)
        var dao = CollectDao(this)
        var list = dao.findAll()
        var adapter = CollectAdapter(this, list)
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL))
        recyclerView.adapter = adapter
    }
}
