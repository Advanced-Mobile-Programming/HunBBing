package com.example.hunbbing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.hunbbing.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val databaseUrl = "https://hunbbing-default-rtdb.asia-southeast1.firebasedatabase.app/"
        database = Firebase.database.reference
        auth = Firebase.auth

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val name = binding.signupName.text.toString()
            val birth = binding.signupBirth.text.toString()


            if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty() && birth.isNotEmpty()) {
                signUp(email, password, name, birth)
            } else {
                Toast.makeText(this, "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    private fun signUp(email: String, password: String, name: String, birth: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                val userId=auth.currentUser?.uid!!
                addUserToDatabase(name, email, password ,userId, birth)
                Log.d("SignUpActivity", "회원가입 성공: $userId")
            } else {
                Log.d("SignUpActivity",it.exception.toString())
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addUserToDatabase(name: String, email: String, password: String, userId: String, birth: String) {
        val user = User(name, email, userId, password, birth)
        database.child("user").child(userId).setValue(user).addOnSuccessListener {
            Log.d("SignUpActivity", "데이터베이스에 사용자 추가 성공: $userId")
        }.addOnFailureListener { exception ->
            Log.e("SignUpActivity", "데이터베이스에 사용자 추가 실패", exception)
        }
        saveUserToPreferences(name, email, userId, birth)
    }

    private fun saveUserToPreferences(name: String, email: String, userId: String, birth: String) {
        // SharedPreferences 인스턴스를 가져옵니다.
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 사용자 정보를 SharedPreferences에 저장합니다.
        editor.putString("UserName", name)
        editor.putString("UserEmail", email)
        editor.putString("UserId", userId)
        editor.putString("UserBirth", birth)

        // 변경사항을 적용합니다.
        editor.apply()

        // 로그를 통해 저장이 성공했는지 확인합니다.
        Log.d("SignUpActivity", "SharedPreferences에 사용자 정보 저장 성공: $userId")
    }


}