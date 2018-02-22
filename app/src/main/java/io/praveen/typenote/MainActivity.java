package io.praveen.typenote;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import java.util.List;
import io.praveen.typenote.SQLite.ClickListener;
import io.praveen.typenote.SQLite.DatabaseHandler;
import io.praveen.typenote.SQLite.Note;
import io.praveen.typenote.SQLite.NoteAdapter;
import io.praveen.typenote.SQLite.RecyclerTouchListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity{

    FloatingActionButton fab;
    CoordinatorLayout sv;
    NoteAdapter mAdapter;
    SharedPreferences preferences;

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/whitney.ttf").setFontAttrId(R.attr.fontPath).build());
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("Type Note");
        SS.setSpan (new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(SS);
        }
        fab = findViewById(R.id.fab);
        sv = findViewById(R.id.fabView);
        populateData();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NoteActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            }
        });
        boolean b = false;
        boolean c = false;
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("note")) {
            b = getIntent().getExtras().getBoolean("note");
        }
        if (this.getIntent().getExtras() != null && this.getIntent().getExtras().containsKey("note")) {
            c = getIntent().getExtras().getBoolean("edit");
        }
        if (b){
            Snackbar.make(sv, "Note added successfully!", Snackbar.LENGTH_SHORT).show();
        }
        if (c){
            Snackbar.make(sv, "Note edited successfully!", Snackbar.LENGTH_SHORT).show();
        }
        boolean shortcut = preferences.getBoolean("shortcut", true);
        if (!shortcut) {
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
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                builder.setContentTitle("Tap to add a note!");
                builder.setContentText("Note something productive today!");
                builder.setContentIntent(pendingIntent);
                builder.setTicker("Add Notes");
                builder.setChannelId(channelId);
                builder.setOngoing(true);
                builder.setAutoCancel(true);
                builder.setSmallIcon(R.drawable.notification_white);
                builder.setPriority(NotificationManager.IMPORTANCE_LOW);
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
        } else{
            NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (nMgr != null) {
                nMgr.cancelAll();
            }
        }
    }

    public void populateData(){
        final DatabaseHandler db = new DatabaseHandler(this);
        final List<Note> l = db.getAllNotes();
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final RelativeLayout rl = findViewById(R.id.placeholder);
        if (l.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            rl.setVisibility(View.VISIBLE);
        }
        mAdapter = new NoteAdapter(l);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                final Note note = l.get(position);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", note.getNote());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                }
                Intent intent = new Intent(MainActivity.this, ViewActivity.class);
                intent.putExtra("note", note.getNote());
                intent.putExtra("id", note.getID());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onLongClick(View view, final int position) {
                final Note note = l.get(position);
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Delete Note");
                alertDialog.setMessage("Do you want to delete the note? This action cannot be undone!");
                alertDialog.setIcon(R.drawable.ic_delete);
                alertDialog.setPositiveButton("GO BACK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {}
                });
                alertDialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteNote(note);
                        Snackbar.make(sv, "Note deleted!", Snackbar.LENGTH_SHORT).show();
                        l.remove(position);
                        mAdapter.notifyItemRemoved(position);
                        if (l.isEmpty()){
                            recyclerView.setVisibility(View.GONE);
                            rl.setVisibility(View.VISIBLE);
                        }
                    }
                });
                alertDialog.show();
            }
        }));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings){
            Intent i = new Intent(MainActivity.this, SettingsActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        } else if (item.getItemId() == R.id.about){
            Intent i = new Intent(MainActivity.this, AboutActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }
        return true;
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }
            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mAdapter.getFilter().filter("");
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mAdapter.getFilter().filter("");
                return true;
            }
        });
    }

}
