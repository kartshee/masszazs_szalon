package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String LOG_TAG = Register.class.getName();
    public static final String PREF_KEY = Register.class.getPackage().toString();
    private SharedPreferences preferences;
    private static final int SECRET_KEY=99;
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText userPasswordEditText;
    EditText phoneEdittext;
    Spinner spinner;
    EditText adressEditText;

    private FirebaseAuth mauth;

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


    EditText userPasswordUjraEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

       int secret_key=  getIntent().getIntExtra("SECRET_KEY",0);

       if (secret_key!=99){
           finish();
       }

        userNameEditText = findViewById(R.id.usernameedittext);
        userEmailEditText = findViewById(R.id.userEmail);
        userPasswordEditText = findViewById(R.id.passwordEditText);
        userPasswordUjraEditText = findViewById(R.id.passwordUjra);
        phoneEdittext = findViewById(R.id.phoneEditText);
        spinner = findViewById(R.id.phoneSpinner);
        adressEditText = findViewById(R.id.adress);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password= preferences.getString("password", "");

        userNameEditText.setText(userName);
        userNameEditText.setText(password);


        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.phone_modes,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mauth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG,"onCreate" );




    }

    public void register(View view) {
        String userName = userNameEditText.getText().toString().trim();
        String userEmail = userEmailEditText.getText().toString().trim();
        String userPassword = userPasswordEditText.getText().toString();
        String userPasswordUjra = userPasswordUjraEditText.getText().toString();
        String phoneNumber = phoneEdittext.getText().toString().trim();
        String address = adressEditText.getText().toString().trim();

        if (userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() ||
                userPasswordUjra.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {

            Toast.makeText(this, "Kérlek, tölts ki minden mezőt!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userPassword.equals(userPasswordUjra)) {
            Toast.makeText(this, "A megadott jelszavak nem egyeznek!", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Nem egyezik a két jelszó.");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + userName + " Email: " + userEmail);

        mauth.createUserWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Sikeres regisztráció");

                    String userId = mauth.getCurrentUser().getUid();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> userData = new HashMap<>();
                    userData.put("felhasznalonev", userName);
                    userData.put("email", userEmail);
                    userData.put("telefon", phoneNumber);
                    userData.put("cim", address);

                    db.collection("felhasznalok").document(userId).set(userData)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(LOG_TAG, "Felhasználói adatok mentve Firestore-ba");
                                startMain();
                            })
                            .addOnFailureListener(e -> {
                                Log.w(LOG_TAG, "Hiba Firestore mentéskor", e);
                                Toast.makeText(Register.this, "Sikertelen adatmentés", Toast.LENGTH_SHORT).show();
                            });
                }

            }
        });
    }


    protected void startMain(){
        Intent intent = new Intent(this, Fooldal.class);
        startActivity(intent);
    }
    public void cancel(View view) {
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       String selectedItem=  parent.getItemAtPosition(position).toString();
       Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}