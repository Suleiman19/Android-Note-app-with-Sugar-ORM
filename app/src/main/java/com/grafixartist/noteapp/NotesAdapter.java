package com.grafixartist.noteapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Suleiman19 on 2/13/16.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteVH> {
    Context context;
    List<Note> notes;

    OnItemClickListener clickListener;

    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;

    }


    @Override
    public NoteVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        NoteVH viewHolder = new NoteVH(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NoteVH holder, int position) {

        holder.title.setText(notes.get(position).getTitle());
        holder.note.setText(notes.get(position).getNote());

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, note;

        public NoteVH(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.note_item_title);
            note = (TextView) itemView.findViewById(R.id.note_item_desc);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(v, getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

}
