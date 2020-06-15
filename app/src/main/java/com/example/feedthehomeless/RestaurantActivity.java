package com.example.feedthehomeless;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.feedthehomeless.RestaurantUI.account.AccountFragment;
import com.example.feedthehomeless.RestaurantUI.home.HomeFragment;
import com.example.feedthehomeless.RestaurantUI.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class RestaurantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        final BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.rest_nav_home, R.id.rest_nav_account, R.id.rest_nav_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.restaurant_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
