package com.example.cvorapp.ui.fragments.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.cvorapp.models.ShareHistory;
import com.example.cvorapp.repositories.ShareHistoryRepository;
import com.example.cvorapp.ui.activities.whatsnew.WhatsNewActivity;
import com.example.cvorapp.viewmodels.CoreViewModel;
import com.example.cvorapp.viewmodels.WatermarkViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShareFragment extends Fragment {

    private CoreViewModel coreViewModel;
    private WatermarkViewModel watermarkViewModel;
    private ShareHistoryRepository shareHistoryRepository;

    // ActivityResultLauncher to replace startActivityForResult
    private final ActivityResultLauncher<Intent> shareLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    // Pass the intent to logShareDetails
                    Intent shareIntent = result.getData(); // Get the intent used for sharing
                    if (shareIntent != null) {
                        logShareDetails(shareIntent); // Pass the intent to the method
                    }
                    navigateToWhatsNew();
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);
        watermarkViewModel = new ViewModelProvider(requireActivity()).get(WatermarkViewModel.class);
        shareHistoryRepository = new ShareHistoryRepository(requireContext()); // Initialize repository

        // Automatically launch the share modal
        openNativeShareModal();
    }

    private void openNativeShareModal() {
        List<File> processedFiles = coreViewModel.getProcessedFiles().getValue();

        if (processedFiles == null || processedFiles.isEmpty()) {
            Toast.makeText(requireContext(), "No files to share", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent;

        if (processedFiles.size() == 1) {
            // Single file share
            File file = processedFiles.get(0);
            Uri fileUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    file
            );

            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.setType(getMimeType(file.getName())); // Get the MIME type dynamically
        } else {
            // Multiple file share
            ArrayList<Uri> fileUris = new ArrayList<>();
            for (File file : processedFiles) {
                Uri fileUri = FileProvider.getUriForFile(
                        requireContext(),
                        requireContext().getPackageName() + ".fileprovider",
                        file
                );
                fileUris.add(fileUri);
            }

            shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileUris);
            shareIntent.setType("*/*"); // Allow sharing multiple files of various types
        }

        // Grant URI permissions to external apps
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent = Intent.createChooser(shareIntent, "Share Files");
        shareLauncher.launch(shareIntent);
    }

    private void logShareDetails(Intent shareIntent) {
        List<File> processedFiles = coreViewModel.getProcessedFiles().getValue();
        if (processedFiles == null || processedFiles.isEmpty()) return;

        // Retrieve sharedWith and purpose from WatermarkViewModel
        String sharedWith = watermarkViewModel.getShareWith().getValue();
        if ((sharedWith == null) || sharedWith.isEmpty()) {
            sharedWith = "Unknown";
        }
        String purpose = watermarkViewModel.getPurpose().getValue();
        if (purpose == null || purpose.isEmpty()) {
            purpose = "General purpose";
        }

        // Extract the sharing app's package name
        String sharingApp = shareIntent.getComponent() != null
                ? shareIntent.getComponent().getPackageName()
                : "Unknown";

        // Capture share details and save to the repository
        for (File file : processedFiles) {
            ShareHistory shareHistory = new ShareHistory(
                    file.getName(),
                    new Date(),
                    "Shared with app: " + sharingApp,
                    sharedWith,
                    purpose
            );

            // Persist the share history
            shareHistoryRepository.insertShareHistory(shareHistory);
        }
    }

    private void navigateToWhatsNew() {
        Intent intent = new Intent(requireContext(), WhatsNewActivity.class);
        startActivity(intent);
        requireActivity().finish(); // End ShareFragment lifecycle
    }

    /**
     * Returns the MIME type based on the file's extension.
     *
     * @param fileName The name of the file.
     * @return The MIME type as a string.
     */
    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        // Fallback if MIME type is not found
        return mimeType != null ? mimeType : "application/octet-stream";
    }
}
