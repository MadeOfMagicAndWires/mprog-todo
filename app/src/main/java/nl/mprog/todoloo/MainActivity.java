package nl.mprog.todoloo;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> tasks = new ArrayList<>();
    ArrayAdapter<String> tasksAdapter;
    boolean noTasks = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readTasks();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds an item to the tasklist and refreshes the List- and EditText views
     * Will also remove the dummy task 'Add a new task' if it was present.
     * @param view floating action button, not actually used.
     */
    public void addTask(View view) {
        EditText taskView = (EditText) findViewById(R.id.newtask_text);
        String newtask = taskView.getText().toString();

        if(newtask.isEmpty()) {
            Snackbar.make(taskView,R.string.empty_task, Snackbar.LENGTH_SHORT).show();
        }
        else {
            tasks.add(newtask);

            if (noTasks) {
                tasks.remove(tasks.remove(0));
                noTasks = false;
            }

            tasksAdapter.notifyDataSetChanged();
            taskView.setText("");
        }
    }

    /**
     * Calls writeTasks() whenever the Activity reaches the onStop phase.
     */
    @Override
    public void onStop() {
        super.onStop();
        if(!noTasks) {writeTasks();}

    }

    /**
     *  Reads tasks from the file 'tasks' and initializes the List from it.
     *  Each line in the file corresponds to one task in the list.
     *  If the file is empty readTasks() provides a dummy task called "Add a new task"
     */

    public void readTasks() {
        Scanner scan;

        try {
            scan = new Scanner(openFileInput("tasks"));
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                tasks.add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(tasks.isEmpty()) {
            tasks.add(0, getString(R.string.no_tasks));
            noTasks = true;
        }

        tasksAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tasks);

        ListView tasksList = (ListView) findViewById(R.id.list);
        tasksList.setAdapter(tasksAdapter);
        tasksList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView taskView = (TextView) view;
                String task = String.valueOf(taskView.getText());
                if(removeTask(task)) {
                    tasksAdapter.notifyDataSetChanged();
                    return true;
                }
                else {
                    return false;
                }
            }
        });


    }

    /**
     * Removes a task from the task list
     * If no tasks remain after the current task is removed,
     * then it adds the dummy task 'Add a new task'
     * @param task item to delete
     * @return true if the task was in the list and is now removed, false if otherwise.
     */
    public boolean removeTask(String task) {
        if(tasks.contains(task)) {
            tasks.remove(tasks.indexOf(task));
            //Log.v("tasks", "removed task: " + task)

            if(tasks.isEmpty()) {
                tasks.add(0, getString(R.string.no_tasks));
                noTasks = true;
            }

            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Writes the task list to the file 'tasks', one task per line.
     *
     */
    public void writeTasks() {

        PrintStream out;
        try{
            out = new PrintStream(openFileOutput("tasks", MODE_PRIVATE));

            for(int i=0;i<tasks.size();i++) {
                out.println(tasks.get(i));
                //Log.v("tasks", "Wrote " + tasks.get(i));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.v("tasks", "saved tasks");
    }

}
