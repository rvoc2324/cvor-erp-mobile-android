package com.example.cvorapp.ui.fragments.filesource;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cvorapp.viewmodels.CoreViewModel;

/**
 * FileManagerFragment
 * Automatically launches a system file picker for the user to select a file.
 * On file selection, updates the CoreViewModel with the selected file's Uri.
 */
public class FileManagerFragment extends Fragment {

    private static final String TAG = "FileManagerFragment";

    private CoreViewModel coreViewModel;

    // Launchers for file picker and permission requests
    private ActivityResultLauncher<Intent> filePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the CoreViewModel
        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);

        // Register file picker result handler
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri fileUri = result.getData().getData();
                        if (fileUri != null) {
                            handleSelectedFile(fileUri);
                        } else {
                            Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "File selection cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

        // Register permission result handler
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchFilePicker();
                    } else {
                        Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Automatically launch the file picker
        launchFilePicker();
    }

    /**
     * Launches the system file picker based on the Android version and permissions.
     */
    private void launchFilePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use Photo Picker API for Android 14+
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("*/*");
            filePickerLauncher.launch(intent);
        } else if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Use traditional file picker for older versions
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            filePickerLauncher.launch(intent);
        } else {
            // Request permission for older versions
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    /**
     * Handles the selected file and updates the CoreViewModel.
     *
     * @param fileUri The Uri of the selected file.
     */
    private void handleSelectedFile(@NonNull Uri fileUri) {
        try {
            String fileName = getFileName(fileUri);
            coreViewModel.setSelectedFileUri(fileUri);
            // Toast.makeText(requireContext(), "File selected: " + fileName, Toast.LENGTH_SHORT).show();
            // requireActivity().getOnBackPressedDispatcher().onBackPressed(); // Navigate back after selection
        } catch (Exception e) {
            Log.e(TAG, "Error handling file: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Failed to process the selected file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Extracts the display name of the file from its Uri.
     *
     * @param uri The Uri of the file.
     * @return The file's name, or a placeholder if unavailable.
     */
    private String getFileName(@NonNull Uri uri) {
        String fileName = "Unknown";
        try (android.database.Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving file name: " + e.getMessage(), e);
        }
        return fileName;
    }
}
