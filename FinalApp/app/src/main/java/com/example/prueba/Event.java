package com.example.prueba;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;



@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
    public long ID;

    @ColumnInfo(name = "TITLE")
    public String title;

    @ColumnInfo(name = "LONGITUD")
    public Float longitude;

    @ColumnInfo(name = "LATITUD")
    public Float latitude;

    @ColumnInfo(name = "DESCRIPCION")
    public String description;

    @ColumnInfo(name = "IMAGE")
    public String image;

    @ColumnInfo(name = "PREFERENCE")
    public String preference_event;

    @ColumnInfo(name = "DATE")
    public long date;

    public Event() {
        this.ID = ID;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.image = image;
        this.preference_event = preference_event;
        this.date = date;
    }

    @Ignore
    public Event(String title, Float longitude, Float latitude, String description, String image, String preference_event, int date) {
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.image = image;
        this.preference_event = preference_event;
        this.date = date;
    }

    @Override // we will use it for changing EditText to String variable type
    public  String toString(){
        return title;
    }

}



