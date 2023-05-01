package com.example.screenbreak.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SlideshowViewModel extends ViewModel {

    private MutableLiveData<List<String>> mTextList;

    public SlideshowViewModel() {
        mTextList = new MutableLiveData<>();
        List<String> initialList = new ArrayList<>();
        initialList.add("Edgar Daniel López Carbajal");
        initialList.add("Cedrick Marcial Quintero");
        initialList.add("Erick");
        initialList.add("Maira");
        initialList.add("Carlos");
        mTextList.setValue(initialList);
    }

    public LiveData<List<String>> getTextList() {
        return mTextList;
    }
}