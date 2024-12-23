package com.example.cvorapp.ui.activities.core;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.cvorapp.R;
import com.example.cvorapp.viewmodels.CoreViewModel;

public class CoreActivity extends AppCompatActivity {

    private CoreViewModel coreViewModel;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        // Initialize the ViewModel
        coreViewModel = new ViewModelProvider(this).get(CoreViewModel.class);

        // Retrieve actionType passed from HomeActivity
        String actionType = getIntent().getStringExtra("actionType");
        if (actionType != null) {
            coreViewModel.setActionType(actionType);
        }

        // Set up Navigation
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_core);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        } else {
            Toast.makeText(this, "Error initializing navigation", Toast.LENGTH_SHORT).show();
            return;
        }

        // Default navigation to FileSourceFragment
        if (savedInstanceState == null) {
            navController.navigate(R.id.fileSourceFragment);
        }

        // Set up navigation UI
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController != null && navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        coreViewModel.clearState(); // Clear only non-persistent states
    }
}
