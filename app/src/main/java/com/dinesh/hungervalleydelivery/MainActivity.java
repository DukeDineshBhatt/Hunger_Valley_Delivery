package com.dinesh.hungervalleydelivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    public static final String PREFS_NAME = "MyPrefsFile";
    String user_id, token,name;
    Button orders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("hasLoggedIn", true);
        editor.commit();

        SharedPreferences shared = getSharedPreferences("myAppPrefs", MODE_PRIVATE);
        user_id = (shared.getString("user_id", ""));
        token = (shared.getString("token", ""));
        name = (shared.getString("name", ""));

        Toast.makeText(this, user_id, Toast.LENGTH_SHORT).show();
        orders = findViewById(R.id.orders);

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, NewOrdersActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);

            }
        });
    }
}