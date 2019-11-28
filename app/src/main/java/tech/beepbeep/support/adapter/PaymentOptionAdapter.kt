package tech.beepbeep.support.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tech.beepbeep.beep_support.payment.PaymentOptions
import tech.beepbeep.support.App
import tech.beepbeep.support.databinding.BeepPaymentItemLayoutBinding

/**
 *desc:
 *Author: roy
 *Date:2019-11-25
 */
class PaymentOptionAdapter : RecyclerView.Adapter<PaymentOptionAdapter.MyViewHolder>() {

    private val paymentOptions: List<PaymentOptions> = App.getPaymentOptions()!!
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = BeepPaymentItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount() = paymentOptions.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.options = paymentOptions[position]
        holder.itemView.setOnClickListener {
            listener?.onItemClick(it, paymentOptions[position])
        }
    }

    class MyViewHolder(var binding: BeepPaymentItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, value: PaymentOptions)
    }
}