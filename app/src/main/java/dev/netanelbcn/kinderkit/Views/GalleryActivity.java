package dev.netanelbcn.kinderkit.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

import dev.netanelbcn.kinderkit.Adapters.GalleryAdapter;
import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.Models.MyPhoto;
import dev.netanelbcn.kinderkit.R;

public class GalleryActivity extends AppCompatActivity {

    public static int SPAN_COUNT = 2;
    public GalleryAdapter adapter;
    private ArrayList<MyPhoto> images;
    private int currentKidPosition;
    private Kid myKid;
    private RecyclerView GA_RV_gallery;
    private MaterialButton EA_MB_add_photo;
    private StorageReference storageReference;
    private Uri image;
    private Uri fbImage;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    image = o.getData().getData();
                    Toast.makeText(GalleryActivity.this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                    uploadImage(image); // Move uploadImage() call here
                } else {
                    Toast.makeText(GalleryActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
    private ShapeableImageView GA_SIV_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_gallery);
        connectUI();
        //Glide.with(this).load(R.drawable.gallerybackground).placeholder(R.drawable.ic_launcher_background).into(GA_SIV_gallery);
        getIntents();
        FirebaseApp.initializeApp(GalleryActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference();
        attachListeners();
        myKid = DataManager.getInstance().getKids().get(currentKidPosition);
        images = myKid.getPhotosUri();
        GA_RV_gallery.setLayoutManager(new
                GridLayoutManager(this, SPAN_COUNT));

//        GA_RV_gallery.setLayoutManager(new
//                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        adapter = new GalleryAdapter(this, images, currentKidPosition);
        adapter.setDelPicCallback((uri) -> {
            DataManager.getInstance().removePhotoUri(uri, myKid);
            adapter.notifyDataSetChanged();
        });

        adapter.setSetAsProfilePictureCallback((uri) -> {
            DataManager.getInstance().setProfilePhotoUri(uri, currentKidPosition);

            adapter.notifyDataSetChanged();
            finish();
        });
        GA_RV_gallery.setAdapter(adapter);
    }

    private void attachListeners() {
        EA_MB_add_photo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
    }

    private void getIntents() {
        currentKidPosition = getIntent().getIntExtra("kidPosition", -1);
    }

    private void connectUI() {
        GA_RV_gallery = findViewById(R.id.GA_RV_gallery);
        EA_MB_add_photo = findViewById(R.id.EA_MB_add_photo);
        GA_SIV_gallery = findViewById(R.id.GA_SIV_gallery);
    }

    private void uploadImage(Uri image) {
        StorageReference reference = storageReference.child(UUID.randomUUID().toString() + ".jpg");
        reference.putFile(image).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, now get the download URL
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Get the download URL and use it to store or display the image
                fbImage = uri;
                MyPhoto myPhoto = new MyPhoto().setPhotoUri(fbImage.toString());
                Kid kid = DataManager.getInstance().getKids().get(currentKidPosition);
                DataManager.getInstance().addPhotoUri(fbImage, myKid);
                //    Kid kid = DataManager.getInstance().getKids().get(currentKidPosition);
                //     DataManager.getInstance().db.refreshKidDB(kid);

                adapter.notifyDataSetChanged();
                Toast.makeText(GalleryActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(GalleryActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e("errr", e.getMessage());
            Toast.makeText(GalleryActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            Toast.makeText(GalleryActivity.this, "Upload In Progress", Toast.LENGTH_SHORT).show();
        });
    }


}