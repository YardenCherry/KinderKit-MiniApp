package dev.netanelbcn.kinderkit.Views;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.R;

public class KidInfoActivity extends AppCompatActivity {
    private MaterialTextView FH_MTV_title;
    private ShapeableImageView FH_SIV_image;
    private MaterialTextView FH_MTV_full_name;
    private MaterialTextView FH_MTV_age;
    private MaterialTextView FH_MTV_bDay;
    private MaterialButton FH_BTN_photos;
    private MaterialButton FH_BTN_events;
    private MaterialButton FH_BTN_immunization;
    private MaterialButton FH_BTN_delete;

    private ShapeableImageView FH_SIV_background;
    private int currentKidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_kid_info);
        connectUI();
        //Glide.with(this).load(R.drawable.kidinfoback).placeholder(R.drawable.ic_launcher_background).into(FH_SIV_background);

        getIntents();
        attachListeners();

    }

    private void attachListeners() {
        FH_BTN_delete.setOnClickListener(v -> {
            Kid kid = DataManager.getInstance().getKids().get(currentKidId);
            DataManager.getInstance().removeKid(kid);

            finish();
        });
        FH_BTN_immunization.setOnClickListener(v -> {
                    Intent intent = new Intent(KidInfoActivity.this, ImmunizationsActivity.class);
                    intent.putExtra("kidPosition", currentKidId);
                    startActivity(intent);
                }
        );
        FH_BTN_events.setOnClickListener(v -> {
            Intent intent = new Intent(KidInfoActivity.this, EventKidActivity.class);
            intent.putExtra("kidPosition", currentKidId);
            startActivity(intent);
        });
        FH_BTN_photos.setOnClickListener(v -> {
            Intent intent = new Intent(KidInfoActivity.this, GalleryActivity.class);
            intent.putExtra("kidPosition", currentKidId);
            startActivity(intent);
        });
    }

    private void getIntents() {
        FH_MTV_full_name.setText(getIntent().getStringExtra("kidfname") + " " + getIntent().getStringExtra("kidlname"));
        FH_MTV_age.setText(getIntent().getStringExtra("kidage"));
        FH_MTV_bDay.setText(getIntent().getStringExtra("kidDate"));
        currentKidId = getIntent().getIntExtra("kidPosition", -1);
        Glide.with(this).load(getIntent().getStringExtra("uri")).into(FH_SIV_image);
    }

    private void connectUI() {
        FH_MTV_title = findViewById(R.id.FH_MTV_title);
        FH_SIV_image = findViewById(R.id.FH_SIV_image);
        FH_MTV_full_name = findViewById(R.id.FH_MTV_full_name);
        FH_MTV_age = findViewById(R.id.FH_MTV_age);
        FH_MTV_bDay = findViewById(R.id.FH_MTV_bDay);
        FH_BTN_photos = findViewById(R.id.FH_BTN_photos);
        FH_BTN_events = findViewById(R.id.FH_BTN_events);
        FH_BTN_immunization = findViewById(R.id.FH_BTN_immunization);
        FH_BTN_delete = findViewById(R.id.FH_BTN_delete);
        FH_SIV_background = findViewById(R.id.FH_SIV_background);
    }


    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(this).load(DataManager.getInstance().getKids().get(currentKidId).getProfilePhotoUri()).into(FH_SIV_image);
    }
}