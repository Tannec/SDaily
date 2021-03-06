package ru.tanec.sdaily.services.recivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.number.Scale;
import android.os.Vibrator;
import android.util.Range;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ru.tanec.sdaily.activity.MainActivity;
import ru.tanec.sdaily.adapters.items.NoteDataItem;
import ru.tanec.sdaily.adapters.items.RangeItem;
import ru.tanec.sdaily.database.DataBase;
import ru.tanec.sdaily.database.DataBaseApl;
import ru.tanec.sdaily.database.NoteDao;
import ru.tanec.sdaily.database.NoteEntity;
import ru.tanec.sdaily.database.TimeTableDao;
import ru.tanec.sdaily.database.TimeTableEntity;
import ru.tanec.sdaily.services.NotificationService;

public class NotificationReceiver extends BroadcastReceiver {
    DataBase db = DataBaseApl.instance.getDatabase();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("notify");
        int id = intent.getIntExtra("note", 0);
        if (action.equals("dismiss")) {
            noteReplace(id);
        } else if (action.equals("yes")) {
            noteExecute(id, 1);
        } else if (action.equals("no")) {
            noteExecute(id, 0);
        } else if (action.equals("activity")) {
            context.startActivity(new Intent(context, MainActivity.class));
        }

        NotificationManager n = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        n.cancelAll();

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }

    public void noteReplace(long id) {
        NoteDao nd = db.noteDao();
        Calendar calendar = Calendar.getInstance();
        long t = calendar.getTime().getTime();

        NoteDataItem newNote = new NoteDataItem();
        NoteEntity noteToReplase = nd.getById(id);
        newNote.setFromEntity(noteToReplase);
        nd.delete(noteToReplase);

        NoteEntity[] notesLow = nd.getByType(0);
        NoteEntity[] notesMedium = nd.getByType(1);
        NoteEntity[] notesHigh = nd.getByType(2);

        ArrayList<Long> d = new ArrayList<>();
        HashMap<Long, NoteEntity> typesTable = new HashMap<Long, NoteEntity>();
        for (NoteEntity note: notesLow) {
            d.add(note.beginDateMls);
            typesTable.put(note.beginDateMls, note);
        }
        for (NoteEntity note: notesMedium) {
            d.add(note.beginDateMls);
            typesTable.put(note.beginDateMls, note);
        }
        for (NoteEntity note: notesHigh) {
            d.add(note.beginDateMls);
            typesTable.put(note.beginDateMls, note);
        }
        Collections.sort(d);

        boolean changed = false;
        int i = 1;
        while (i < d.size()) {
            long d1 = d.get(i);
            long d0 = d.get(i - 1);

            if (d0 > t) {
                if (d1 - d0 - typesTable.get(d0).duration >= newNote.duration) {
                    newNote.beginDateMls = d1 - newNote.duration;
                    changed = true;
                    break;
                } else if (newNote.type > typesTable.get(d0).type & d1 - d0 >= newNote.duration) {
                    newNote.beginDateMls = d0;
                    NoteEntity noteD0 = typesTable.get(d0);
                    noteReplace(noteD0.id);
                    nd.update(noteD0);
                    changed = true;
                    break;
                }
            }
            i++;
        }
        if (!changed) {
            long dl = d.get(d.size() - 1);
            NoteEntity newNoteEntity = newNote.getEntity();
            newNoteEntity.notified = false;
            newNoteEntity.postNotified = false;
            newNoteEntity.beginDateMls = dl + typesTable.get(dl).duration;
        }
        nd.insert(newNote.getEntity());
    }

    public Boolean[] getDayFill(RangeItem[] ranges) {
        Boolean[] fill = new Boolean[24];
        for (RangeItem rangeItem : ranges) {
            if (rangeItem != null) {
                int start = rangeItem.getStartTime()[0];
                int end = rangeItem.getEndTime()[0];
                int end_minute = rangeItem.getEndTime()[1];
                if (end_minute > 40) {
                    end += 1;
                } else if (20 < end_minute & end_minute < 40) {
                    fill[end] = null;
                }
                for (int k = start; k < end; k++) {
                    fill[k] = true;
                }
            }
        }
        return fill;
    }

    public void noteExecute(int id, int state) {
        new Thread(() -> {
            NoteDao nd = db.noteDao();
            NoteEntity note = nd.getById((long) id);
            if (state == 1) {
                note.finished = true;
            }
            nd.update(note);
        }).start();

    }
}
