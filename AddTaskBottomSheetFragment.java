package com.example.myapplication.ui;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.myapplication.R;
import com.example.myapplication.model.Task;
import com.example.myapplication.notifications.AlarmReceiver;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Calendar;

public class AddTaskBottomSheetFragment extends BottomSheetDialogFragment {

    private EditText editTitle;
    private EditText editDescription;
    private Spinner spinnerPriority;
    private Button buttonSave;
    private Button buttonDate;
    private TextView textSelectedDate;
    private OnTaskAddedListener listener;
    private long selectedDateTimestamp = 0;

    public interface OnTaskAddedListener {
        void onTaskAdded(Task task);
    }

    public void setOnTaskAddedListener(OnTaskAddedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_add_task, container, false);

        editTitle = view.findViewById(R.id.edit_title);
        editDescription = view.findViewById(R.id.edit_description);
        spinnerPriority = view.findViewById(R.id.spinner_priority);
        buttonSave = view.findViewById(R.id.button_save);
        buttonDate = view.findViewById(R.id.button_date);
        textSelectedDate = view.findViewById(R.id.text_selected_date);

        buttonDate.setOnClickListener(v -> showDatePicker());
        buttonSave.setOnClickListener(v -> saveTask());

        return view;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth, 10, 0);
                    selectedDateTimestamp = selected.getTimeInMillis();

                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    textSelectedDate.setText("📅 Напоминание: " + date);
                    textSelectedDate.setVisibility(View.VISIBLE);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void saveTask() {
        String title = editTitle.getText().toString().trim();
        if (title.isEmpty()) {
            editTitle.setError("Введите название задачи");
            return;
        }

        String description = editDescription.getText().toString().trim();
        int priority = spinnerPriority.getSelectedItemPosition();

        Task newTask = new Task(title, description, priority);
        newTask.setDueDate(selectedDateTimestamp);

        // Устанавливаем напоминание
        if (selectedDateTimestamp > 0) {
            scheduleNotification(title, description, selectedDateTimestamp);
        }

        if (listener != null) {
            listener.onTaskAdded(newTask);
        }

        dismiss();
        Toast.makeText(getContext(), "Задача добавлена", Toast.LENGTH_SHORT).show();
    }

    private void scheduleNotification(String title, String description, long timeInMillis) {
        if (getContext() == null) return;

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                (int) timeInMillis,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
        }
    }
}
