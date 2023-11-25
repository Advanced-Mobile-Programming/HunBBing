package com.example.hunbbing

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class SellListViewModel : ViewModel() {
    private val _items = MutableLiveData<List<BoardItem>>()
    val items: LiveData<List<BoardItem>> get() = _items
    private val _barState = MutableLiveData<Boolean>()
    val barState: LiveData<Boolean> get() = _barState
    private val originalList = mutableListOf<BoardItem>(
        // 여기에 초기 데이터 아이템 추가

        // 추가 아이템...
    )

    fun addItem(item: BoardItem) {
        originalList.add(0, item)
        _items.value = originalList.toList()
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
    fun searchProduct(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.intro.contains(query, ignoreCase = true) ||
                        it.tag.contains(query, ignoreCase = true)
            }
        }
        _items.value = filteredList
    }
}
