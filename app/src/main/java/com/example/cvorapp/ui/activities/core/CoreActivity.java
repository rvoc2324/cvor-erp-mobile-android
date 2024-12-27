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

        // Observe changes to the SourceType in the ViewModel and navigate accordingly
        coreViewModel.getSourceType().observe(this, sourceType -> {
            if (sourceType != null) {
                // Navigate based on sourceType
                if (sourceType == CoreViewModel.SourceType.CAMERA) {
                    navToCamera();
                } else if (sourceType == CoreViewModel.SourceType.FILE_MANAGER) {
                    navToFileManager();
                }
            }
        });

        // Observe actionType to manage the navigation flows based on it
        coreViewModel.getActionType().observe(this, action -> {
            if (action != null) {
                handleActionType(action);
            }
        });
    }

    /**
     * Method to show the FileSourceFragment as a bottom sheet.
     */
    private void showFileSourceBottomSheet() {
        FileSourceFragment fileSourceFragment = new FileSourceFragment();
        fileSourceFragment.show(getSupportFragmentManager(), fileSourceFragment.getTag());
    }

    /**
     * Navigate to CameraFragment based on the source type (CAMERA).
     */
    private void navToCamera() {
        // Navigate to Camera Fragment
        navController.navigate(R.id.action_fileSourceFragment_to_cameraFragment);
    }

    /**
     * Navigate to FileManagerFragment based on the source type (FILE_MANAGER).
     */
    private void navToFileManager() {
        // Navigate to File Manager Fragment
        navController.navigate(R.id.action_fileSourceFragment_to_fileManagerFragment);
    }

    /**
     * Handle different action types and navigate accordingly.
     */
    private void handleActionType(String actionType) {
        CoreViewModel.SourceType sourceType = coreViewModel.getSourceType().getValue();

        if (sourceType == null) {
            // Handle cases where source type is not set
            return;
        }

        switch (sourceType) {
            case CAMERA:
                // Navigate based on action type when source is CAMERA
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
                    default:
                        // Handle unexpected action types for CAMERA
                        break;
                }
                break;

            case FILE_MANAGER:
                // Navigate based on action type when source is FILE_MANAGER
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
                    default:
                        // Handle unexpected action types for FILE_MANAGER
                        break;
                }
                break;

            default:
                // Handle unexpected source types
                break;
        }
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
