package com.example.myapplication.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.myapplication.model.Task;
import com.example.myapplication.repository.TaskRepository;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository repository;
    private final LiveData<List<Task>> allTasks;

    public TaskViewModel(Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        repository.insertTask(task);
    }

    public void update(Task task) {
        repository.updateTask(task);
    }

    public void delete(Task task) {
        repository.deleteTask(task);
    }

    public void deleteAllCompletedTasks() {
        repository.deleteAllCompletedTasks();
    }
}
