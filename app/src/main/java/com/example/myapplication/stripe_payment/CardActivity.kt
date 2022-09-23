package com.example.myapplication.stripe_payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.myapplication.databinding.ActivityCardBinding
import com.example.myapplication.util.TokenManager
import com.google.gson.GsonBuilder
import com.stripe.android.ApiResultCallback

import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.model.StripeIntent
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import com.stripe.android.payments.paymentlauncher.PaymentResult
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCardBinding

    private lateinit var paymentIntentClientSecret: String
    private lateinit var stripe: Stripe
    private val httpClient = OkHttpClient()
    private lateinit var publishableKey: String
     val baseUrl = "http://3.80.40.156:8000/"


    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        publishableKey = "pk_test_51Lc2bHH57MyAphUO4vTUHpSPhfVz6v1gUIdx4u9gJaUgSLCtkQx17CutH3babgjM3iTl4zBOgX8eIUhvLET6jmpM00O3ia1yyB"

        PaymentConfiguration.init(applicationContext, publishableKey )

        stripe = Stripe(this, PaymentConfiguration.getInstance(applicationContext).publishableKey)

       startCheckout()


    }



    private fun displayAlert(
        title: String,
        message: String
    ){
        runOnUiThread{
            val builder = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)

            builder.setPositiveButton("Ok", null)
            builder.create().show()
        }
    }

    private fun startCheckout() {
      createPaymentIntent("card","usd", completion = {
            paymentIntentClientSecret, error ->
            run {
                paymentIntentClientSecret?.let {
                    this.paymentIntentClientSecret = it
                }
                error?.let {
                    displayAlert("Failed to load PaymentIntent",
                        "Error: $error")
                }
                Log.e("main","payment errror ${error}")
            }
        })
        //confrom the paymentIntent with the card widget
        binding.payButton.setOnClickListener {
            binding.cardInputWidget.paymentMethodCreateParams?.let { params ->
                if (params != null){
                    val confirmParms = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret)
                    stripe.confirmPayment(this,confirmParms)
                }
                else{
                    displayAlert("somethis wrong", "error ${params}")
                }


            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        stripe.onPaymentResult(resultCode, data, object : ApiResultCallback<PaymentIntentResult>{
            override fun onError(e: Exception) {
                displayAlert("error", e.message.toString())
            }

            override fun onSuccess(result: PaymentIntentResult) {
                val paymentIntent = result.intent
                if (paymentIntent.status == StripeIntent.Status.Succeeded){
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    displayAlert("payment successed", gson.toJson(paymentIntent))
                }else if (paymentIntent.status == StripeIntent.Status.RequiresPaymentMethod){
                    displayAlert("Payment failed", paymentIntent.lastPaymentError?.message.orEmpty())
                }
            }
        })
    }


    fun createPaymentIntent(
        paymentMethodType: String,
        currency: String,
        completion: (paymentIntentClientSecret: String?, error: String?)-> Unit
    ){
        Log.d("key", "hello")
        // Create a PaymentIntent by calling the server's endpoint.
      
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestJson = """
            {
           "currency":"$currency"
            "stripe_payment_method_id": "$paymentMethodType",
            "price_id": "",
            "coupon_code": ""
            }
        """.trimIndent()

        val body = requestJson.toRequestBody(mediaType)
        val token = tokenManager.getToken()
        Log.e("tag", "token ${token}")

        val request = Request.Builder()
            .url(baseUrl + "payment/api/v1/create-stripe-payment-method/")
            .post(body)
            .addHeader("Authorization", "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjozMCwidXNlcm5hbWUiOiJzdDByaW5nIiwiZXhwIjoxNjYyOTEzMDU3LCJlbWFpbCI6InU1NXNlckBleGFtcGxlLmNvbSJ9.vg5US4LyvW4zFBf7N1EAgtEXt9mBDxOcTKWGTD6zpMM")
            .build()


        httpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                completion(null,"$e")

            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful){
                    completion(null, "$response")
                }else{
                    val responseData = response.body?.string()
                    val responsJson = responseData?.let { JSONObject(it) } ?: JSONObject()
                    var paymentIntentClientSecret : String = responsJson.getString("clientSecret")
                    completion(paymentIntentClientSecret, null)
                }
            }

        })

    }

}