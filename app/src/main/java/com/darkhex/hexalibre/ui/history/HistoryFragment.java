package com.darkhex.hexalibre.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.darkhex.hexalibre.BookHistory;
import com.darkhex.hexalibre.BookService;
import com.darkhex.hexalibre.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        List<BookService> books=new ArrayList<>();
        books.add(new BookService("test1","Issued","12/12/24"));
        books.add(new BookService("test1","Returned","12/12/24"));
        books.add(new BookService("test1","On Hold","12/12/24"));
        books.add(new BookService("test1","Issued","12/12/24"));

        // Check if there are books to display
        if (books.isEmpty()) {
            binding.tvNoHistory.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        } else {
            BookHistory bookAdapter=new BookHistory(books);
            binding.recyclerView.setAdapter(bookAdapter);
            binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.recyclerView.getContext()));
            binding.tvNoHistory.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}