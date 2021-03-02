package app.doggy.la_original

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import app.doggy.la_original.MainAdapter
import app.doggy.la_original.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainFragment : Fragment() {

    private lateinit var mainAdapter: MainAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mainAdapter = MainAdapter(this)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = mainAdapter

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.setText(R.string.text_label_chart)
                    tab.setIcon(R.drawable.ic_baseline_pie_chart_24)
                }
                1 -> {
                    tab.setText(R.string.text_label_calendar)
                    tab.setIcon(R.drawable.ic_baseline_calendar_today_24)
                }
            }
        }.attach()
    }
}