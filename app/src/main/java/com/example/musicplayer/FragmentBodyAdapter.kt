package com.example.musicplayer

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentBodyAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {

        return when(position) {
            0-> HomeFragment()
            1-> LibraryFragment()
            2-> VisualizerFragment()
            3-> SongsFragment()

            else -> HomeFragment()
        }
    }
}