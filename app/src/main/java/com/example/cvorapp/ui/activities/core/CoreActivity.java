package com.example.cvorapp.ui.activities.core;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cvorapp.R;
import com.example.cvorapp.viewmodels.CoreViewModel;
import com.example.cvorapp.ui.fragments.filesource.FileSourceFragment;

public class CoreActivity extends AppCompatActivity {

    private CoreViewModel coreViewModel;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        // Initialize ViewModel
        coreViewModel = new ViewModelProvider(this).get(CoreViewModel.class);

        // Get the actionType from intent extras
        String actionType = getIntent().getStringExtra("actionType");
        if (actionType != null) {
            coreViewModel.setActionType(actionType);  // Set the action type in ViewModel
        }

        // Initialize the NavController for fragment navigation
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_core);

        if (savedInstanceState == null) {
            showFileSourceBottomSheet();  // Show file source bottom sheet on activity launch
        }

        // Observe SourceType to navigate to the appropriate source fragment
        coreViewModel.getSourceType().observe(this, sourceType -> {
            if (sourceType != null) {
                if (sourceType == CoreViewModel.SourceType.CAMERA) {
                    navToCamera();
                } else if (sourceType == CoreViewModel.SourceType.FILE_MANAGER) {
                    navToFileManager();
                }
            }
        });

        // Observe ActionType to handle navigation based on selected action
        coreViewModel.getActionType().observe(this, action -> {
            if (action != null) {
                handleActionType(action);
            }
        });

        // Observe Navigation Events to manage flow between fragments
        coreViewModel.getNavigationEvent().observe(this, event -> {
            if (event != null) {
                handleNavigationEvent(event);
            }
        });

        // Observe Action Completion to navigate to the preview fragment
        coreViewModel.isActionCompleted().observe(this, isCompleted -> {
            if (isCompleted != null && isCompleted) {
                navToPreview();
            }
        });
    }

    /**
     * Show the FileSourceFragment as a bottom sheet.
     */
    private void showFileSourceBottomSheet() {
        FileSourceFragment fileSourceFragment = new FileSourceFragment();
        fileSourceFragment.show(getSupportFragmentManager(), fileSourceFragment.getTag());
    }

    /**
     * Navigate to CameraFragment.
     */
    private void navToCamera() {
        navController.navigate(R.id.action_fileSourceFragment_to_cameraFragment);
    }

    /**
     * Navigate to FileManagerFragment.
     */
    private void navToFileManager() {
        navController.navigate(R.id.action_fileSourceFragment_to_fileManagerFragment);
    }

    /**
     * Handle ActionType navigation.
     */
    private void handleActionType(String actionType) {
        CoreViewModel.SourceType sourceType = coreViewModel.getSourceType().getValue();
        if (sourceType == null) return;

        switch (sourceType) {
            case CAMERA:
                switch (actionType) {
                    case "addwatermark":
                        navController.navigate(R.id.action_cameraFragment_to_watermarkFragment);
                        break;
                    case "combinepdf":
                        navController.navigate(R.id.action_cameraFragment_to_combinePdfFragment);
                        break;
                    case "convertpdf":
                        navController.navigate(R.id.action_cameraFragment_to_imageToPdfFragment);
                        break;
                }
                break;

            case FILE_MANAGER:
                switch (actionType) {
                    case "addwatermark":
                        navController.navigate(R.id.action_fileManagerFragment_to_watermarkFragment);
                        break;
                    case "combinepdf":
                        navController.navigate(R.id.action_fileManagerFragment_to_combinePdfFragment);
                        break;
                    case "convertpdf":
                        navController.navigate(R.id.action_fileManagerFragment_to_imageToPdfFragment);
                        break;
                }
                break;
        }
    }

    /**
     * Handle Navigation Events for Preview and Share steps.
     */
    private void handleNavigationEvent(String event) {
        switch (event) {
            case "navigate_to_preview":
                navToPreview();
                break;
            case "navigate_to_share":
                navToShare();
                break;
        }
        // Reset navigation event after handling
        coreViewModel.setNavigationEvent(null);
    }

    /**
     * Navigate to PreviewFragment.
     */
    private void navToPreview() {
        String actionType = coreViewModel.getActionType().getValue();
        if (actionType == null || actionType.isEmpty()) {
            return; // Return early if actionType is not set
        }

        switch (actionType) {
            case "addwatermark":
                navController.navigate(R.id.action_watermarkFragment_to_previewFragment);
                break;

            case "combinepdf":
                navController.navigate(R.id.action_combinePdfFragment_to_previewFragment);
                break;

            case "convertpdf":
                navController.navigate(R.id.action_imageToPdfFragment_to_previewFragment);
                break;

            default:
                // Handle unexpected actionType (optional)
                break;
        }
    }

    /**
     * Navigate to ShareFragment.
     */
    private void navToShare() {
        navController.navigate(R.id.action_previewFragment_to_shareFragment);
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
