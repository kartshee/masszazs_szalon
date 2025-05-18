package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context context;
    private ArrayList<Appointment> appointments;

    public AppointmentAdapter(@NonNull Context context, ArrayList<Appointment> appointments) {
        super(context, 0, appointments);
        this.context = context;
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Appointment appointment = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.appointment_item, parent, false);
        }

        TextView tipusText = convertView.findViewById(R.id.masszazsTipus);
        TextView datumText = convertView.findViewById(R.id.datum);
        TextView idopontText = convertView.findViewById(R.id.idopont);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        if (appointment != null) {
            tipusText.setText(appointment.getMasszazsTipus());
            datumText.setText(appointment.getDatum());
            idopontText.setText(appointment.getIdopont());

            deleteButton.setOnClickListener(v -> {
                String appointmentId = appointment.getId();
                FirebaseFirestore.getInstance()
                        .collection("foglalasok")
                        .document(appointmentId)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            appointments.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Foglalás törölve", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Hiba történt a törléskor", Toast.LENGTH_SHORT).show();
                        });
            });
        }

        Button editButton = convertView.findViewById(R.id.edit_button);

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditAppointmentActivity.class);
            intent.putExtra("appointmentId", appointment.getId());
            intent.putExtra("datum", appointment.getDatum());
            intent.putExtra("idopont", appointment.getIdopont());
            intent.putExtra("masszazsTipus", appointment.getMasszazsTipus());
            context.startActivity(intent);
        });


        return convertView;
    }

}
