package com.example.cvorapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.cvorapp.models.WatermarkOptions;

/**
 * ViewModel for managing state and logic in WatermarkFragment.
 */
public class WatermarkViewModel extends AndroidViewModel {

    private final MutableLiveData<String> shareWith = new MutableLiveData<>();
    private final MutableLiveData<String> purpose = new MutableLiveData<>();
    private final MutableLiveData<String> generatedWatermarkText = new MutableLiveData<>();

    private WatermarkOptions options;

    public WatermarkViewModel(@NonNull Application application) {
        super(application);
    }

    public void setInputs(String organizationName, String purpose) {
        this.shareWith.setValue(organizationName);
        this.purpose.setValue(purpose);
        this.options = new WatermarkOptions(organizationName, purpose);
        generatedWatermarkText.setValue(options.generateWatermarkText());
    }

    public LiveData<String> getGeneratedWatermarkText() {
        return generatedWatermarkText;
    }

    public WatermarkOptions getOptions() {
        return options;
    }
}
