package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    private static final int SECRET_KEY = 99;
    public static final String LOG_TAG = MainActivity.class.getName();
    public static final String PREF_KEY = MainActivity.class.getPackage().toString();

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);


        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG,"onCreate" );
    }

    public void login(View view) {
        String userStr = username.getText().toString();
        String passStr = password.getText().toString();


        if (username == null || password == null) {
            Log.e(LOG_TAG, "EditText fields are not initialized");
            return;
        }

        if (userStr.isEmpty() || passStr.isEmpty()) {
            // Ha a mezők üresek, jelezze a felhasználónak
            Toast.makeText(MainActivity.this, "Kérjük, töltse ki mindkét mezőt", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(userStr, passStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres belépés");
                    startMain();
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        Log.e(LOG_TAG, "Hiba a bejelentkezés során: " + exception.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void startMain() {
        Intent intent = new Intent(this, Fooldal.class);
        startActivity(intent);
    }
    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG,"onStart" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG,"onStop" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG,"onDestroy" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName",username.getText().toString());
        editor.putString("password",password.getText().toString());
        editor.apply();
        Log.i(LOG_TAG,"onPause" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG,"onResume" );
    }
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG,"onRestart" );
    }

    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Anonym sikeres belépés. UID: " + mAuth.getCurrentUser().getUid());

                    new Handler().postDelayed(() -> {
                        startMain();
                    }, 300); // 300 ms delay
                } else {
                    Log.d(LOG_TAG, "Anonym sikeres belépés. UID: " + mAuth.getCurrentUser().getUid());
                    Toast.makeText(MainActivity.this, "Anonym Sikertelen bejelentkezés: "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
