package com.example.hunbbing

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.content.Context
import android.os.Build
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
import android.Manifest
import com.google.firebase.Firebase
import com.google.firebase.auth.auth



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


        val backButton = findViewById<ImageView>(R.id.iv_back)
        backButton.setOnClickListener {

            finish()
        }

        item = findViewById(R.id.itemText)
        price = findViewById(R.id.priceText)
        explain = findViewById(R.id.explainText)
        tag = findViewById(R.id.tagText)

        addImgbtn = findViewById(R.id.addImgBtn)
        plusbtn = findViewById(R.id.plusbtn)


        DbR = FirebaseDatabase.getInstance().getReference("AddItems")

        plusbtn.setOnClickListener {

            /*
            if (selectedImgUri != null) {
                uploadImageToFirebaseStorage()
            } else {
                Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }*/


            addItemToDatabase()
        }

        addImgbtn.setOnClickListener {
            requestStoragePermission()
            //selectImage()
        }




    }

    //imageUrl: String , imageUrl

    private fun addItemToDatabase() {
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
        val newItem = Item(itemId, itemName, itemPrice, itemexplain, itemTags, uid, name)

        itemId?.let {
            DbR.child(it).setValue(newItem).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 데이터베이스에 추가가 성공했을 때만 새로운 Activity로 이동합니다.
                    Toast.makeText(applicationContext, "상품이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, SellListActivity::class.java))
                } else {
                    // 데이터베이스에 추가가 실패했을 때 오류 메시지를 표시합니다.
                    showError("상품 등록에 실패했습니다.")
                }
            }
        } ?: showError("상품을 등록할 수 없습니다.")
    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }





    /*
    아래 코드 죄다 카메라 권한 실패
     */
    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001;
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
                        //addItemToDatabase(imageUrl)
                        // 이제 imageUrl을 Firebase Realtime Database에 저장하면 됩니다.
                    }
                }
                .addOnFailureListener {
                    // 업로드 실패 처리
                }
        }
    }


    private fun requestStoragePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 권한이 거부되었다면 권한 요청
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                // 권한이 이미 있으면 이미지 선택
                selectImage()
            }
        } else {
            // 시스템 OS가 마시멜로우 미만일 경우
            selectImage()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한이 승인됨
                    selectImage()
                } else {
                    // 권한이 거부됨
                    Toast.makeText(this, "권한이 거부되었습니다", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }



}