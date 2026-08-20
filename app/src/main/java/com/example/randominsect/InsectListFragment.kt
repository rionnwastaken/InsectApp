package com.example.randominsect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InsectListFragment : Fragment(R.layout.fragment_insect_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val insects = (activity as? MainActivity)?.insectList ?: emptyList()
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_insects)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = InsectAdapter(insects)
    }

    private class InsectAdapter(private val items: List<String>) :
        RecyclerView.Adapter<InsectAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tv_insect_name)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_insect, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvName.text = items[position]
        }

        override fun getItemCount(): Int = items.size
    }
}
