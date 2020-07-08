package com.example.salvadore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.salvadore.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PrivacyPolicyFragment extends BaseFragmentV2 implements View.OnClickListener {

    View rootView;

    @BindView(R.id.frame1)
    FrameLayout frame1;
    @BindView(R.id.frame2)
    FrameLayout frame2;
    @BindView(R.id.frame3)
    FrameLayout frame3;
    @BindView(R.id.frame4)
    FrameLayout frame4;
    @BindView(R.id.frame5)
    FrameLayout frame5;
    @BindView(R.id.frame6)
    FrameLayout frame6;
    @BindView(R.id.frame7)
    FrameLayout frame7;
    @BindView(R.id.frame8)
    FrameLayout frame8;
    @BindView(R.id.frame9)
    FrameLayout frame9;
    @BindView(R.id.frame10)
    FrameLayout frame10;
    @BindView(R.id.frame11)
    FrameLayout frame11;
    @BindView(R.id.frame12)
    FrameLayout frame12;

    @BindView(R.id.textView1)
    TextView textView1;
    @BindView(R.id.textView2)
    TextView textView2;
    @BindView(R.id.textView3)
    TextView textView3;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.textView6)
    TextView textView6;
    @BindView(R.id.textView7)
    TextView textView7;
    @BindView(R.id.textView8)
    TextView textView8;
    @BindView(R.id.textView9)
    TextView textView9;
    @BindView(R.id.textView10)
    TextView textView10;
    @BindView(R.id.textView11)
    TextView textView11;
    @BindView(R.id.textView12)
    TextView textView12;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        ButterKnife.bind(this,rootView);

        frame1.setOnClickListener(this);
        frame2.setOnClickListener(this);
        frame3.setOnClickListener(this);
        frame4.setOnClickListener(this);
        frame5.setOnClickListener(this);
        frame6.setOnClickListener(this);
        frame7.setOnClickListener(this);
        frame8.setOnClickListener(this);
        frame9.setOnClickListener(this);
        frame10.setOnClickListener(this);
        frame11.setOnClickListener(this);
        frame12.setOnClickListener(this);

        return rootView;
    }

    public void changeVisibility(){
        textView1.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.GONE);
        textView4.setVisibility(View.GONE);
        textView5.setVisibility(View.GONE);
        textView6.setVisibility(View.GONE);
        textView7.setVisibility(View.GONE);
        textView8.setVisibility(View.GONE);
        textView9.setVisibility(View.GONE);
        textView10.setVisibility(View.GONE);
        textView11.setVisibility(View.GONE);
        textView12.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame1:
                changeVisibility();
                textView1.setVisibility(View.VISIBLE);
                break;
            case R.id.frame2:
                changeVisibility();
                textView2.setVisibility(View.VISIBLE);
                break;
            case R.id.frame3:
                changeVisibility();
                textView3.setVisibility(View.VISIBLE);
                break;
            case R.id.frame4:
                changeVisibility();
                textView4.setVisibility(View.VISIBLE);
                break;
            case R.id.frame5:
                changeVisibility();
                textView5.setVisibility(View.VISIBLE);
                break;
            case R.id.frame6:
                changeVisibility();
                textView6.setVisibility(View.VISIBLE);
                break;
            case R.id.frame7:
                changeVisibility();
                textView7.setVisibility(View.VISIBLE);
                break;
            case R.id.frame8:
                changeVisibility();
                textView8.setVisibility(View.VISIBLE);
                break;
            case R.id.frame9:
                changeVisibility();
                textView9.setVisibility(View.VISIBLE);
                break;
            case R.id.frame10:
                changeVisibility();
                textView10.setVisibility(View.VISIBLE);
                break;
            case R.id.frame11:
                changeVisibility();
                textView11.setVisibility(View.VISIBLE);
                break;
            case R.id.frame12:
                changeVisibility();
                textView12.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}