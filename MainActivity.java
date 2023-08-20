package com.christopherhield.AndroidNotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import android.widget.Toast;
import com.example.androidnotes.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {


    private final String TAG = getClass().getSimpleName();
    private final ArrayList<notes> noteList = new ArrayList<>();
    private RecyclerView recyclerView;
    private notesadapter notesadapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private notes presentnotes;
    private MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        recyclerView = findViewById( R.id.recycler );

        notesadapter = new notesadapter( this, noteList );
        recyclerView.setAdapter( notesadapter );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
        loadDataFromFile();


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult );
    }
   public void handleResult(ActivityResult result) {
       if (result == null || result.getData() == null) {
           Log.d( TAG, "handleResult: NULL ActivityResult received" );
           return;
       }
       Intent data = result.getData();
       if (result.getResultCode() == RESULT_OK) {
           if (data.hasExtra( "NEW_NOTE" )) {
               notes nt = (notes) data.getSerializableExtra( "NEW_NOTE" );
               noteList.add( nt );
               Collections.sort( noteList );
               notesadapter.notifyItemRangeChanged( 0, noteList.size() );
           } else if (data.hasExtra( "EDIT_NOTE" )) {
               notes nt = (notes) data.getSerializableExtra( "EDIT_NOTE" );
               presentnotes.setTitle( nt.getTitle() );
               presentnotes.setDes( nt.getDes() );
               presentnotes.setDate( nt.getDate() );
               Collections.sort( noteList );
               notesadapter.notifyItemRangeChanged( 0, noteList.size() );
           }
       }
       setTitle( getString( R.string.app_name ) + " [" + noteList.size() + "]" );

   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate( R.menu.notes_menu, menu );
        return true;
        }


    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item){
        if (item.getItemId() == R.id.Add_1) {
            Intent intent = new Intent( this, new_notes.class );
            activityResultLauncher.launch( intent );
        } else if (item.getItemId() == R.id.info_1) {
                Intent intent = new Intent( this, about_notes.class );
                startActivity( intent );
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
            super.onPause();
            saveDataToFile();
        }


    private void saveDataToFile() {
        Log.d( TAG, "saveDataToFile: " + noteList.size() );

        JSONArray jsonArray = new JSONArray();
        for (notes n : noteList) {
            try {
                jsonArray.put( n.toJSON() );
            } catch (JSONException e) {
                Log.d( TAG, "saveDataToFile: " + e.getMessage() );
                e.printStackTrace();
            }
        }

        try {
            FileOutputStream fos = getApplicationContext().openFileOutput( "JSONText.json", MODE_PRIVATE );
            PrintWriter pw = new PrintWriter( fos );
            pw.println( jsonArray );
            pw.close();
            fos.close();
        } catch (Exception e) {
            Log.d( TAG, "saveDataToFile: " + e.getMessage() );
                e.printStackTrace();
            }
            Log.w( TAG, "saveDataToFile: " + jsonArray );
        }

    private void loadDataFromFile() {
        FileInputStream fis;
        try {
            fis = getApplicationContext().openFileInput( "JSONText.json" );
        } catch (FileNotFoundException e) {
            Log.d( TAG, "loadDataFromFile: " + e.getMessage() );
            e.printStackTrace();
            return;
        }
            StringBuilder fileContent = new StringBuilder();

            try {
                byte[] buffer = new byte[1024];
                int n;
                while ((n = fis.read( buffer )) != -1) {
                    fileContent.append( new String( buffer, 0, n ) );
                }
                JSONArray jsonArray = new JSONArray( fileContent.toString() );
                Log.d( TAG, "readFromFile: " );
                for (int i = 0; i < jsonArray.length(); i++) {
                    noteList.add( notes.createFromJSON( jsonArray.getJSONObject( i ) ) );
                }
            } catch (Exception e) {
                Log.d( TAG, "loadDataFromFile: " + e.getMessage() );
                e.printStackTrace();
                return;
            }
            Log.d( TAG, "loadDataFromFile: " + noteList.size() );
            setTitle( getString( R.string.app_name ) + " [" + noteList.size() + "]" );
        }


    @Override
    public void onBackPressed() {
            Log.d( TAG, "onBackPressed: " );
            super.onBackPressed();
        }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition( view );
        presentnotes = noteList.get( pos );
        Intent intent = new Intent( this, new_notes.class );
        intent.putExtra( "EDIT_NOTE", presentnotes );
        activityResultLauncher.launch( intent );
    }

    @Override
    public boolean onLongClick(View view) {
        notesholder nh = new notesholder( view );
        String str = nh.titletext.getText().toString();
        int pos = recyclerView.getChildLayoutPosition( view );
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setPositiveButton( "Yes", (dialog, which) -> {
            noteList.remove( pos );
            notesadapter.notifyItemRemoved( pos );
            setTitle( getString( R.string.app_name ) + " [" + noteList.size() + "]" );
            Toast.makeText( this, "Note " + str + " Deleted", Toast.LENGTH_LONG ).show();
        } );
        builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
            }
        } );

        builder.setTitle( "Delete Note '" + str + "'?" );
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }
}