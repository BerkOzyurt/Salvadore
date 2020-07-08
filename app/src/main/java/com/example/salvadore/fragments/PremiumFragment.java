package com.example.salvadore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.salvadore.R;
import com.example.salvadore.activities.MapActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PremiumFragment extends Fragment implements View.OnClickListener {

    View rootView;

    @BindView(R.id.btn_buy)
    Button btn_buy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_premium, container, false);
        ButterKnife.bind(this,rootView);

        btn_buy.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), MapActivity.class);
        startActivity(intent);
    }
}