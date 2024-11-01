package com.example.homemaintenancetracker;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<Item> taskItems;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.myDb = firebaseDatabase.getReference("Tasks");
        this.taskItems = new ArrayList<>();
        loadItemTasks();
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> showAddTaskDialog());

        listView.setOnItemClickListener((parent, view, position, id) -> showEditDeleteDialog(position));
    }

    public void loadItemTasks() {
        taskItems.clear();
        myDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Item screen = snapshot.getValue(Item.class);
                    taskItems.add(screen);
                    displayTasksOnLoad();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error: " + databaseError.getMessage());
            }
        });

    }

    private void displayTasksOnLoad() {
        List<String> descriptions = new ArrayList<>();
        for (Item task : taskItems) {
            descriptions.add(task.getTask() + " - " + task.getDate());
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

        builder.setPositiveButton("Add", (dialog, which) -> {
            String description = descriptionInput.getText().toString();
            String date = dateInput.getText().toString();
            String provider = providerInput.getText().toString();
            Item item = new Item();
            item.setTask(description);
            item.setDate(date);
            item.setProvider(provider);
            addTaskItem(item);
            loadItemTasks();
            Toast.makeText(MainActivity.this, "Task Added", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addTaskItem(final Item item) {
        item.setId(taskItems.size() + 1);
        taskItems.add(item);
        myDb.setValue(taskItems);
    }

    private void showEditDeleteDialog(final int position) {
        Item selectedTask = taskItems.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit or Delete Task");
        builder.setMessage("Task: " + selectedTask.getTask());

        builder.setPositiveButton("Edit", (dialog, which) -> showEditTaskDialog(selectedTask));

        builder.setNegativeButton("Delete", (dialog, which) -> {
            taskItems.remove(selectedTask);
            myDb.setValue(taskItems);
            loadItemTasks();
            Toast.makeText(MainActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNeutralButton("Cancel", null);
        builder.show();
    }

    private void showEditTaskDialog(Item task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);
        final EditText descriptionInput = dialogView.findViewById(R.id.descriptionInput);
        final EditText dateInput = dialogView.findViewById(R.id.dateInput);
        final EditText providerInput = dialogView.findViewById(R.id.providerInput);

        descriptionInput.setText(task.getTask());
        dateInput.setText(task.getDate());
        providerInput.setText(task.getProvider());

        builder.setView(dialogView);

        builder.setPositiveButton("Save", (dialog, which) -> {
            task.setTask(descriptionInput.getText().toString());
            task.setDate(dateInput.getText().toString());
            task.setProvider(providerInput.getText().toString());
            myDb.setValue(taskItems);
            loadItemTasks();
            Toast.makeText(MainActivity.this, "Task Updated", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
