package com.example.myapplication.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Модель задачи.
 */
@Entity(tableName = "tasks")
public class Task {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String title;
    private String description;
    private int priority; // 0 - Высокий, 1 - Средний, 2 - Низкий
    private boolean isCompleted;
    private long dueDate;

    // Конструктор
    public Task(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.isCompleted = false;
    }

    // Геттеры
    public long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getPriority() { return priority; }
    public boolean isCompleted() { return isCompleted; }
    public long getDueDate() { return dueDate; }

    // Сеттеры
    public void setId(long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
}
