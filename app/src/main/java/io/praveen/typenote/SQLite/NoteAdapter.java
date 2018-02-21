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

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> implements Filterable {

    private List<Note> notes;
    private List<Note> fullNote;
    private List<Note> filtered;

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.length() == 0) {
                    ArrayList<Note> filteredList = new ArrayList<>();
                    for (Note i : fullNote) {
                        filteredList.add(i);
                    }
                    notes = filteredList;
                } else {
                    ArrayList<Note> filteredList = new ArrayList<>();
                    for (Note i : fullNote) {
                        if (i.getNote().toLowerCase().contains(charString)) {
                            filteredList.add(i);
                        }
                    }
                    notes = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filtered = (ArrayList<Note>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text, date;
        MyViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.text_note);
            date = view.findViewById(R.id.text_date);
        }
    }

    public NoteAdapter(List<Note> notes) {
        this.notes = notes;
        fullNote = notes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.setIsRecyclable(false);
        holder.text.setText(note.getNote());
        holder.date.setText(note.getDate());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

}