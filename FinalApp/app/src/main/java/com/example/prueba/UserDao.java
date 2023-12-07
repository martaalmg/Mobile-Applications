package com.example.prueba;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE email LIKE :email LIMIT 1")
    User selectByEmail(String email);

    @Query("SELECT * FROM User WHERE name LIKE :name LIMIT 1")
    User selectByName(String name);

    @Query("SELECT * FROM User WHERE telefono LIKE :phone LIMIT 1")
    User selectByPhone(String phone);

    @Insert()
    long insert(User user);
}