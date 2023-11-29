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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().reference
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
                // 사용자 ID를 가져옵니다.
                val userId = auth.currentUser?.uid


                userId?.let { uid ->
                    database.child("user").child(uid)
                        .addListenerForSingleValueEvent(/* listener = */ object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                val user = snapshot.getValue(User::class.java)
                                user?.let {

                                    saveUserToPreferences(it.name, it.email, uid, it.birth)
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {

                                Log.e("LoginActivity", "데이터 가져오기 실패: ${databaseError.message}")
                            }
                        })

                    val intent = Intent(this, SellListActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                } ?: run {

                    Toast.makeText(this, "오류 발생: 사용자 ID를 찾을 수 없음", Toast.LENGTH_SHORT).show()
                }
            } else {

                Toast.makeText(this, "로그인 실패: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
