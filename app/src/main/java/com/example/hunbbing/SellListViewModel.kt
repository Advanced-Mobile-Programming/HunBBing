package com.example.hunbbing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SellListViewModel : ViewModel() {
    private val _barState = MutableLiveData<Boolean>()

    val barState : LiveData<Boolean> get()= _barState

    init{
        _barState.value = true
    }

    fun changeValueTF() {
        if(_barState.value==true){
            _barState.value = false
        }
    }
    fun changeValueFT() {
        if(_barState.value==false){
            _barState.value = true
        }
    }


}