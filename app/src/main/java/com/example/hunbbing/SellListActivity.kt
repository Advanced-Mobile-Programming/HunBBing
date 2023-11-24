package com.example.hunbbing

import android.content.ContentUris
import android.content.Intent
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch


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

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_list)

        Firebase.auth.currentUser ?: finish()

        // 가져오기 val imageRef1 = storageRef.child("images/computer_sangsangbugi.jpg")


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


