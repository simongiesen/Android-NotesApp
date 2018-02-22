package io.praveen.typenote.SQLite;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.praveen.typenote.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.MyViewHolder> {

    private List<String> notes;

    public AboutAdapter(List<String> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String s = notes.get(position);
        holder.text.setText(s);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        MyViewHolder(@NonNull View view) {
            super(view);
            text = view.findViewById(R.id.about_text);
        }
    }

}