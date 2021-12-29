package ru.aasmc.taskie.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.aasmc.taskie.ui.notes.NotesFragment
import ru.aasmc.taskie.ui.profile.ProfileFragment

/**
 * Displays the pages for the main screen.
 */
class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(NotesFragment(), ProfileFragment())
    private val titles = listOf("Notes", "Profile")

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}