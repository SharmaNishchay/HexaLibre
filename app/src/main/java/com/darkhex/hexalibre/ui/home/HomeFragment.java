package com.darkhex.hexalibre.ui.home;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.darkhex.hexalibre.MainActivity2;
import com.darkhex.hexalibre.MainActivity;
import com.darkhex.hexalibre.R;
import com.darkhex.hexalibre.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainActivity2 mainActivity2;

    public void filterData(String query){
        mainActivity2.filterBooks(query);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        TextView elevatedTextView = root.findViewById(R.id.elevated_text_view);
        elevatedTextView.setText(Html.fromHtml(getString(R.string.welcome_message), Html.FROM_HTML_MODE_LEGACY));
        elevatedTextView.startAnimation(fadeIn);
        elevatedTextView.setVisibility(View.VISIBLE);
        mainActivity2 = new MainActivity2(root);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void onBackPressed() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).handleDoubleBackPress(); // Call the method in MainActivity2 to handle double back press
        }
    }
}

