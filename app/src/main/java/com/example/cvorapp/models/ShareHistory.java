package com.example.cvorapp.models;

import java.util.Date;

public class ShareHistory {
    private String fileName;
    private Date sharedDate;
    private String shareMedium;
    private String additionalDetails;

    public ShareHistory(String fileName, Date sharedDate, String shareMedium, String additionalDetails) {
        this.fileName = fileName;
        this.sharedDate = sharedDate;
        this.shareMedium = shareMedium;
        this.additionalDetails = additionalDetails;
    }

    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Date getSharedDate() {
        return sharedDate;
    }

    public void setSharedDate(Date sharedDate) {
        this.sharedDate = sharedDate;
    }

    public String getShareMedium() {
        return shareMedium;
    }

    public void setShareMedium(String shareMedium) {
        this.shareMedium = shareMedium;
    }

    public String getAdditionalDetails() {
        return additionalDetails;
    }

    public void setAdditionalDetails(String additionalDetails) {
        this.additionalDetails = additionalDetails;
    }
}
