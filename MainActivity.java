package com.example.myapplication;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.model.Task;
import com.example.myapplication.ui.AddTaskBottomSheetFragment;
import com.example.myapplication.ui.TaskAdapter;
import com.example.myapplication.viewmodel.TaskViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AddTaskBottomSheetFragment.OnTaskAddedListener {

    private TaskAdapter taskAdapter;
    private TaskViewModel viewModel;
    private TextView textCounter;
    private TabLayout tabLayout;
    private List<Task> allTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настройка Toolbar для отображения меню
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        textCounter = findViewById(R.id.text_counter);

        // Настройка адаптера
        taskAdapter = new TaskAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        // Наблюдение за изменениями в базе данных
        viewModel.getAllTasks().observe(this, tasks -> {
            allTasks = tasks;
            applyCurrentFilter();
            updateCounter();
        });

        // Обработчик изменения статуса задачи (чекбокс)
        taskAdapter.setOnTaskClickListener((task, isChecked) -> {
            task.setCompleted(isChecked);
            viewModel.update(task);
        });

        // Удаление свайпом влево
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task taskToDelete = taskAdapter.getTaskAtPosition(position);

                viewModel.delete(taskToDelete);

                Snackbar.make(recyclerView, "Задача удалена", Snackbar.LENGTH_LONG)
                        .setAction("Отмена", v -> {
                            viewModel.insert(taskToDelete);
                        })
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Настройка вкладок
        setupTabs();

        // Кнопка добавления задачи
        fabAdd.setOnClickListener(v -> {
            AddTaskBottomSheetFragment bottomSheet = new AddTaskBottomSheetFragment();
            bottomSheet.setOnTaskAddedListener(this);
            bottomSheet.show(getSupportFragmentManager(), "AddTaskBottomSheet");
        });
    }

    @Override
    public void onTaskAdded(Task task) {
        viewModel.insert(task);
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Все"));
        tabLayout.addTab(tabLayout.newTab().setText("Активные"));
        tabLayout.addTab(tabLayout.newTab().setText("Выполненные"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                applyCurrentFilter();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        applyCurrentFilter();
    }

    private void applyCurrentFilter() {
        if (allTasks == null) return;

        int selectedPosition = tabLayout.getSelectedTabPosition();
        List<Task> filteredTasks = new ArrayList<>();

        for (Task task : allTasks) {
            if (selectedPosition == 0) {
                filteredTasks.add(task);
            } else if (selectedPosition == 1 && !task.isCompleted()) {
                filteredTasks.add(task);
            } else if (selectedPosition == 2 && task.isCompleted()) {
                filteredTasks.add(task);
            }
        }

        taskAdapter.setTasks(filteredTasks);
    }

    private void updateCounter() {
        if (allTasks == null) return;

        int total = allTasks.size();
        int completed = 0;

        for (Task task : allTasks) {
            if (task.isCompleted()) {
                completed++;
            }
        }

        textCounter.setText("Всего: " + total + " | Выполнено: " + completed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Устанавливаем состояние чекбокса тёмной темы
        MenuItem themeItem = menu.findItem(R.id.action_dark_theme);
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        themeItem.setChecked(currentMode == AppCompatDelegate.MODE_NIGHT_YES);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_dark_theme) {
            // Переключаем тему
            boolean isChecked = !item.isChecked();
            item.setChecked(isChecked);

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
            return true;

        } else if (id == R.id.action_delete_completed) {
            // Удаляем все выполненные задачи
            viewModel.deleteAllCompletedTasks();
            Snackbar.make(findViewById(R.id.recycler_view), "Выполненные задачи удалены", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
