package com.example.cvorapp.ui.activities.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cvorapp.R;
import com.example.cvorapp.ui.activities.core.CoreActivity;
import com.example.cvorapp.ui.activities.learnmore.LearnMoreActivity;

public class HomeActivity extends AppCompatActivity {
    // Declare UI components
    private Button btnAddWatermark, btnCombinePDFs, btnConvertToPDF;
    private TextView tvAnimatedText;
    private View bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize and set up the UI components
        initUIComponents();

        // Set click listeners for buttons
        setupActionButtons();

        // Set up animated text
        setupAnimatedText();

        // Set up bottom navigation
        setupBottomNavigation();
    }

    // Initialize UI components and bind them to XML elements
    private void initUIComponents() {
        try {
            btnAddWatermark = findViewById(R.id.btn_add_watermark);
            btnCombinePDFs = findViewById(R.id.btn_combine_pdfs);
            btnConvertToPDF = findViewById(R.id.btn_convert_to_pdf);
            tvAnimatedText = findViewById(R.id.tv_animated_text);
            bottomNavigationView = findViewById(R.id.bottom_navigation); // Ensure the ID matches the XML file
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing components: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Set up button click listeners
    private void setupActionButtons() {
        btnAddWatermark.setOnClickListener(view -> navigateToCoreActivity("watermark"));
        btnCombinePDFs.setOnClickListener(view -> navigateToCoreActivity("combine"));
        btnConvertToPDF.setOnClickListener(view -> navigateToCoreActivity("convert"));
    }

    // Utility method to navigate to CoreActivity with an action type
    private void navigateToCoreActivity(String actionType) {
        try {
            Intent intent = new Intent(HomeActivity.this, CoreActivity.class);
            intent.putExtra("actionType", actionType);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to navigate to Core Activity", Toast.LENGTH_SHORT).show();
        }
    }

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
            Toast.makeText(this, "Error setting up animated text", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnClickListener(item -> {
            if (item.getItemId() == R.id.nav_learn_more) {
                Intent intent = new Intent(this, LearnMoreActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
