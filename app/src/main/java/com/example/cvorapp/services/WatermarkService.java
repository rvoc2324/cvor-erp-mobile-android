package com.example.cvorapp.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;

import com.example.cvorapp.models.WatermarkOptions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class WatermarkService {

    private static final String TAG = "WatermarkService";

    public File applyWatermark(Uri inputFile, WatermarkOptions options, Context context) throws IOException {
        try {
            String watermarkText = options.generateWatermarkText();
            InputStream inputStream = context.getContentResolver().openInputStream(inputFile);
            if (inputStream == null) {
                throw new IOException("Failed to open input file.");
            }

            // Check file type (image or PDF)
            String fileType = context.getContentResolver().getType(inputFile);
            if (fileType != null && fileType.startsWith("image/")) {
                return watermarkImage(inputStream, watermarkText, context);
            } else if (fileType != null && fileType.equals("application/pdf")) {
                return watermarkPdf(inputStream, watermarkText, context);
            } else {
                throw new IOException("Unsupported file type.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying watermark: " + e.getMessage(), e);
            throw new IOException("Error applying watermark.", e);
        }
    }

    private File watermarkImage(InputStream inputStream, String watermarkText, Context context) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        Bitmap watermarkedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(watermarkedBitmap);
        Paint paint = new Paint();
        paint.setColor(0x80000000); // Semi-transparent black
        paint.setTextSize(48);
        paint.setAntiAlias(true);

        float angle = -45; // Rotate watermark text at -45 degrees
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);

        float textWidth = paint.measureText(watermarkText);
        float textHeight = paint.descent() - paint.ascent();

        int stepX = (int) textWidth + 50;
        int stepY = (int) textHeight + 50;

        for (int y = -stepY; y < canvas.getHeight(); y += stepY) {
            for (int x = -stepX; x < canvas.getWidth(); x += stepX) {
                canvas.save();
                canvas.rotate(angle, x, y);
                canvas.drawText(watermarkText, x, y, paint);
                canvas.restore();
            }
        }

        File watermarkedFile = new File(context.getCacheDir(), "watermarked_image.png");
        try (FileOutputStream out = new FileOutputStream(watermarkedFile)) {
            watermarkedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        return watermarkedFile;
    }

    private File watermarkPdf(InputStream inputStream, String watermarkText, Context context) throws IOException {
        File watermarkedFile = new File(context.getCacheDir(), "watermarked_document.pdf");
        try (PDDocument document = PDDocument.load(inputStream)) {
            for (PDPage page : document.getPages()) {
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    contentStream.setNonStrokingColor(0, 0, 0, 128); // Semi-transparent black
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 48);

                    float pageWidth = page.getMediaBox().getWidth();
                    float pageHeight = page.getMediaBox().getHeight();

                    float textWidth = contentStream.getFont().getStringWidth(watermarkText) / 1000 * 48;
                    float textHeight = 48;

                    int stepX = (int) textWidth + 50;
                    int stepY = (int) textHeight + 50;

                    contentStream.beginText();
                    for (float y = -stepY; y < pageHeight + stepY; y += stepY) {
                        for (float x = -stepX; x < pageWidth + stepX; x += stepX) {
                            contentStream.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(-45), x, y));
                            contentStream.showText(watermarkText);
                        }
                    }
                    contentStream.endText();
                }
            }
            document.save(watermarkedFile);
        }
        return watermarkedFile;
    }
}
