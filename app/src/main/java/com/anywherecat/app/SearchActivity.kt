package com.anywherecat.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import com.anywherecat.app.adapter.SuggestAdapter
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.*
import com.anywherecat.app.utils.RecycleViewDivider

/**
 * @author xinwii
 * 搜索地址
 */
class SearchActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var mPoiSearch: PoiSearch
    private lateinit var adapter: SuggestAdapter
    private var list: ArrayList<PoiInfo> = ArrayList()
    private val recyclerView: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv_search)
    }
    private var poiListener: OnGetPoiSearchResultListener = object : OnGetPoiSearchResultListener {
        override fun onGetPoiDetailResult(p0: PoiDetailResult?) {

        }

        override fun onGetPoiResult(p0: PoiResult) {
            if (p0.allPoi != null && p0.allPoi.size != 0) {
                adapter = SuggestAdapter(this@SearchActivity, p0.allPoi as ArrayList<PoiInfo>)
                recyclerView.adapter = adapter
            }
        }

        override fun onGetPoiDetailResult(p0: PoiDetailSearchResult?) {

        }

        override fun onGetPoiIndoorResult(p0: PoiIndoorResult?) {

        }
    }
    private val searchEt: EditText by lazy {
        findViewById<EditText>(R.id.et_search)
    }
    private val cityEt: EditText by lazy {
        findViewById<EditText>(R.id.et_search_city)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mPoiSearch = PoiSearch.newInstance()
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener)
        adapter = SuggestAdapter(this, list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.HORIZONTAL))
        recyclerView.adapter = adapter
        searchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, s.toString().trim())
                mPoiSearch.searchInCity((PoiCitySearchOption())
                        .city(cityEt.text.toString().trim())
                        .keyword(s.toString().trim())
                        .pageNum(20))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mPoiSearch.destroy()
    }
}
