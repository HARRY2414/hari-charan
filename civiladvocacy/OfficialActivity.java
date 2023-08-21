package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.civiladvocacy.databinding.ActivityOfficialBinding;
import com.example.civiladvocacy.models.Address;
import com.example.civiladvocacy.models.Office;
import com.example.civiladvocacy.models.Official;
import com.google.gson.Gson;

import org.parceler.Parcels;

public class OfficialActivity extends AppCompatActivity {

    private ActivityOfficialBinding binding;
    private Official official;
    private Office offices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfficialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpData();
    }

    private void setUpData() {

        offices = Parcels.unwrap(getIntent().getParcelableExtra("office"));
        official = Parcels.unwrap(getIntent().getParcelableExtra("official"));
        String searchTerm = getIntent().getStringExtra("searchTerm");
        String officialJson = new Gson().toJson(official);

        binding.tvLocation.setText(searchTerm);
        binding.tvOfficialName.setText(official.getName());
        binding.tvOfficialTag.setText(offices.getName());
        Glide.with(this).asBitmap().load(official.getPhotoUrl())
                .placeholder(AppCompatResources.getDrawable(this, R.drawable.missing))
                .into(binding.ivOfficial);

        binding.ivOfficial.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OfficialActivity.this, PhotoDetailActivity.class);
                intent.putExtra("name", official.getName());
                intent.putExtra("office", offices.getName());
                intent.putExtra("photo", official.getPhotoUrl());
                intent.putExtra("backgroundColor", getBackgroundColor(officialJson));
                intent.putExtra("searchTerm", searchTerm);
                intent.putExtra("party", official.getParty());
                startActivity(intent);
            }
        });

        if (officialJson.contains("line1")){
            Address address = official.getAddress().get(0);

            if (address.toString().contains("line1")) {
                binding.tvAddress.setText(address.getLine1() + "\n" + address.getCity() + ", " + address.getState() + " " + address.getZip());

                binding.tvAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mapIntent(address.getLine1() + "," + address.getCity());
                    }
                });
            }
            else {
                binding.tvAddress.setText("" + "\n" + address.getCity() + ", " + address.getState() + " " + address.getZip());

                binding.tvAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mapIntent(address.getCity());

                    }
                });
            }
        }
        else {
            binding.tvAddress.setText("");
        }


        if (officialJson.contains("phones")) {
            binding.tvPhone.setText(official.getPhones().get(0));

            binding.tvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + official.getPhones().get(0)));
                    startActivity(intent);
                }
            });
        }
        else {
            binding.tvPhone.setText("");
        }

        if (officialJson.contains("emails")) {
            binding.tvEmail.setText(official.getEmails().get(0));

            binding.tvEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("mailto:"+official.getEmails().get(0)));
                    startActivity(intent);
                }
            });
        } else {
            binding.tvEmail.setText("");
        }

        if (officialJson.contains("party")) {

            binding.tvParty.setText("("+ official.getParty() + ")");

            if (official.getParty().equals("Democratic Party") || official.getParty().equals("Democrat")) {
                binding.officialContainer.setBackgroundColor(getColor(R.color.blue));
                binding.ivPartyLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.dem_logo));
            } else if (official.getParty().equals("Republican Party") || official.getParty().equals("Republican")) {
                binding.officialContainer.setBackgroundColor(getColor(R.color.red));
                binding.ivPartyLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.rep_logo));
            } else {
                binding.officialContainer.setBackgroundColor(getColor(R.color.black));

            }
        } else {
            binding.officialContainer.setBackgroundColor(getColor(R.color.black));
        }

        if (officialJson.contains("urls")) {
            binding.tvWebsite.setText(official.getUrls().get(0));

            binding.tvWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(official.getUrls().get(0)));
                    startActivity(webIntent);
                }
            });

        }
        else {
            binding.tvWebsite.setText("");
        }


        if (officialJson.contains("channels")){

            if (officialJson.contains("Facebook")){

                binding.ivFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        facebookIntent(official.getChannels().get(0).getId());
                    }
                });
            }

            if (officialJson.contains("Twitter")){

                binding.ivTwitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        twitterIntent(official.getChannels().get(0).getId());
                    }
                });

            }

            if (officialJson.contains("Twitter") && officialJson.contains("Facebook")){

                binding.ivTwitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        twitterIntent(official.getChannels().get(1).getId());
                    }
                });

                binding.ivFacebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        facebookIntent(official.getChannels().get(0).getId());

                    }
                });

            }
        }
    }

    private void twitterIntent(String userId) {
        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + userId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + userId));
        }
        startActivity(intent);
    }

    private void facebookIntent(String userId) {
        Intent intent = null;
        try {
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/=" + userId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + userId));
        }
        startActivity(intent);
    }

    private void mapIntent(String address) {

        try {
            Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        }
        catch (Exception e) {
            Toast.makeText(this, "cannot open maps", Toast.LENGTH_LONG).show();
        }

    }

    public Integer getBackgroundColor(String officialJson) {
        if (officialJson.contains("party")) {
            if (official.getParty().equals("Democratic Party") || official.getParty().equals("Democrat")) {
                return getColor(R.color.blue);
            } else if (official.getParty().equals("Republican Party") || official.getParty().equals("Republican")) {
                return getColor(R.color.red);
            } else {
                return getColor(R.color.black);
            }
        }else {
                return getColor(R.color.black);
        }
    }
}