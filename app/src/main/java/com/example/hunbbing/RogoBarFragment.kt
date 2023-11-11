package com.example.hunbbing

import SearchFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels


class RogoBarFragment : Fragment() {
    private val viewModel by activityViewModels<SellListViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_logo_bar_fragment, container, false)
        val search = view.findViewById<ImageButton>(R.id.search_btn)
        if(viewModel.barState.value == true) {
            search.setOnClickListener {
                // 현재 프래그먼트의 뷰를 찾습니다.
                val currentFragmentView = view
                // 현재 뷰에 fade_out 애니메이션을 적용합니다.
                val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)
                currentFragmentView.startAnimation(fadeOut)
                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        val fragmentSearch = SearchFragment()
                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, 0)
                            .replace(R.id.bar_fragment, fragmentSearch)
                            .commit()
                        viewModel.changeValueTF()
                    }

                    override fun onAnimationRepeat(animation: Animation) {
                    }
                })
            }
        }
        return view

    }


}