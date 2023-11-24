package com.example.hunbbing


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EItemActivity : AppCompatActivity() {

    private lateinit var DbR : DatabaseReference

    private lateinit var item : EditText
    private lateinit var price : EditText
    private lateinit var explain : EditText
    private lateinit var tag: EditText

    //private lateinit var addImgbtn: Button
    private lateinit var plusbtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_add)

        item = findViewById(R.id.itemText)
        price = findViewById(R.id.priceText)
        explain = findViewById(R.id.explainText)
        tag = findViewById(R.id.tagText)

        //addImgbtn = findViewById(R.id.addImgBtn)
        plusbtn = findViewById(R.id.plusbtn)


        DbR = FirebaseDatabase.getInstance().getReference("AddItems")

        val backButton = findViewById<ImageView>(R.id.iv_back)
        backButton.setOnClickListener {

            finish()
        }


        //val itemId = "-Nk-mzCnIx7WLeBDNjF6" // 여기서는 예시로 "item_id"를 사용합니다.
        val itemId = intent.getStringExtra("ITEM_ID") ?: return // 기본값 처리 필요
        loadItemData(itemId)

        plusbtn.setOnClickListener {

            /*
            if (selectedImgUri != null) {
                uploadImageToFirebaseStorage()
            } else {
                Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }*/


            updateItemToDatabase()
        }

        /*
        addImgbtn.setOnClickListener {
            requestStoragePermission()
            //selectImage()
        }*/

    }

    private fun loadItemData(itemId: String) {
        val sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val name = sharedPref.getString("UserName","알 수 없음")
        val uid = Firebase.auth.currentUser?.uid ?: return showError("사용자 인증에 실패했습니다.")

        DbR.child(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터를 Item 객체로 변환
                val itemData = dataSnapshot.getValue(Item::class.java)
                itemData?.let {
                    // 현재 로그인한 사용자가 소유자인 경우에만 데이터 표시
                    if (it.userId == uid) {
                        item.setText(it.itemName)
                        price.setText(it.price.toString())
                        explain.setText(it.itemInfo)
                        tag.setText(it.tags)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "데이터 로드 실패: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }


    private fun updateItemToDatabase() {
        val sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val name = sharedPref.getString("UserName","알 수 없음")
        val uid = Firebase.auth.currentUser?.uid ?: return showError("사용자 인증에 실패했습니다.")
        val itemName = item.text.toString().trim()
        val itemPriceText = price.text.toString().trim()
        val itemexplain = explain.text.toString().trim()
        val itemTags = tag.text.toString().trim()

        // 모든 필드가 채워졌는지 검사합니다.
        if (itemName.isBlank()) {
            showError("상품 이름을 입력해주세요.")
            return
        }

        if (itemPriceText.isBlank()) {
            showError("상품 가격을 입력해주세요.")
            return
        }

        val itemPrice = itemPriceText.toIntOrNull()
        if (itemPrice == null || itemPrice <= 0) {
            showError("유효한 상품 가격을 입력해주세요.")
            return
        }

        if (itemexplain.isBlank()) {
            showError("상품 정보를 입력해주세요.")
            return
        }

        if (itemName.isEmpty() || itemPriceText.isEmpty() || itemexplain.isEmpty()) {
            // 필수 필드가 채워지지 않았으므로 오류 메시지를 표시하고 함수를 종료합니다.
            showError("모든 필드를 채워주세요.")
            return
        }

        // 모든 검사를 통과하면 데이터베이스에 아이템을 추가합니다.
        val itemId = DbR.push().key
        val updataItem = Item(itemId, itemName, itemPrice, itemexplain, itemTags, uid, name)

        itemId?.let {
            DbR.child(it).setValue(updataItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 데이터베이스에 추가가 성공했을 때만 새로운 Activity로 이동합니다.
                    Toast.makeText(applicationContext, "상품이 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SellListActivity::class.java))
                } else {
                    // 데이터베이스에 추가가 실패했을 때 오류 메시지를 표시합니다.
                    showError("상품 업데이트에 실패했습니다.")
                }
            }
        } ?: showError("상품을 업데이트 할 수 없습니다.")
    }


    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

}

