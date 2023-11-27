package com.example.hunbbing

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


interface OnItemClickListener {
    fun onItemClicked(position: Int)
}
data class BoardItem(
    val img: Uri?,
    var name: String,
    val imgUser: Uri?,
    val imgLike: Uri?,
    val imgMsg: Uri?,
    var price: String,
    var intro: String,
    var tag: String,
    val owner: String,
    val msgState: Boolean,
    val message: Int,
    val like: Int,
    val likeState: Boolean,
    var state: String,
    var ownerUid: String,
    val itemId: String
)


class BoardAdapter(
    val itemList: ArrayList<BoardItem>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
    var desiredWidth = 200 // 원하는 가로 크기 (픽셀 단위)
    var desiredHeight = 200 // 원하는 세로 크기 (픽셀 단위)

    fun updateItems(newItems: List<BoardItem>) {
        itemList.clear()
        itemList.addAll(newItems)
        notifyDataSetChanged() // 데이터가 변경되었음을 어댑터에 알림
    }
    fun getItems() : ArrayList<BoardItem>{
        return itemList;
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_menu, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(itemList[position].img)
            .override(desiredWidth, desiredHeight) // 이미지 크기를 지정합니다.
            .into(holder.imgView)
        holder.name.text = itemList[position].name
        holder.price.text = itemList[position].price
        holder.intro.text = itemList[position].intro
        holder.tag.text = itemList[position].tag
        holder.owner.text = itemList[position].owner
        holder.message.text = itemList[position].message.toString()
        holder.like.text = itemList[position].like.toString()
        holder.state.text = itemList[position].state
        holder.imgLike.setImageURI(itemList[position].imgLike)
        holder.imgUser.setImageURI(itemList[position].imgUser)
        holder.imgMsg.setImageURI(itemList[position].imgMsg)

        holder.itemView.setOnClickListener {
            listener.onItemClicked(position)
        }

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgUser = itemView.findViewById<ImageView>(R.id.imgUser)
        var imgLike = itemView.findViewById<ImageView>(R.id.imgLike)
        var imgMsg = itemView.findViewById<ImageView>(R.id.imgMsg)
        var imgView = itemView.findViewById<ImageView>(R.id.product)
        val name = itemView.findViewById<TextView>(R.id.product_name)
        val price = itemView.findViewById<TextView>(R.id.product_price)
        val intro = itemView.findViewById<TextView>(R.id.product_intro)
        val tag = itemView.findViewById<TextView>(R.id.product_tag)
        val owner = itemView.findViewById<TextView>(R.id.product_owner)
        val message = itemView.findViewById<TextView>(R.id.product_message)
        val like = itemView.findViewById<TextView>(R.id.product_like)
        val state = itemView.findViewById<TextView>(R.id.product_state)
    }
}

class SellListActivity : AppCompatActivity() , OnItemClickListener {

    private val viewModel by viewModels<SellListViewModel>()
    private lateinit var databaseReference: DatabaseReference
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("addItems")
    companion object {
        private const val REQUEST_CODE_EDIT = 1 // 요청 코드 상수 정의
    }
    lateinit var storage: FirebaseStorage

    override fun onItemClicked(position: Int) {


        if (viewModel.barState.value == false) {
            // 현재 프래그먼트를 찾습니다.
            val currentFragment = supportFragmentManager.findFragmentById(R.id.bar_fragment)

            // 현재 프래그먼트에 fade_out 애니메이션을 적용합니다.
            currentFragment?.let {
                val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
                it.view?.startAnimation(fadeOut)
                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        // 애니메이션 시작 시 필요한 작업 (필요하다면)
                    }

                    override fun onAnimationEnd(animation: Animation) {

                        if (viewModel.barState.value == false) {
                            // 애니메이션이 끝난 후 새 프래그먼트로 교체합니다.
                            val newFragment = RogoBarFragment()
                            supportFragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, 0)
                                .replace(R.id.bar_fragment, newFragment)
                                .commit()
                            viewModel.changeValueFT()
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                        // 애니메이션 반복 시 필요한 작업 (필요하다면)
                    }
                })
            }
        }
        else{
            val item = viewModel.items.value?.get(position)
            item?.let { selectedItem ->
                pushNextPage(selectedItem,position)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_EDIT && resultCode == Activity.RESULT_OK) {
            data?.let {
                val state = it.getStringExtra("product_st")
                val name = it.getStringExtra("editName")
                val price = it.getStringExtra("editPrice")
                val intro = it.getStringExtra("editIntro")
                val tag = it.getStringExtra("editTag")
                val ownerUid = it.getStringExtra("editOwnerUid")
                val pos = it.getStringExtra("position")?.toInt()

                if (state != null && name != null && price != null && intro != null && tag != null && ownerUid != null && pos != null) {
                    viewModel.updateItem(state, name, price, intro, tag, ownerUid, pos)
                }
                val itemId = it.getStringExtra("itemId") // 상품의 고유 ID 또는 식별자

                if (state != null && itemId != null) {
                    val itemRef = myRef.child(itemId) // 여기서 'itemId'는 업데이트하려는 항목의 ID입니다.
                    itemRef.child("state").setValue(state)
                }


            }
        }
    }

    fun pushNextPage(item: BoardItem,position: Int) {
        val intent = Intent(this, Look::class.java).apply {
            putExtra("name", item.name)
            putExtra("img", item.img.toString())
            putExtra("imgUser", item.imgUser.toString())
            putExtra("imgLike", item.imgLike.toString())
            putExtra("imgMsg", item.imgMsg.toString())
            putExtra("price", item.price)
            putExtra("intro", item.intro)
            putExtra("tag", item.tag)
            putExtra("owner", item.owner)
            putExtra("msgState", item.msgState)
            putExtra("message", item.message)
            putExtra("like", item.like)
            putExtra("likeState", item.likeState)
            putExtra("state", item.state)
            putExtra("ownerUid", item.ownerUid)
            putExtra("itemId", item.itemId)
            putExtra("pos",position)
        }
        startActivityForResult(intent, REQUEST_CODE_EDIT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_list)
        val database = FirebaseDatabase.getInstance()
        val addedItemIds = mutableSetOf<String>()

        val myRef = database.getReference("addItems")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // dataSnapshot 객체에 AddItems 아래의 모든 데이터가 포함됩니다.
                for (itemSnapshot in dataSnapshot.children) {

                    val itemId = itemSnapshot.key ?: "Default Name"
                    if (!addedItemIds.contains(itemId)) {
                        // 중복되지 않은 경우에만 아이템을 추가합니다.
                        addedItemIds.add(itemId)
                        val imageUrl = itemSnapshot.child("imageUrl").getValue(String::class.java)
                            ?: "기본 이미지 URL"
                        val itemName = itemSnapshot.child("name").getValue(String::class.java)
                            ?: "Default Name"
                        val itemInfo =
                            itemSnapshot.child("description").getValue(String::class.java)
                                ?: "Defaul Name"
                        val price =
                            itemSnapshot.child("price").getValue(Long::class.java)?.toString()
                                ?: "Default Name"
                        val message =
                            itemSnapshot.child("message").getValue(Long::class.java)?.toInt()
                                ?: "Default Name"
                        val tags = itemSnapshot.child("tags").getValue(String::class.java)
                            ?: "Default Name"
                        val userId = itemSnapshot.child("userId").getValue(String::class.java)
                            ?: "Default Name"
                        val sharedPref = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        val state = itemSnapshot.child("state").getValue(String::class.java)
                            ?: "Default Name"
                        val userName = itemSnapshot.child("userName").getValue(String::class.java)
                            ?: "Default Name"
                        val item = BoardItem(
                            Uri.parse(imageUrl),
                            itemName,
                            Uri.parse("android.resource://com.example.hunbbing/drawable/usericon"),
                            Uri.parse("android.resource://com.example.hunbbing/drawable/like_off"),
                            Uri.parse("android.resource://com.example.hunbbing/drawable/message"),
                            price + "원",
                            itemInfo,
                            tags,
                            userName,
                            msgState = true,
                            5,
                            5,
                            false,
                            state,
                            userId,
                            itemId
                        )
                        viewModel.addItem(item)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 에러 처리를 합니다.
            }
        })


        val fragmentLogo = RogoBarFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.bar_fragment, fragmentLogo)
            .commit()


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@SellListActivity)
            adapter = BoardAdapter(ArrayList(), this@SellListActivity)
        }
        (recyclerView.adapter as? BoardAdapter)?.desiredWidth = 200
        (recyclerView.adapter as? BoardAdapter)?.desiredHeight = 200

        // LiveData 관찰
        viewModel.items.observe(this, Observer { items ->
            (recyclerView.adapter as? BoardAdapter)?.updateItems(items)
        })

        val radioGroup = findViewById<RadioGroup>(R.id.order)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = findViewById<RadioButton>(checkedId)
            val selectedOption = radioButton.text.toString()
            when(selectedOption)
            {
                "최신순"->viewModel.orderDate()
                "이름순"->viewModel.orderName()
                else->return@setOnCheckedChangeListener
            }
        }

        val addbtn = findViewById<FloatingActionButton>(R.id.addbtn)
        addbtn.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        val chatRoombtn = findViewById<FloatingActionButton>(R.id.chattingroom_btn)
        chatRoombtn.setOnClickListener{
            val intent = Intent(this, ChatRoomActivity::class.java)
            startActivity(intent)
        }


    }
}