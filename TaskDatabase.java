package com.example.myapplication.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.myapplication.model.Task;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {

    private static volatile TaskDatabase instance;

    public abstract TaskDao taskDao();

    public static synchronized TaskDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    TaskDatabase.class,
                    "task_database"
            ).build();
        }
        return instance;
    }
}
