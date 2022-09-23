package com.example.myapplication.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CartActivity
import com.example.myapplication.ResultRespons
import com.example.myapplication.databinding.PaymentLayoutBinding

class PaymentAdapter(var context: Context):  ListAdapter<ResultRespons, PaymentAdapter.PaymentViewHolder>(differ()){


    inner class PaymentViewHolder(private val binding: PaymentLayoutBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(payment: ResultRespons){
                    binding.paymentLatyoutTitleId.text = payment.name
                    binding.paymentLayoutPriceId.text = payment.price
                    binding.paymentLayoutIntervelId.text = payment.interval

                    binding.paymentLayoutBtnId.setOnClickListener {
                        val intent = Intent(Intent(context, CartActivity::class.java))
                        intent.putExtra("paymentid", payment.id.toString())
                        intent.putExtra("price", payment.price)
                        context.startActivity(intent)
                        Toast.makeText(context, "adapter", Toast.LENGTH_LONG).show()
                    }

                }

            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
    val binding = PaymentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PaymentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payments = getItem(position)
        payments?.let {
            holder.bind(it)

        }



    }

    class differ : DiffUtil.ItemCallback<ResultRespons>(){
        override fun areItemsTheSame(oldItem: ResultRespons, newItem: ResultRespons): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ResultRespons, newItem: ResultRespons): Boolean {
            return oldItem == newItem
        }

    }
}