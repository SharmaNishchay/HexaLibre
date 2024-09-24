package com.darkhex.hexalibre;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
        recyclerView.setLayoutManager(new GridLayoutManager(activity.getContext(), 2));

        Data data = new Data(activity.getContext());
        // Fetch books from Node.js server asynchronously
        data.getBooks(new BookFetchCallback() {
            @Override
            public void onBooksFetched(List<Book> books) {
                bookAdapter = new BookAdapter(books);
                recyclerView.setAdapter(bookAdapter);
            }
        });
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

    public BookAdapter(List<Book> books) {
        this.books = books;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.nameTextView.setText(book.name);

        // Load image using Glide or Picasso
//        Glide.with(holder.itemView.getContext()).load(book.imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return books.size();
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
}
