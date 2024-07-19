package dev.netanelbcn.kinderkit.Views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Parent;
import dev.netanelbcn.kinderkit.R;
import dev.netanelbcn.kinderkit.Views.AddActivities.AddKidActivity;
import dev.netanelbcn.kinderkit.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;
    private ShapeableImageView NHM_IV_profilePic;
    private MaterialTextView NHM_TV_userName;
    private MaterialTextView NHM_TV_email;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMenu.toolbar);
        binding.appBarMenu.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MenuActivity.this, AddKidActivity.class);
            startActivity(intent);
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        connectUI();
        getIntents();
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.log_out_BTN) {
                logOut();
            }
            if (item.getItemId() == R.id.babysitting_services_BTN) {
                Intent intent = new Intent(MenuActivity.this, ActivityHomeParent.class);
                startActivity(intent);
            }


            return false;
        });
    }

    private void logOut() {
        startActivity(new Intent(MenuActivity.this, LoginActivity.class));
        finish();
    }

    private void deleteAccount() {
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("DeleteAccount", "User account deleted.");
                    }
                });
        DataManager.getInstance().deleteUser();
    }

    private void getIntents() {
        Parent p = DataManager.getInstance().getParent();
        String name = p.getName();
        String email = p.getMail();
        String phone = p.getPhone();
        String uri = "https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/parent.jpg?alt=media&token=ae401f45-29f2-4d92-999b-8b0d579f2261";
        if (name != null) {
            NHM_TV_userName.setText(name);

        } else
            NHM_TV_userName.setText("Phone User:");


        if (email != null)
            NHM_TV_email.setText(email);
        else if (phone != null)
            NHM_TV_email.setText(phone);
        else {
            Log.d("LogInError", "No email or phone number");
            return;
        }
        Glide.with(this).load(uri)
                .placeholder(R.drawable.ic_launcher_background).
                into(NHM_IV_profilePic);
    }

    private void connectUI() {
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        NHM_IV_profilePic = headerView.findViewById(R.id.NHM_IV_profilePic);
        NHM_TV_userName = headerView.findViewById(R.id.NHM_TV_userName);
        NHM_TV_email = headerView.findViewById(R.id.NHM_TV_email);
    }

    @NonNull
    private NavController getNavController() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_menu);
        if (!(fragment instanceof NavHostFragment)) {
            throw new IllegalStateException("Activity " + this
                    + " does not have a NavHostFragment");
        }
        return ((NavHostFragment) fragment).getNavController();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


}