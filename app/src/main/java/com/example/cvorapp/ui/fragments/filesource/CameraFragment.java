package com.example.cvorapp.ui.fragments.filesource;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cvorapp.R;
import com.example.cvorapp.viewmodels.CoreViewModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";

    private Button buttonFlashToggle;
    private Button buttonConfirm;
    private Button buttonRetake;
    private ImageView imageViewPreview;

    private Uri photoUri;
    private File photoFile;
    private boolean isFlashOn = false;

    private CoreViewModel viewModel;

    // Launcher for capturing images
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (photoUri != null) {
                        imageViewPreview.setImageURI(photoUri);
                        buttonConfirm.setVisibility(View.VISIBLE);
                        buttonRetake.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(requireContext(), "Error: No image captured.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Camera canceled.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    // Launcher for permission requests
    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean cameraPermission = result.get(Manifest.permission.CAMERA);
                Boolean storagePermission = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (Boolean.TRUE.equals(cameraPermission) && Boolean.TRUE.equals(storagePermission)) {
                    launchCamera();
                } else {
                    Toast.makeText(requireContext(), "Camera and storage permissions are required.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);
        // SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);

        buttonFlashToggle = view.findViewById(R.id.buttonFlashToggle);
        Button buttonCapture = view.findViewById(R.id.buttonCapture);
        buttonConfirm = view.findViewById(R.id.buttonConfirm);
        buttonRetake = view.findViewById(R.id.buttonRetake);
        imageViewPreview = view.findViewById(R.id.imageViewPreview);

        buttonFlashToggle.setOnClickListener(v -> {
            isFlashOn = !isFlashOn;
            buttonFlashToggle.setText(isFlashOn ? "Flash ON" : "Flash OFF");
        });

        buttonCapture.setOnClickListener(v -> checkPermissionsAndLaunchCamera());

        buttonConfirm.setOnClickListener(v -> confirmImage());
        buttonRetake.setOnClickListener(v -> resetCaptureState());
    }

    private void checkPermissionsAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(requireContext(),
                            "com.example.cvorapp.fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                    // Pass flash mode as an Intent extra
                    cameraIntent.putExtra("android.intent.extras.FLASH_MODE", isFlashOn ? "on" : "off");

                    cameraLauncher.launch(cameraIntent);
                } else {
                    Toast.makeText(requireContext(), "Error creating file for the image.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error creating file for the image.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error creating image file", e);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (storageDir != null && !storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Failed to create directory");
        }

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void confirmImage() {
        if (photoUri != null) {
            boolean savePermanently = getSavePreference();

            Uri finalUri;
            if (savePermanently) {
                File permanentFile = saveImagePermanently(photoFile);
                if (permanentFile != null) {
                    finalUri = FileProvider.getUriForFile(requireContext(),
                            "com.example.cvorapp.fileprovider", permanentFile);
                } else {
                    Toast.makeText(requireContext(), "Error saving image.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                finalUri = photoUri;
            }

            viewModel.setSelectedFileUri(finalUri);
            Toast.makeText(requireContext(), "Image saved!", Toast.LENGTH_SHORT).show();
            Navigation.findNavController(requireView()).navigate(R.id.action_cameraFragment_to_watermarkFragment);
        } else {
            Toast.makeText(requireContext(), "No image to confirm.", Toast.LENGTH_SHORT).show();
        }
    }

    private File saveImagePermanently(File tempFile) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            return saveImageUsingMediaStore(tempFile);
        } else {
            return saveImageToExternalStorage(tempFile);
        }
    }

    private File saveImageUsingMediaStore(File tempFile) {
        try {
            String fileName = tempFile.getName();
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/CVORApp");

            Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri);
                     InputStream in = new FileInputStream(tempFile)) {
                    // Check if 'out' is null before proceeding
                    if (out == null) {
                        throw new IOException("Failed to open output stream.");
                    }

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
                return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CVORApp/" + fileName);
            } else {
                throw new IOException("Failed to create MediaStore entry.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving image using MediaStore", e);
            return null;
        }
    }


    private File saveImageToExternalStorage(File tempFile) {
        try {
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/CVORApp");
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                throw new IOException("Failed to create directory: " + storageDir.getAbsolutePath());
            }

            File permanentFile = new File(storageDir, tempFile.getName());
            try (InputStream in = new FileInputStream(tempFile);
                 OutputStream out = new FileOutputStream(permanentFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            return permanentFile;
        } catch (Exception e) {
            Log.e(TAG, "Error saving image to external storage", e);
            return null;
        }
    }

    private boolean getSavePreference() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        return prefs.getBoolean("save_images_permanently", true); // Adjust key as needed
    }

    private void resetCaptureState() {
        imageViewPreview.setImageURI(null);
        buttonConfirm.setVisibility(View.GONE);
        buttonRetake.setVisibility(View.GONE);
    }
}
