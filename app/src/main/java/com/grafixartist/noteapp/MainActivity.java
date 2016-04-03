package com.grafixartist.noteapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton fab;

    NotesAdapter adapter;
    List<Note> notes = new ArrayList<>();

    long initialCount;

    int modifyPos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Main", "onCreate");

        recyclerView = (RecyclerView) findViewById(R.id.main_list);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        recyclerView.setLayoutManager(gridLayoutManager);

        initialCount = Note.count(Note.class);

        if (savedInstanceState != null)
            modifyPos = savedInstanceState.getInt("modify");


        if (initialCount >= 0) {

            notes = Note.listAll(Note.class);

            adapter = new NotesAdapter(MainActivity.this, notes);
            recyclerView.setAdapter(adapter);

            if (notes.isEmpty())
                Snackbar.make(recyclerView, "No notes added.", Snackbar.LENGTH_LONG).show();

        }

        // tinting FAB icon
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_add_24dp);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, Color.WHITE);
            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);

            fab.setImageDrawable(drawable);

        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this, AddNoteActivity.class);
                startActivity(i);

            }
        });


        // Handling swipe to delete
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Remove swiped item from list and notify the RecyclerView

                final int position = viewHolder.getAdapterPosition();
                final Note note = notes.get(viewHolder.getAdapterPosition());
                notes.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(position);

                note.delete();
                initialCount -= 1;

                Snackbar.make(recyclerView, "Note deleted", Snackbar.LENGTH_SHORT)
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                note.save();
                                notes.add(position, note);
                                adapter.notifyItemInserted(position);
                                initialCount += 1;

                            }
                        })
                        .show();
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        adapter.SetOnItemClickListener(new NotesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Log.d("Main", "click");

                Intent i = new Intent(MainActivity.this, AddNoteActivity.class);
                i.putExtra("isEditing", true);
                i.putExtra("note_title", notes.get(position).title);
                i.putExtra("note", notes.get(position).note);
                i.putExtra("note_time", notes.get(position).time);

                modifyPos = position;

                startActivity(i);
            }
        });


//        List<Note> notes = Note.findWithQuery(Note.class, "Select title from Note where title = ?", "%note%");
//        if (notes.size() > 0)
//            Log.d("Notes", "note: " + notes.get(0).title);


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("modify", modifyPos);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        modifyPos = savedInstanceState.getInt("modify");
    }

    @Override
    protected void onResume() {
        super.onResume();

        final long newCount = Note.count(Note.class);

        if (newCount > initialCount) {
            // A note is added
            Log.d("Main", "Adding new note");

            // Just load the last added note (new)
            Note note = Note.last(Note.class);

            notes.add(note);
            adapter.notifyItemInserted((int) newCount);

            initialCount = newCount;
        }

        if (modifyPos != -1) {
            notes.set(modifyPos, Note.listAll(Note.class).get(modifyPos));
            adapter.notifyItemChanged(modifyPos);
        }

    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFormat(long date) {
        return new SimpleDateFormat("dd MMM yyyy").format(new Date(date));
    }


}
