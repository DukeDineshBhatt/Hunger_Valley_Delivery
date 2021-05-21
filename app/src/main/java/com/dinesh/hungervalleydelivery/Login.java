package com.dinesh.hungervalleydelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity {

    ProgressBar progressbar;
    private Toolbar toolbar;
    int flags;
    EditText editTextMobile,editTextName;
    Button btn_continue;
    int randomNumber;
    String base;
    TextView login;
    DatabaseReference mOtpdatabase;
    String sender_id, message, authorization,success_message_txt;
    String mobile,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        flags = getWindow().getDecorView().getSystemUiVisibility(); // get current flag
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;   // add LIGHT_STATUS_BAR to flag
        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().setStatusBarColor(Color.WHITE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setTitle("Login");

        FirebaseApp.initializeApp(this);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        editTextMobile = findViewById(R.id.mobile);
        editTextName = findViewById(R.id.name);
        progressbar = findViewById(R.id.progressbar);
        login = findViewById(R.id.login);


        final DatabaseReference usersRef = database.getReference("DeliveryBoys");

        mOtpdatabase = database.getReference("Admin").child("OTP");

        mOtpdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                sender_id = dataSnapshot.child("sender_id").getValue().toString();
                message = dataSnapshot.child("message").getValue().toString();
                authorization = dataSnapshot.child("authorization").getValue().toString();
                success_message_txt = dataSnapshot.child("success_message_txt").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressbar.setVisibility(View.VISIBLE);

                mobile = editTextMobile.getText().toString().trim();
                name = editTextName.getText().toString().trim();

                if (mobile.isEmpty() || mobile.length() < 10) {
                    progressbar.setVisibility(View.GONE);

                    editTextMobile.setError("Enter a valid mobile number");
                    editTextMobile.requestFocus();
                    return;

                } else if (name.isEmpty()) {
                    progressbar.setVisibility(View.GONE);

                    editTextName.setError("Enter name");
                    editTextName.requestFocus();
                    return;

                } else {

                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {

                            if (snapshot.hasChild(mobile)) {

                                progressbar.setVisibility(View.GONE);

                                Toast.makeText(Login.this, "Account already exist with this mobile number!", Toast.LENGTH_LONG).show();

                                editTextMobile.setError("Account Already exist!");
                                editTextMobile.requestFocus();

                            } else {

                                Random random = new Random();
                                randomNumber = random.nextInt(99999);

                                Bean b = (Bean) getApplicationContext();

                                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                                logging.level(HttpLoggingInterceptor.Level.HEADERS);
                                logging.level(HttpLoggingInterceptor.Level.BODY);

                                OkHttpClient client = new OkHttpClient.Builder().writeTimeout(1000, TimeUnit.SECONDS).readTimeout(1000, TimeUnit.SECONDS).connectTimeout(1000, TimeUnit.SECONDS).addInterceptor(logging).build();

                                base = b.baseurl;

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(b.baseurl)
                                        .client(client)
                                        .addConverterFactory(ScalarsConverterFactory.create())
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                AllApiIneterface cr = retrofit.create(AllApiIneterface.class);

                                Call<OtpBean> call = cr.getOtp(sender_id, "english", "qt", editTextMobile.getText().toString(),
                                        message, "{#AA#}", String.valueOf(randomNumber), authorization);
                                call.enqueue(new Callback<OtpBean>() {
                                    @Override
                                    public void onResponse(@NotNull Call<OtpBean> call, @NotNull Response<OtpBean> response) {

                                        if (response.body().getMessage().get(0).equals(success_message_txt)) {


                                            Intent intent = new Intent(Login.this, OTP.class);
                                            intent.putExtra("OTP", randomNumber);
                                            intent.putExtra("mobile", mobile);
                                            intent.putExtra("name", name);
                                            startActivity(intent);

                                        } else {
                                            Toast.makeText(Login.this, "Please try again", Toast.LENGTH_SHORT).show();

                                        }

                                        progressbar.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onFailure(Call<OtpBean> call, Throwable t) {
                                        progressbar.setVisibility(View.GONE);
                                        Toast.makeText(Login.this, "Some error occured", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                            progressbar.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });
    }
}
