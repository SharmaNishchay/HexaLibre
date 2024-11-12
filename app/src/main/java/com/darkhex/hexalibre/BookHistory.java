package com.darkhex.hexalibre;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookHistory extends RecyclerView.Adapter<BookHistory.BookViewHolder> {
    private final List<BookService> bookList;

    public BookHistory(List<BookService> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_history, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookService book = bookList.get(position);
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvActionStatus.setText(book.getStatus());
        String dt=book.getStatus()+" on: "+book.getDate();
        holder.tvDate.setText(dt);

        // Customize the style based on the book status
        if ("Issued".equals(book.getStatus())) {
            holder.tvActionStatus.setTextAppearance(R.style.IssuedText);
        } else if ("Returned".equals(book.getStatus())) {
            holder.tvActionStatus.setTextAppearance(R.style.ReturnedText);
        } else if ("On Hold".equals(book.getStatus())) {
            holder.tvActionStatus.setTextAppearance(R.style.onHoldText);
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookTitle, tvActionStatus, tvDate;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookTitle = itemView.findViewById(R.id.tvBookTitle);
            tvActionStatus = itemView.findViewById(R.id.tvActionStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}

