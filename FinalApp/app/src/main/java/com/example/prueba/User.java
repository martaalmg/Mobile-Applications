package com.example.prueba;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = true)
    public long ID;

    @ColumnInfo(name = "NAME")
    public String user_name;

    @ColumnInfo(name = "EMAIL")
    public String email;

    @ColumnInfo(name = "PASSWORD")
    public String password;

    @ColumnInfo(name = "LANGUAGE")
    public String language;

    @ColumnInfo(name = "PREFERENCE")
    public String preference;

    @ColumnInfo(name = "TELEFONO")
    public String phone;

    public User() {
        this.ID = ID;
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.language = language;
        this.preference = preference;
        this.phone = phone;
    }

    @Ignore
    public User(String user_name, String email, String password, String language, String preference, String phone) {
        this.user_name = user_name;
        this.email = email;
        this.password = password;
        this.language = language;
        this.preference = preference;
        this.phone = phone;
    }

    @Override // we will use it for changing EditText to String variable type
    public  String toString(){
        return user_name;
    }

}



