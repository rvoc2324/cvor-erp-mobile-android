package com.example.cvorapp.ui.fragments.watermark;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.cvorapp.R;
import com.example.cvorapp.services.WatermarkService;
import com.example.cvorapp.viewmodels.CoreViewModel;
import com.example.cvorapp.viewmodels.WatermarkViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.Objects;

public class WatermarkFragment extends Fragment {

    private WatermarkViewModel watermarkViewModel;
    private CoreViewModel coreViewModel;

    private TextInputEditText shareWithInput, purposeInput;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_watermark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModels
        watermarkViewModel = new ViewModelProvider(this).get(WatermarkViewModel.class);
        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);

        // Bind UI components
        shareWithInput = view.findViewById(R.id.input_sharing_with);
        purposeInput = view.findViewById(R.id.input_purpose);

        // Observe real-time watermark text generation
        watermarkViewModel.getGeneratedWatermarkText().observe(getViewLifecycleOwner(), watermarkText ->
                Toast.makeText(requireContext(), "Watermark: " + watermarkText, Toast.LENGTH_SHORT).show()
        );

        // Handle "Preview" button click
        view.findViewById(R.id.button_preview).setOnClickListener(v -> {
            String shareWith = Objects.requireNonNull(shareWithInput.getText()).toString().trim();
            String purpose = Objects.requireNonNull(purposeInput.getText()).toString().trim();

            if (shareWith.isEmpty()) {
                Toast.makeText(requireContext(), "Share with is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            watermarkViewModel.setInputs(shareWith, purpose);
            Uri selectedFileUri = coreViewModel.getSelectedFileUri().getValue();

            if (selectedFileUri == null) {
                Toast.makeText(requireContext(), "No file selected.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Perform watermarking
            WatermarkService watermarkService = new WatermarkService();
            try {
                File watermarkedFile = watermarkService.applyWatermark(selectedFileUri, watermarkViewModel.getOptions(), requireContext());
                coreViewModel.setProcessedFile(watermarkedFile);

                // Navigate to PreviewFragment
                Navigation.findNavController(v).navigate(R.id.action_watermarkFragment_to_previewFragment);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error applying watermark.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
