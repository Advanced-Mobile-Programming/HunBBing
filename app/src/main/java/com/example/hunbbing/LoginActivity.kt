package com.example.hunbbing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hunbbing.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference
        // 로그인 버튼
        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "빈 칸 없이 작성해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        // 회원가입 버튼
        binding.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignUpActivity::class.java)
            startActivity(signupIntent)
        }
    }

    // 로그인 함수
    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // 로그인 성공
                val intent = Intent(this, SellListActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
            } else {
                // 로그인 실패
                Toast.makeText(this, "로그인 실패: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
