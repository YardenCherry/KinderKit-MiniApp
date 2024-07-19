package dev.netanelbcn.kinderkit.Views;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.netanelbcn.kinderkit.Adapters.BabysitterAdapter;
import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Babysitter;
import dev.netanelbcn.kinderkit.R;
import dev.netanelbcn.kinderkit.Uilities.MapsFragment;

public class ActivityHomeParent extends AppCompatActivity implements BabysitterAdapter.BabysitterClickListener {
    private RecyclerView recyclerView;
    private BabysitterAdapter adapter;
    private List<Babysitter> babysitters;
    private MapsFragment mapsFragment;
    private DataManager dataManager = DataManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_parent);

        /// Ensure MapsFragment is not null
        mapsFragment = new MapsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mapContainer, mapsFragment).commit();
        recyclerView = findViewById(R.id.rvBabysitters);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        babysitters = new ArrayList<>();
        adapter = new BabysitterAdapter(babysitters, this, dataManager, this);
        recyclerView.setAdapter(adapter);

        loadBabysitters();

//        findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ActivityHomeParent.this, ActivitySetting.class));
//            }
//        });

        findViewById(R.id.btnSortByExperience).setOnClickListener(v -> {
            Collections.sort(babysitters, (b1, b2) -> Double.compare(b2.getExperience(), b1.getExperience()));
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

        findViewById(R.id.btnSortByHourlyWage).setOnClickListener(v -> {
            Collections.sort(babysitters, (b1, b2) -> Double.compare(b2.getHourlyWage(), b1.getHourlyWage()));
            adapter.notifyDataSetChanged(); // Notify the adapter to refresh the UI.
        });

        findViewById(R.id.btnSortByDistance).setOnClickListener(v -> {
            dataManager.sortBabysittersByDistance(new DataManager.OnBabysittersLoadedListener() {
                @Override
                public void onBabysittersLoaded(List<Babysitter> loadedBabysitters) {
                    babysitters.clear();
                    babysitters.addAll(loadedBabysitters);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(ActivityHomeParent.this, "Failed to load babysitters: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadBabysitters() {
        dataManager.loadAllBabysitters(new DataManager.OnBabysittersLoadedListener() {
            @Override
            public void onBabysittersLoaded(List<Babysitter> loadedBabysitters) {
                babysitters.clear();
                babysitters.addAll(loadedBabysitters);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(ActivityHomeParent.this, "Failed to load babysitters: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBabysitterClicked(Babysitter babysitter) {
        if (mapsFragment != null) {
            mapsFragment.zoom(babysitter.getLatitude(), babysitter.getLongitude());
        } else {
            Toast.makeText(this, "Map is not ready", Toast.LENGTH_SHORT).show();
        }
    }
}


