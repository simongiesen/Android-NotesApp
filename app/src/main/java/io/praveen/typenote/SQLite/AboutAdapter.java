package io.praveen.typenote.SQLite;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.praveen.typenote.R;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.MyViewHolder>{

    private List<String> notes;

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        MyViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.about_text);
        }
    }

    public AboutAdapter(List<String> notes) {
        this.notes = notes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String s = notes.get(position);
        holder.text.setText(s);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}