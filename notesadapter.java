package com.christopherhield.AndroidNotes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidnotes.R;


public class notesadapter extends RecyclerView.Adapter<notesholder> {

    private final String TAG = getClass().getSimpleName();
    private final MainActivity mainActivity;
    private final ArrayList<notes> noteList;
    private final SimpleDateFormat sdf =
            new SimpleDateFormat("EEE MMM d, h:mm a", Locale.getDefault());

    public notesadapter(MainActivity mainActivity, ArrayList<notes> noteList) {
        this.mainActivity = mainActivity;
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public notesholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.notes_entry, parent, false);

        itemView.setOnClickListener(mainActivity);
        itemView.setOnLongClickListener(mainActivity);

        return new notesholder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull notesholder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        notes note = noteList.get(position);
        holder.titletext.setText(note.getTitle());

        String full = note.getDes();
        if (full.length() > 80){
            String trime = full.substring(0,79);
            trime += "...";
            holder.contenttext.setText(trime);
        }
        else {holder.contenttext.setText(note.getDes());}
        holder.datetext.setText(sdf.format(new Date(note.getDate())));
    }
    @Override
    public int getItemCount() {return noteList.size();}
}
