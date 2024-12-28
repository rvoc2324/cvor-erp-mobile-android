package com.example.cvorapp.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents the options for watermarking, including organization name, purpose, and generated watermark text.
 */
public class WatermarkOptions {

    private final String shareWith;
    private final String purpose;
    private final String date;

    /**
     * Constructor to initialize share with, purpose, and current date.
     *
     * @param shareWith Mandatory share with name.
     * @param purpose Optional purpose for watermarking.
     */
    public WatermarkOptions(String shareWith, String purpose) {
        this.shareWith = shareWith;
        this.purpose = purpose != null ? purpose : "";
        this.date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    public String getShareWith() {
        return shareWith;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getDate() {
        return date;
    }

    /**
     * Generates the final watermark text dynamically.
     *
     * @return The formatted watermark text.
     */
    public String generateWatermarkText() {
        return "Shared with " + shareWith + " for " + (purpose.isEmpty() ? "general purposes" : purpose) +
                " on " + date;
    }
}
