package com.example.hunbbing

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchET = view.findViewById<EditText>(R.id.search_et)

        // 부모 액티비티의 리사이클러뷰 참조 얻기
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.let {
            // 리사이클러뷰의 어댑터에 접근
            val adapter = it.adapter as? BoardAdapter
            adapter?.let { boardAdapter ->
                val itemList = boardAdapter.getItems()

                // 검색 텍스트 변경에 따른 처리
                searchET.doAfterTextChanged {
                    val searchText = searchET.text.toString()
                    searchProduct(searchText, itemList)
                }
            }
        }

        return view
    }

    fun searchProduct(product: String, itemList: ArrayList<BoardItem>) {
        val filteredList = itemList.filter {
            it.name.contains(product, ignoreCase = true) ||
                    it.intro.contains(product, ignoreCase = true) ||
                    it.tag.contains(product, ignoreCase = true)
        }
        val adapter = activity?.findViewById<RecyclerView>(R.id.recyclerView)?.adapter as? BoardAdapter
        adapter?.filterList(ArrayList(filteredList))
    }

}
