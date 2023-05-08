package com.godq.test.sideslidewidget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.godq.sideslidemenuwidget.SideSlideRVItemTouchListener
import com.godq.test.R
import com.lazylite.mod.widget.BaseFragment

class SideSlideMenuFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.activity_side_slide_menu, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = MyAdapter()
        rv.addOnItemTouchListener(SideSlideRVItemTouchListener())
    }

    class MyAdapter: RecyclerView.Adapter<MyHolder>() {

        private val data = mutableListOf<String>()

        init {
            for (i in 0 .. 8) {
                data.add("item-$i")
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(LayoutInflater.from(parent.context).inflate(R.layout.side_slide_menu_item_layout, parent, false))
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.tv?.text = data[position]
            holder.data = data[position]
        }

        override fun getItemCount(): Int {
            return data.size
        }

    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var data: String? = null
        val tv: TextView? = itemView.findViewById<TextView?>(R.id.side_slide_test_tv)?.apply {
            setOnClickListener {
                Log.e("slide", "tv=$data")
            }
        }
        val menu: View? = itemView.findViewById<View?>(R.id.menu)?.apply {
            setOnClickListener {
                Log.e("slide", "menu=$data")
            }
        }
    }

    override fun getEdgeSize(): Int {
        return 1080
    }
}