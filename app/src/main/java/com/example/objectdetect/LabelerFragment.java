package com.example.objectdetect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LabelerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LabelerFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

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
    public static LabelerFragment newInstance(String param1, String param2) {
        LabelerFragment fragment = new LabelerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Bitmap uriToBitmap(Uri selectedFileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    requireActivity().getContentResolver().openFileDescriptor(selectedFileUri, "r");
            assert parcelFileDescriptor != null;
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected void passImageToFragment(Bitmap image) {
        Bundle result = new Bundle();
        result.putParcelable("BitmapImage", image);
        getParentFragmentManager().setFragmentResult("requestKey", result);
        Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                .navigate(R.id.action_labelerFragment_to_imagePreviewFragment);
    }



    ActivityResultLauncher<Intent> mCameraImage = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(), result -> {
        Log.d("CAMERA", "Camera closed");
        if(result.getData()!=null) {
            Bitmap image = (Bitmap) Objects.requireNonNull(result.getData().getExtras()).get("data");
            passImageToFragment(image);
        }
        else {
            Log.d("CAMERA", "Null photo");
        }
    });

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    Log.d("PHOTOPICKER", "Selected URI: " + uri);

                    try {
                        ParcelFileDescriptor parcelFileDescriptor = requireActivity()
                                .getContentResolver().openFileDescriptor(uri, "r");
                        assert parcelFileDescriptor != null;
                        long filesize = parcelFileDescriptor.getStatSize();
                        Log.i("file size", String.valueOf(parcelFileDescriptor.getStatSize()));
                        if (filesize > 5000000) {
                            Toast.makeText(requireContext(), R.string.image_dimen_string,
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Bitmap image = uriToBitmap(uri);
                            // Load bitmap into cache
                            //loadBitmap(uri, image);

                            try {
                                ExifInterface exif = new ExifInterface(
                                        Objects.requireNonNull(this.requireContext().getContentResolver()
                                                .openInputStream(uri))
                                );
                                //TODO insert loading animation
                                TaskRunner taskRunner = new TaskRunner();
                                taskRunner.executeAsync(new MatrixCalculator(exif), (matrix) -> {

                                    assert image != null;
                                    Bitmap adjustedBitmap = Bitmap.createBitmap(image, 0, 0,
                                            image.getWidth(), image.getHeight(), matrix, true);

                                    passImageToFragment(adjustedBitmap);
                                });

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    } catch (Exception e) {
                        //escape logic here
                    }




                } else {
                    Log.d("PHOTOPICKER", "No media selected");
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




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
            Log.d("CAMERA", "Camera opened");
        });
        galleryButton.setOnClickListener(view2 -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            Log.d("PHOTOPICKER", "PhotoPicker Opened");
        });
    }

}