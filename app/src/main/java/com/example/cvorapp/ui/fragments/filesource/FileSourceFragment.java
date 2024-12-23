package com.example.cvorapp.ui.fragments.filesource;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.cvorapp.R;
import com.example.cvorapp.viewmodels.CoreViewModel;

/**
 * FileSourceFragment
 * This fragment provides the user with two options for file selection:
 * 1. Using the Camera.
 * 2. Using the File Manager.
 * It sets the source type in the shared CoreViewModel and navigates to the respective fragment.
 */
public class FileSourceFragment extends Fragment {

    private LinearLayout cameraOption; // UI element for selecting the camera option
    private LinearLayout fileManagerOption; // UI element for selecting the file manager option
    private CoreViewModel coreViewModel; // Shared ViewModel for managing state

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_file_source, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);

        // Initialize UI components
        cameraOption = view.findViewById(R.id.option_camera);
        fileManagerOption = view.findViewById(R.id.option_file_manager);

        // Set click listeners for navigation
        setupListeners(view);
    }

    /**
     * Sets up click listeners for the camera and file manager options.
     *
     * @param view The root view of the fragment.
     */
    private void setupListeners(@NonNull View view) {
        NavController navController = Navigation.findNavController(view);

        // Handle camera option click
        cameraOption.setOnClickListener(v -> {
            try {
                // Update source type in ViewModel
                coreViewModel.setSourceType(CoreViewModel.SourceType.CAMERA);
                // Navigate to CameraFragment
                navController.navigate(R.id.action_fileSourceFragment_to_cameraFragment);
            } catch (Exception e) {
                // Error handling for navigation failure
                Toast.makeText(requireContext(), "Unable to navigate to Camera", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle file manager option click
        fileManagerOption.setOnClickListener(v -> {
            try {
                // Update source type in ViewModel
                coreViewModel.setSourceType(CoreViewModel.SourceType.FILE_MANAGER);
                // Navigate to FileManagerFragment
                navController.navigate(R.id.action_fileSourceFragment_to_fileManagerFragment);
            } catch (Exception e) {
                // Error handling for navigation failure
                Toast.makeText(requireContext(), "Unable to navigate to File Manager", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
