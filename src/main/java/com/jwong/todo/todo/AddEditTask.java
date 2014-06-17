package com.jwong.todo.todo;

/**
 * Created by jonathan on 5/30/2014.
 */

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.AsyncTask;
import android.app.DialogFragment;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddEditTask extends Activity{

    private long rowID;
    private EditText taskNameEditText;
    private EditText taskDescriptionEditText;
    private TextView dateTextView;
    private TextView completeDateTextView;

    private int dateYear;
    private int dateMonth;
    private int dateDay;
    private String dateString;

    private static final String TAG = "AddEditTask";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_task);

        taskNameEditText = (EditText) findViewById(R.id.taskNameEditText);
        taskDescriptionEditText = (EditText) findViewById(R.id.taskDescriptionEditText);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        completeDateTextView = (TextView) findViewById(R.id.completeDateTextView);
        dateYear = 0;

        Bundle extras = getIntent().getExtras(); // get Bundle of extras

        if (extras != null)
        {
            dateString = extras.getString("date");
            rowID = extras.getLong("row_id");
            taskNameEditText.setText(extras.getString("name"));
            taskDescriptionEditText.setText(extras.getString("description"));
            dateTextView.setText(dateString);

            if(dateString.length() > 1) {
                dateYear = Integer.parseInt(dateString.split("/")[2]);
                dateMonth = Integer.parseInt(dateString.split("/")[0]);
                dateDay = Integer.parseInt(dateString.split("/")[1]);
            }


        }

//        Button saveTaskButton = (Button) findViewById(R.id.saveTaskButton);
//        saveTaskButton.setOnClickListener(saveTaskButtonListener);

        completeDateTextView.setOnClickListener(dateTextViewListener);
        dateTextView.setOnClickListener(dateTextViewListener);
        dateTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dateTextView.setText("");
                dateYear = dateMonth = dateDay = 0;
                Log.v(TAG, "Year = " + dateYear + ", Month = " + dateMonth + ", Day = " + dateDay);
                return true;
            }
        });

//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        //Log.v(TAG,"Test");
    }

    OnClickListener dateTextViewListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
/*
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
*/
            final Date d;
            if(dateYear != 0) {
               GregorianCalendar gc = new GregorianCalendar(dateYear,dateMonth-1,dateDay);
               d = gc.getTime();
            } else {
                d = new Date();
            }
            DialogFragment newFrag = DatePickerFragment.newInstance(d, dateSetListener);
            //newFrag.getDialog().setCanceledOnTouchOutside(true);
            newFrag.show(getFragmentManager(), "date");
            //newFrag.show(getSupportFragmentManager(), "date");
        }
    };

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {


        //boolean fired = false;
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
           if( datePicker.isShown() ){
               dateDay = day;
               dateMonth = month+1;
               dateYear = year;
               dateTextView.setText(dateMonth + "/" + dateDay + "/" + dateYear);
               Log.v(TAG, "onDateSet");

           }
        }
    };

    //method to send text in textviews to database connector
    private void saveTask() {
        DatabaseConnector dbConnector = new DatabaseConnector(this);
        String name, description, date;

        name = taskNameEditText.getText().toString();

        if(taskDescriptionEditText.getText()==null){ description = ""; }
        else { description = taskDescriptionEditText.getText().toString(); }

        if(dateTextView.getText()==null){ date = ""; }
        else { date = dateTextView.getText().toString(); }

        if(getIntent().getExtras() == null) {
            dbConnector.insertTask(name, description, date);
        } else {
            dbConnector.updateTask(rowID, name, description, date);
        }

        Log.v(TAG, "saveTask()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_action_bar_buttons, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_task_save:
                if (taskNameEditText.getText().length() != 0){
                    AsyncTask<Object, Object, Object> saveTask = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params)
                        {
                            Log.v(TAG, "Task ActionBar Save Pressed");
                            saveTask();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result)
                        {
                            finish();
                        }
                    };

                    saveTask.execute((Object[]) null);
                } else {
                    // create a new AlertDialog Builder
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(AddEditTask.this);

                    builder.setTitle(R.string.errorTitle);
                    builder.setMessage(R.string.errorMessage);
                    builder.setPositiveButton(R.string.errorButton, null);
                    builder.show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


/*
    public void setDateInts(int year, int month, int day){
        dateYear = year;
        dateMonth = month;
        dateDay = day;
    }
*/
}
/*
class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        AddEditTask.setDateInts(year, month, day);
    }
}
*/

/*
    OnClickListener saveTaskButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v){
            if (taskNameEditText.getText().length() != 0){
                AsyncTask<Object, Object, Object> saveTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params)
                    {
                        saveTask();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result)
                    {
                        finish();
                    }
                };

                saveTask.execute((Object[]) null);
            } else {
                // create a new AlertDialog Builder
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(AddEditTask.this);

                builder.setTitle(R.string.errorTitle);
                builder.setMessage(R.string.errorMessage);
                builder.setPositiveButton(R.string.errorButton, null);
                builder.show();
            }
        }
    };
 */
