package com.example.myapplication.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.myapplication.*
import com.example.myapplication.util.Helper.Companion.hideKeyboard

import com.example.myapplication.databinding.ActivitySignUpScreenBinding
import com.example.myapplication.models.UserRequest
import com.example.myapplication.util.NetworkResult
import com.example.myapplication.util.TokenManager
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.ln

@AndroidEntryPoint
class SignUpScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpScreenBinding
    private val authViewModel: AuthViewModel by viewModels()


    @Inject
    lateinit var tokenManager: TokenManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.singUpTextViewId.setOnClickListener {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
        }

        binding.signUpBtnId.setOnClickListener {

            Validatin()
        }
        bindObservers()

    }


   private fun Validatin(){
       val fname = binding.SignUpFirstNameId.text.toString()
       val lname = binding.SignUpLastNameId.text.toString()
       val email = binding.SingUpEmailId.text.toString()
       val password = binding.SignUpPasswordId.text.toString()
       val confPass = binding.SignUpConfPasswordId.text.toString()

       if (fname.isEmpty()){
           showError(binding.SignUpFirstNameId, " enter your first name")
       }
       else if (lname.isEmpty()){
           showError(binding.SignUpLastNameId, "enter your last name")
       }
       else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
           showError(binding.SingUpEmailId,"Email is not valid")
       }
       else if (password.isEmpty() || password.length <6){
           showError(binding.SignUpPasswordId,"Password must be 6 character")
       }
       else if (confPass.isEmpty() || !confPass.equals(password) ){
           showError(binding.SignUpConfPasswordId," password not match")
       }else{
           val userRequest = getUserRequest()
           authViewModel.registerUser(userRequest)
           Toast.makeText(applicationContext, "Register successfull", Toast.LENGTH_LONG).show()
       }

    }

    private fun showError(error: TextInputEditText, s: String) {
        error.setError(s)
        error.requestFocus()
    }

    private fun getUserRequest(): UserRequest {
        return binding.run {
           UserRequest(
               SingUpEmailId.text.toString(),
               SignUpFirstNameId.text.toString(),
               SignUpLastNameId.text.toString(),
               SignUpPasswordId.text.toString()
           )
        }
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(this, Observer {
           // binding.progressBar.isVisible = false
            when (it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    val intent = Intent(this, MainActivity2::class.java)
                    startActivity(intent)
                }
                is NetworkResult.Error -> {

                }
                is NetworkResult.Loading ->{
                 //   binding.progressBar.isVisible = true
                }
            }
        })
    }




  }