package com.example.civiladvocacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.civiladvocacy.adapter.OfficialListAdapter;
import com.example.civiladvocacy.databinding.ActivityMainBinding;
import com.example.civiladvocacy.models.Office;
import com.example.civiladvocacy.models.Official;
import com.example.civiladvocacy.models.OfficialResponse;
import com.example.civiladvocacy.network.CivilApi;
import com.example.civiladvocacy.network.CivilApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private OfficialListAdapter adapter;
    private List<Official> officials;
    private List<Office> offices;

    String searchTerm;

    private FusedLocationProviderClient mFusedLocationClient;
    private static final int LOCATION_REQUEST = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        determineLocation();
        fetchOfficials(searchTerm);
    }

    private void determineLocation() {
        // Check perm - if not then start the  request and return
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        searchTerm = getPlace(location);
                        binding.tvLocation.setText(searchTerm);
                        Toast.makeText(this, searchTerm, Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(this, e ->
                        Toast.makeText(MainActivity.this,
                                e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                    binding.tvLocation.setText(searchTerm);
                    fetchOfficials(searchTerm);
                } else {
                    binding.tvLocation.setText("Location permission was denied - cannot determine address");
                }
            }
        }
    }


    private String getPlace(Location loc) {

        StringBuilder sb = new StringBuilder();

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            sb.append(String.format(
                    Locale.getDefault(),
                    "%s, %s",
                    city, state));

            Log.i("country", city + state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.about_menu:
                navigateToAbout();
                return true;
            case R.id.menu_search:
                showSearchPopUp();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void navigateToAbout() {

        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void showSearchPopUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View popup = LayoutInflater.from(this).inflate(R.layout.search_popup, null);
        EditText mEdit = popup.findViewById(R.id.et_searchTerm);
        builder.setMessage("Enter Address")
                .setView(popup)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        searchTerm = mEdit.getText().toString().trim();
                        binding.tvLocation.setText(searchTerm);
                        binding.progress.setVisibility(View.VISIBLE);
                        fetchOfficials(searchTerm);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void fetchOfficials(String address){

        CivilApi client = CivilApiClient.getClient();
        Call<OfficialResponse> call = client.getOfficialsData(address, CivilApiClient.API_KEY);

        call.enqueue(new Callback<OfficialResponse>() {
            @Override
            public void onResponse(Call<OfficialResponse> call, Response<OfficialResponse> response) {
                hideProgressBar();
                binding.tvErrorMessage.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    offices = response.body().getOffices();
                    officials = response.body().getOfficials();

                    adapter = new OfficialListAdapter(
                            getApplicationContext(),
                            officials,
                            offices,
                            searchTerm
                    );
                    binding.officialsRecycler.setAdapter(adapter);

                    RecyclerView.LayoutManager layoutManager =
                            new LinearLayoutManager(getApplicationContext());
                    binding.officialsRecycler.setLayoutManager(layoutManager);

                    showOfficials();
                }
                else {
                    showUnsuccessfulMessage();
                }
            }

            @Override
            public void onFailure(Call<OfficialResponse> call, Throwable t) {
                Log.e("Error Message", "onFailure: ",t );
                hideProgressBar();
                showFailureMessage();
            }
        });
    }

    //shows progress to the user
    private void showFailureMessage() {
        binding.errorWrapper.setVisibility(View.VISIBLE);
        binding.errorMessage.setText("No network connection");
        binding.tvErrorMessage.setText("Data cannot be accessed without an internet connection");
        binding.officialsRecycler.setVisibility(View.GONE);

    }


    private void showUnsuccessfulMessage() {
        binding.errorWrapper.setVisibility(View.VISIBLE);
        binding.errorMessage.setText("Error!");
        binding.tvErrorMessage.setText("Something went wrong. Please try again later");
        binding.officialsRecycler.setVisibility(View.GONE);

    }

    private void showOfficials() {
        binding.errorWrapper.setVisibility(View.GONE);
        binding.officialsRecycler.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.progress.setVisibility(View.GONE);
    }

}