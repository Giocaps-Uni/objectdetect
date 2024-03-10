package com.example.objectdetect;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);




        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;


            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            //todo add loading animation

            MainActivity mainActivity = (MainActivity) requireActivity();
            ImagesDatabase imagesDB = mainActivity.imagesDB;
            ImagesDao imagesDao = imagesDB.imagesDao();
            imagesDao.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<List<LabeledImage>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(List<LabeledImage> labeledImages) {
                            Log.d("DAO", "Retrieved database elements");
                            recyclerView.setAdapter(new ItemRecyclerViewAdapter(labeledImages));
                        }

                        @Override
                        public void onError(Throwable e) {
                        }
                    });

            EditText searchQueryText = requireActivity().findViewById(R.id.searchQuery);

            searchQueryText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                    String query = String.valueOf(searchQueryText.getText());
                    Log.d("QUERY", query);
                    imagesDao.getFilteredList(query)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<List<LabeledImage>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                }

                                @Override
                                public void onSuccess(List<LabeledImage> labeledImages) {
                                    Log.d("DAO", labeledImages.toString());

                                    recyclerView.setAdapter(new ItemRecyclerViewAdapter(labeledImages));

                                }

                                @Override
                                public void onError(Throwable e) {
                                }
                            });

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }
        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Workaround to simulate fragment fullscreen -> set textview non visible, fragment jumps up
        requireActivity().findViewById(R.id.app_title).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.app_explain).setVisibility(View.GONE);
        requireActivity().findViewById(R.id.database_layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Workaround to simulate fragment fullscreen -> set textview visible again
        requireActivity().findViewById(R.id.app_title).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.app_explain).setVisibility(View.VISIBLE);
        requireActivity().findViewById(R.id.database_layout).setVisibility(View.GONE);
    }
}
