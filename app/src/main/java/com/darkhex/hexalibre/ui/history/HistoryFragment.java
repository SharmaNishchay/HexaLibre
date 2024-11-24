package com.darkhex.hexalibre.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.darkhex.hexalibre.BookHistory;
import com.darkhex.hexalibre.BookService;
import com.darkhex.hexalibre.Data;
import com.darkhex.hexalibre.History;
import com.darkhex.hexalibre.HistoryCallback;
import com.darkhex.hexalibre.MainActivity;
import com.darkhex.hexalibre.R;
import com.darkhex.hexalibre.databinding.FragmentHistoryBinding;
import com.darkhex.hexalibre.oneBookCallback;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;
    private List<BookService> books = new ArrayList<>();
    private BookHistory bookAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView and Adapter
        bookAdapter = new BookHistory(books);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(binding.recyclerView.getContext()));
        binding.recyclerView.setAdapter(bookAdapter);

        // Fetch history and update UI
        fetchHistoryData();

        return root;
    }

    private void fetchHistoryData() {
        MainActivity mainActivity = (MainActivity) requireActivity(); // Get the activity context
        mainActivity.set_history(new HistoryCallback() {
            @Override
            public void onHistoryFetched(List<History> histories) {
                if (histories.isEmpty()) {
                    showEmptyState();
                    return;
                }

                for (History history : histories) {
                    Data data = new Data();
                    data.showPreview(history.getISBN(), new oneBookCallback() {
                        @Override
                        public void onBooksFetched(String title, String url) {
                            // Add book to the list and update UI
                            books.add(new BookService(title, history.getStatus(), history.getIssue()));
                            updateRecyclerView();
                        }

                        @Override
                        public void onBook_notFetched() {
                            Log.d("HistoryFragment", "Failed to fetch book details for ISBN: " + history.getISBN());
                        }
                    });
                }
            }

            @Override
            public void onHistory_notFetched() {
                showEmptyState();
            }
        });
    }

    private void updateRecyclerView() {
        // Run on the main thread to ensure UI updates correctly
        requireActivity().runOnUiThread(() -> {
            bookAdapter.notifyDataSetChanged();
            if (!books.isEmpty()) {
                binding.tvNoHistory.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.imageLoadingViewAnimation.setVisibility(View.GONE);


            }
        });
    }

    private void showEmptyState() {
        requireActivity().runOnUiThread(() -> {
            binding.tvNoHistory.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
