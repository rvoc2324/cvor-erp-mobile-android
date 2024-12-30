package com.example.cvorapp.ui.activities.core;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cvorapp.R;
import com.example.cvorapp.ui.fragments.filesource.FileSourceFragment;
import com.example.cvorapp.viewmodels.CoreViewModel;

public class CoreActivity extends AppCompatActivity {

    private CoreViewModel coreViewModel;
    private NavController navController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_core);

        // Initialize ViewModel
        coreViewModel = new ViewModelProvider(this).get(CoreViewModel.class);

        // Set actionType from intent extras
        String actionType = getIntent().getStringExtra("actionType");
        if (actionType != null) {
            coreViewModel.setActionType(actionType);
        }

        // Initialize the NavController
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_core);

        if (savedInstanceState == null) {
            showFileSourceBottomSheet();
        }

        // Observe source type for initial navigation
        coreViewModel.getSourceType().observe(this, sourceType -> {
            if (sourceType != null) {
                if (sourceType == CoreViewModel.SourceType.CAMERA) {
                    navToCamera();
                } else if (sourceType == CoreViewModel.SourceType.FILE_MANAGER) {
                    navToFileManager();
                }
            }
        });

        // Observe navigation events for flow management
        coreViewModel.getNavigationEvent().observe(this, event -> {
            if (event != null) {
                switch (event) {
                    case "navigate_to_action":
                        navToAction();
                        break;

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
        });
    }

    /**
     * Show the FileSourceFragment as a bottom sheet on activity launch.
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
     * Handle navigation to the next action based on source and action types.
     */
    private void navToAction() {
        CoreViewModel.SourceType sourceType = coreViewModel.getSourceType().getValue();
        String actionType = coreViewModel.getActionType().getValue();

        if (sourceType == null || actionType == null) return;

        switch (sourceType) {
            case CAMERA:
                navigateFromCamera(actionType);
                break;

            case FILE_MANAGER:
                navigateFromFileManager(actionType);
                break;
        }
    }

    private void navigateFromCamera(String actionType) {
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
    }

    private void navigateFromFileManager(String actionType) {
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
    }

    /**
     * Navigate to PreviewFragment after processing is complete.
     */
    private void navToPreview() {
        navController.navigate(R.id.action_watermarkFragment_to_previewFragment);
    }

    /**
     * Navigate to ShareFragment from PreviewFragment.
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
        coreViewModel.clearState();
    }
}
