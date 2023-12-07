package com.example.prueba;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;
@Dao
public interface EventDao {
    @Query("SELECT * FROM Event")
    List<Event> selectAll();

    @Query("SELECT * FROM event WHERE preference = :preference")
    List<Event> selectByPreference(String preference);

    @Query("SELECT * FROM event WHERE ID = :ID")
    Event selectByID(int ID);

    @Query("SELECT * FROM event WHERE Title = :title")
    Event selectByTitle(String title);

}