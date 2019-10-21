package com.maihuythong.a1712800_simplenote;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Date;


public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText noteTitle, noteContent, noteTag;
    private NotesDao dao;
    private Note temp;
    private ChipGroup chipGroup;
    private ArrayList<String> arrayTag;
    TextView dateEdit;

    public static final String NOTE_EXTRA_KEY = "note_id";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.edit_note_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        noteTitle = findViewById(R.id.txt_title);
        noteContent = findViewById(R.id.txt_content);
        noteTag = findViewById(R.id.txt_tag);
        chipGroup = findViewById(R.id.chipGroup);
        arrayTag = new ArrayList<>();

        dateEdit = findViewById(R.id.date_edit);


        dao = NotesDB.getInstance(this).notesDao();
        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_KEY, 0);
            temp = dao.getNoteById(id);
            noteTitle.setText(temp.getNoteTitle());
            noteContent.setText(temp.getNoteContent());
            dateEdit.setText(FormatDateTime.dateFromLong(temp.getNoteDate()));



            noteTag.setText(temp.getNoteTag());

            LayoutInflater inflater = LayoutInflater.from(EditNoteActivity.this);
            String txtTag = temp.getNoteTag();
            String arrTag[] = txtTag.split(" ");
            if (txtTag != null && !txtTag.isEmpty()) {
                for (String t : arrTag) {
                    if(!t.equals(" ")) {
                        arrayTag.add(t);
                        Chip chip = (Chip) inflater.inflate(R.layout.chip_template, null, false);

                        if (t!= null && !t.isEmpty()) {
                            if (!t.substring(t.length() - 1).equals(" "))
                                t += " ";
                        }
                        chip.setText(t);
                        chip.isCloseIconVisible();
                        chip.setOnCloseIconClickListener(EditNoteActivity.this);
                        chipGroup.addView(chip);
                    }
                }
                noteTag.getText().clear();
            }

        } else{
            noteTitle.setFocusable(true);
            noteContent.setFocusable(true);
            noteTag.setFocusable(true);
        }


        noteTag.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String txtTag = noteTag.getText().toString();
                    if (txtTag != null && !txtTag.isEmpty() && !txtTag.equals(" ")) {
                        LayoutInflater inflater = LayoutInflater.from(EditNoteActivity.this);
                        Chip chip = (Chip) inflater.inflate(R.layout.chip_template, null, false);

                        if (!txtTag.substring(txtTag.length()-1).equals(" "))
                            txtTag+=" ";
                        chip.setText(txtTag);
                        chip.isCloseIconVisible();
                        chipGroup.addView(chip);
                        //tempTag += noteTag.getText().toString();
                        chip.setOnCloseIconClickListener(EditNoteActivity.this);
                        arrayTag.add(txtTag);
                        noteTag.getText().clear();
                    }
                }
            }
        });


        noteTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txtTag = s.toString();
                if(txtTag!=null && !txtTag.isEmpty() && !txtTag.equals(" ")) {
                    //
                    if (txtTag.equals("  ")) {//
                        noteTag.getText().clear();
                    } else {
                        if (txtTag.substring(txtTag.length() - 1).equals(" ")) {
                            LayoutInflater inflater = LayoutInflater.from(EditNoteActivity.this);
                            Chip chip = (Chip) inflater.inflate(R.layout.chip_template, null, false);
                            String str = noteTag.getText().toString().trim();

                            if (!str.substring(str.length() - 1).equals(" "))
                                str += " ";

                            chip.setText(str);
                            chip.isCloseIconVisible();
                            chip.setOnCloseIconClickListener(EditNoteActivity.this);
                            chipGroup.addView(chip);
                            //tempTag += txtTag;

                            arrayTag.add(str);
                            noteTag.getText().clear();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_note)
            onSaveNote();

        if (item.getItemId() == android.R.id.home) {
            ///
            String title = noteTitle.getText().toString();
            String content = noteContent.getText().toString();
            String tagNotAChip = noteTag.getText().toString();
            StringBuilder sBTag = new StringBuilder();
            for (String texttemp : arrayTag){
                if(!texttemp.substring(texttemp.length()-1).equals(" ")){
                    texttemp+= " ";
                }
                sBTag.append(texttemp);
            }
            if (tagNotAChip!= null && !tagNotAChip.isEmpty()){
                sBTag.append(tagNotAChip);
            }
            String tag = sBTag.toString();


            if(title.isEmpty() && tag.isEmpty() && content.isEmpty()){
                finish();
             }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
                builder.setMessage("Do you want to save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onSaveNote();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                builder.create();
                builder.show();
                try{ Looper.loop(); }
                catch(RuntimeException e){}
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveNote() {
        String title = noteTitle.getText().toString();
        String content = noteContent.getText().toString();
        String tagNotAChip = noteTag.getText().toString();
        //String tag = noteTag.getText().toString();
        StringBuilder sBTag = new StringBuilder();
        for (String texttemp : arrayTag){
            if(!texttemp.substring(texttemp.length()-1).equals(" ")){
                texttemp+= " ";
            }
            sBTag.append(texttemp);
        }

        if (tagNotAChip!= null && !tagNotAChip.isEmpty()){
            sBTag.append(tagNotAChip);
        }

        String tag = sBTag.toString();



        if (title.isEmpty())
            title = "(No title)";

        if(tag.isEmpty() && content.isEmpty()){
            long date = new Date().getTime();
            temp = new Note(title,"",date,"");
            temp.setNoteDate(date);
            if(getIntent().getExtras() == null) {

                dao.insertNote(temp);
                finish();
            }
//          dao.insertNote(temp);
            dao.updateNote(temp);
            finish();
        }

        if(!content.isEmpty() || !tag.isEmpty()){
            long date = new Date().getTime();

            if (temp == null){
                temp = new Note(title, content, date, tag);
                dao.insertNote(temp);
            }else {
                temp.setNoteTitle(title);
                temp.setNoteContent(content);
                if (tag != null && !tag.isEmpty()){
                    temp.setNoteTag(tag);
                }
                if (tag.isEmpty())
                    temp.setNoteTag("");

                temp.setNoteDate(date);
                dao.updateNote(temp);
            }
            finish();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String title = noteTitle.getText().toString();
            String content = noteContent.getText().toString();
            String tagNotAChip = noteTag.getText().toString();
            StringBuilder sBTag = new StringBuilder();
            for (String texttemp : arrayTag){
                if(!texttemp.substring(texttemp.length()-1).equals(" ")){
                    texttemp+= " ";
                }
                sBTag.append(texttemp);
            }
            if (tagNotAChip!= null && !tagNotAChip.isEmpty()){
                sBTag.append(tagNotAChip);
            }
            String tag = sBTag.toString();


            if(title.isEmpty() && tag.isEmpty() && content.isEmpty()){
                finish();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditNoteActivity.this);
                builder.setMessage("Do you want to save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                onSaveNote();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                builder.create();
                builder.show();
                try{ Looper.loop(); }
                catch(RuntimeException e){}
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        Chip chip = (Chip) v;
        chipGroup.removeView(chip);
        String chipText = chip.getText().toString().trim();
        for (int i = 0; i < arrayTag.size(); ++i){
            String str = arrayTag.get(i).toString();
            if (str.trim().equals(chipText)){
                arrayTag.remove(i);
                break;
            }
        }
    }
}

