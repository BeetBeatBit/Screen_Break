package com.example.screenbreak.ui.slideshow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.screenbreak.R;
import com.example.screenbreak.ui.slideshow.SlideshowViewModel;

import java.util.List;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_slideshow, container, false);

        // Get an instance of the SlideshowViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SlideshowViewModel.class);

        // Get the list of strings from the SlideshowViewModel
        LiveData<List<String>> textListLiveData = viewModel.getTextList();

        // Create an observer that updates the ListView adapter when the list changes
        textListLiveData.observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> textList) {
                ListView listView = view.findViewById(R.id.my_listview);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_list_item_1, textList);
                listView.setAdapter(adapter);
            }
        });

        return view;
    }
}


