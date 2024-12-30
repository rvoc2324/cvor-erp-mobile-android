package com.example.cvorapp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cvorapp.R;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    private List<Uri> fileUris;
    private final OnFileActionListener actionListener;

    public FileListAdapter(OnFileActionListener listener) {
        this.actionListener = listener;
    }

    public void submitList(List<Uri> uris) {
        this.fileUris = uris;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        Uri fileUri = fileUris.get(position);
        holder.fileNameTextView.setText(fileUri.getLastPathSegment());
        holder.fileTypeImageView.setImageResource(fileUri.toString().endsWith(".pdf") ? R.drawable.ic_pdf : R.drawable.ic_image);

        // Delete button action
        holder.deleteButton.setOnClickListener(v -> actionListener.onRemove(fileUri));
    }

    @Override
    public int getItemCount() {
        return fileUris != null ? fileUris.size() : 0;
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameTextView;
        ImageView fileTypeImageView;
        ImageView deleteButton;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameTextView = itemView.findViewById(R.id.file_name_text_view);
            fileTypeImageView = itemView.findViewById(R.id.file_type_image_view);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnFileActionListener {
        void onRemove(Uri uri);
    }
}
