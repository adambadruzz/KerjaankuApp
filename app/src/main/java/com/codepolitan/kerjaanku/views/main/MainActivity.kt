package com.codepolitan.kerjaanku.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.codepolitan.kerjaanku.R
import com.codepolitan.kerjaanku.views.home.HomeFragment
import com.codepolitan.kerjaanku.views.newtask.NewTaskActivity
import com.codepolitan.kerjaanku.views.taskcomplete.TaskCompleteFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigation()
        onClick()
    }

    private fun onClick() {
        btnAddTask.setOnClickListener {
            startActivity<NewTaskActivity>()
        }
    }

    private fun setupBottomNavigation() {
        btmNavMain.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.action_home -> {
                    openHomeFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_task_complete -> {
                    openHomeFragment(TaskCompleteFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            return@setOnNavigationItemSelectedListener false
        }

        btmNavMain.selectedItemId = R.id.action_home
    }

    private fun openHomeFragment(fragment: Fragment){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameMain, fragment)
            .addToBackStack(null)
            .commit()
    }
}
