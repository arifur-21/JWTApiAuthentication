package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.PaymentAdapter
import com.example.myapplication.databinding.ActivityPaymentBinding
import com.example.myapplication.util.ApiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var list = ArrayList<ResultRespons>()

    private val postViewModel: PostViewModel by viewModels()
    private lateinit var adapter: PaymentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)


        adapter = PaymentAdapter(this)

        binding.paymentRecycleViewId.layoutManager = LinearLayoutManager(this)

        binding.paymentRecycleViewId.adapter = adapter


        postViewModel.getPost()
        lifecycleScope.launchWhenStarted {
            postViewModel._postStateFlow.collect {
                when(it){
                    is ApiState.Loading->{

                    }
                    is ApiState.Failure->{

                        Log.e("tag", "error :${it.msg}")
                    }
                    is ApiState.Success->{

                        adapter.submitList(it.data.body()!!.results)

                        val listRespons = it.data.body()!!.results
                        list.addAll(listRespons)

                        Log.e("tag", "one data :${   list.get(0).name}")
                        Log.e("tag", "all data :${listRespons}")
                        Log.e("tag", "data respons :${listRespons.get(0).id}")
                    }
                    is ApiState.Empty->{

                    }
                }
            }
        }

    }
}