package ru.tanec.sdaily.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import ru.tanec.sdaily.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import ru.tanec.sdaily.adapters.items.NoteDataItem;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    NoteDataItem[] list;
    public Context context;
    FragmentActivity activity;
    int cnt;

    public GoalAdapter(Context context, FragmentActivity activity, NoteDataItem[] data) {
        this.context = context;
        this.list = data;
        this.activity = activity;
        cnt = -1;
    }

    public void setList(NoteDataItem[] list) {
        this.list = list;
        cnt = -1;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.note_item;

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(layoutIdForListItem, parent, false);

        cnt++;

        return new GoalViewHolder(view, list[cnt]);

    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {

        boolean opened;
        NoteDataItem obj;
        TextView title;
        TextView description;
        ImageView type;
        CardView card;



        public GoalViewHolder(@NonNull View itemView, NoteDataItem it) {
            super(itemView);

            opened = false;
            description = itemView.findViewById(R.id.description);
            obj = it;
            title = itemView.findViewById(R.id.title);
            title.setText(obj.title);
            type = itemView.findViewById(R.id.type);

            description.setText(obj.getDescriptionSmall());

            if (it.type == 0) {
                type.setImageResource(R.drawable.new_moon);
            } else if (it.type == 1) {
                type.setImageResource(R.drawable.yellow_btn);
            } else {
                type.setImageResource(R.drawable.red_btn);
            }

            type.setOnClickListener(l -> {

            });

            description.setOnClickListener(l -> {
                if (opened) {
                    description.setText(obj.getDescriptionSmall());
                    opened = false;

                } else {
                    description.setText(obj.description);
                    opened = true;
                }
            });

        }

    }
}






