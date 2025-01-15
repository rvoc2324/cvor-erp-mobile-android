package com.rvoc.cvorapp.ui.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;

import com.rvoc.cvorapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rvoc.cvorapp.ui.activities.core.CoreActivity;
import com.rvoc.cvorapp.ui.activities.whatsnew.WhatsNewActivity;

public class HomeActivity extends AppCompatActivity {
    // Declare UI components
    private Button btnAddWatermark, btnCombinePDFs, btnConvertToPDF;
    private TextView tvAnimatedText;
    private BottomNavigationView bottomNavigationView;
    // private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize and set up the UI components
        initUIComponents();
        /*
        // Set up NavController
        try {
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment_home);
            if (navHostFragment != null) {
                navController = navHostFragment.getNavController();
                NavigationUI.setupActionBarWithNavController(this, navController);
            } else {
                throw new IllegalStateException("NavHostFragment not found in HomeActivity.");
            }
        } catch (Exception e) {
            Log.e("HomeActivity", "NavController setup failed: " + e.getMessage());
            Toast.makeText(this, "NavController setup failed", Toast.LENGTH_SHORT).show();
        }*/

        // Set click listeners for buttons
        setupActionButtons();

        // Set up animated text
        // setupAnimatedText();

        // Set up bottom navigation
        // setupBottomNavigation();
    }

    // Initialize UI components and bind them to XML elements
    private void initUIComponents() {
        try {
            btnAddWatermark = findViewById(R.id.btn_add_watermark);
            btnCombinePDFs = findViewById(R.id.btn_combine_pdfs);
            btnConvertToPDF = findViewById(R.id.btn_convert_to_pdf);
            tvAnimatedText = findViewById(R.id.tv_animated_text);
            bottomNavigationView = findViewById(R.id.bottom_navigation);
        } catch (Exception e) {
            Log.e("HomeActivity", "Error initializing components: " + e.getMessage());
            Toast.makeText(this, "Error initializing components", Toast.LENGTH_SHORT).show();
        }
    }

    // Set up button click listeners
    private void setupActionButtons() {
        btnAddWatermark.setOnClickListener(view -> navigateToCoreActivity("addwatermark"));
        btnCombinePDFs.setOnClickListener(view -> navigateToCoreActivity("combinepdf"));
        btnConvertToPDF.setOnClickListener(view -> navigateToCoreActivity("converttopdf"));
    }

    private void navigateToCoreActivity(String actionType) {
        try {
            // Create an intent to start CoreActivity
            Intent intent = new Intent(HomeActivity.this, CoreActivity.class);

            // Pass the actionType as an extra in the intent
            intent.putExtra("actionType", actionType);

            // Start the CoreActivity
            startActivity(intent);
        } catch (Exception e) {
            Log.e("HomeActivity", "Failed to navigate: " + e.getMessage());
            Toast.makeText(this, "Failed to navigate", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    private int getActionIdForType(String actionType) {
        return switch (actionType) {
            case "addwatermark" -> R.id.action_home_to_coreActivity_watermark;
            case "combinepdf" -> R.id.action_home_to_coreActivity_combine;
            case "converttopdf" -> R.id.action_home_to_coreActivity_convert;
            default -> -1;  // Invalid action type
        };
    }*/

    // Set up animated text
    private void setupAnimatedText() {
        try {
            AlphaAnimation fadeInOut = new AlphaAnimation(0.0f, 1.0f);
            fadeInOut.setDuration(1500); // Animation duration
            fadeInOut.setRepeatCount(AlphaAnimation.INFINITE);
            fadeInOut.setRepeatMode(AlphaAnimation.REVERSE);
            tvAnimatedText.setText(getString(R.string.app_slogan));
            tvAnimatedText.startAnimation(fadeInOut);
        } catch (Exception e) {
            Log.e("HomeActivity", "Error setting up animated text: " + e.getMessage());
            Toast.makeText(this, "Error setting up animated text", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    // Set up bottom navigation
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> switch (item.getItemId()) {
            case R.id.nav_share_history ->
                    navigateToShareHistoryActivity();
                    yield true;
            case R.id.nav_whatsnew -> {
                navigateToWhatsNewActivity();
                yield true;
            }
            case R.id.nav_settings -> {
                navigateToSettings();
                yield true;
            }
            default -> false;
        });
    }

    // Navigate to WhatsNewActivity
    private void navigateToWhatsNewActivity() {
        try {
            Intent intent = new Intent(HomeActivity.this, WhatsNewActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e("HomeActivity", "Failed to navigate to WhatsNewActivity: " + e.getMessage());
            Toast.makeText(this, "Failed to navigate to What's New", Toast.LENGTH_SHORT).show();
        }
    }*/
}
