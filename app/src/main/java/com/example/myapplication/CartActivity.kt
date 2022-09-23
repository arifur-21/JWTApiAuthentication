package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.databinding.ActivityCardBinding
import com.example.myapplication.databinding.ActivityCartBinding
import com.example.myapplication.stripe_payment.CardActivity
import com.example.myapplication.util.TokenManager
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = tokenManager.getToken()
        Log.e("tag", "token ${token}")

        var price = intent.getStringExtra("price")
        Log.e("price", "Price ${price}")

        var paymentid = intent.getStringExtra("paymentid")
        Log.e("id", "id ${paymentid.toString()}")
        binding.cardPricId.text = price

        binding.cartBtnId.setOnClickListener {
            Toast.makeText(this, "cart view", Toast.LENGTH_LONG).show()
      val intent = Intent(Intent(this, CheckoutActivityJava::class.java))
            intent.putExtra("token", token)
            intent.putExtra("paymentid", paymentid.toString())
            startActivity(intent)
        }

    }
}