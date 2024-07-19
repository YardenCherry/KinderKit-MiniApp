package dev.netanelbcn.kinderkit.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Models.Parent;
import dev.netanelbcn.kinderkit.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    ProgressDialog progressDialog;
    DataManager dataManager;
    private Gson gson = new Gson();
    // private UserService userService;
    //private ParentService parentService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataManager = DataManager.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Please Wait...");

        binding.alreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ActivityRegister.class));
            finish();
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    progressDialog.show();
                    login(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Enter Email and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String email, String password) {
        dataManager.loginUser(email, password, new DataManager.OnLoginListener() {
            @Override
            public void onSuccess(Parent user) {
                progressDialog.dismiss();
                DataManager.getInstance().getParent().setMail(user.getMail());
                DataManager.getInstance().getParent().setName(user.getName());
                DataManager.getInstance().getParent().setUid(user.getUid());
                DataManager.getInstance().getParent().setPhone(user.getPhone());
                DataManager.getInstance().getParent().setAddress(user.getAddress());
                DataManager.getInstance().getParent().setPassword(user.getPassword());
                DataManager.getInstance().getParent().setLatitude(user.getLatitude());
                DataManager.getInstance().getParent().setLongitude(user.getLongitude());
                DataManager.getInstance().getParent().setKidsId(user.getKidsId());
                DataManager.getInstance().getParent().setKids(user.getKids());
                Log.d("kidos", user.getKids().toString());
                // ObjectBoundary myUserB=DataManager.getInstance().getParent().toBoundary();
                // myUserB.setObjectId();
                // DataManager.getInstance().db.setMyUserData(myUserB)
                ;
                // Log.d("userr",myUserB.toString()+"");
                //DataManager.getInstance().InitGeneralData();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}