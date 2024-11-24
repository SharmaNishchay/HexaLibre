package com.darkhex.hexalibre;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

// MainActivity2 class with book and category setup
public class MainActivity2 {

    private RecyclerView recyclerViewBooks;
    private BookAdapter bookAdapter;
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private boolean isFetched;
    private boolean isScrolled;
    private List<String>test=new ArrayList<>();
    private final View activity;



    // Constructor
    public MainActivity2(View activity) {
        this.activity=activity;
        initializeRecyclerView();
    }

    // Initialize RecyclerView
    private void initializeRecyclerView() {
        recyclerViewBooks = activity.findViewById(R.id.recycler_view);
        recyclerViewCategories = activity.findViewById(R.id.categories);
        ProgressBar progressBar = activity.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        bookAdapter = new BookAdapter(new ArrayList<>(), book -> {
            MainActivity m=(MainActivity)activity.getContext();
            m.BookDescriptionService(activity,book);
        });
        recyclerViewBooks.setAdapter(bookAdapter);
        recyclerViewBooks.setLayoutManager(new GridLayoutManager(activity.getContext(), 2));


        // Fetch books from server asynchronously
        Get_paths p = new Get_paths();
        p.getPath("College1","",cols -> {
            List<String> Booklist = new ArrayList<>(cols);
            test.addAll(cols);
            isFetched=true;
            Data data = new Data();
            data.Allbooks(Booklist,new BookFetchCallback() {
                @Override
                public void onBooksFetched(List<Book> books) {
                    bookAdapter.addBooks(books);
                    progressBar.setVisibility(View.GONE);
                    recyclerViewBooks.setVisibility(View.VISIBLE);
//                recyclerViewCategories.setVisibility(View.VISIBLE);
//                    List<Book>test=new ArrayList<>();
//                    test.add(new Book("Test","url","1234"));
//                    bookAdapter.addBooks(test);

                }
            });
        });
//        recyclerViewBooks.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (isFetched && !recyclerView.canScrollVertically(1)) {
//                    Log.d("Data","Scrolled");
//                }
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


    }

    // Filter books based on query
    public void filterBooks(String query) {
        if (bookAdapter != null) {
            bookAdapter.filterBooks(query);
        }
    }
}

class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private final List<Book> books;
    private final List<Book> filteredBooks;
    private final OnBookClickListner bookClickListener;

    public BookAdapter(List<Book> books,OnBookClickListner bookClickListener) {
        this.books = books;
        this.filteredBooks = new ArrayList<>(books); // Initialize filteredBooks
        this.bookClickListener = bookClickListener;
    }
    public void addBooks(List<Book> newBooks) {
        int startPosition = books.size();
        books.addAll(newBooks);
        filteredBooks.addAll(newBooks);

        notifyItemRangeInserted(startPosition, newBooks.size());
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
        holder.isbnView.setText(book.isbn);
        holder.itemView.setOnClickListener(v -> bookClickListener.onBookClick(book));
        // Load image using Glide or Picasso
        ProfileActivity p = new ProfileActivity();
        if (p.isValidUrl(book.imageUrl)) {
            Glide.with(holder.itemView.getContext())
                    .load(book.imageUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            if (e != null) e.logRootCauses("GlideError");
                            Log.d("GlideError", "Image load failed for URL: " + book.imageUrl+" "+e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.loadingAnimation.setVisibility(View.GONE);
                            holder.imageView.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(holder.imageView);

        }
    }

    @Override
    public int getItemCount() {
        return filteredBooks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageView;
        public TextView isbnView;
        public LottieAnimationView loadingAnimation;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            imageView = itemView.findViewById(R.id.image_view);
            isbnView=itemView.findViewById(R.id.isbn);
            loadingAnimation=itemView.findViewById(R.id.image_loading_animation);
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
