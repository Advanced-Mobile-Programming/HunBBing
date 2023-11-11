package com.example.hunbbing

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


interface OnItemClickListener {
    fun onItemClicked(position: Int)
}
data class BoardItem(
    val img: Uri?, val name: String,val imgUser: Uri?,val imgLike: Uri?,val imgMsg: Uri?, val price: String, val intro: String, val tag: String, val owner: String,
    val msgState: Boolean,
    val message: Int, val like: Int,
    val likeState: Boolean, val state: String)


class BoardAdapter(val itemList: ArrayList<BoardItem>,private val listener: OnItemClickListener) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    private val filteredItems = MutableLiveData<ArrayList<BoardItem>>(ArrayList(itemList))



    fun getItemsLiveData(): LiveData<ArrayList<BoardItem>> {
        return filteredItems
    }
    fun resetList() {
        filteredItems.value = ArrayList(itemList) // LiveData에 값을 할당하여 변경 감지
    }

    fun filterList(filteredItems: ArrayList<BoardItem>) {
        this.filteredItems.value = filteredItems // LiveData에 값을 할당하여 변경 감지
    }
    fun addItem(item: BoardItem) {
        itemList.add(item)
        notifyItemInserted(itemList.size - 1)
    }

    fun removeItem(position: Int) {
        itemList.removeAt(position)
        notifyItemRemoved(position)
    }
    fun getItems(): ArrayList<BoardItem> {
        return itemList
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

class SellListActivity : AppCompatActivity() , OnItemClickListener{
    private val viewModel by viewModels<SellListViewModel>()
    lateinit var storage: FirebaseStorage
    override fun onItemClicked(position: Int) {

        if(viewModel.barState.value==false) {
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

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_list)

        Firebase.auth.currentUser ?: finish()





        // 가져오기 val imageRef1 = storageRef.child("images/computer_sangsangbugi.jpg")



        val fragmentLogo = RogoBarFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.bar_fragment,fragmentLogo)
            .commit()




        val imageResourceUri1 = Uri.parse("android.resource://com.example.hunbbing/drawable/product")
        val imageResourceUri2 = Uri.parse("android.resource://com.example.hunbbing/drawable/like_off")
        val imageResourceUri3 = Uri.parse("android.resource://com.example.hunbbing/drawable/usericon")
        val imageResourceUri4 = Uri.parse("android.resource://com.example.hunbbing/drawable/message")

        val list = ArrayList<BoardItem>()
        list.add(BoardItem(imageResourceUri1, "닌텐도 스위치", imageResourceUri3,imageResourceUri2,imageResourceUri4,"100,000원", "스위치 입니다.", "#게임", "코고는 이나경", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "닝텐도", imageResourceUri3,imageResourceUri2,imageResourceUri4,"10000원", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "상품3", imageResourceUri3,imageResourceUri2,imageResourceUri4,"가격1", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "상품4", imageResourceUri3,imageResourceUri2,imageResourceUri4,"가격1", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "상품5", imageResourceUri3,imageResourceUri2,imageResourceUri4,"가격1", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))


        val adapter = BoardAdapter(list, this)
        adapter.getItemsLiveData().observe(this, Observer { items ->
            // 아이템 리스트가 변경될 때 마다 리사이클러뷰 업데이트
            adapter.notifyDataSetChanged()
        })
        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }
}