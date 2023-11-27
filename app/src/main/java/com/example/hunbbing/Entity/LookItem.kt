package com.example.hunbbing.Entity

data class LookItem(
    val description: String,
    val imageUrl: String,
    val name: String,
    val price: Int,
    val state: String,
    val tags: String,
    val userId: String,
    val userName: String
){constructor():this("", "", "", 0, "", "", "", "")}
