package com.example.objectdetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.objectdetect.R;
import com.example.objectdetect.fragments.ImagePreviewFragment;
import com.google.mlkit.vision.label.ImageLabel;

import java.util.List;

/**
 * Adapter used for the label list recyclerview in {@link ImagePreviewFragment }
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    List<ImageLabel> labelList;

    public CustomAdapter(List<ImageLabel> labelList, Context context) {
        this.labelList = labelList;
    }

    @NonNull
    @Override
    public CustomAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.text_row_item,parent,
                false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.text.setText(labelList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder{

        protected TextView text;

        public CustomViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_id);
        }
    }
}