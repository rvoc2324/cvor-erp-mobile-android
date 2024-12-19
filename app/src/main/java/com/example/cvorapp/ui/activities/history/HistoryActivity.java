package com.example.cvorapp.ui.activities.history;

// Import statements
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// Main activity that serves as the entry point and navigation hub for the app.
public class HomeActivity extends AppCompatActivity {
    // Declare UI components
    private BottomNavigationView bottomNavigationView;
    private Button btnAddWatermark, btnCombinePDFs, btnConvertToPDF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize and set up the UI components
        initUIComponents();

        // Handle bottom navigation
        setupBottomNavigation();

        // Set click listeners for primary and secondary actions
        setupActionButtons();
    }

    // Initialize UI components and bind them to XML elements
    private void initUIComponents() {
        try {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            btnAddWatermark = findViewById(R.id.btn_add_watermark);
            btnCombinePDFs = findViewById(R.id.btn_combine_pdfs);
            btnConvertToPDF = findViewById(R.id.btn_convert_to_pdf);
        } catch (Exception e) {
            // Handle potential UI initialization errors gracefully
            Toast.makeText(this, "Error initializing components: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Configure bottom navigation to load different fragments or activities
    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.nav_files:
                    selectedFragment = new FileSourceFragment(); // Fragment for managing files
                    break;
                case R.id.nav_watermark:
                    selectedFragment = new WatermarkFragment(); // Fragment for adding watermarks
                    break;
                case R.id.nav_learn_more:
                    Intent intent = new Intent(this, LearnMoreActivity.class);
                    startActivity(intent); // Navigate to Learn More screen
                    return true;
            }
            return loadFragment(selectedFragment);
        });

        // Load default fragment on activity start
        if (getSupportFragmentManager().getFragments().isEmpty()) {
            loadFragment(new FileSourceFragment());
        }
    }

    // Utility method to replace the current fragment with the selected one
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    // Set up primary and secondary actions with their respective functionalities
    private void setupActionButtons() {
        btnAddWatermark.setOnClickListener(view -> {
            try {
                // Navigate to the WatermarkFragment
                loadFragment(new WatermarkFragment());
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Watermark functionality", Toast.LENGTH_SHORT).show();
            }
        });

        btnCombinePDFs.setOnClickListener(view -> {
            // Navigate to Combine PDFs functionality
            Toast.makeText(this, "Combine PDFs functionality coming soon!", Toast.LENGTH_SHORT).show();
        });

        btnConvertToPDF.setOnClickListener(view -> {
            // Navigate to Convert Images to PDF functionality
            Toast.makeText(this, "Convert to PDF functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
    }
}
