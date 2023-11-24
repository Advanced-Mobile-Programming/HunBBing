package com.example.hunbbing

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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


interface OnItemClickListener {
    fun onItemClicked(position: Int)
}
data class BoardItem(
    val img: Uri?,
    val name: String,
    val imgUser: Uri?,
    val imgLike: Uri?,
    val imgMsg: Uri?,
    val price: String,
    val intro: String,
    val tag: String,
    val owner: String,
    val msgState: Boolean,
    val message: Int,
    val like: Int,
    val likeState: Boolean,
    val state: String
)


class BoardAdapter(val itemList: ArrayList<BoardItem>,private val listener: OnItemClickListener) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {
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
        holder.imgView.setImageURI(itemList[position].img)
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
                pushNextPage(selectedItem)
            }
        }

    }

    fun pushNextPage(item: BoardItem) {
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
        }
        startActivity(intent)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_list)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("AddItems")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // dataSnapshot 객체에 AddItems 아래의 모든 데이터가 포함됩니다.
                for (itemSnapshot in dataSnapshot.children) {

                    val itemId = itemSnapshot.child("itemId").getValue(String::class.java)?: "Default Name"
                    val itemName = itemSnapshot.child("itemName").getValue(String::class.java)?: "Default Name"
                    val itemInfo = itemSnapshot.child("itemInfo").getValue(String::class.java)?: "Defaul Name"
                    val price = itemSnapshot.child("price").getValue(Long::class.java)?.toString()?: "Default Name"
                    val message = itemSnapshot.child("message").getValue(Long::class.java)?.toInt()?: "Default Name"
                    val tags = itemSnapshot.child("tags").getValue(String::class.java)?: "Default Name"
                    val userId = itemSnapshot.child("userId").getValue(String::class.java)?: "Default Name"
                    val userName= itemSnapshot.child("userName").getValue(String::class.java)?: "Default Name"
                    val item = BoardItem(
                        Uri.parse("android.resource://com.example.hunbbing/drawable/product"),
                        itemName,
                        Uri.parse("android.resource://com.example.hunbbing/drawable/usericon"),
                        Uri.parse("android.resource://com.example.hunbbing/drawable/like_off"),
                        Uri.parse("android.resource://com.example.hunbbing/drawable/message"),
                        price,
                        itemInfo,
                        tags,
                        userName,
                        msgState = true,
                        5,
                        5,
                        false,
                        "판매중")
                    viewModel.addItem(item);
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

        // LiveData 관찰
        viewModel.items.observe(this, Observer { items ->
            (recyclerView.adapter as? BoardAdapter)?.updateItems(items)
        })


        val addbtn = findViewById<FloatingActionButton>(R.id.addbtn)
        addbtn.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)

        }


    }
}