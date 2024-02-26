package com.example.objectdetect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LabelerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LabelerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LabelerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LabelerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LabelerFragment newInstance(String param1, String param2) {
        LabelerFragment fragment = new LabelerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    ActivityResultLauncher<Intent> mCameraImage = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(), result -> {
                // Add same code that you want to add in onActivityResult method
                Log.d("Camera", "Camera opened");
            });

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        requireActivity().findViewById(R.id.app_title).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.app_explain).setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_labeler, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Button cameraButton = requireActivity().findViewById(R.id.labeler_camera_button);
        Button galleryButton = requireActivity().findViewById(R.id.labeler_gallery_button);
        cameraButton.setOnClickListener(view1 -> {
            mCameraImage.launch(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE));
        });
        galleryButton.setOnClickListener(view2 -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        requireActivity().findViewById(R.id.app_title).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.app_explain).setVisibility(View.VISIBLE);
    }

    public void openCamera(View view){

    }
}