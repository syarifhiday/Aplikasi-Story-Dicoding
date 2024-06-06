package com.dicoding.syarifh.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.syarifh.storyapp.data.pref.UserModel
import com.dicoding.syarifh.storyapp.data.response.LoginResponse
import com.dicoding.syarifh.storyapp.databinding.ActivityLoginBinding
import com.dicoding.syarifh.storyapp.retrofit.ApiConfig
import com.dicoding.syarifh.storyapp.retrofit.ApiService
import com.dicoding.syarifh.storyapp.view.ViewModelFactory
import com.dicoding.syarifh.storyapp.view.main.MainActivity
import com.dicoding.syarifh.storyapp.view.signup.SignupActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {

        binding.loginButton.setOnClickListener {

            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            binding.passwordEditTextLayout.error = null

            if (binding.edLoginEmail.error == null && binding.edLoginPassword.error == null){
                showLoading(true)
                val client = ApiConfig.getApiService(this).login(email, password)
                client.enqueue(object : Callback<LoginResponse> {

                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if(response.isSuccessful){
                            val token = response.body()?.loginResult?.token
                            if(token != null){
                                Log.d("Login", "Pesan : $token")
                                viewModel.saveSession(UserModel(email, "$token"))
                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle("Yeah!")
                                    setMessage("Login berhasil, selamat belajar.")
                                    setPositiveButton("Lanjut") { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }else{
                            Log.d(ContentValues.TAG, "onFailure: ${response.message()}")
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Maaf!")
                                setMessage("Email atau password anda salah.")
                                setPositiveButton("Ok") { _, _ -> }
                                create()
                                show()
                            }
                        }
                        showLoading(false)
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                    }
                })
            }
        }
        binding.textSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val loginBtn = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(1000)


        val together = AnimatorSet().apply {
            playTogether(tvEmail, tvPassword, edEmail, edPassword)
        }
        AnimatorSet().apply {
            playSequentially(together, loginBtn)
            start()
        }
    }
}