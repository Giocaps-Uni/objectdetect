package com.example.objectdetect;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Objects;


public class ImagePreviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imageView;

    public ImagePreviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ImagePreviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImagePreviewFragment newInstance(String param1, String param2) {
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Bitmap to4BytesPerPixelBitmap(@NonNull final Bitmap input){
        final Bitmap bitmap = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        // Instantiate the canvas to draw on:
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(input, 0, 0, null);
        // Return the new bitmap:
        return bitmap;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            Bitmap result = bundle.getParcelable("BitmapImage");
            assert result != null;
            //To use only for preview, post necessary to get width and height
            //TODO Check orientation of images
            imageView.post(() -> {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(result, imageView.getWidth(),
                        imageView.getHeight());
                imageView.setImageBitmap(thumbnail);
            });


        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_image_preview, container, false);
        imageView = (ImageView) rootView.findViewById(R.id.imagePreview);

        // Dynamical calculation of imageview size based on screen size
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        params.width = (int) (0.85*screenWidth);
        params.height = (int) (0.4*screenHeight);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.topMargin = 50;
        imageView.setLayoutParams(params);

        return rootView;
    }
}