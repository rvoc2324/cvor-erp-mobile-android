package com.example.cvorapp.services;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PDFHandlingService {

    // Combine multiple PDFs into one
    public File combinePDF(@NonNull List<Uri> inputFiles, @NonNull File outputFile, @NonNull Context context) throws Exception {
        PDFMergerUtility mergerUtility = new PDFMergerUtility();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (Uri uri : inputFiles) {
            PDDocument document = null;
            try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                document = PDDocument.load(inputStream);
                if (document.isEncrypted()) {
                    document = decryptPDF(document, context);
                }
                if (document != null) {
                    ByteArrayOutputStream decryptedStream = new ByteArrayOutputStream();
                    document.save(decryptedStream);
                    document.close();
                    mergerUtility.addSource(new ByteArrayInputStream(decryptedStream.toByteArray()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new IOException("Failed to process file: " + uri.toString());
            }
        }

        mergerUtility.setDestinationFileName(outputFile.getPath());
        mergerUtility.mergeDocuments(null);
        return outputFile;
    }

    // Convert images to a single PDF
    public File convertImagesToPDF(@NonNull List<Uri> imageUris, @NonNull File outputFile, @NonNull Context context) throws Exception {
        try (PDDocument document = new PDDocument()) {
            for (Uri uri : imageUris) {
                try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
                    // Convert the image to PDImageXObject
                    assert inputStream != null;
                    PDImageXObject pdImage = PDImageXObject.createFromByteArray(
                            document,
                            readStream(inputStream),
                            "image"
                    );

                    // Create a new page and add the image
                    PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
                    document.addPage(page);
                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IOException("Failed to process image: " + uri.toString());
                }
            }

            document.save(outputFile);
        }
        return outputFile;
    }

    // Decrypt a PDF document if it's password protected
    private PDDocument decryptPDF(@NonNull PDDocument document, @NonNull Context context) throws Exception {
        if (document.isEncrypted()) {
            String password = promptForPassword(context);
            if (password == null || password.isEmpty()) {
                throw new IOException("Password is required to decrypt the PDF.");
            }

            try {
                document = PDDocument.load(document, password);
                if (document.isEncrypted()) {
                    document.setAllSecurityToBeRemoved(true);
                }
            } catch (Exception e) {
                throw new IOException("Failed to decrypt PDF with the provided password.");
            }
        }
        return document;
    }

    // Prompt the user for a password
    private String promptForPassword(Context context) {
        // Use an Android AlertDialog to prompt for a password
        final String[] password = new String[1];
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Enter PDF Password");

        // Add an EditText to the dialog
        final android.widget.EditText input = new android.widget.EditText(context);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> password[0] = input.getText().toString());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

        return password[0];
    }

    // Helper to read InputStream into a byte array
    private byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return buffer.toByteArray();
    }
}
