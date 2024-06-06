package com.dicoding.syarifh.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.syarifh.storyapp.data.response.CreateResponse
import com.dicoding.syarifh.storyapp.databinding.ActivitySignupBinding
import com.dicoding.syarifh.storyapp.retrofit.ApiConfig
import com.dicoding.syarifh.storyapp.view.login.LoginActivity
import retrofit2.Call
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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
        binding.signupButton.setOnClickListener {

            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString()

            if (binding.edRegisterName.error == null && binding.edRegisterEmail.error == null && binding.edRegisterPassword.error == null){
                showLoading(true)
                val client = ApiConfig.getApiService(this).register(name, email, password)
                client.enqueue(object : retrofit2.Callback<CreateResponse> {

                    override fun onResponse(
                        call: Call<CreateResponse>,
                        response: Response<CreateResponse>
                    ) {
                        if (response.isSuccessful) {
                            val message = response.message()
                            if (message != null) {
                                Log.d("Pesan", "Pesan : $message")

                                AlertDialog.Builder(this@SignupActivity).apply {
                                    setTitle("Yeah!")
                                    setMessage("Akun sudah jadi nih. Yuk, login dan belajar coding.")
                                    setPositiveButton("Lanjut") { _, _ ->
                                        val intent = Intent(context, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        } else {
                            Log.d(TAG, "onFailure: ${response.message()}")
                            AlertDialog.Builder(this@SignupActivity).apply {
                                setTitle("Maaf!")
                                setMessage("Email sudah digunakan")
                                setPositiveButton("Ok") { _, _ -> }
                                create()
                                show()
                            }
                        }
                        showLoading(false)
                    }

                    override fun onFailure(call: Call<CreateResponse>, t: Throwable) {
                        Log.e(TAG, "onFailure: ${t.message}")

                    }
                })
            }
        }
        binding.textSignIn.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
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
        val tvName = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(1000)
        val edName = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val tvEmail = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(1000)
        val edEmail = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val tvPassword = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(1000)
        val edPassword = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(1000)
        val signupBtn = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(1000)


        val together = AnimatorSet().apply {
            playTogether(tvName, edName, tvEmail, tvPassword, edEmail, edPassword)
        }
        AnimatorSet().apply {
            playSequentially(together, signupBtn)
            start()
        }
    }
}