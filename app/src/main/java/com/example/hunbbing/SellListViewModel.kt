package com.example.hunbbing

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase


class SellListViewModel : ViewModel() {
    private val _items = MutableLiveData<List<BoardItem>>()
    val items: LiveData<List<BoardItem>> get() = _items

    private val _barState = MutableLiveData<Boolean>()
    val barState: LiveData<Boolean> get() = _barState

    private val originalList = mutableListOf<BoardItem>(
        // 여기에 초기 데이터 아이템 추가
        BoardItem(
            Uri.parse("android.resource://com.example.hunbbing/drawable/product"),
            "닌텐도 스위치",
            Uri.parse("android.resource://com.example.hunbbing/drawable/usericon"),
            Uri.parse("android.resource://com.example.hunbbing/drawable/like_off"),
            Uri.parse("android.resource://com.example.hunbbing/drawable/message"),
            "100,000원",
            "스위치 입니다.",
            "#게임",
            "코고는 이나경",
            false,
            5,
            5,
            false,
            "판매중",
            "",
            ""
        ),
        // 추가 아이템...
    )

    fun addItem(item: BoardItem) {
        originalList.add(0, item)
        _items.value = originalList.toList()
    }
    fun f5(){
        _items.value = originalList.toList()
    }

    fun clearList(){
        originalList.clear()
        _items.value = originalList
    }
    fun updateItem(
        state: String,
        name: String,
        price: String,
        intro: String,
        tag: String,
        uid: String,
        position: Int,
        itemId: String
    ) {
        val item = originalList.getOrNull(position)
        item?.let {
            it.state = state
            it.name = name
            it.price = price
            it.intro = intro
            it.tag = tag
            it.ownerUid = uid
            Log.d("에",itemId)
            Log.d("에w",state)
            val itemRef = FirebaseDatabase.getInstance().getReference("addItems").child(itemId)
            itemRef.child("state").setValue(state)
            _items.value = originalList.toList()
        }
    }


    init {
        // 초기 데이터 로드
        _items.value = originalList
        _barState.value = true
    }

    fun changeValueTF() {
        if (_barState.value == true) {
            _barState.value = false
        }
    }

    fun changeValueFT() {
        if (_barState.value == false) {
            _barState.value = true
        }
    }

    fun orderName() {
        val filteredList = originalList.sortedBy { it.name }
        _items.value = filteredList
    }

    fun orderDate (){
        _items.value = originalList
    }

    fun onSale(){
        val filteredList = originalList.filter { it.state.contains("판매중") }
        _items.value = filteredList
    }
    fun soldOut(){
        val filteredList = originalList.filter { it.state.contains("판매 완료") }
        _items.value = filteredList
    }
    fun searchProduct(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.intro.contains(query, ignoreCase = true) ||
                        it.tag.contains(query, ignoreCase = true) ||
                        it.owner.contains(query, ignoreCase = true)
            }
        }
        _items.value = filteredList
    }
}