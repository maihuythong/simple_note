package com.maihuythong.a1712800_simplenote;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.Collections;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";

    private Context context;
    private ArrayList<Note> notes;
   // private ArrayList<Note> notesFiltered;
    private NoteEventListener listener;
    private boolean multiCheckMode = false;

    private NotesDao dao;


    public RecyclerViewAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        Collections.reverse(notes);
        this.notes = notes;
        //this.notesFiltered = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.layout_note,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Note note = getNote(position);
        if (note != null){
            holder.noteTitle.setText(note.getNoteTitle());
  //          holder.noteTag.setText(note.getNoteTag());
            String strTag = note.getNoteTag();

            LayoutInflater inflater = LayoutInflater.from(context);

         //   int count = holder.cgTag.getChildCount();
          //  for (int i = 0; i < count; ++i) {
                holder.cgTag.removeAllViews();
                holder.notePreview.setText(note.getNoteContent());
           // }

            String arrTag[] = strTag.split(" ");
            if (strTag != null && !strTag.isEmpty()) {
                for (String t : arrTag) {
                    if(!t.equals(" ")) {
                        Chip chip = (Chip) inflater.inflate(R.layout.chip_template_in_rcv, null, false);

                        if (t!= null && !t.isEmpty()) {
                            if (!t.substring(t.length() - 1).equals(" "))
                                t += " ";
                        }
                        chip.setText(t);
                        chip.setClickable(false);

                        holder.cgTag.addView(chip);
                    }
                }
            }


            holder.noteDate.setText(FormatDateTime.dateFromLong(note.getNoteDate()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNoteClick(note);
                }
            });
            // long click
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new AlertDialog.Builder(context).setMessage("Are you sure to delete?")
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dao.deleteNote(note);
                                    notes.remove(note);
                                    notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                    return true;
                }
            });

            if (multiCheckMode == true){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(note.isChecked());
                holder.checkBox.setChecked(note.isChecked());
            } else holder.checkBox.setVisibility(View.GONE);

        }
    }

    @Override
    public int getItemCount() {

        return notes.size();
    }

    private Note getNote(int position) {
        return notes.get(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle;
        ChipGroup cgTag;
   //    TextView noteTag;
        TextView notePreview;
        TextView noteDate;
        LinearLayout parentLayout;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            noteDate = itemView.findViewById(R.id.note_date);
            notePreview = itemView.findViewById(R.id.note_preview);
            //noteTag = itemView.findViewById(R.id.note_tag);
            //noteTag = itemView.findViewById(R.id.chip_group_tag);
            cgTag = itemView.findViewById(R.id.chip_group_tag);
            noteTitle = itemView.findViewById(R.id.note_title);
            parentLayout = itemView.findViewById(R.id.parent_layout);
            checkBox = itemView.findViewById(R.id.checkBox);

            dao = NotesDB.getInstance(context).notesDao();
        }
    }

    public void setListener(NoteEventListener listener) {
        this.listener = listener;
    }



    public void setFilter(ArrayList<Note> newArray){
        notes = new ArrayList<>();
        notes.addAll(newArray);
    }
}
