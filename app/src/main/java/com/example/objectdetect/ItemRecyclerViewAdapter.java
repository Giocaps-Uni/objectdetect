package com.example.objectdetect;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.objectdetect.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.objectdetect.databinding.FragmentItemBinding;
import com.google.mlkit.vision.label.ImageLabel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private final List<LabeledImage> mValues;

    public ItemRecyclerViewAdapter(List<LabeledImage> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_row_item,
                parent, false);
        return new ItemRecyclerViewAdapter.ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.image.setImageURI(mValues.get(position).uri);
        holder.labels.setText(mValues.get(position).labels.stream().map(ImageLabel::getText)
                .collect(Collectors.joining(",")));
        holder.confidence.setText(String.valueOf(mValues.get(position).confidence));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView image;
        public final TextView labels;
        public final TextView confidence;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.dbImage);
            labels = itemView.findViewById(R.id.labelsText);
            confidence = itemView.findViewById(R.id.dbconfidenceTextView);
        }

    }
}