package com.example.hunbbing

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
class AddItemActivity : AppCompatActivity() {

    private lateinit var selectedImg: ImageView
    private var selectedImgUri: Uri? = null // 사용자가 선택한 이미지

    private lateinit var item: EditText
    private lateinit var price: EditText
    private lateinit var explain: EditText
    private lateinit var tag: EditText

    private lateinit var addImgbtn: Button
    private lateinit var plusbtn: Button

    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_add)

        selectedImg = findViewById(R.id.iv_selected_image)
        item = findViewById(R.id.itemText)
        price = findViewById(R.id.priceText)
        explain = findViewById(R.id.explainText)
        tag = findViewById(R.id.tagText)

        addImgbtn = findViewById(R.id.addImgBtn)
        plusbtn = findViewById(R.id.plusbtn)

        databaseReference = FirebaseDatabase.getInstance().getReference("addItems")

        findViewById<ImageView>(R.id.iv_back).setOnClickListener {
            finish()
        }

        addImgbtn.setOnClickListener {
            openGalleryForImage()
        }

        plusbtn.setOnClickListener {
            if (selectedImgUri != null) {
                // 이미지를 선택한 후 plusbtn 클릭 시에만 등록
                uploadImageToFirebaseStorage()
            } else {
                showError("이미지를 선택해주세요")
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImgUri = data?.data
            selectedImg.setImageURI(selectedImgUri)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        selectedImgUri?.let { uri ->
            val storageRef = FirebaseStorage.getInstance().getReference("images/${UUID.randomUUID()}")
            storageRef.putFile(uri).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    saveItemData(imageUrl)
                }
            }.addOnFailureListener {
                showError("이미지 업로드 실패: ${it.message}")
            }
        }
    }

    private fun saveItemData(imageUrl: String) {
        val sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val userName = sharedPref.getString("UserName", "알 수 없음")
        val itemName = item.text.toString().trim()
        val itemPrice = price.text.toString().trim().toIntOrNull()
        val itemDescription = explain.text.toString().trim()
        val itemTags = tag.text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val newItem = hashMapOf(
            "state" to "판매중",
            "userName" to userName,
            "name" to itemName,
            "price" to itemPrice,
            "description" to itemDescription,
            "tags" to itemTags,
            "userId" to userId,
            "imageUrl" to imageUrl
        )

        val itemId = databaseReference.push().key ?: return
        databaseReference.child(itemId).setValue(newItem).addOnSuccessListener {
            val intent = Intent(this, SellListActivity::class.java)
            intent.putExtra("ITEM_ID", itemId) // 아이템 ID 전달
            startActivity(intent)
            Toast.makeText(this, "상품이 등록되었습니다.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            showError("상품 등록에 실패했습니다.")
        }

    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
