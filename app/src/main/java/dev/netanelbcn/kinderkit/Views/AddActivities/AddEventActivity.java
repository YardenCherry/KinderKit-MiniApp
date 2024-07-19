package dev.netanelbcn.kinderkit.Views.AddActivities;

import android.os.Bundle;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.Models.KidEvent;
import dev.netanelbcn.kinderkit.R;

public class AddEventActivity extends AppCompatActivity {
    private AppCompatEditText AE_ET_eventName;
    private DatePicker AE_MDP_eventDate;
    private MaterialButton AR_MB_add_record;
    private ShapeableImageView GA_SIV_back;
    private int currentKidPosition;
    private Kid kid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_add_event);
        connectUI();
        Glide.with(this).load(R.drawable.ak_back).placeholder(R.drawable.ic_launcher_background).into(GA_SIV_back);
        getIntents();
        attachListeners();
    }

    private void attachListeners() {
        AR_MB_add_record.setOnClickListener(v -> {
            String eventName;
            if (!AE_ET_eventName.getText().toString().isEmpty())
                eventName = AE_ET_eventName.getText().toString();
            else
                eventName = "Untitled Event";
            Kid kid = DataManager.getInstance().getKids().get(currentKidPosition);
            int day = AE_MDP_eventDate.getDayOfMonth();
            int month = AE_MDP_eventDate.getMonth();
            int year = AE_MDP_eventDate.getYear();
            KidEvent kidEvent = new KidEvent().setEventTitle(eventName).setEDate(day, month + 1, year);
            kidEvent.initEId();
            for (KidEvent event : kid.getEvents()) {
                if (event.getEventTitle().equals(kidEvent.getEventTitle()) && event.getEDate().equals(kidEvent.getEDate())) {
                    finish();
                    return;
                }
            }
            DataManager manager = DataManager.getInstance();
            manager.addKidEvent(kidEvent, currentKidPosition);

            manager.db.refreshUserInDB(manager.getParent());

            finish();
        });
    }

//    private void refreshKidEventsDB(Kid kid) {
//        DataManager manager=DataManager.getInstance();
//        ObjectBoundary kidB = kid.toBoundary();
//        kidB.getObjectId().setId(kid.getUid());
//        kidB.getObjectId().setSuperapp(manager.getSuperapp());
//        kidB.getCreatedBy().getUserId().setSuperapp(manager.getSuperapp());
//        Log.d("kid1",kid.toString());
//
//        manager.db.updateUserRole(kid.getMail(), Role.SUPERAPP_USER, new DataManager.OnUserUpdateListener() {
//            @Override
//            public void onSuccess() {
//                Log.d("kidB",kidB.toString());
//
//                manager.db.updateObject(kidB, new DataManager.OnUserUpdateListener (){
//                    @Override
//                    public void onSuccess() {
//                        Toast.makeText(AddEventActivity.this, "Event Added successful", Toast.LENGTH_SHORT).show();
//                        Log.d("kid2",kid.toString());
//                        manager.db.updateUserRole(kid.getMail(), Role.MINIAPP_USER, new DataManager.OnUserUpdateListener() {
//                            @Override
//                            public void onSuccess() {
//                            }
//
//                            @Override
//                            public void onFailure(Exception exception) {
//                                Toast.makeText(AddEventActivity.this, "failed Role1 " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onFailure(Exception exception) {
//                        Log.e("errr",exception.getMessage());
//                        Toast.makeText(AddEventActivity.this, "failed update KID " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                },kid);
//
//            }
//
//            @Override
//            public void onFailure(Exception exception) {
//                Toast.makeText(AddEventActivity.this, "failed Role2 " + exception.getMessage(), Toast.LENGTH_SHORT).show();
//                Log.e("problt",exception.getMessage());
//
//            }
//        });


    //  finish();
    //}

    private void getIntents() {
        currentKidPosition = getIntent().getIntExtra("kidPosition", -1);
    }

    private void connectUI() {
        AE_ET_eventName = findViewById(R.id.AE_ET_eventName);
        AE_MDP_eventDate = findViewById(R.id.AE_MDP_eventDate);
        AR_MB_add_record = findViewById(R.id.AR_MB_add_record);
        GA_SIV_back = findViewById(R.id.GA_SIV_back);
    }
}