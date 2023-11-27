package com.example.hunbbing

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.example.hunbbing.Entity.LookItem
import com.example.hunbbing.databinding.ActivityLookBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LookActivity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    private lateinit var binding: ActivityLookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // intent 값 받아오기
        val name = intent.getStringExtra("name") ?: "알 수 없음"
        val price = intent.getStringExtra("price") ?: "-1"
        val description = intent.getStringExtra("intro")
        val ownerName = intent.getStringExtra("owner") ?: "알 수 없음"
        val ownerUid = intent.getStringExtra("ownerUid") ?: ""
        var state = intent.getStringExtra("state") ?: "판매 중"
        val itemId = intent.getStringExtra("itemId") ?: ""
        val img = intent.getStringExtra("img") ?: null
        val tag = intent.getStringExtra("tag") ?: ""
        val imgUser = intent.getStringExtra("imgUser")

        // 바인딩에 값 추가
        binding.productName.setText(name)
        binding.productPrice.setText(price)
        binding.productDescription.setText(description)
        binding.productOwner.setText(ownerName)
        binding.productStateTv.setText(state)
        binding.productTag.setText(tag)

        val state_sp = findViewById<Spinner>(R.id.product_state_spinner)
        val array_pr = resources.getStringArray(R.array.product_state)
        val adapter_sp = ArrayAdapter(this, android.R.layout.simple_spinner_item, array_pr)
        adapter_sp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        state_sp.adapter = adapter_sp

        // 인텐트에서 "state" 값을 가져옵니다.
        val initialState = intent.getStringExtra("state") ?: array_pr[0] // 기본값 설정
        // ArrayAdapter에서 이 값의 인덱스를 찾습니다.
        val spinnerPosition = adapter_sp.getPosition(initialState)
        // 스피너의 선택을 해당 인덱스로 설정합니다.
        state_sp.setSelection(spinnerPosition, false)

        state_sp.onItemSelectedListener = object:AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when(array_pr[p2]){
                    "판매중" -> state = "판매중"
                    "판매 완료" -> state = "판매 완료"
                    else -> finish()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        val backButton = findViewById<ImageView>(R.id.back_btn)
        backButton.setOnClickListener {
            finish()
        }

        // 상품 이미지 적용
        Glide.with(this)
            .load(img)
            .into(binding.product)

        if(imgUser.toString() != "android.resource://com.example.hunbbing/drawable/usericon"){ // 프로필 사진 적용
            Glide.with(this)
                .load(imgUser)
                .into(binding.imgUser)
        }
        else{ // 이미지가 없으면 기본이미지 적용
            binding.imgUser.setImageDrawable(resources.getDrawable(R.drawable.chatroom_user_icon, null))
        }
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference

        val editItemBtn = findViewById<Button>(R.id.editItemBtn)
        val chatBtn = findViewById<Button>(R.id.chatBtn)
        // 사용자가 올린 상품이라면 수정 버튼 활성화
        if(uid == ownerUid) {
            chatBtn.visibility = View.GONE
            var nameet = EditText(this) // 이름 EditText
            var taget = EditText(this) // 태그 EditText
            var descriptionet = EditText(this) // 상품 내용 EditText
            var priceet = EditText(this) // 상품 가격 EditText

            var nametv = binding.productName
            var tagtv = binding.productTag
            var descriptiontv = binding.productDescription
            var pricetv = binding.productPrice
            editItemBtn.setOnClickListener {
                if(editItemBtn.text == "수정하기"){
                    // 수정하기 버튼을 수정완료 버튼으로 변경
                    editItemBtn.text = "수정완료"

                    binding.productStateTv.visibility = View.GONE // state TextView 삭제
                    binding.productStateSpinner.visibility = View.VISIBLE // spinner 보이기

                    //TextView -> EditText로 변환
                    toEditText(nametv, nameet)!! // 상품 이름
                    toEditText(tagtv, taget)!! // 상품 태그
                    toEditText(descriptiontv, descriptionet)!! // 상품 설명
                    toEditText(pricetv, priceet)!! // 상품 가격
                    descriptionet.gravity = Gravity.TOP
                    pricetv.inputType = InputType.TYPE_CLASS_NUMBER // 상품 가격 EditText는 숫자만 입력가능하도록
                    priceet.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6)) // 최대 7자 까지만 입력 가능하도록
                }
                // "수정완료" 버튼을 누르면
                else {
                    editItemBtn.text = "수정하기"

                    toTextView(nameet, nametv)!!
                    toTextView(taget, tagtv)!!
                    toTextView(descriptionet, descriptiontv)!!
                    toTextView(priceet, pricetv)!!


                    binding.productStateTv.text = binding.productStateSpinner.selectedItem.toString()
                    binding.productStateTv.visibility = View.VISIBLE // state TextView 보이기
                    binding.productStateSpinner.visibility = View.GONE // spinner 삭제


                    val curLookItem = LookItem(
                        binding.productDescription.text.toString(), img ?: "", binding.productName.text.toString(),
                        priceet.text.toString().toInt(), binding.productStateSpinner.selectedItem.toString(),
                        binding.productTag.text.toString(), ownerUid, ownerName
                    )
                    Log.i("curLookItem", curLookItem.toString())
                    // 변경된 값 데이터 베이스에 저장
                    database.child("addItems").child(itemId).setValue(curLookItem).addOnCompleteListener{
                        result ->
                        if(result.isSuccessful){
                            Log.i("LookActivity", "데이터 변경 저장 성공")
                            Snackbar.make(binding.root, "내용을 수정하였습니다.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .show()
                        }
                        else{
                            Log.e("LookActivity", "데이터 변경 저장 실패")
                            Snackbar.make(binding.root, "내용 수정에 실패하였습니다.", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null)
                                .show()
                        }
                    }
                }
            }
        }
        // 아니면 채팅 버튼 활성화
        else {
            editItemBtn.visibility = View.GONE
            chatBtn.setOnClickListener {
                // 채팅방이 없는 경우 채팅방 생성
                val intent = Intent(this, ChatActivity::class.java)
                isChatRoomExists("user/$uid/chatRooms", ownerUid) { result ->
                    if (!result) {
                        Log.i("Look", "채팅방이 존재하지 않아 채팅방을 새로 생성합니다.")
                        val chatRoomId =
                            database.child("user").child(uid).child("chatRooms").child(ownerUid)
                                .push().key.toString()
                        database.child("user").child(uid).child("chatRooms").child(ownerUid)
                            .child("chatRoomId").setValue(chatRoomId.toString())
                        database.child("user").child(ownerUid).child("chatRooms").child(uid)
                            .child("chatRoomId").setValue(chatRoomId.toString())
                        database.child("chatRooms").child(chatRoomId).child("lastChat")
                            .setValue("아직 채팅이 없습니다.")
                        val chatId =
                            database.child("chatRooms").child(chatRoomId).child("chatId").push().key
                        database.child("chatRooms").child(chatRoomId).child("chatId")
                            .setValue(chatId)
                        Log.i("Look", "채팅방 생성 성공")
                    }
                    // 넘길 데이터
                    var chatRoomId: String? = null
                    var chatId: String? = null

                    val chatRoomRef: DatabaseReference =
                        database.child("user").child(uid).child("chatRooms").child(ownerUid)
                    chatRoomRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (childSnapshot in dataSnapshot.children) {
                                chatRoomId = childSnapshot.value.toString()
                            }
                            if (chatRoomId != null) {
                                database.child("chatRooms").child(chatRoomId!!).child("chatId")
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                                            chatId = dataSnapshot.value.toString()
                                            intent.putExtra("name", ownerName)
                                            intent.putExtra("uId", ownerUid)
                                            intent.putExtra("chatId", chatId)
                                            intent.putExtra("chatRoomId", chatRoomId)

                                            // 채팅 액티비티로 전송

                                            startActivity(intent)
                                        }

                                        override fun onCancelled(databaseError: DatabaseError) {
                                            // 데이터 읽기 실패

                                        }
                                    })
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // 데이터 읽기 실패

                        }
                    })
                }
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()



    }

    // Firebase 데이터 존재 여부를 확인하는 함수
    private fun isChatRoomExists(parentPath: String, childKey: String, callback: (Boolean) -> Unit) {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("$parentPath/$childKey")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터 스냅샷이 존재하면 해당 데이터가 존재하는 것으로 간주
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                callback(false)
            }
        })
    }


    // TextView -> EditText 변환 함수
    private fun toEditText(textView : TextView?, editText: EditText?) {
        // textView를 EditText로 변경
        if(textView != null && editText != null) {
            editText.layoutParams = textView.layoutParams

            editText.setText(textView.text)
            editText.id = textView.id

            val layoutParams = editText.layoutParams
            layoutParams.height = textView.height
            Log.i("id", textView.id.toString() + " " + R.id.product_description)
            if(textView.id == R.id.product_description) layoutParams.width = textView.width
            else layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
            editText.layoutParams = layoutParams

            editText.setPadding(
                textView.paddingLeft,
                textView.paddingTop,
                textView.paddingRight ,
                textView.paddingBottom
            )
            //editText는 px단위를 사용하므로 textView의 textSize를 dp단위에서 px단위로 변환해야함
            // textSize의 반환은 dp로 변환한 단위 -> 다시 px단위로 변환해야함
            editText.textSize = pxToDp(this, textView.textSize) // 텍스트 크기
            editText.typeface = textView.typeface // bold 인지

            val normalDrawable = resources.getDrawable(R.drawable.change_background)
            val focusedDrawable = resources.getDrawable(R.drawable.change_background_focus)
            val stateListDrawable = StateListDrawable()
            val normalLayer = LayerDrawable(arrayOf(normalDrawable))
            val margin = -5.0 // dp단위
            normalLayer.setLayerInset(0, dpToPx(margin.toFloat()).toInt(), dpToPx(margin.toFloat()).toInt(), dpToPx(margin.toFloat()).toInt(), dpToPx(5.0.toFloat()).toInt())  // Left, Top, Right, Bottom 마진

            val focusedLayer = LayerDrawable(arrayOf(focusedDrawable))
            focusedLayer.setLayerInset(0, dpToPx(margin.toFloat()).toInt(), dpToPx(margin.toFloat()).toInt(), dpToPx(margin.toFloat()).toInt(), dpToPx(5.0.toFloat()).toInt())  // Left, Top, Right, Bottom 마진


//            stateListDrawable.addState(intArrayOf(android.R.attr.state_focused), focusedLayer)
            stateListDrawable.addState(intArrayOf(android.R.attr.state_focused), focusedDrawable)
//            stateListDrawable.addState(intArrayOf(), normalLayer)
            stateListDrawable.addState(intArrayOf(), normalDrawable)
            editText.background = stateListDrawable
        }
        // 부모 뷰에서 TextView를 제거하고 EditText를 추가
        val parentLayout = textView?.parent as? ViewGroup
        parentLayout?.removeView(textView)
        parentLayout?.addView(editText)

        if (textView != null) {
            textView.visibility = View.GONE
        }
        if (editText != null) {
            editText.visibility = View.VISIBLE
        }
    }

    // EditText -> TextView 변환 함수
    private fun toTextView(editText: EditText?, textView: TextView) {

        if(textView != null && editText != null) {
            textView.layoutParams = editText.layoutParams
            textView.setText(editText.text)
            textView.id = editText.id
        }

        val parentLayout = editText?.parent as? ViewGroup
        parentLayout?.removeView(editText)
        parentLayout?.addView(textView)

        if(textView != null){
            textView.visibility = View.VISIBLE
        }
        if(editText != null){
            editText.visibility = View.GONE
        }
    }
    // dp단위를 px 단위로 변경
    private fun dpToPx(dp : Float) : Float{
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        )
    }

    fun pxToDp(context: Context, px: Float): Float {
        val density = context.resources.displayMetrics.density
        return px / density
    }
}
