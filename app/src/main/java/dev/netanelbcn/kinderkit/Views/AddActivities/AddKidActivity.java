package dev.netanelbcn.kinderkit.Views.AddActivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatEditText;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.ExternalModels.utils.Role;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.Models.MyPhoto;
import dev.netanelbcn.kinderkit.R;
import dev.netanelbcn.kinderkit.Views.MenuActivity;

public class AddKidActivity extends AppCompatActivity {

    private AppCompatEditText AK_ACET_editTextFirstName;
    private AppCompatEditText AK_ACET_editTextLastName;
    private AppCompatEditText AK_ACET_editTextPhone;
    private DatePicker AK_DP_datePicker;
    private MaterialButton AK_MB_buttonAddPhoto;
    private MaterialButton AK_MB_buttonAddKid;
    private StorageReference storageReference;
    private ShapeableImageView AK_SIV_imageViewProfile;
    private Uri image;
    private Uri fbImage;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                if (o.getData() != null) {
                    image = o.getData().getData();
                    Toast.makeText(AddKidActivity.this, "Image selected successfully", Toast.LENGTH_SHORT).show();
                    uploadImage(image); // Move uploadImage() call here
                } else {
                    Toast.makeText(AddKidActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_add_kid);
        connectUI();
        Glide.with(this).load(R.drawable.addeventbackground).placeholder(R.drawable.ic_launcher_background).into(AK_SIV_imageViewProfile);
        FirebaseApp.initializeApp(AddKidActivity.this);
        storageReference = FirebaseStorage.getInstance().getReference();

        attachListeners();
    }

    private void attachListeners() {
        AK_MB_buttonAddPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
        AK_MB_buttonAddKid.setOnClickListener(v -> {
            String firstName = AK_ACET_editTextFirstName.getText().toString();
            String lastName = AK_ACET_editTextLastName.getText().toString();
            String phoneNum = AK_ACET_editTextPhone.getText().toString(); // to add
            if (!(firstName.isEmpty() || lastName.isEmpty())) {
                int day = AK_DP_datePicker.getDayOfMonth();
                int month = AK_DP_datePicker.getMonth();
                int year = AK_DP_datePicker.getYear();
                Kid kid;
                if (phoneNum.isEmpty())
                    kid = new Kid().setBirthDate(day, month + 1, year).setfName(firstName).setlName(lastName);

                else
                    kid = new Kid(phoneNum).setBirthDate(day, month + 1, year).setfName(firstName).setlName(lastName);
                if (fbImage != null) {
                    kid.setProfilePhotoUri(fbImage.toString());
                    kid.getPhotosUri().add(new MyPhoto().setPhotoUri(fbImage.toString()));

                } else
                    kid.setProfilePhotoUri("https://firebasestorage.googleapis.com/v0/b/kinderkit-68d4c.appspot.com/o/DEFAULT.jpg?alt=media&token=f55bdee7-a8dd-4e7a-822d-ac0b9b97d873");
                //  DataManager.getInstance().getParent().addNewKid(kid);
                createUserKid(kid);
                //  DataManager.getInstance().db.updateUser();

            } else
                Toast.makeText(this, "Can't create Kid with missing fields", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void createUserKid(Kid kid) {
        DataManager manager = DataManager.getInstance();
        manager.db.createUser(kid.getMail(), kid, new DataManager.OnUserCreationListener() {
            @Override
            public void onUserCreated(String email) {
            }

            @Override
            public void onFailure(Exception exception) {
                //rogressDialog.dismiss();
                Toast.makeText(AddKidActivity.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, new DataManager.OnDataSavedListener() {
            @Override
            public void onSuccess(ObjectBoundary kidB) {
                kid.setUid(kidB.getObjectId().getId());
                Log.d("mykid", kid.toString());

                ObjectBoundary parent = manager.getParent().toBoundary();
                parent.getObjectId().setId(manager.getParent().getUid());
                parent.getObjectId().setSuperapp(manager.getSuperapp());
                parent.getCreatedBy().getUserId().setSuperapp(manager.getSuperapp());
                Log.d("myparent", DataManager.getInstance().getParent().toString());
                Log.d("mykid", kid.toString());
                DataManager.getInstance().getParent().addNewKid(kid);
                Log.d("ids", DataManager.getInstance().getParent().getKidsId().toString());
                Log.d("myparent2", DataManager.getInstance().getParent().toString());
                parent.getObjectDetails().put("kidsId", DataManager.getInstance().getParent().getKidsId());
                parent.getObjectDetails().put("kids", DataManager.getInstance().getParent().getKids());
                Log.d("myparent3", parent.toString());

                manager.db.updateUserRole(manager.getParent().getMail(), Role.SUPERAPP_USER, new DataManager.OnUserUpdateListener() {
                    @Override
                    public void onSuccess() {

                        manager.db.updateObject(parent, new DataManager.OnUserUpdateListener() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(AddKidActivity.this, "Kid created successful", Toast.LENGTH_SHORT).show();
                                manager.db.updateUserRole(manager.getParent().getMail(), Role.MINIAPP_USER, new DataManager.OnUserUpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        startActivity(new Intent(AddKidActivity.this, MenuActivity.class));
                                    }

                                    @Override
                                    public void onFailure(Exception exception) {
                                        Toast.makeText(AddKidActivity.this, "failed Role " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                Toast.makeText(AddKidActivity.this, "failed update parent " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }, manager.getParent());
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(AddKidActivity.this, "failed Role " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(AddKidActivity.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                // progressDialog.dismiss();
                Toast.makeText(AddKidActivity.this, "Data saving failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }, new DataManager.OnUserUpdateListener() {
            @Override
            public void onSuccess() {
                // progressDialog.dismiss();
                Toast.makeText(AddKidActivity.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                //progressDialog.dismiss();
                Toast.makeText(AddKidActivity.this, "Data updating failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("DataManager", "Error in createUser: " + exception.getMessage());
            }
        });
    }


    private void connectUI() {
        AK_ACET_editTextFirstName = findViewById(R.id.AK_ACET_editTextFirstName);
        AK_ACET_editTextLastName = findViewById(R.id.AK_ACET_editTextLastName);
        AK_ACET_editTextPhone = findViewById(R.id.AK_ACET_editTextPhone);
        AK_DP_datePicker = findViewById(R.id.AK_DP_datePicker);
        AK_MB_buttonAddPhoto = findViewById(R.id.AK_MB_buttonAddPhoto);
        AK_MB_buttonAddKid = findViewById(R.id.AK_MB_buttonAddKid);
        AK_SIV_imageViewProfile = findViewById(R.id.AK_SIV_imageViewProfile);
    }


    private void uploadImage(Uri image) {
        StorageReference reference = storageReference.child(UUID.randomUUID().toString() + ".jpg");
        reference.putFile(image).addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully, now get the download URL
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Get the download URL and use it to store or display the image
                fbImage = uri;
                AK_MB_buttonAddKid.setClickable(true);
                // Do something with the imageUrl, such as storing it in the database
                Toast.makeText(AddKidActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                // Handle any errors getting the download URL
                AK_MB_buttonAddKid.setClickable(true);
                Toast.makeText(AddKidActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            // Handle any errors uploading the image
            AK_MB_buttonAddKid.setClickable(true);
            Toast.makeText(AddKidActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            // Observe state change events such as progress, pause, and resume
            // Get the current progress
            AK_MB_buttonAddKid.setClickable(false);
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Toast.makeText(AddKidActivity.this, "Upload In Progress", Toast.LENGTH_SHORT).show();
        });
    }


}