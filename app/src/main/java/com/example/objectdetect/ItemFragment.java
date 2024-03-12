package com.example.objectdetect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    private ImageView imageView;


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


        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireActivity(), R.id.fragmentContainerView)
                        .navigate(R.id.action_itemFragment_to_buttonsFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.list);


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
                        recyclerView.setAdapter(new ItemRecyclerViewAdapter(labeledImages, requireActivity()));
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @SuppressLint("CheckResult")
                            @Override
                            public void onItemClick(View view, int position) {
                                RecyclerView.ViewHolder viewHolder = recyclerView
                                        .findViewHolderForAdapterPosition(position);
                                Bundle uriBundle = new Bundle();
                                ItemRecyclerViewAdapter adapter =
                                        (ItemRecyclerViewAdapter) recyclerView.getAdapter();
                                assert adapter != null;

                                uriBundle.putString("URI", adapter.getItem(position).uri.toString());
                                getParentFragmentManager().setFragmentResult("urikey", uriBundle);
                                Navigation
                                .findNavController(requireActivity(), R.id.fragmentContainerView)
                                        .navigate(R.id.action_itemFragment_to_zoomFragment);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage(R.string.delete_item_string).setPositiveButton(
                                        R.string.yes_string, (dialog, which) -> {
                                            ItemRecyclerViewAdapter adapter =
                                                    (ItemRecyclerViewAdapter) recyclerView.getAdapter();
                                            assert adapter != null;
                                            imagesDao.deleteImage(adapter.getItem(position))
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(new SingleObserver<Integer>() {
                                                        @Override
                                                        public void onSubscribe(Disposable d) {
                                                        }

                                                        @Override
                                                        public void onSuccess(@NonNull Integer integer) {
                                                            Log.d("DAO", "Deleted item");
                                                            Toast.makeText(view.getContext(),
                                                                    R.string.item_deleted_string,
                                                                    Toast.LENGTH_SHORT).show();
                                                            adapter.removeItem(position);
                                                            adapter.notifyItemRemoved(position);
                                                            adapter.notifyItemRangeChanged(position,
                                                                    adapter.getItemCount() - position);
                                                        }


                                                        @Override
                                                        public void onError(Throwable e) {
                                                        }
                                                    });
                                        }).setNegativeButton(R.string.no_string, (dialog, which) -> {
                                    dialog.cancel();
                                }).show();
                            }
                        })
        );


        EditText searchQueryText = view.findViewById(R.id.searchQuery);

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

                                recyclerView.setAdapter(new ItemRecyclerViewAdapter(labeledImages, requireActivity()));

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

        return view;
    }
}
