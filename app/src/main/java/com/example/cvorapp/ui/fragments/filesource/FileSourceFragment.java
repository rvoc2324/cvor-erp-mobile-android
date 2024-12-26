package com.example.cvorapp.ui.fragments.filesource;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.cvorapp.R;
import com.example.cvorapp.viewmodels.CoreViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * FileSourceFragment serves as the modal bottom sheet to choose file source (Camera or File Manager).
 */
public class FileSourceFragment extends BottomSheetDialogFragment {

    private LinearLayout cameraOption; // Option to select Camera
    private LinearLayout fileManagerOption; // Option to select File Manager
    private CoreViewModel coreViewModel; // Shared ViewModel to store the selected source type

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_source, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);

        // Initialize the UI components
        cameraOption = view.findViewById(R.id.option_camera);
        fileManagerOption = view.findViewById(R.id.option_file_manager);

        // Setup listeners for both options
        setupListeners();
    }

    /**
     * Set up listeners for both options to select Camera or File Manager.
     */
    private void setupListeners() {
        // Handle camera option selection
        cameraOption.setOnClickListener(v -> {
            // Update the ViewModel with the selected source type
            coreViewModel.setSourceType(CoreViewModel.SourceType.CAMERA);

            // Dismiss the bottom sheet after selection
            dismiss();

            // Trigger navigation to CameraFragment (handled in CoreActivity or other observer)
            navigateToSelectedSource();
        });

        // Handle file manager option selection
        fileManagerOption.setOnClickListener(v -> {
            // Update the ViewModel with the selected source type
            coreViewModel.setSourceType(CoreViewModel.SourceType.FILE_MANAGER);

            // Dismiss the bottom sheet after selection
            dismiss();

            // Trigger navigation to FileManagerFragment (handled in CoreActivity or other observer)
            navigateToSelectedSource();
        });
    }

    /**
     * Navigate to the appropriate fragment based on the selected source type.
     */
    private void navigateToSelectedSource() {
        CoreViewModel.SourceType selectedSource = coreViewModel.getSourceType().getValue();
        if (selectedSource != null) {
            // Handle navigation logic based on selected source
            if (selectedSource == CoreViewModel.SourceType.CAMERA) {
                // Navigate to CameraFragment (or any specific fragment)
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_core, new CameraFragment())
                        .addToBackStack(null)
                        .commit();
            } else if (selectedSource == CoreViewModel.SourceType.FILE_MANAGER) {
                // Navigate to FileManagerFragment (or any specific fragment)
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment_core, new FileManagerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        }
    }
}
