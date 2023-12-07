package com.example.prueba;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface FavouriteDao {
    @Query("SELECT * FROM Favourite")
    List<Favourite> selectAll();

    @Query("SELECT * FROM Favourite WHERE event_id = :event_id")
    List<Favourite> selectByEventId(long event_id);

    @Query("SELECT * FROM Favourite WHERE user_id = :user_id")
    List<Favourite> selectByUserId(long user_id);

    @Query("SELECT * FROM Favourite WHERE user_id = :user_id AND event_id = :event_id LIMIT 1")
    Favourite selectByUserAndEventId(long user_id, long event_id);

    @Query("SELECT * FROM Favourite WHERE ID = :ID")
    Favourite selectByID(int ID);

    @Insert()
    long insert(Favourite favourite);

    @Delete
    int delete(Favourite favourite);

}