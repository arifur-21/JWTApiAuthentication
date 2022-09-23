package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.lifecycleScope
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.auth.LoginScreen
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.util.TokenManager
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private val authViewModel : AuthViewModel by viewModels()


    var SECRET_KEY = "sk_test_51LcXlzJ12DsicsZnRZ4RV9IFF8l7bIuuZLW2RrE4uqsjfi3ISKBsAJikSpm0dYO0elxDOzMlw4LpQdjtDHfHkd0W00pg9gO2xr"
    var PUBLISH_KEY = "pk_test_51LcXlzJ12DsicsZnXbU6i8O2p5rUphHONbLe0aVPs5giK5gtePQdg9hUMIKfMjakItfuqQLRlbpD89r2kiiA038800X79kCOQ7"
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var customerId: String
    private lateinit var EphericalKey: String
    private lateinit var ClientSecret: String


    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.firstItem -> {
                        val intent = Intent(Intent(applicationContext, PaymentActivity::class.java))
                        startActivity(intent)
                        finish()

                    }
                    R.id.secondtItem -> {
                        logout()
                        val intent = Intent(Intent(applicationContext, LoginScreen::class.java))
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@MainActivity, "Second Item Clicked", Toast.LENGTH_SHORT).show()
                    }
                    R.id.thirdItem -> {
                        Toast.makeText(this@MainActivity, "third Item Clicked", Toast.LENGTH_SHORT).show()
                     paymentFlow()

                    }
                }
                true
            }

        }

        PaymentConfiguration.init(this, PUBLISH_KEY)
        paymentSheet = PaymentSheet(
            this
        ) { paymentSheetResult: PaymentSheetResult? ->
            if (paymentSheetResult != null) {
                onPaymentResult(paymentSheetResult)
            }
        }


        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://api.stripe.com/v1/customers",
            Response.Listener { response ->
                try {
                    val `object` = JSONObject(response)
                    customerId = `object`.getString("id")
                    Toast.makeText(applicationContext, customerId, Toast.LENGTH_LONG).show()
                    Log.e("main", customerId.toString())

                    getEmperricalKey(customerId.toString())


                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: MutableMap<String, String> = HashMap()
                header["Authorization"] = "Bearer $SECRET_KEY"
                return header
            }
        }

        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

    fun logout() = lifecycleScope.launch {
        tokenManager.logout()
        authViewModel.logout()

    }

    private fun onPaymentResult(paymentSheetResult: PaymentSheetResult) {
        when(paymentSheetResult) {
            is PaymentSheetResult.Canceled -> {
                Toast.makeText(applicationContext, "Cancel", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Failed -> {
                Toast.makeText(applicationContext, "${paymentSheetResult.error}", Toast.LENGTH_LONG).show()
            }
            is PaymentSheetResult.Completed -> {
                // Display for example, an order confirmation screen
                Toast.makeText(applicationContext, "payment success", Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun getEmperricalKey(customerId: String) {
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://api.stripe.com/v1/ephemeral_keys",
            Response.Listener { response ->
                try {
                    val `object` = JSONObject(response)
                    EphericalKey = `object`.getString("id")
                    Toast.makeText(applicationContext, EphericalKey, Toast.LENGTH_LONG).show()

                    getClientSecret(customerId, EphericalKey.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: MutableMap<String, String> = java.util.HashMap()
                header["Authorization"] = "Bearer $SECRET_KEY"
                header["Stripe-Version"] = "2020-08-27"
                return header
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["customer"] = customerId
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }


    private fun getClientSecret(customerId: String, ephericalKey: String) {
        val stringRequest: StringRequest = object : StringRequest(
            Method.POST,
            "https://api.stripe.com/v1/payment_intents",
            Response.Listener { response ->
                try {
                    val `object` = JSONObject(response)
                    ClientSecret = `object`.getString("client_secret")
                    Toast.makeText(applicationContext, ClientSecret, Toast.LENGTH_LONG).show()
                    Log.e("main", ClientSecret.toString())
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val header: MutableMap<String, String> = java.util.HashMap()
                header["Authorization"] = "Bearer $SECRET_KEY"
                return header
            }

            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String>? {
                val params: MutableMap<String, String> = java.util.HashMap()
                params["customer"] = customerId
                params["amount"] = "1000" + "00"
                params["currency"] = "usd"
                params["automatic_payment_methods[enabled]"] = "true"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(applicationContext)
        requestQueue.add(stringRequest)
    }


    private fun paymentFlow() {
        paymentSheet!!.presentWithPaymentIntent(
            ClientSecret!!, PaymentSheet.Configuration(
                "ABC Company",
                PaymentSheet.CustomerConfiguration(
                    customerId,
                    EphericalKey
                )
            )
        )
    }

}