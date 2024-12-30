package com.example.cvorapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cvorapp.R;
import com.example.cvorapp.adapters.FileListAdapter;
import com.example.cvorapp.viewmodels.CoreViewModel;

public class PdfHandlingFragment extends Fragment {
    private CoreViewModel coreViewModel;
    private FileListAdapter fileListAdapter;
    private Button actionButton;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pdf_handling, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        coreViewModel = new ViewModelProvider(requireActivity()).get(CoreViewModel.class);

        RecyclerView fileRecyclerView = view.findViewById(R.id.recycler_view_files);
        actionButton = view.findViewById(R.id.action_button);
        progressBar = view.findViewById(R.id.progress_bar);

        // Setup RecyclerView
        fileRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        fileListAdapter = new FileListAdapter(coreViewModel::removeSelectedFileUri);
        fileRecyclerView.setAdapter(fileListAdapter);

        // Observe selected files
        coreViewModel.getSelectedFileUris().observe(getViewLifecycleOwner(), uris -> fileListAdapter.submitList(uris));

        // Enable drag-and-drop and reordering
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                coreViewModel.reorderSelectedFileUris(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No swipe actions
            }
        });
        itemTouchHelper.attachToRecyclerView(fileRecyclerView);

        // Observe action type and update button label
        coreViewModel.getActionType().observe(getViewLifecycleOwner(), actionType -> {
            if ("CombinePDF".equals(actionType)) {
                actionButton.setText(R.string.combine_pdf);
            } else if ("ConvertToPDF".equals(actionType)) {
                actionButton.setText(R.string.convert_to_pdf);
            }
        });

        // Handle action button click
        actionButton.setOnClickListener(v -> processFiles());
    }

    private void processFiles() {
        progressBar.setVisibility(View.VISIBLE);
        actionButton.setEnabled(false);

        coreViewModel.processSelectedFiles(result -> {
            progressBar.setVisibility(View.GONE);
            actionButton.setEnabled(true);

            if (result.isSuccess()) {
                coreViewModel.addProcessedFile(result.getFile());
                coreViewModel.setNavigationEvent("navigate_to_preview");
                // Navigate to PreviewFragment
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new PreviewFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(requireContext(), result.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

