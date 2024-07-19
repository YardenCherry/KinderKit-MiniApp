package dev.netanelbcn.kinderkit.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import dev.netanelbcn.kinderkit.Adapters.EvenetAdapter;
import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.Models.KidEvent;
import dev.netanelbcn.kinderkit.R;
import dev.netanelbcn.kinderkit.Views.AddActivities.AddEventActivity;

public class EventKidActivity extends AppCompatActivity {
    private EvenetAdapter adapter;
    private ArrayList<KidEvent> events;
    private int currentKidPosition;
    private RecyclerView EA_RV_events;
    private MaterialButton EA_MB_add_event;
    private ShapeableImageView GA_SIV_event;
    private DataManager dataManager = DataManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_event_kid);
        connectUI();
        //Glide.with(this).load(R.drawable.eventbackground).placeholder(R.drawable.ic_launcher_background).into(GA_SIV_event);
        getIntents();
        attachListeners();
        Kid myKid = dataManager.getKids().get(currentKidPosition);
        events = myKid.getEvents();
        Log.d("NewEvents", events.toString());
        EA_RV_events.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new EvenetAdapter(this, events, currentKidPosition);
        Log.d("NewEvents1", events.toString());
        refreshEventsList();
        adapter.setEventCallback((event, position) ->
        {
            dataManager.removeKidEvent(event, currentKidPosition);
            adapter.notifyDataSetChanged();
        });
        EA_RV_events.setAdapter(adapter);
    }

    private void attachListeners() {
        EA_MB_add_event.setOnClickListener(v -> {
            Intent intent = new Intent(EventKidActivity.this, AddEventActivity.class);
            intent.putExtra("kidPosition", currentKidPosition);
            startActivity(intent);
        });
    }

    private void getIntents() {
        currentKidPosition = getIntent().getIntExtra("kidPosition", -1);
    }

    private void connectUI() {
        EA_RV_events = findViewById(R.id.EA_RV_events);
        EA_MB_add_event = findViewById(R.id.EA_MB_add_event);
        GA_SIV_event = findViewById(R.id.GA_SIV_event);
    }

    public void refreshEventsList() {
        events.sort((o1, o2) -> o1.getEDate().compareTo(o2.getEDate()));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEventsList();
    }
}