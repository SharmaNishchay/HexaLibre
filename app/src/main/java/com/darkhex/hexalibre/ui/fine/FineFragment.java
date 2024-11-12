package com.darkhex.hexalibre.ui.fine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.darkhex.hexalibre.databinding.ActivityFineBinding;

public class FineFragment extends Fragment {

    private ActivityFineBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FineViewModel galleryViewModel =new ViewModelProvider(this).get(FineViewModel.class);

        binding = ActivityFineBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}