package com.example.salvadore.interfaces;

public interface ICallBack {
    void onSuccess();

    void onSuccess(String result);

    void onFailed();
}