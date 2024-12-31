package com.example.cvorapp.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cvorapp.R;

import java.util.List;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.PreviewViewHolder> {

    private final List<Bitmap> pageBitmaps;

    // Constructor
    public PreviewAdapter(List<Bitmap> pageBitmaps) {
        this.pageBitmaps = pageBitmaps;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preview_page, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        Bitmap pageBitmap = pageBitmaps.get(position);

        // Bind the bitmap to the ImageView using efficient memory management
        holder.pageImageView.setImageBitmap(pageBitmap);
    }

    @Override
    public int getItemCount() {
        return pageBitmaps.size();
    }

    // ViewHolder class for managing each preview page
    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView pageImageView;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            pageImageView = itemView.findViewById(R.id.preview_image_view);
        }
    }
}
