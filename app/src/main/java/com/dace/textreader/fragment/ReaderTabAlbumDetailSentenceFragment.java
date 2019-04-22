package com.dace.textreader.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dace.textreader.R;

public class ReaderTabAlbumDetailSentenceFragment extends Fragment {

    private View view;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reader_tab, container, false);

        initData();
        initView();
        loadTopData();
        loadItemData();;

        return view;
    }

    private void initData() {

    }

    private void initView() {

    }

    private void loadTopData() {

    }

    private void loadItemData() {
    }
}
