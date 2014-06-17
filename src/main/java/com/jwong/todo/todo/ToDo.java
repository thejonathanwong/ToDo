package com.jwong.todo.todo;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.ListIterator;


public class ToDo extends Activity {

    private SimpleCursorAdapter taskAdapter;
    private Menu menu;
    private ListView list;

    private final static String TAG = "ToDo";
    private final int OPEN_MAX_LINES = 100;
    private final int CLOSED_MAX_LINES = 2;

    private ArrayList<Integer> selectedItems = new ArrayList<Integer>();
    private ArrayList<Long> selectedItemIDs = new ArrayList<Long>();

    public static final String ROW_ID = "row_id"; // Intent extra key

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.cardListView);

        list.addHeaderView(new View(this));
        list.addFooterView(new View(this));

        String[] from = new String[]{"name", "description", "date"};
        int[] to = new int[] {R.id.cardTaskName, R.id.cardTaskDescription ,R.id.cardTaskDate};
        taskAdapter = new SimpleCursorAdapter(ToDo.this, R.layout.list_item_card,null,from,to,0);

        list.setOnItemClickListener(viewTaskListener);
        list.setOnItemLongClickListener(taskLongClickListener);
        list.setAdapter(taskAdapter);


/*
        BaseInflaterAdapter<CardItemData> adapter = new BaseInflaterAdapter<CardItemData>(new CardInflater());
        for (int i = 0; i < 50; i++)
        {
            CardItemData data = new CardItemData("Item " + i + " Line 1", "Item " + i + " Line 2", "Item " + i + " Line 3");
*/
    }
    @Override
    protected void onResume() {
        super.onResume();

        new GetTask().execute((Object[]) null);
        Log.v(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        Cursor cursor = taskAdapter.getCursor();

        if (cursor != null) {
            cursor.close();
        }

        taskAdapter.changeCursor(null);
        Log.v(TAG,"onStop");
        super.onStop();

    }

    private class GetTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector = new DatabaseConnector(ToDo.this);

        @Override
        protected Cursor doInBackground(Object... params){
            Log.v(TAG,"GetTask.doInBackground");
            databaseConnector.open();

            // get a cursor containing call contacts
            return databaseConnector.getAllTasks();
        }

        @Override
        protected void onPostExecute(Cursor result) {
            taskAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        //super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
/*        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
*/
        View card;
        switch(item.getItemId()) {
            case R.id.addItem:
                Intent addNewTask = new Intent(ToDo.this, AddEditTask.class);
                Log.v(TAG, "addItemPressed");
                startActivity(addNewTask);
                return true;
            case R.id.editItem:
                Log.v(TAG, "editItemPressed");
                Intent addEditTask = new Intent(this, AddEditTask.class);
                int position = selectedItems.get(0);
                long id = selectedItemIDs.get(0);
                View view = list.getChildAt(position);

                TextView taskName = (TextView) view.findViewById(R.id.cardTaskName);
                TextView taskDescription = (TextView) view.findViewById(R.id.cardTaskDescription);
                TextView taskDate = (TextView) view.findViewById(R.id.cardTaskDate);

                // pass the selected contact's data as extras with the Intent
                addEditTask.putExtra(ToDo.ROW_ID, id);
                addEditTask.putExtra("name", taskName.getText());
                addEditTask.putExtra("description", taskDescription.getText());
                addEditTask.putExtra("date", taskDate.getText());
                startActivity(addEditTask);
                //buttonOnClick(view.findViewById(R.id.cardSelectedButton));

                return true;
            case R.id.deleteItem:

                Log.v(TAG, "deleteItemPressed");
                deleteTask();
                return true;
            case R.id.done:
                Log.v(TAG,"donePressed selectedItems = " + selectedItems.toString() + "size = " + selectedItems.size());
           /*     ListIterator<Long> iterator = selectedItemIDs.listIterator();
                while(iterator.hasNext()) {
                    int index = iterator.nextIndex();
                    Long next = iterator.next();
                    card = list.getChildAt(selectedItems.get(index));
                    toggleCard(card);
                    toggleSelected(card, selectedItems.get(index), iterator.next());
                }
            */  for(int i = 0; i < selectedItems.size(); i++) {
                    card = list.getChildAt(selectedItems.get(i));
                    toggleCard(card);
                    Log.v(TAG, "" + selectedItems.get(i));
                    card.findViewById(R.id.cardSelectedButton).setVisibility(View.INVISIBLE);
                    card.setFocusable(false);
                    //toggleSelected(card, selectedItems.get(i), selectedItemIDs.get(i));
                    //card.findViewById(R.id.cardSelectedButton).setVisibility(View.INVISIBLE);
                    //card.setTag("closed");
                    //toggleCard( list.getAdapter().getView(selectedItems.get(i),null,list), i);
                }

                //Log.v(TAG, "list.getChildCount() " +list.getChildCount());
                selectedItems.clear();
                selectedItemIDs.clear();
                showOption(R.id.addItem);
                hideOption(R.id.editItem);
                hideOption(R.id.deleteItem);
                hideOption(R.id.done);
                Log.v(TAG, "donePressed");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void toggleCard(View itemClicked) {

        TextView description = (TextView) itemClicked.findViewById(R.id.cardTaskDescription);
        //ImageView selectedButton = (ImageView) itemClicked.findViewById(R.id.cardSelectedButton);
        if(itemClicked.getTag().toString() != "open") {
            description.setMaxLines(OPEN_MAX_LINES);
         //   selectedButton.setImageResource(R.drawable.card_selected_grey);
            //selectedButton.setClickable(true);
         //   selectedButton.setVisibility(View.VISIBLE);
            //selectedButton.setOnClickListener(buttonOnClickListener);
            itemClicked.setTag("open");
        } else {
            description.setMaxLines(CLOSED_MAX_LINES);
            //selectedButton.setClickable(false);
          //  selectedButton.setImageResource(R.drawable.card_selected_white);
          //  selectedButton.setVisibility(View.INVISIBLE);
            itemClicked.setTag("closed");
        }


/*        if(description.getLineCount() > CLOSED_MAX_LINES) {
            if ((description.getHint() != "open")) {
                description.setMaxLines(OPEN_MAX_LINES);
                description.setHint("open");
            } else {
                description.setMaxLines(CLOSED_MAX_LINES);
                description.setHint("closed");
            }
            Log.v(TAG, "toggleCard " + position + description.getHint().toString());

        }
*/

    }

    public void toggleSelected(View itemLongClicked, int position, long id ) {
        ImageView button = (ImageView) itemLongClicked.findViewById(R.id.cardSelectedButton);
        if(selectedItemIDs.contains(id)) { //has already been long clicked
            selectedItems.remove((Integer) position);
            selectedItemIDs.remove((Long) id);
            button.setVisibility(View.INVISIBLE);
            itemLongClicked.setFocusable(false);
        } else { //has not been long clicked
            selectedItems.add(position);
            selectedItemIDs.add(id);
            button.setTag(R.string.position,position);
            button.setTag(R.string.id,id);
            button.setVisibility(View.VISIBLE);
            itemLongClicked.setFocusable(true);
        }

        if(selectedItems.size() == 0) {
            showOption(R.id.addItem);
            hideOption(R.id.editItem);
            hideOption(R.id.deleteItem);
            hideOption(R.id.done);
        } else {
            hideOption(R.id.addItem);
            if (selectedItems.size() == 1) {
                showOption(R.id.editItem);
            } else {
                hideOption(R.id.editItem);
            }
            showOption(R.id.deleteItem);
            showOption(R.id.done);
        }
    }
/*
    View.OnClickListener buttonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.v(TAG, "selectedButtonClicked");
        }
    };
    */
    public void buttonOnClick(View v) {
        int position = (Integer) v.getTag(R.string.position);
        long id = (Long) v.getTag(R.string.id);

        View card = list.getChildAt(position);
        //if(card.getTag().toString() == "closed") {
            toggleCard(card);
       // }
        toggleSelected(card,position,id);
        //card.performLongClick();

        //v.long
        Log.v(TAG, "selectedButtonClicked");
    }

    AdapterView.OnItemClickListener viewTaskListener = new AdapterView.OnItemClickListener()
    {

        //private Boolean open = false;
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                long id)
        {
            if(!selectedItems.contains(position) && !selectedItemIDs.contains(id)) {
                toggleCard(itemClicked);
            }
            Log.v(TAG, "onItemClick");
            // create an Intent to launch the ViewContact Activity
            /*Intent viewTask =
                    new Intent(JwongMovieCollection.this, ViewMovie.class);


            viewMovie.putExtra(ROW_ID, arg3);
            startActivity(viewMovie);
            */

        }
    };

    AdapterView.OnItemLongClickListener taskLongClickListener = new AdapterView.OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View itemLongClicked, int position, long id) {

            if(itemLongClicked.getTag().toString() != "open") {
                toggleCard(itemLongClicked);
            }
            toggleSelected(itemLongClicked, position, id);
            //TableLayout cardLayout = (TableLayout) itemLongClicked.findViewById(R.id.cardTableLayout);


            //Log.v(TAG, "onLongClick focusable=" + itemLongClicked.isFocusable());

            Log.v(TAG, "onItemLongClick selectedItems = " + selectedItems.toString());
           // menu.clear();
            //Log.v(TAG, "onItemLongClick");

            return true;
        }
    };

    private void deleteTask()
    {
        // create a new AlertDialog Builder
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ToDo.this);

        builder.setTitle(R.string.confirmTitle);
        builder.setMessage(R.string.confirmMessage);

        // provide an OK button that simply dismisses the dialog
        builder.setPositiveButton(R.string.button_delete,
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnector databaseConnector = new DatabaseConnector(ToDo.this);
                        AsyncTask<ArrayList<Long>, Object, Object> deleteTask =
                                new AsyncTask<ArrayList<Long>, Object, Object>(){
                                    @Override
                                    protected Object doInBackground(ArrayList<Long>... params)
                                    {
                                        ArrayList<Long> itemArray = params[0];
                                        for(int i = 0; i < itemArray.size(); i++) {
                                            databaseConnector.deleteTask(itemArray.get(i));

                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        //finish(); // return to the AddressBook Activity
                                       // taskAdapter.notifyDataSetChanged();
                                        selectedItemIDs.clear();
                                        selectedItems.clear();
                                        new GetTask().execute((Object[]) null);
                                        return;
                                    }
                                };

                        // execute the AsyncTask to delete contact at rowID
                        //long itemLong = selectedItemIDs.get(0);

                        /*
                        int size = selectedItemIDs.size();
                        long[] itemArray = new long[size];
                        for(int i = 0; i<size; i++) {
                            itemArray[i] = selectedItemIDs.get(i);
                            selectedItemIDs.remove(i);
                        }
                        */


                        deleteTask.execute(selectedItemIDs);
                        showOption(R.id.addItem);
                        hideOption(R.id.editItem);
                        hideOption(R.id.deleteItem);
                        hideOption(R.id.done);
                    //    deleteTask.execute(new Long[] { rowID });
                    }
                }
        );

        builder.setNegativeButton(R.string.button_cancel, null);
        builder.show();


        return;
    }

    private void hideOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id)
    {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    private void setOptionTitle(int id, String title)
    {
        MenuItem item = menu.findItem(id);
        item.setTitle(title);
    }

    private void setOptionIcon(int id, int iconRes)
    {
        MenuItem item = menu.findItem(id);
        item.setIcon(iconRes);
    }
/*
    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
    */
}
