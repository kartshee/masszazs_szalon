package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private TextView emailText;
    private ListView appointmentsListView;
    private FirebaseFirestore db;
    private ArrayList<Appointment> appointmentsList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        emailText = findViewById(R.id.emailText);
        appointmentsListView = findViewById(R.id.appointmentsListView);
        db = FirebaseFirestore.getInstance();
        appointmentsList = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            emailText.setText("Bejelentkezve: " + user.getEmail());
            loadAppointments(user.getUid());
        } else {
            emailText.setText("Nincs bejelentkezett felhasználó.");
        }

        View rootView = findViewById(android.R.id.content);
        fadeInView(rootView);
    }
    private void fadeInView(View view) {
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(700);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    private void loadAppointments(String userId) {
        db.collection("foglalasok")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        for (DocumentSnapshot document : querySnapshot) {
                            Appointment appointment = document.toObject(Appointment.class);
                            appointment.setId(document.getId());
                            appointmentsList.add(appointment);
                        }

                        AppointmentAdapter adapter = new AppointmentAdapter(ProfileActivity.this, appointmentsList);
                        appointmentsListView.setAdapter(adapter);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hiba történt a foglalások betöltésekor.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            appointmentsList.clear();
            loadAppointments(user.getUid());
        }
    }

}
