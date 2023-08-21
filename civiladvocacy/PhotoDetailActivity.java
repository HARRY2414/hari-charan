package com.example.civiladvocacy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.civiladvocacy.databinding.ActivityPhotoDetailBinding;

public class PhotoDetailActivity extends AppCompatActivity {

    private ActivityPhotoDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpData();
    }

    private void setUpData() {

        String name = getIntent().getStringExtra("name");
        String office = getIntent().getStringExtra("office");
        String image = getIntent().getStringExtra("photo");
        String searchTerm = getIntent().getStringExtra("searchTerm");
        String party = getIntent().getStringExtra("party");
        int color = getIntent().getIntExtra("backgroundColor", 0);

        Glide.with(this).load(image)
                .placeholder(AppCompatResources.getDrawable(this, R.drawable.missing))
                .into(binding.ivOfficial);
        binding.tvLocation.setText(searchTerm);
        binding.tvOfficialName.setText(name);
        binding.tvOfficialTag.setText(office);
        binding.officialContainer.setBackgroundColor(color);

        if (party != null) {
            if (party.equals("Democratic Party") || party.equals("Democrat")) {
                binding.ivPartyLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.dem_logo));
            } else if (party.equals("Republican Party") || party.equals("Republican")) {
                binding.ivPartyLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.rep_logo));
            } else {
                binding.ivPartyLogo.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.missing));
            }
        }
    }
}