package com.example.hunbbing

import org.junit.Test

class SaveUserTest {
    @Test
    fun saveUserTest(){
        val signUpActivity = SignUpActivity()
        val name = "junhaa";
        val email = "wooha34567@gmail.com"
        val birth = "2000/04/07"
        val uid = "5S2fkfWundPzzQVBZvZLVxIFzul1"

        signUpActivity.saveUser(uid, email, name, birth);
    }
}