package io.praveen.typenote;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;

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
    ImageView edit, share;
    NoteAdapter mAdapter;
    View v;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/whitney.ttf").setFontAttrId(R.attr.fontPath).build());
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("Type Note");
        SS.setSpan (new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(SS);
        fab = findViewById(R.id.fab);
        sv = findViewById(R.id.fabView);
        populateData();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoteActivity.class));
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
        Intent intent = new Intent(this, NoteActivity.class);
        intent.putExtra("IS_FROM_NOTIFICATION",true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("Tap to add a note");
        builder.setContentIntent(pendingIntent);
        builder.setTicker("Add Notes");
        builder.setOngoing(true);
        builder.setContentText("Note something productive today!");
        builder.setAutoCancel(true);
        builder.setSmallIcon(R.drawable.notification_white);
        builder.setPriority(Notification.PRIORITY_MAX);
        Notification notification = builder.build();
        NotificationManager notificationManger = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManger != null) {
            notificationManger.notify(1, notification);
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
                if (v != view){
                    if (edit != null) {
                        edit.setVisibility(View.GONE);
                        share.setVisibility(View.GONE);
                    }
                }
                v = view;
                edit = view.findViewById(R.id.list_edit);
                share = view.findViewById(R.id.list_share);
                edit.setVisibility(View.VISIBLE);
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, EditActivity.class);
                        intent.putExtra("note", note.getNote());
                        intent.putExtra("id", note.getID());
                        startActivity(intent);
                        finish();
                    }
                });
                share.setVisibility(View.VISIBLE);
                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = note.getNote();
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                });
                Snackbar.make(sv, "Note copied!", Snackbar.LENGTH_SHORT).show();
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

}
