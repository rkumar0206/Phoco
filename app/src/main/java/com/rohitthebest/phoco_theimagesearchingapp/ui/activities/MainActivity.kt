package com.rohitthebest.phoco_theimagesearchingapp.ui.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //No night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        binding.bottomNavigationView.menu.findItem(R.id.profileFragment).isVisible = false

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {

                R.id.homeFragment -> {

                    showBottomNav()
                    handleButtons(R.id.homeFragment)
                }

                R.id.favouritesFragment -> {

                    showBottomNav()
                    handleButtons(R.id.favouritesFragment)
                }

//                R.id.profileFragment -> {
//                    showBottomNav()
//                    handleButtons(R.id.profileFragment)
//                }

                R.id.searchFragment -> {

                    showBottomNav()
                    handleButtons(R.id.searchFragment)
                }
                R.id.loginSignUpFragment -> {

                    showBottomNav()
                }

                else -> hideBottomNav()
            }
        }
    }

    private fun handleButtons(id: Int) {

        binding.bottomNavigationView.menu.findItem(R.id.homeFragment).isEnabled = true
        binding.bottomNavigationView.menu.findItem(R.id.favouritesFragment).isEnabled = true
        //binding.bottomNavigationView.menu.findItem(R.id.profileFragment).isEnabled = true
        binding.bottomNavigationView.menu.findItem(R.id.searchFragment).isEnabled = true

        binding.bottomNavigationView.menu.findItem(id).isEnabled = false
    }


    private fun showBottomNav() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun hideBottomNav() {
        binding.bottomNavigationView.visibility = View.GONE
    }

}