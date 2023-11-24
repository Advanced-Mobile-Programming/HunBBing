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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class AddItemActivity : AppCompatActivity() {

    private lateinit var selectedImg : ImageView
    private var selectedImgUri: Uri? = null //사용자가 선택한 이미지

    private lateinit var DbR : DatabaseReference

    private lateinit var item : EditText
    private lateinit var price : EditText
    private lateinit var explain : EditText
    private lateinit var tag: EditText

    private lateinit var addImgbtn: Button
    private lateinit var plusbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_add)


        DbR = FirebaseDatabase.getInstance().getReference("AddItems")

        plusbtn.setOnClickListener {
            if (selectedImgUri != null) {
                uploadImageToFirebaseStorage()
            } else {
                Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        addImgbtn.setOnClickListener {
            selectImage()
        }


        item = findViewById(R.id.itemText)
        price = findViewById(R.id.priceText)
        explain = findViewById(R.id.explainText)
        tag = findViewById(R.id.tagText)

        addImgbtn = findViewById(R.id.addImgBtn)
        plusbtn = findViewById(R.id.plusbtn)

    }


    private fun addItemToDatabase(imageUrl: String) {
        val itemName = item.text.toString()
        val itemPrice = price.text.toString().toInt()
        val itemPriceText = price.text.toString()
        val itemexplain = explain.text.toString()
        val itemTags = tag.text.toString()

        if (itemName.isEmpty() || itemPriceText.isEmpty() || itemexplain.isEmpty()  != null) {
            val itemId = DbR.push().key
            val newItem = Item(itemId, itemName, itemPrice, itemexplain, itemTags, imageUrl)

            itemId?.let {
                DbR.child(it).setValue(newItem).addOnCompleteListener {
                    Toast.makeText(applicationContext, "등록 되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        if(itemName.isEmpty()) {
            Toast.makeText(applicationContext, "상품 이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
        if(itemPriceText.isEmpty()) {
            Toast.makeText(applicationContext, "상품 가격을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
        if(itemexplain.isEmpty()) {
            Toast.makeText(applicationContext, "상품 정보을 입력해주세요.", Toast.LENGTH_SHORT).show()
        }

    }



    private fun selectImage() {
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
            val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        addItemToDatabase(imageUrl)
                        // 이제 imageUrl을 Firebase Realtime Database에 저장하면 됩니다.
                    }
                }
                .addOnFailureListener {
                    // 업로드 실패 처리
                }
        }
    }


}