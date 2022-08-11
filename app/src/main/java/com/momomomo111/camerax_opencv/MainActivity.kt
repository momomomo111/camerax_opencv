package com.momomomo111.camerax_opencv

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.switchmaterial.SwitchMaterial
import com.momomomo111.camerax_opencv.data.SettingsViewModel
import com.momomomo111.camerax_opencv.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        val settingsViewModel: SettingsViewModel by viewModels()
        val navController = (
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment)
                as NavHostFragment
            ).navController
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return

        val vibSwitch = SwitchMaterial(this)
        val vibrationEnable = sharedPref.getBoolean(getString(R.string.vibration_enable), true)
        vibSwitch.isChecked = vibrationEnable
        settingsViewModel.onVibrationChange(vibrationEnable)
        vibSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean(getString(R.string.vibration_enable), isChecked)
                settingsViewModel.onVibrationChange(isChecked)
                apply()
            }
        }
        binding.navView.menu.findItem(R.id.vibrationSwitch).actionView = vibSwitch
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    companion object {
        init {
            System.loadLibrary("opencv_java4")
        }
    }
}
