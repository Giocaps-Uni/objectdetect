package com.example.objectdetect.adapters;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.objectdetect.MainActivity;
import com.example.objectdetect.R;
import com.example.objectdetect.database.LabeledImage;
import com.example.objectdetect.fragments.ItemFragment;
import com.google.mlkit.vision.label.ImageLabel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter used to inflate the recyclerview of {@link ItemFragment} with data retrieved from the db
 *
 */
public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {

    private final List<LabeledImage> mValues;
    private final Context context;

    public ItemRecyclerViewAdapter(List<LabeledImage> items, Context context) {
        this.mValues = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_row_item,
                parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Uri uri = mValues.get(position).uri;
        Log.d("HOLDERURI", mValues.get(position).uri.toString());
        // Try to load the image from cache, if no entry is found load from disk
        final Bitmap bitmap = ((MainActivity) context).getBitmapFromMemCache(uri.toString());
        if (bitmap!=null)
            Glide.with(holder.image.getContext()).asBitmap().load(bitmap)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);
        else
            Glide.with(holder.image.getContext()).asBitmap().load(uri)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);
        List<ImageLabel> imageLabels = mValues.get(position).labels;
        // Calls getText on every ImageLabel element and joins the final string with a delimiter
        if (imageLabels.isEmpty())
            holder.labels.setText(R.string.no_labels_found);
        else
            holder.labels.setText(mValues.get(position).labels.stream().map(ImageLabel::getText)
                .collect(Collectors.joining(",")));
        holder.confidence.setText(String.valueOf(mValues.get(position).confidence));
        holder.bind(mValues.get(position).id);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public LabeledImage getItem(int position) {
        return mValues.get(position);
    }

    public void removeItem(int position) {
        mValues.remove(position);
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

        //Experimental
        public void bind(int id) {
            ViewCompat.setTransitionName(image, String.valueOf(id));

        }

    }
}