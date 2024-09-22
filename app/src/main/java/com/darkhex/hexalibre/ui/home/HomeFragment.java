package com.darkhex.hexalibre.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.darkhex.hexalibre.MainActivity2;
import com.darkhex.hexalibre.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        MainActivity2 mainActivity2 = new MainActivity2(binding);
//        AppCompatActivity activity = (AppCompatActivity) getActivity();
//        if (activity != null) {
//            MainActivity2 mainActivity2 = new MainActivity2(activity);  // Initialize MainActivity2
//        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}