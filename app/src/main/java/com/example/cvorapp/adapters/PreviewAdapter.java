package com.example.cvorapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cvorapp.R;

import java.util.List;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewViewHolder> {

    private final List<Uri> fileUris;
    private final Context context;

    public PreviewAdapter(List<Uri> fileUris, Context context) {
        this.fileUris = fileUris;
        this.context = context;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_preview, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        Uri uri = fileUris.get(position);

        if (uri.toString().endsWith(".pdf")) {
            // Display PDF icon or first page preview
            holder.filePreviewImageView.setImageResource(R.drawable.ic_pdf_preview);
        } else {
            // Display image thumbnail
            holder.filePreviewImageView.setImageURI(uri);
        }
    }

    @Override
    public int getItemCount() {
        return fileUris.size();
    }

    public static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView filePreviewImageView;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            filePreviewImageView = itemView.findViewById(R.id.file_preview_image_view);
        }
    }
}
