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

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;


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

    private LruCache<String, Bitmap> memoryCache;

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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    class BitmapLoaderTask implements Callable<Void> {

        private final String urikey;
        private final Bitmap bitmap;

        public BitmapLoaderTask(String input, Bitmap bitmapInput) {
            this.urikey = input;
            this.bitmap = bitmapInput;
        }

        @Override
        public Void call(){
            addBitmapToMemoryCache(urikey, bitmap);
            return null;
        }
    }

    public void loadBitmap(Uri uri, Bitmap image) {
        final String imageKey = uri.toString();
        TaskRunner taskRunner = new TaskRunner();
        taskRunner.executeAsync(new BitmapLoaderTask(imageKey, image), (Null) -> {
            Log.d("CACHING", String.valueOf(memoryCache.size()));
        });

    }

    ActivityResultLauncher<Intent> mCameraImage = registerForActivityResult(new
                    ActivityResultContracts.StartActivityForResult(), result -> {
                // Add same code that you want to add in onActivityResult method
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

                    Bitmap image = uriToBitmap(uri);
                    // Load bitmap into cache
                    loadBitmap(uri, image);

                    try {
                        ExifInterface exif = new ExifInterface(
                                Objects.requireNonNull(this.requireContext().getContentResolver()
                                        .openInputStream(uri))
                        );

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


                    //passImageToFragment(image);
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
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };


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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Workaround to simulate fragment fullscreen -> set textview non visible, fragment jumps up
        requireActivity().findViewById(R.id.app_title).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.app_explain).setVisibility(View.GONE);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        // Workaround to simulate fragment fullscreen -> set textview visible again
        requireActivity().findViewById(R.id.app_title).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.app_explain).setVisibility(View.VISIBLE);
    }


}