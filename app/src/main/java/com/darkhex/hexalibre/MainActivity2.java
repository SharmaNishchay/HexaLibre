package com.darkhex.hexalibre;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.darkhex.hexalibre.databinding.FragmentHomeBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.List;

public class MainActivity2 {

    private GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;

    // Constructor
    public MainActivity2(FragmentHomeBinding activity) {
//        initializeSignIn(activity);
        initializeRecyclerView(activity);
    }

    // Initialize Google Sign-In
    private void initializeSignIn(AppCompatActivity activity) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);


    }

    // Initialize RecyclerView
    private void initializeRecyclerView(FragmentHomeBinding activity) {

        recyclerView = activity.getRoot().findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(activity.getRoot().getContext(), 2));

        Data data = new Data(activity.getRoot().getContext());
        // Fetch books from Node.js server asynchronously
        data.getBooks(new BookFetchCallback() {
            @Override
            public void onBooksFetched(List<Book> books) {
                bookAdapter = new BookAdapter(books);
                recyclerView.setAdapter(bookAdapter);
            }
        });
    }

    // Handle the sign-out logic
    public void signOut(Context context) {
        mGoogleSignInClient.signOut().addOnCompleteListener((AppCompatActivity) context, task -> {
            Toast.makeText(context, "Signed Out", Toast.LENGTH_SHORT).show();
            context.startActivity(new Intent(context, MainActivity.class));
            ((AppCompatActivity) context).finish();
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
