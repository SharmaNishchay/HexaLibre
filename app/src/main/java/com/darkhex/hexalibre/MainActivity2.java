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

// MainActivity2 class with book and category setup
public class MainActivity2 {

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;


    // Constructor
    public MainActivity2(View activity) {
        initializeRecyclerView(activity);
    }

    // Initialize RecyclerView
    private void initializeRecyclerView(View activity) {
        recyclerViewBooks = activity.findViewById(R.id.recycler_view);
        recyclerViewCategories = activity.findViewById(R.id.categories);
        ProgressBar progressBar = activity.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);

//        Data data = new Data();

        // Fetch books from server asynchronously
//        data.getBooks(new BookFetchCallback() {
//            @Override
//            public void onBooksFetched(List<Book> books) {
//                bookAdapter = new BookAdapter(books);
//                recyclerViewBooks.setAdapter(bookAdapter);
//                recyclerViewBooks.setLayoutManager(new GridLayoutManager(activity.getContext(), 2));
//                progressBar.setVisibility(View.GONE);
//                recyclerViewBooks.setVisibility(View.VISIBLE);
////                recyclerViewCategories.setVisibility(View.VISIBLE);
//
//            }
//        });

        // Sample category list for categories
        List<Cat> cats = new ArrayList<>();
        cats.add(new Cat("Category 1", "img1"));
        cats.add(new Cat("Category 2", "img1"));
        cats.add(new Cat("Category 3", "img1"));
        cats.add(new Cat("Category 4", "img1"));
        cats.add(new Cat("Category 5", "img1"));
        cats.add(new Cat("Category 6", "img1"));
        cats.add(new Cat("Category 4", "img1"));
        cats.add(new Cat("Category 5", "img1"));
        cats.add(new Cat("Category 6", "img1"));

        categoryAdapter = new CategoryAdapter(cats);
        recyclerViewCategories.setAdapter(categoryAdapter);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(activity.getContext(), 3));
        recyclerViewCategories.setVisibility(View.VISIBLE);


        List<Book>books=new ArrayList<>();
        books.add(new Book("Book1","img"));
        books.add(new Book("Book2","img"));
        books.add(new Book("Book3","img"));
        books.add(new Book("Book4","img"));
        books.add(new Book("Book5","img"));
        books.add(new Book("Book6","img"));
        books.add(new Book("Book7","img"));
        books.add(new Book("Book8","img"));
        books.add(new Book("Book9","img"));
        books.add(new Book("Book10","img"));
        books.add(new Book("Book11","img"));
        books.add(new Book("Book12","img"));
        books.add(new Book("Book13","img"));
        books.add(new Book("Book14","img"));
        books.add(new Book("Book15","img"));
        books.add(new Book("Book16","img"));
        bookAdapter = new BookAdapter(books);
        recyclerViewBooks.setAdapter(bookAdapter);
        recyclerViewBooks.setLayoutManager(new GridLayoutManager(activity.getContext(), 2));
        progressBar.setVisibility(View.GONE);
        recyclerViewBooks.setVisibility(View.VISIBLE);
    }

    // Filter books based on query
    public void filterBooks(String query) {
        if (bookAdapter != null) {
            bookAdapter.filterBooks(query);
        }
    }
}

//====================================================================
// Book Class and Book Adapter
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
        this.filteredBooks = new ArrayList<>(books); // Initialize filteredBooks
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
        // Glide.with(holder.itemView.getContext()).load(book.imageUrl).into(holder.imageView);
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

    // Filter books by query
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
        notifyDataSetChanged(); // Notify adapter to refresh
    }
}

//====================================================================
// Category Class and Category Adapter
class Cat {
    public String name;
    public String imageUrl;

    public Cat(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}

class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private final List<Cat> categories;

    public CategoryAdapter(List<Cat> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cat category = categories.get(position);
        holder.nameTextView.setText(category.name);
        // Load category image
        // Glide.with(holder.itemView.getContext()).load(category.imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.cat_name);
            imageView = itemView.findViewById(R.id.cat_img);
        }
    }
}
