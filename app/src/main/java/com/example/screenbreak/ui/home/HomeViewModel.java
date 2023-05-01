package com.example.screenbreak.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.screenbreak.R;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private List<String> mTextList;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Desarrollada por:");
        mTextList = new ArrayList<>();
    }

    public List<String> getTextList() {
        return mTextList;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void addToTextList(String text) {
        mTextList.add(text);
    }
}


