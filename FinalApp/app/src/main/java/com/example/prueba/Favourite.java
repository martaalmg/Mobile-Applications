package com.example.prueba;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

@Entity(foreignKeys = {@ForeignKey(entity = Event.class, parentColumns = "ID",
        childColumns = "EVENT_ID"), @ForeignKey(entity = User.class, parentColumns = "ID",
        childColumns = "USER_ID")})
public class Favourite {
    @PrimaryKey(autoGenerate = true)
    public long ID;

    @ColumnInfo(name = "EVENT_ID")
    public long event_id;

    @ColumnInfo(name = "USER_ID")
    public long user_id;

    @ColumnInfo(name = "PREFERENCE")
    public String preference_favourite;

    public Favourite() {
        this.ID = ID;
        this.event_id = event_id;
        this.user_id = user_id;
        this.preference_favourite = preference_favourite;

    }

    @Ignore
    public Favourite(long event_id, long user_id, String preference_favourite) {
        this.event_id = event_id;
        this.user_id = user_id;
        this.preference_favourite = preference_favourite;
    }

}

