package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Fooldal extends AppCompatActivity {

    private static final String LOG_TAG = Fooldal.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooldal);


        Log.d(LOG_TAG, "Fooldal elindult");

        CardView card1 = findViewById(R.id.card1);
        CardView card2 = findViewById(R.id.card2);

        setCardTouchEffect(card1);
        setCardTouchEffect(card2);

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().toLowerCase().trim();

                String tipus1 = "kopoly masszázs";
                String tipus2 = "svéd masszázs";

                if (query.isEmpty()) {
                    card1.setVisibility(View.VISIBLE);
                    card2.setVisibility(View.VISIBLE);
                } else {
                    card1.setVisibility(tipus1.contains(query) ? View.VISIBLE : View.GONE);
                    card2.setVisibility(tipus2.contains(query) ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        card1.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                showLoginAlert();
            } else {
                Intent intent = new Intent(Fooldal.this, FoglalasActivity.class);
                intent.putExtra("masszazs_tipus", "Kopoly masszázs");
                startActivity(intent);
            }
        });

        card2.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                showLoginAlert();
            } else {
                Intent intent = new Intent(Fooldal.this, FoglalasActivity.class);
                intent.putExtra("masszazs_tipus", "Svéd masszázs");
                startActivity(intent);
            }
        });

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logout());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        View rootView = findViewById(android.R.id.content);
        fadeInView(rootView);
    }

    private void fadeInView(View view) {
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(700);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            Intent intent = new Intent(Fooldal.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(Fooldal.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setCardTouchEffect(CardView card) {
        card.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    scaleCard(v, 1.1f);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    new Handler().postDelayed(() -> scaleCard(v, 1f), 1000);
                    break;
            }
            return false;
        });
    }

    private void scaleCard(View card, float scale) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1f, scale,
                1f, scale,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        card.startAnimation(scaleAnimation);
    }

    private void showLoginAlert() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Bejelentkezés szükséges")
                .setMessage("Foglaláshoz előbb be kell jelentkezned.")
                .setPositiveButton("Bejelentkezés", (dialog, which) -> {
                    Intent intent = new Intent(Fooldal.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Mégsem", null)
                .show();
    }
}
