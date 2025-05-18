package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditAppointmentActivity extends AppCompatActivity {

    private TextView masszazsTipusTextView;
    private TextView selectedDateTimeTextView;
    private Button dateButton, timeButton, saveButton;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private String appointmentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        masszazsTipusTextView = findViewById(R.id.masszazs_tipus);
        selectedDateTimeTextView = findViewById(R.id.selected_datetime);
        dateButton = findViewById(R.id.date_button);
        timeButton = findViewById(R.id.time_button);
        saveButton = findViewById(R.id.save_button);

        appointmentId = getIntent().getStringExtra("appointmentId");
        String tipus = getIntent().getStringExtra("masszazsTipus");
        String datum = getIntent().getStringExtra("datum");
        String idopont = getIntent().getStringExtra("idopont");

        masszazsTipusTextView.setText(tipus);

        if (datum != null && !datum.isEmpty()) {
            String[] dateParts = datum.split("-");
            selectedYear = Integer.parseInt(dateParts[0]);
            selectedMonth = Integer.parseInt(dateParts[1]) - 1; // hónap 0-indexelt
            selectedDay = Integer.parseInt(dateParts[2]);
        }
        if (idopont != null && !idopont.isEmpty()) {
            String[] timeParts = idopont.split(":");
            selectedHour = Integer.parseInt(timeParts[0]);
            selectedMinute = Integer.parseInt(timeParts[1]);
        }

        updateSelectedText();

        dateButton.setOnClickListener(v -> showDatePicker());
        timeButton.setOnClickListener(v -> showTimePicker());
        saveButton.setOnClickListener(v -> saveChanges());
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedYear = year;
                    selectedMonth = month;
                    selectedDay = dayOfMonth;
                    updateSelectedText();
                },
                selectedYear != 0 ? selectedYear : c.get(Calendar.YEAR),
                selectedMonth != 0 ? selectedMonth : c.get(Calendar.MONTH),
                selectedDay != 0 ? selectedDay : c.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker() {
        String[] availableTimes = {"08:00", "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00", "17:00"};

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Válassz időpontot")
                .setItems(availableTimes, (dialog, which) -> {
                    String selectedTime = availableTimes[which];
                    String[] parts = selectedTime.split(":");
                    selectedHour = Integer.parseInt(parts[0]);
                    selectedMinute = Integer.parseInt(parts[1]);
                    updateSelectedText();
                })
                .show();
    }


    private void updateSelectedText() {
        if (selectedYear != 0 && selectedDay != 0) {
            String text = String.format("%04d-%02d-%02d %02d:%02d",
                    selectedYear, selectedMonth + 1, selectedDay, selectedHour, selectedMinute);
            selectedDateTimeTextView.setText("Kiválasztott időpont: " + text);
        }
    }

    private void saveChanges() {
        if (selectedYear == 0 || selectedDay == 0) {
            Toast.makeText(this, "Kérlek válassz dátumot és időpontot!", Toast.LENGTH_SHORT).show();
            return;
        }

        String ujDatum = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
        String ujIdopont = String.format("%02d:%02d", selectedHour, selectedMinute);
        String tipus = masszazsTipusTextView.getText().toString();

        Map<String, Object> updated = new HashMap<>();
        updated.put("datum", ujDatum);
        updated.put("idopont", ujIdopont);
        updated.put("tipus", tipus);

        FirebaseFirestore.getInstance()
                .collection("foglalasok")
                .document(appointmentId)
                .update(updated)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Foglalás sikeresen frissítve", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Frissítés sikertelen: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
