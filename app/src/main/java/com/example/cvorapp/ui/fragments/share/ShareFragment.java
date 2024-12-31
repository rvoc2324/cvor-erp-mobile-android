package com.example.cvorapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cvorapp.R;
import com.example.cvorapp.models.ShareHistory;
import com.example.cvorapp.viewmodels.CoreViewModel;
import com.example.cvorapp.viewmodels.WatermarkViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShareFragment extends Fragment {

    private CoreViewModel coreViewModel;
    private WatermarkViewModel watermarkViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);
        watermarkViewModel = new ViewModelProvider(requireActivity()).get(WatermarkViewModel.class);

        Button shareButton = view.findViewById(R.id.share_button);
        Button downloadButton = view.findViewById(R.id.download_button);

        shareButton.setOnClickListener(v -> shareFiles());
        downloadButton.setOnClickListener(v -> downloadFiles());
    }

    private void shareFiles() {
        List<Uri> processedFiles = coreViewModel.getProcessedFileUris().getValue();

        if (processedFiles == null || processedFiles.isEmpty()) {
            Toast.makeText(requireContext(), "No files to share", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent;
        if (processedFiles.size() == 1) {
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, processedFiles.get(0));
            shareIntent.setType("application/pdf"); // Assuming PDF files
        } else {
            shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> fileUris = new ArrayList<>(processedFiles);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
            shareIntent.setType("application/pdf");
        }

        // Add additional details from WatermarkViewModel
        String additionalDetails = watermarkViewModel.getAdditionalDetails();
        shareIntent.putExtra(Intent.EXTRA_TEXT, additionalDetails);

        startActivity(Intent.createChooser(shareIntent, "Share Files"));

        // Log share history
        logShareDetails(processedFiles);
    }

    private void downloadFiles() {
        // Placeholder for download logic
        // Check user settings if download is enabled
        boolean isDownloadEnabled = true; // Replace with actual settings check

        if (isDownloadEnabled) {
            List<Uri> processedFiles = coreViewModel.getProcessedFileUris().getValue();

            if (processedFiles != null && !processedFiles.isEmpty()) {
                // Simulate file download (actual implementation would involve file operations)
                Toast.makeText(requireContext(), "Files downloaded successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "No files to download", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logShareDetails(List<Uri> processedFiles) {
        for (Uri fileUri : processedFiles) {
            ShareHistory shareHistory = new ShareHistory(
                    fileUri.getLastPathSegment(),
                    new Date(),
                    "Share Medium Placeholder", // Actual medium can be derived if feasible
                    watermarkViewModel.getAdditionalDetails()
            );
            // Save shareHistory to database (Room or SharedPreferences)
            // Placeholder for persistence logic
        }

        Toast.makeText(requireContext(), "Share details logged", Toast.LENGTH_SHORT).show();

        // Navigate to "What's New" activity
        navigateToWhatsNew();
    }

    private void navigateToWhatsNew() {
        Intent intent = new Intent(requireContext(), WhatsNewActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
}
