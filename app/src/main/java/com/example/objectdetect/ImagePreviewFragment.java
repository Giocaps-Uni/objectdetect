package com.example.objectdetect;

import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.File;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ImagePreviewFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_imagePreviewFragment_to_buttonsFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        getParentFragmentManager().setFragmentResultListener("requestKey",
                this, (requestKey, bundle) -> {
            result = bundle.getParcelable("BitmapImage");
            assert result != null;
            //To use only for preview, post necessary to get width and height
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(result, (int) getResources().getDimension(R.dimen.thumbnail_dimen), (int) getResources().getDimension(R.dimen.thumbnail_dimen));
            Glide.with(this).asBitmap().load(thumbnail).
                    diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
            /*imageView.post(() -> {
                Bitmap thumbnail = ThumbnailUtils.extractThumbnail(result, imageView.getWidth(),
                        imageView.getHeight());
                Glide.with(this).asBitmap().load(thumbnail).
                        diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);

            });*/
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
        /* Dynamical calculation of imageview size based on screen size
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
        */
        return rootView;
    }

    protected void goToLabelerFragment() {
        imageView.setImageDrawable(null);
        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_imagePreviewFragment_to_labelerFragment);
    }

    protected void saveToDBAndFileSystem(List<ImageLabel> labels, float confidence){
        ContextWrapper cw = new ContextWrapper(requireActivity().getApplicationContext());
        // Save image in app specific folder
        String filename = String.format(Locale.ITALY, "%d.jpg", System.currentTimeMillis());


        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new FileSaver(filename, cw, result), (Null) -> {

        });

        Uri uri = Uri.fromFile(new File(cw.getFilesDir(), filename));
        MainActivity mainActivity = (MainActivity) requireActivity();

        mainActivity.loadBitmap(uri, result);

        ImagesDatabase imagesDB = mainActivity.imagesDB;
        ImagesDao imagesDao = imagesDB.imagesDao();
        imagesDao.insertImage(new LabeledImage(uri, labels, confidence))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onSuccess(Long aLong) {
                        Log.d("INSERT", "INSERT COMPLETED");
                        Snackbar.make(requireView().findViewById(R.id.gotodb_button_id),
                                R.string.snackbar_insert, Snackbar.LENGTH_LONG)
                                .setAction(R.string.action_text, v -> Navigation.findNavController(requireActivity(),
                                    R.id.fragmentContainerView).navigate(
                                            R.id.action_imagePreviewFragment_to_itemFragment)).show();
                    }
                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    protected void injectButton(boolean isSaveButton, List<ImageLabel> labels, float confidence) {
        Button button = new Button(requireContext());
        button.setTextColor(getResources().getColor(R.color.white, null));
        button.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.buttons_style));
        button.setTextSize(18);
        button.setHeight(50);
        button.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        if (isSaveButton) {
            button.setText(R.string.save_to_db);
            button.setOnClickListener(v1 -> saveToDBAndFileSystem(labels, confidence));
            button.setId(R.id.gotodb_button_id);
        }
        else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 20, 0, 0);
            button.setLayoutParams(params);
            button.setText(R.string.choose_another);
            button.setOnClickListener(v2 -> goToLabelerFragment());
        }
        linearLayout.addView(button);
    }
    protected void showResults(List<ImageLabel> labels, float confidence) {
        launchButton.setVisibility(View.GONE);
        chooseConf.setVisibility(View.GONE);
        confSlider.setVisibility(View.GONE);
        chooseAnother.setVisibility(View.GONE);
        imageView.getLayoutParams().height = 400;
        imageView.getLayoutParams().width = 400;
        TextView labelsTitle = new TextView(requireContext());
        if (labels.isEmpty())
            labelsTitle.setText(R.string.no_labels_found);
        else
            labelsTitle.setText(R.string.labels_title);
        labelsTitle.setTextSize(24f);
        labelsTitle.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(labelsTitle, 3);


        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recView.setLayoutManager(layoutManager);
        CustomAdapter adapter = new CustomAdapter(labels,requireContext());
        recView.setAdapter(adapter);
        recView.setVisibility(View.VISIBLE);

        injectButton(true, labels, confidence);
        injectButton(false, labels, confidence);


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
                    showResults(labels, confidence);
                }).addOnFailureListener(e -> Log.d("EXCEPTION", e.toString()));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chooseAnother.setOnClickListener(view1 -> goToLabelerFragment());

        launchButton.setOnClickListener(view2 -> launchLabeler());
    }

}


