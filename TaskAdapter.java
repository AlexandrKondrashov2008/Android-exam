package com.example.myapplication.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Task;
import java.util.ArrayList;
import java.util.List;

/**
 * Адаптер для отображения списка задач в RecyclerView.
 * Поддерживает приоритеты, статус выполнения и удаление свайпом.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;

    /**
     * Интерфейс для обработки кликов по задаче.
     */
    public interface OnTaskClickListener {
        void onTaskCheckedChanged(Task task, boolean isChecked);
    }

    /**
     * Устанавливает слушатель кликов.
     *
     * @param listener Слушатель
     */
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    /**
     * Устанавливает новый список задач.
     *
     * @param tasks Новый список задач
     */
    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    /**
     * Возвращает задачу по позиции (для удаления свайпом).
     *
     * @param position Позиция в списке
     * @return Задача
     */
    public Task getTaskAtPosition(int position) {
        return tasks.get(position);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    /**
     * ViewHolder для одной задачи.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardTask;
        private final View priorityStrip;
        private final CheckBox checkBoxCompleted;
        private final TextView textTitle;
        private final TextView textDescription;
        private final TextView textDueDate;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTask = (CardView) itemView;
            priorityStrip = itemView.findViewById(R.id.priority_strip);
            checkBoxCompleted = itemView.findViewById(R.id.checkbox_completed);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            textDueDate = itemView.findViewById(R.id.text_due_date);
        }

        /**
         * Привязывает данные задачи к View.
         *
         * @param task     Задача
         * @param listener Слушатель кликов
         */
        public void bind(Task task, OnTaskClickListener listener) {
            // Название задачи
            textTitle.setText(task.getTitle());

            // Описание (показываем, если есть)
            if (task.getDescription() != null && !task.getDescription().isEmpty()) {
                textDescription.setText(task.getDescription());
                textDescription.setVisibility(View.VISIBLE);
            } else {
                textDescription.setVisibility(View.GONE);
            }

            // Дата (пока не используем, скрываем)
            textDueDate.setVisibility(View.GONE);

            // Цвет полосы приоритета
            int priorityColor;
            switch (task.getPriority()) {
                case 0: // Высокий
                    priorityColor = 0xFFFF4444; // Красный
                    break;
                case 1: // Средний
                    priorityColor = 0xFFFFBB44; // Жёлтый
                    break;
                default: // Низкий
                    priorityColor = 0xFF44CC44; // Зелёный
            }
            priorityStrip.setBackgroundColor(priorityColor);

            // Стиль для выполненной задачи
            if (task.isCompleted()) {
                cardTask.setAlpha(0.6f);
                textTitle.setPaintFlags(textTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                checkBoxCompleted.setChecked(true);
            } else {
                cardTask.setAlpha(1.0f);
                textTitle.setPaintFlags(textTitle.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                checkBoxCompleted.setChecked(false);
            }

            // Обработчик чекбокса
            checkBoxCompleted.setOnCheckedChangeListener(null);
            checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    task.setCompleted(isChecked);
                    listener.onTaskCheckedChanged(task, isChecked);
                }
            });
        }
    }
}
