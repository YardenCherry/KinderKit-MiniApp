package dev.netanelbcn.kinderkit.Views;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.ExternalModels.Bounderies.ObjectBoundary;
import dev.netanelbcn.kinderkit.Models.Parent;
import dev.netanelbcn.kinderkit.R;
import dev.netanelbcn.kinderkit.Uilities.GPSTracker;

public class ActivityRegister extends AppCompatActivity {

    double latitude, longitude;
    private EditText etName, etPhone, etMail, etAddress, etNumberOfChildren, etPassword, etHourlyWage, etExperience;

    private TextView alreadyAccount, tvAge;
    private Button btnRegister;
    private ProgressDialog progressDialog;

    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dataManager = dataManager.getInstance();
        ;

        initializeUIComponents();
        setupUIListeners();
    }

    private void initializeUIComponents() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etMail = findViewById(R.id.etMail);
        etAddress = findViewById(R.id.etAddress);
        etNumberOfChildren = findViewById(R.id.etNumberOfChildren);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        alreadyAccount = findViewById(R.id.tvAlreadyAccount);
    }

    private void setupUIListeners() {

        btnRegister.setOnClickListener(v -> registerUser());

        alreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(ActivityRegister.this, LoginActivity.class));
            finish();
        });
        etAddress.setOnClickListener(v -> {
            GPSTracker gpsTracker = new GPSTracker(ActivityRegister.this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                getAddressFromLocation(etAddress, latitude, longitude);
            } else {
                gpsTracker.showSettingsAlert();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    LocalDate selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                    tvAge.setText(String.valueOf(calculateAge(selectedDate)));
                }, year, month, day);
        datePickerDialog.show();
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void getAddressFromLocation(EditText etAddress, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressObj = addresses.get(0);
                String address = addressObj.getAddressLine(0);
                etAddress.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etMail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String uid = UUID.randomUUID().toString();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isEmailValid(email)) {
            Toast.makeText(ActivityRegister.this, "Invalid email address", Toast.LENGTH_SHORT).show();
        } else {
            String passwordError = getPasswordError(password);
            if (passwordError != null) {
                Toast.makeText(ActivityRegister.this, passwordError, Toast.LENGTH_SHORT).show();
            } else {

                progressDialog = ProgressDialog.show(this, "Registering", "Please wait...", true);

                Parent parent = new Parent(uid, name, phone, email, address, password,
                        latitude, longitude);

                if (parent != null) {
                    dataManager.db.createUser(email, parent, new DataManager.OnUserCreationListener() {
                        @Override
                        public void onUserCreated(String email) {
                            Toast.makeText(ActivityRegister.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(ActivityRegister.this, LoginActivity.class));
                            finish();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(ActivityRegister.this, "Registration failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, new DataManager.OnDataSavedListener() {
                        @Override
                        public void onSuccess(ObjectBoundary object) {
                            Toast.makeText(ActivityRegister.this, "Data saved successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(ActivityRegister.this, "Data saving failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }, new DataManager.OnUserUpdateListener() {
                        @Override
                        public void onSuccess() {
                            progressDialog.dismiss();
                            Toast.makeText(ActivityRegister.this, "Data updated successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(ActivityRegister.this, "Data updating failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("DataManager", "Error in createUser: " + exception.getMessage());
                        }
                    });
                }

            }
        }


    }


    private void showParentFields() {
        tvAge.setVisibility(View.GONE);
        etHourlyWage.setVisibility(View.GONE);
        etExperience.setVisibility(View.GONE);
        etNumberOfChildren.setVisibility(View.VISIBLE);
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getPasswordError(String password) {
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        Pattern lowerCasePattern = Pattern.compile("[a-z]");
        Pattern digitPattern = Pattern.compile("[0-9]");
        Pattern specialCharPattern = Pattern.compile("[^a-zA-Z0-9]");

        if (!upperCasePattern.matcher(password).find()) {
            return "Password must contain at least one uppercase letter";
        }
        if (!lowerCasePattern.matcher(password).find()) {
            return "Password must contain at least one lowercase letter";
        }
        if (!digitPattern.matcher(password).find()) {
            return "Password must contain at least one digit";
        }
        if (!specialCharPattern.matcher(password).find()) {
            return "Password must contain at least one special character";
        }
        return null;
    }

}