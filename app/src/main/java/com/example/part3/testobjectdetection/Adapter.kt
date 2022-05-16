package com.example.part3.testobjectdetection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener

class Adapter(val mButtonClickListener : (String) -> Unit) : ListAdapter<detectedItem,Adapter.ViewHolder>(diffUtil){

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item : detectedItem) {
            val itemTextView = itemView.findViewById<TextView>(R.id.itemTextView)
            itemTextView.text = item.name

            itemView.findViewById<Button>(R.id.getImageButton).setOnClickListener {
                mButtonClickListener(itemTextView.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: Adapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<detectedItem>() {
            override fun areItemsTheSame(oldItem: detectedItem, newItem: detectedItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: detectedItem, newItem: detectedItem): Boolean {
                return  oldItem.name == newItem.name
            }
        }
    }
}


