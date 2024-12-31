package com.example.cvorapp.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cvorapp.R;
import com.example.cvorapp.adapters.PreviewAdapter;
import com.example.cvorapp.viewmodels.CoreViewModel;

import java.util.List;

public class PreviewFragment extends Fragment {

    private CoreViewModel coreViewModel;
    private PreviewAdapter previewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);

        RecyclerView previewRecyclerView = view.findViewById(R.id.preview_recycler_view);
        Button backButton = view.findViewById(R.id.back_button);
        Button shareButton = view.findViewById(R.id.share_button);

        previewRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Observe the processed file's pages and update UI
        coreViewModel.getProcessedFilePages().observe(getViewLifecycleOwner(), new Observer<List<Bitmap>>() {
            @Override
            public void onChanged(List<Bitmap> bitmaps) {
                previewAdapter = new PreviewAdapter(bitmaps);
                previewRecyclerView.setAdapter(previewAdapter);
            }
        });

        // Back button navigation
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Share button navigation
        shareButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_previewFragment_to_shareFragment);
        });
    }
}
