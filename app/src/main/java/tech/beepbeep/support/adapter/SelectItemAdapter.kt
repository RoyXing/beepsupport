package tech.beepbeep.support.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.beepbeep.support.databinding.BeepSelectItemLayoutBinding

/**
 *desc:
 *Author: roy
 *Date:2019-11-25
 */
class SelectItemAdapter : RecyclerView.Adapter<SelectItemAdapter.MyViewHolder>() {

    private var lists = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "clear", "0", "del")
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            BeepSelectItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(
            binding
        )
    }

    override fun getItemCount() = lists.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.value = lists[position]
        holder.itemView.setOnClickListener {
            listener?.onItemClick(it, lists[position])
        }
    }

    class MyViewHolder(val binding: BeepSelectItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, value: String)
    }
}