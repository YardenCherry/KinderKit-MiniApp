package dev.netanelbcn.kinderkit.Uilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dev.netanelbcn.kinderkit.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_maps, container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        addMarker(sydney, "Sydney");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));
    }

    public void zoom(double latitude, double longitude) {
        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            addMarker(location, "Selected Location");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));
        }
    }

    private void addMarker(LatLng location, String title) {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(location).title(title));
        }
    }
}
