package com.example.cvorapp.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;

/**
 * CoreViewModel manages the state of the file and navigation arguments throughout the lifecycle of CoreActivity.
 */
public class CoreViewModel extends AndroidViewModel {

    public enum SourceType {
        CAMERA,
        FILE_MANAGER
    }

    private final MutableLiveData<SourceType> sourceType = new MutableLiveData<>(null);
    private final MutableLiveData<Uri> selectedFileUri = new MutableLiveData<>(null);
    private final MutableLiveData<File> processedFile = new MutableLiveData<>(null);
    private final MutableLiveData<String> actionType = new MutableLiveData<>("");

    // Navigation events
    private final MutableLiveData<String> navigationEvent = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> isActionCompleted = new MutableLiveData<>(false); // Tracks action completion (e.g., watermark added, PDF combined)

    public CoreViewModel(@NonNull Application application) {
        super(application);
    }

    // Action Type
    public void setActionType(String type) {
        actionType.setValue(type);
    }

    public LiveData<String> getActionType() {
        return actionType;
    }

    // Source Type
    public void setSourceType(SourceType type) {
        sourceType.setValue(type);
    }

    public LiveData<SourceType> getSourceType() {
        return sourceType;
    }

    // Selected File URI
    public void setSelectedFileUri(Uri uri) {
        selectedFileUri.setValue(uri);
    }

    public LiveData<Uri> getSelectedFileUri() {
        return selectedFileUri;
    }

    // Processed File
    public void setProcessedFile(File file) {
        processedFile.setValue(file);
    }

    public LiveData<File> getProcessedFile() {
        return processedFile;
    }

    // Navigation Events
    public void setNavigationEvent(String event) {
        navigationEvent.setValue(event);
    }

    public LiveData<String> getNavigationEvent() {
        return navigationEvent;
    }

    // Action Completion
    public void setActionCompleted(boolean completed) {
        isActionCompleted.setValue(completed);
    }

    public LiveData<Boolean> isActionCompleted() {
        return isActionCompleted;
    }

    // Utility Methods
    public boolean isActionTypeSet() {
        return actionType.getValue() != null && !actionType.getValue().isEmpty();
    }

    public boolean isSourceTypeSet() {
        return sourceType.getValue() != null;
    }

    // Clear State
    public void clearState() {
        sourceType.setValue(null);
        selectedFileUri.setValue(null);
        processedFile.setValue(null);
        navigationEvent.setValue(null);
        isActionCompleted.setValue(false);
    }
}
