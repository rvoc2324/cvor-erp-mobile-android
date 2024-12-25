package com.example.cvorapp.ui.fragments.filesource;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cvorapp.R;
import com.example.cvorapp.viewmodels.CoreViewModel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";

    private Preview preview;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private Button buttonFlashToggle;
    private Button buttonConfirm;
    private Button buttonRetake;
    private ImageButton buttonCapture;

    private CoreViewModel viewModel;
    private boolean isFlashOn = false;
    private Uri photoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);
        buttonFlashToggle = view.findViewById(R.id.buttonFlashToggle);
        buttonCapture = view.findViewById(R.id.buttonCapture);
        buttonConfirm = view.findViewById(R.id.buttonConfirm);
        buttonRetake = view.findViewById(R.id.buttonRetake);

        buttonFlashToggle.setOnClickListener(v -> toggleFlash());
        buttonCapture.setOnClickListener(v -> captureImage());
        buttonConfirm.setOnClickListener(v -> confirmImage());
        buttonRetake.setOnClickListener(v -> resetCaptureState());

        cameraExecutor = Executors.newSingleThreadExecutor();

        startCamera();
    }

    private void startCamera() {
        ProcessCameraProvider cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview setup
                preview = new Preview.Builder().build();

                // ImageCapture setup
                imageCapture = new ImageCapture.Builder()
                        .setFlashMode(isFlashOn ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Bind use cases to lifecycle
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageCapture);

                preview.setSurfaceProvider(((androidx.camera.view.PreviewView) requireView().findViewById(R.id.previewView)).getSurfaceProvider());

            } catch (Exception e) {
                Log.e(TAG, "Use case binding failed", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void toggleFlash() {
        isFlashOn = !isFlashOn;
        buttonFlashToggle.setText(isFlashOn ? "Flash ON" : "Flash OFF");
        if (imageCapture != null) {
            imageCapture.setFlashMode(isFlashOn ? ImageCapture.FLASH_MODE_ON : ImageCapture.FLASH_MODE_OFF);
        }
    }

    private void captureImage() {
        if (imageCapture == null) {
            Toast.makeText(requireContext(), "Image capture not ready", Toast.LENGTH_SHORT).show();
            return;
        }

        File photoFile = new File(requireContext().getExternalFilesDir(null),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()) + ".jpg");

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                photoUri = Uri.fromFile(photoFile);
                buttonConfirm.setVisibility(View.VISIBLE);
                buttonRetake.setVisibility(View.VISIBLE);
                Toast.makeText(requireContext(), "Image captured!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Image capture failed: " + exception.getMessage());
            }
        });
    }

    private void confirmImage() {
        if (photoUri != null) {
            viewModel.setSelectedFileUri(photoUri);
            Navigation.findNavController(requireView()).navigate(R.id.action_cameraFragment_to_watermarkFragment);
        } else {
            Toast.makeText(requireContext(), "No image to confirm", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetCaptureState() {
        photoUri = null;
        buttonConfirm.setVisibility(View.GONE);
        buttonRetake.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraExecutor.shutdown();
    }
}
