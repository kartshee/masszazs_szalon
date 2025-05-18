package com.example.myapplication;

import java.util.Date;

public class Appointment {
    private String id;
    private String masszazsTipus;
    private String idopont;
    private String datum;
    private String userId;

    public Appointment() {}

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getIdopont() { return idopont; }
    public void setIdopont(String idopont) { this.idopont = idopont; }


    public String getMasszazsTipus() {
        return masszazsTipus;
    }

    public void setMasszazsTipus(String masszazsTipus) {
        this.masszazsTipus = masszazsTipus;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }


}
