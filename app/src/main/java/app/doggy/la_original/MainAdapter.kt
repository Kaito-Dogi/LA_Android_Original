package app.doggy.la_original

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    lateinit var fragment: Fragment

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {

        when(position) {
            0 -> fragment = ChartFragment()
            1 -> fragment = CalendarFragment()
        }

        return fragment
    }
}