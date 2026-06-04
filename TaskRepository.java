package com.example.myapplication.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.myapplication.data.TaskDao;
import com.example.myapplication.data.TaskDatabase;
import com.example.myapplication.model.Task;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {

    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(Context context) {
        TaskDatabase database = TaskDatabase.getInstance(context);
        this.taskDao = database.taskDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Task>> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public void insertTask(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void updateTask(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void deleteTask(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public void deleteAllCompletedTasks() {
        executorService.execute(() -> taskDao.deleteAllCompletedTasks());
    }
}
