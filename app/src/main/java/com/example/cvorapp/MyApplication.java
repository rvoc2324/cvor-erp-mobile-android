package com.example.cvorapp;

import android.app.Application;
import android.util.Log;

import dagger.hilt.android.HiltAndroidApp; // For Dependency Injection

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate; // For Theme management
import androidx.work.Configuration; // For WorkManager configuration
import androidx.work.WorkManager; // WorkManager instance
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

@HiltAndroidApp // Marks this class as the entry point for Hilt Dependency Injection
public class MyApplication extends Application implements Configuration.Provider {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize application-wide configurations here
        initAppTheme();
        initWorkManager();
        initCrashlytics();
        initLogging();
        PDFBoxResourceLoader.init(getApplicationContext());

        Log.d("MyApplication", "Application initialized successfully.");
    }

    /**
     * Initialize the app's theme settings.
     * Since we're using a custom branding theme, we disable dark mode.
     */
    private void initAppTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Log.d("MyApplication", "App theme initialized to MODE_NIGHT_NO.");
    }

    /**
     * Initialize WorkManager for managing background tasks.
     */
    private void initWorkManager() {
        WorkManager.initialize(
                this,
                new Configuration.Builder()
                        .setMinimumLoggingLevel(Log.INFO) // Set logging level
                        .build()
        );
        Log.d("MyApplication", "WorkManager initialized.");
    }

    /**
     * Initialize Crashlytics or any other crash reporting library.
     */
    private void initCrashlytics() {
        try {
            // Example: Firebase Crashlytics initialization
            // FirebaseApp.initializeApp(this);
            // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
            Log.d("MyApplication", "Crashlytics initialized.");
        } catch (Exception e) {
            Log.e("MyApplication", "Crashlytics initialization failed.", e);
        }
    }

    /**
     * Initialize logging frameworks such as Timber.
     */
    private void initLogging() {
        try {
            // Example: Initialize Timber
            // Timber.plant(new Timber.DebugTree());
            Log.d("MyApplication", "Logging framework initialized.");
        } catch (Exception e) {
            Log.e("MyApplication", "Logging framework initialization failed.", e);
        }
    }

    /**
     * Required implementation for WorkManager Configuration.Provider.
     * This allows customization of WorkManager configuration.
     */
    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .build();
    }
}
