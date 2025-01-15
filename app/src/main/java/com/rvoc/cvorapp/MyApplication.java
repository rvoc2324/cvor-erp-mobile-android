package com.rvoc.cvorapp;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate; // For Theme management
import androidx.work.WorkManager; // WorkManager instance
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import dagger.hilt.android.HiltAndroidApp; // For Dependency Injection

@HiltAndroidApp // Marks this class as the entry point for Hilt Dependency Injection
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize application-wide configurations here
        initAppTheme();
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
}
