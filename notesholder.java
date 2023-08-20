package com.christopherhield.AndroidNotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.androidnotes.R;

public class notesholder extends RecyclerView.ViewHolder {

    TextView titletext, contenttext, datetext;

    public notesholder(@NonNull View itemView){
        super(itemView);

        titletext = itemView.findViewById( R.id.title_notes);
        contenttext = itemView.findViewById(R.id.notedescription);
        datetext = itemView.findViewById(R.id.date_time);
    }
}