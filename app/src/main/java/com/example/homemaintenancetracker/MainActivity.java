package com.example.homemaintenancetracker;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView listView;
    private List<Task> taskList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.listView);
        taskList = new ArrayList<>();

        loadTasks();

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showEditDeleteDialog(position);
            }
        });
    }

    private void loadTasks() {
        taskList = dbHelper.getAllTasks();
        List<String> descriptions = new ArrayList<>();
        for (Task task : taskList) {
            descriptions.add(task.getDescription() + " - " + task.getDate());
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, descriptions);
        listView.setAdapter(adapter);
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Task");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_task, null);
        final EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        final EditText dateInput = dialogView.findViewById(R.id.dateInput);
        final EditText providerInput = dialogView.findViewById(R.id.providerInput);
        builder.setView(dialogView);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String description = descriptionInput.getText().toString();
                String date = dateInput.getText().toString();
                String provider = providerInput.getText().toString();
                dbHelper.addTask(description, date, provider);
                loadTasks();
                Toast.makeText(MainActivity.this, "Task Added", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditDeleteDialog(final int position) {
        Task selectedTask = taskList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Task");
        builder.setMessage("Task: " + selectedTask.getDescription());

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showEditTaskDialog(selectedTask);
            }
        });

        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbHelper.deleteTask(selectedTask.getId());
                loadTasks();
                Toast.makeText(MainActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    private void showEditTaskDialog(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);
        final EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        final EditText dateInput = dialogView.findViewById(R.id.dateInput);
        final EditText providerInput = dialogView.findViewById(R.id.providerInput);

        descriptionInput.setText(task.getDescription());
        dateInput.setText(task.getDate());
        providerInput.setText(task.getProvider());

        builder.setView(dialogView);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setDescription(descriptionInput.getText().toString());
                task.setDate(dateInput.getText().toString());
                task.setProvider(providerInput.getText().toString());
                dbHelper.updateTask(task);
                loadTasks();
                Toast.makeText(MainActivity.this, "Task Updated", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void test() {

            FirebaseDatabase db = FirebaseDatabase.getInstance();

            System.out.println();

    }
}
