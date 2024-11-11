package com.darkhex.hexalibre.ui.honorscore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.darkhex.hexalibre.databinding.ActivityHonorscoreBinding;

public class HonorscoreFragment extends Fragment {

    private ActivityHonorscoreBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HonorscoreViewModel honorscoreViewModel =
                new ViewModelProvider(this).get(HonorscoreViewModel.class);

        binding = ActivityHonorscoreBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}