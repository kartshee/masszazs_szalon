package com.example.myapplication;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.app.DatePickerDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class FoglalasActivity extends AppCompatActivity {

    private TextView masszazsTipusTextView;
    private TextView selectedDateTimeTextView;
    private Button dateButton, timeButton, confirmButton;

    private int selectedYear = 0, selectedMonth = -1, selectedDay = 0;
    private int selectedHour = -1, selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foglalas);

        masszazsTipusTextView = findViewById(R.id.masszazs_tipus);
        selectedDateTimeTextView = findViewById(R.id.selected_datetime);
        dateButton = findViewById(R.id.date_button);
        timeButton = findViewById(R.id.time_button);
        confirmButton = findViewById(R.id.confirm_button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "idopont_channel",
                    "Időpont Értesítések",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        String masszazsTipus = getIntent().getStringExtra("masszazs_tipus");
        masszazsTipusTextView.setText(masszazsTipus != null ? masszazsTipus : "Masszázs");

        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());

        confirmButton.setOnClickListener(v -> {
            if (selectedYear == 0 || selectedMonth == -1 || selectedDay == 0 || selectedHour == -1) {
                Toast.makeText(this, "Kérlek válassz dátumot és időpontot!", Toast.LENGTH_SHORT).show();
            } else {
                String datum = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                String idopont = String.format("%02d:%02d", selectedHour, selectedMinute);
                String tipus = masszazsTipusTextView.getText().toString();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Map<String, Object> foglalas = new HashMap<>();
                foglalas.put("userId", userId);
                foglalas.put("datum", datum);
                foglalas.put("idopont", idopont);
                foglalas.put("tipus", tipus);

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("foglalasok")
                        .add(foglalas)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(this, "Sikeres foglalás: " + datum + " " + idopont, Toast.LENGTH_LONG).show();

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "idopont_channel")
                                    .setSmallIcon(R.drawable.ic_launcher_foreground) // Használj saját ikont, ha van
                                    .setContentTitle("Foglalás megerősítve")
                                    .setContentText("Sikeres foglalás: " + tipus + " - " + datum + " " + idopont)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                    == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                notificationManager.notify(1002, builder.build());
                            }

                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Hiba történt a mentéskor!", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        updateSelectedText();
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedYear = year;
            selectedMonth = month;
            selectedDay = dayOfMonth;
            updateSelectedText();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        final String[] availableTimes = new String[11]; // 8-tól 18-ig, egész órák
        for (int i = 0; i <= 10; i++) {
            int hour = 8 + i;
            availableTimes[i] = String.format("%02d:00", hour);
        }

        new android.app.AlertDialog.Builder(this)
                .setTitle("Válassz időpontot (8:00-18:00)")
                .setItems(availableTimes, (dialog, which) -> {
                    String selectedTime = availableTimes[which];
                    selectedHour = Integer.parseInt(selectedTime.split(":")[0]);
                    selectedMinute = 0;
                    updateSelectedText();
                })
                .show();
    }

    private void updateSelectedText() {
        if (selectedYear != 0 && selectedMonth != -1 && selectedDay != 0 && selectedHour != -1) {
            String text = String.format("%04d-%02d-%02d %02d:%02d",
                    selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute);
            selectedDateTimeTextView.setText("Kiválasztott időpont: " + text);
        } else {
            selectedDateTimeTextView.setText("Nincs kiválasztva időpont");
        }
    }
}
