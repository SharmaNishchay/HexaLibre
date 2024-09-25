package com.darkhex.hexalibre;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity2 {

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;

    // Constructor
    public MainActivity2(View activity) {
        initializeRecyclerView(activity);
    }


    // Initialize RecyclerView
    private void initializeRecyclerView(View activity) {

        recyclerView = activity.findViewById(R.id.recycler_view);
        ProgressBar progressBar = activity.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

        Data data = new Data(activity.getContext());
        // Fetch books from Node.js server asynchronously
        data.getBooks(new BookFetchCallback() {
            @Override
            public void onBooksFetched(List<Book> books) {
                bookAdapter = new BookAdapter(books);
                recyclerView.setAdapter(bookAdapter);
                recyclerView.setLayoutManager(new GridLayoutManager(activity.getContext(), 2));
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

            }
        });
    }

    public void filterBooks(String query){
        if(bookAdapter!=null){
            bookAdapter.filterBooks(query);
        }

    }
}
//--------------------------------------------------------------------------------------------------------------------------
//============================================================================================================================

class Book {
    public String name;
    public String imageUrl;

    public Book(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}

class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private final List<Book> books;
    private List<Book> filteredBooks;
    public BookAdapter(List<Book> books) {
        this.books = books;
        this.filteredBooks=new ArrayList<>(books);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = filteredBooks.get(position);
        holder.nameTextView.setText(book.name);

        // Load image using Glide or Picasso
//        Glide.with(holder.itemView.getContext()).load(book.imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return filteredBooks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
    public void filterBooks(String query) {
        filteredBooks.clear();
        if (query.isEmpty()) {
            filteredBooks.addAll(books);
        } else {
            for (Book book : books) {
                if (book.name.toLowerCase().contains(query.toLowerCase())) {
                    filteredBooks.add(book);
                }
            }
        }
        notifyDataSetChanged();
    }
}
