package com.example.objectdetect;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoomFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageView imageView;

    public ZoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ZoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ZoomFragment newInstance(String param1, String param2) {
        ZoomFragment fragment = new ZoomFragment();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_zoom, container, false);
        imageView = view.findViewById(R.id.zoomImageView);
        getParentFragmentManager().setFragmentResultListener("urikey",
                this, (requestKey, bundle) -> {
                    String result = bundle.getString("URI");
                    assert result != null;
                    Log.d("RESULT", result);
                    imageView.setImageURI(Uri.parse(result));
                });
        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    }

}