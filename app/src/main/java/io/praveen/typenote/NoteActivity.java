package io.praveen.typenote;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.praveen.typenote.SQLite.DatabaseHandler;
import io.praveen.typenote.SQLite.Note;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NoteActivity extends AppCompatActivity {

    FloatingActionButton fab;
    TextInputEditText text;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(NoteActivity.this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/whitney.ttf").setFontAttrId(R.attr.fontPath).build());
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("Add Note");
        SS.setSpan (new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(SS);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        boolean b = preferences.getBoolean("shortcut", true);
        if (!b){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int notificationId = 1;
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                String channelId = "TN_1";
                String channelName = "Type Note Shortcuts";
                @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(channelId, channelName, 3);
                if (notificationManager != null) {
                    mChannel.setSound(null, null);
                    notificationManager.createNotificationChannel(mChannel);
                }
                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("IS_FROM_NOTIFICATION", true);
                @SuppressLint("WrongConstant") PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, NotificationManager.IMPORTANCE_LOW);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                builder.setContentTitle("Tap to add a note!");
                builder.setContentText("Note something productive today!");
                builder.setContentIntent(pendingIntent);
                builder.setTicker("Add Notes");
                builder.setChannelId(channelId);
                builder.setOngoing(true);
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.drawable.notification_white);
                builder.setPriority(Notification.PRIORITY_MAX);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                stackBuilder.addNextIntent(intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(resultPendingIntent);
                if (notificationManager != null) {
                    notificationManager.notify(notificationId, builder.build());
                }
            } else {
                Intent intent = new Intent(this, NoteActivity.class);
                intent.putExtra("IS_FROM_NOTIFICATION", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
                Notification.Builder builder = new Notification.Builder(getApplicationContext());
                builder.setContentTitle("Tap to add a note!");
                builder.setContentText("Note something productive today!");
                builder.setContentIntent(pendingIntent);
                builder.setTicker("Add Notes");
                builder.setOngoing(true);
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.drawable.notification_white);
                builder.setPriority(Notification.PRIORITY_MAX);
                Notification notification = builder.build();
                NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (notificationManger != null) {
                    notificationManger.notify(1, notification);
                }
            }
        }
        fab = findViewById(R.id.add_fab);
        text = findViewById(R.id.add_text);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = text.getText().toString();
                if (note.length() > 0){
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
                    String formattedDate = df.format(c.getTime());
                    DatabaseHandler db = new DatabaseHandler(NoteActivity.this);
                    db.addNote(new Note(note, formattedDate));
                    Intent intent = new Intent(NoteActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("note",true);
                    startActivity(intent);
                    finish();
                } else{
                    Snackbar.make(v, "Note is empty!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(NoteActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent i = new Intent(NoteActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
        return true;
    }

}
