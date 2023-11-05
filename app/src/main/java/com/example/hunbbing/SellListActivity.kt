package com.example.hunbbing

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class BoardItem(
    val img: Uri?, val name: String,val imgUser: Uri?,val imgLike: Uri?,val imgMsg: Uri?, val price: String, val intro: String, val tag: String, val owner: String,
    val msgState: Boolean,
    val message: Int, val like: Int,
    val likeState: Boolean, val state: String)


class BoardAdapter(val itemList: ArrayList<BoardItem>) :
    RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

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

class SellListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_list)

        val imageResourceUri1 = Uri.parse("android.resource://com.example.hunbbing/drawable/product")
        val imageResourceUri2 = Uri.parse("android.resource://com.example.hunbbing/drawable/like_off")
        val imageResourceUri3 = Uri.parse("android.resource://com.example.hunbbing/drawable/usericon")
        val imageResourceUri4 = Uri.parse("android.resource://com.example.hunbbing/drawable/message")

        val list = ArrayList<BoardItem>()
        list.add(BoardItem(imageResourceUri1, "닌텐도 스위치", imageResourceUri3,imageResourceUri2,imageResourceUri4,"100000", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "닝텐도", imageResourceUri3,imageResourceUri2,imageResourceUri4,"10000", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "상품3", imageResourceUri3,imageResourceUri2,imageResourceUri4,"가격1", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "상품4", imageResourceUri3,imageResourceUri2,imageResourceUri4,"가격1", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))
        list.add(BoardItem(imageResourceUri1, "상품5", imageResourceUri3,imageResourceUri2,imageResourceUri4,"가격1", "상품 설명1", "태그1", "소유자1", false, 5, 5, false, "판매종료"))


        val adapter = BoardAdapter(list)

        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }
}
