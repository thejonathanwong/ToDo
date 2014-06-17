package com.jwong.todo.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DatabaseConnector {

    private static final String DATABASE_NAME = "TasksDB";
    private SQLiteDatabase database; // database object
    private DatabaseOpenHelper databaseOpenHelper;

    private final static String TAG = "DatabaseConnector";

    public DatabaseConnector(Context context){

        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null,1);

    }

    public void open() throws SQLException {
        database = databaseOpenHelper.getWritableDatabase();
    }

    public void close()
    {
        if (database != null)
            database.close();
    }

    public void insertTask(String name, String description, String date){
        ContentValues newTask = new ContentValues();

        newTask.put("name", name);
        newTask.put("description", description);
        newTask.put("date", date);


        open(); // open the database
        database.insert("tasks", null, newTask);
        close(); // close the database

    }

    public void updateTask(long id, String name, String description, String date){
        ContentValues editTask = new ContentValues();

        editTask.put("name", name);
        editTask.put("description", description);
        editTask.put("date", date);

        open(); // open the database
        database.update("tasks", editTask, "_id=" + id, null);
        close(); // close the database

    }

    /*	public void updateTask2(long id, String name, String genre, String director){
            ContentValues editMovie = new ContentValues();

            editMovie.put("name", name);
            editMovie.put("genre", genre);
            editMovie.put("director", director);


            open(); // open the database
            database.update("movies", editMovie, "_id=" + id, null);
            close(); // close the database

        }
    */
    public Cursor getAllTasks() {
        return database.query("tasks", new String[] {"_id", "name", "description", "date"},
                null, null, null, null, "name");
    }

    public Cursor getOneTask(long id){
        return database.query(
                "tasks", null, "_id=" + id, null, null, null, null);
    }

    // delete the task specified by the given String name
    public void deleteTask(long id){
        open(); // open the database
        database.delete("tasks", "_id=" + id, null);
        close(); // close the database
        Log.v(TAG, "deleteTask");
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper {

        public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context,name,factory,version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createQuery = "CREATE TABLE tasks" +
                    "(_id integer primary key autoincrement," +
                    "name TEXT, description TEXT, date TEXT);";

            db.execSQL(createQuery); // execute the query
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion)
        {}

    }



}
/**
 * Created by jonathan on 5/31/2014.
 */
