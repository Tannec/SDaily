package ru.tanec.sdaily.fragments;

import android.content.Context;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import ru.tanec.sdaily.adapters.items.NoteDataItem;
import ru.tanec.sdaily.R;
import ru.tanec.sdaily.adapters.GoalAdapter;
import ru.tanec.sdaily.custom.StaticValues;
import ru.tanec.sdaily.database.DataBase;
import ru.tanec.sdaily.database.DataBaseApl;
import ru.tanec.sdaily.database.NoteDao;
import ru.tanec.sdaily.database.NoteEntity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Goal extends Fragment {

    FragmentActivity activity;
    Context context;
    RecyclerView goalrecycler;
    DataBase db = DataBaseApl.instance.getDatabase();
    ImageButton type;
    TextView time;

    public Goal() {
        super(R.layout.fragment_goal);
    }

    public Goal(Context context) {
        super(R.layout.fragment_goal);
        this.context = requireContext();
        this.activity = requireActivity();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goalrecycler = view.findViewById(R.id.goal_recycler);

        StaticValues.liveDate.observe(getViewLifecycleOwner(), date -> {
            new Thread(() -> {
                NoteDao nd = db.noteDao();
                NoteEntity[] primaryData = nd.getByDate(StaticValues.viewDate.getTime());
                List<NoteDataItem> data = new ArrayList<>(primaryData.length);
                for (int i = 0; i < primaryData.length; i++) {
                    data.add(new NoteDataItem());
                    data.get(i).setFromEntity(primaryData[i]);
//                    System.out.println("Bulya: " + primaryData[i].time);
                }

                Collections.sort(data, (t1, t2) -> {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH-mm");
                        Date date1 = sdf.parse(t1.getTime());
                        Date date2 = sdf.parse(t2.getTime());
                        return date1 != null ? date1.compareTo(date2) : 0;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return 0;
                    }
                });

                new Handler(Looper.getMainLooper()).post(() -> {
                    goalrecycler.setAdapter(new GoalAdapter(context, activity, data));
                });
            }).start();
        });





        FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            MakeGoals fragment = new MakeGoals();
            fragment.show(requireActivity().getSupportFragmentManager(), "makeGoal");
        });
    }

}

