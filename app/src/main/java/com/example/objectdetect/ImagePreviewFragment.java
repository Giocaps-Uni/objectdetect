package com.example.objectdetect;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.slider.Slider;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;

public class ImagePreviewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imageView;
    private TextView chooseConf;
    private Bitmap result;
    private Slider confSlider;
    private Button launchButton, chooseAnother;
    private RecyclerView recView;
    private LinearLayout linearLayout;

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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        getParentFragmentManager().setFragmentResultListener("requestKey",
                this, (requestKey, bundle) -> {
            result = bundle.getParcelable("BitmapImage");
            assert result != null;
            //To use only for preview, post necessary to get width and height

            imageView.post(() -> {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(result, imageView.getWidth(),
                        imageView.getHeight());
                Glide.with(this).asBitmap().load(thumbnail).
                        diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

            });
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_image_preview, container, false);
        imageView = rootView.findViewById(R.id.imagePreview);
        chooseConf = rootView.findViewById(R.id.confidenceTextView);
        confSlider = rootView.findViewById(R.id.confidenceSlider);
        launchButton = rootView.findViewById(R.id.button_launch_labeler);
        chooseAnother = rootView.findViewById(R.id.button_choose_another);
        recView = rootView.findViewById(R.id.recyclerView);
        linearLayout = rootView.findViewById(R.id.linearLayout);
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
    //todo add buttons to save into database
    protected void showResults(List<ImageLabel> labels) {
        launchButton.setVisibility(View.GONE);
        chooseConf.setVisibility(View.GONE);
        confSlider.setVisibility(View.GONE);
        chooseAnother.setVisibility(View.GONE);
        imageView.getLayoutParams().height = 400;
        imageView.getLayoutParams().width = 400;
        TextView labelsTitle = new TextView(requireContext());
        labelsTitle.setText(R.string.labels_title);
        labelsTitle.setTextSize(24f);
        labelsTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(labelsTitle, 3);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recView.setLayoutManager(layoutManager);

        CustomAdapter adapter = new CustomAdapter(labels,requireContext());
        recView.setAdapter(adapter);
        recView.setVisibility(View.VISIBLE);
    }
    protected void launchLabeler() {
        InputImage image = InputImage.fromBitmap(result, 0);
        float confidence = confSlider.getValue();
        Log.d("CONFIDENCE", String.valueOf(confidence));
        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(confidence)
                        .build();
        ImageLabeler labeler = ImageLabeling.getClient(options);

        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    // Task completed successfully
                    for (ImageLabel label : labels) {
                        String text = label.getText();
                        Log.d("TEXT", text);
                    }
                    showResults(labels);

                }).addOnFailureListener(e -> Log.d("EXCEPTION", e.toString()));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseAnother.setOnClickListener(view1 -> {
            imageView.setImageDrawable(null);
            Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                    .navigate(R.id.action_imagePreviewFragment_to_labelerFragment);
        });

        launchButton.setOnClickListener(view2 -> {
            launchLabeler();
        });
    }

}


