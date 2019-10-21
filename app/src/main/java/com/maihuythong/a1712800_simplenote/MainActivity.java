package com.maihuythong.a1712800_simplenote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.maihuythong.a1712800_simplenote.EditNoteActivity.NOTE_EXTRA_KEY;

//import com.maihuythong.testchipsinput.NoteActionMode;

public class MainActivity extends AppCompatActivity implements NoteEventListener, SearchView.OnQueryTextListener, SearchView.OnKeyListener{

    public static final String APP_PREFERENCES="notepad";
    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private RecyclerViewAdapter adapter;
   // private NoteActionMode noteActionMode;
    private  int chooseNoteCount = 0;
    private NotesDao dao;
    private SearchView searchView;

    private FloatingActionButton fab;

    private EditText noteTag;
    private ChipGroup chipGroup;

    private Spinner spinner;
    private MenuItem itemSpinner;
    private String selectSearchItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.list_note);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateNewNote();
            }
        });

        dao = NotesDB.getInstance(this).notesDao();

    }

    protected void onResume(){
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();
        this.notes.addAll(list);
        this.adapter = new RecyclerViewAdapter(this,this.notes);

        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
        showEmptyView();

        swipeToDeleteHelper.attachToRecyclerView(recyclerView);

    }

    private void showEmptyView(){
        if (notes.size() == 0){
            this.recyclerView.setVisibility(View.GONE);
            findViewById(R.id.empty_notes).setVisibility(View.VISIBLE);
        }else {
            this.recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.empty_notes).setVisibility(View.GONE);
        }
    }
    private void onCreateNewNote() {
        startActivity(new Intent(this,EditNoteActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem search = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) search.getActionView();


        itemSpinner = menu.findItem(R.id.spinner);
        spinner = (Spinner) itemSpinner.getActionView();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,
                R.array.searchOption, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        itemSpinner.setVisible(false);
        spinner.setAdapter(adapter);


        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean newViewFocus)
            {
                if (!newViewFocus)
                {
                    itemSpinner.setVisible(false);

                    search.collapseActionView();
                    loadNotes();

                }
            }
        });

        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                                             @Override
                                             public boolean onMenuItemActionExpand(MenuItem item) {
                                                 return true;
                                             }

                                             @Override
                                             public boolean onMenuItemActionCollapse(MenuItem item) {
                                                 itemSpinner.setVisible(false);
                                                 Toolbar toolbar = findViewById(R.id.toolbar);
                                                 setSupportActionBar(toolbar);
                                              //   search.collapseActionView();
                                                 loadNotes();
                                                 return true;
                                             }
                                         });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectSearchItem = (String) parent.getItemAtPosition(position);
                onQueryTextChange(searchView.getQuery().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.search) {
            itemSpinner.setVisible(true);
            return true;
        }
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNoteClick(Note note) {
        Intent edit = new Intent(this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_KEY, note.getId());
        startActivity(edit);
    }


    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    if (notes != null){
                        Note swipeNote = notes.get(viewHolder.getAdapterPosition());
                        if (swipeNote !=null){
                            swipeDelete(swipeNote,viewHolder);
                        }
                    }
                }
            }
    );

    private void swipeDelete(final Note note, final RecyclerView.ViewHolder viewHolder){
        new AlertDialog.Builder(MainActivity.this).setMessage("Are you sure to delete?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dao.deleteNote(note);
                        notes.remove(note);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        showEmptyView();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                .setCancelable(false)
                .create().show();
    }

    @Override
    public void onBackPressed(){
        if (!searchView.isIconified()){
            searchView.setIconified(true);
            return;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Note> newArr = new ArrayList<>();
        if(newText!= null && !newText.isEmpty()) {
            if (selectSearchItem.equals("Title")) {
                for (Note note : notes) {
                    if (note.getNoteTitle().toLowerCase().contains(newText.toLowerCase())) {
                        newArr.add(note);
                    }
                }
            }
            if (selectSearchItem.equals("Tag")) {
                for (Note note : notes) {
              //      String text = note.getNoteTag();
                    if (note.getNoteTag().toLowerCase().contains(newText.toLowerCase())) {
                        newArr.add(note);
                    }
                }
            }
            if (selectSearchItem.equals("Content")) {
                for (Note note : notes) {
                    if (note.getNoteContent().toLowerCase().contains(newText.toLowerCase())) {
                        newArr.add(note);
                    }
                }
            }
        }

        adapter.setFilter(newArr);
        adapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            loadNotes();
            return true;
        }
        return false;
    }

}
