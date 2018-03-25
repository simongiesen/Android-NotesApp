package io.praveen.typenote;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.LinearLayout;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;
import java.util.List;

import io.praveen.typenote.SQLite.AboutAdapter;
import io.praveen.typenote.SQLite.ClickListener;
import io.praveen.typenote.SQLite.RecyclerTouchListener;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AboutActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor bp;
    LinearLayout sv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApcobfuZFov1KIJgKEKrzp9PP2n1EbBpV/xf9AyhWYN47QY8/rWGPuKht/7b4DmCVnpd6PrnYJqLt/rqR5c+lifLY5XuUH1VGqnkWA33TkPXm4UkGk3q/jvVIbM5xbcdPLqNkLiEoEuBlmAYNxM6K3lf5Kz+ff1HUH1ljYjDE9M38xS0TiLnQIRPm9cfehNxaKWOF81sx5Q9K3vNB1JoNuMyaMfBFQjfMRL6llsMRF42NEf6W/4/2c5Guxvg2qLo14/gGVRLS5H0ZVwqThNZVYTtLRWWNIrgFIwMnCjcbntFkEBK/B987poGN6miDI2r1m6XALRAgLEzM/IUaPnwnWwIDAQAB", this);
        sv = findViewById(R.id.about_scroll);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder().setDefaultFontPath("fonts/whitney.ttf").setFontAttrId(R.attr.fontPath).build());
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/whitney.ttf");
        SpannableStringBuilder SS = new SpannableStringBuilder("About");
        SS.setSpan(new CustomTypefaceSpan("", font2), 0, SS.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(SS);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        List<String> l = new ArrayList<>();
        l.add("Project Contributors");
        l.add("Tools & Licenses Used");
        l.add("Donate and Support");
        l.add("Rate the Application");
        final RecyclerView recyclerView = findViewById(R.id.aboutRecyclerView);
        AboutAdapter mAdapter = new AboutAdapter(l);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                if (position == 0) {
                    Intent i = new Intent(AboutActivity.this, ContributorsActivity.class);
                    startActivity(i);
                } else if (position == 1) {
                    Intent i = new Intent(AboutActivity.this, LicensesActivity.class);
                    startActivity(i);
                } else if (position == 2) {
                    bp.purchase(AboutActivity.this, "typenote_donate");
                } else if (position == 3) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=io.praveen.typenote")));
                }
            }

        }));
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent i = new Intent(AboutActivity.this, MainActivity.class);
        startActivity(i);
        finish();
        return true;
    }

    @Override
    public void onBillingInitialized() {
    }

    @Override
    public void onProductPurchased(@NonNull String productId, TransactionDetails details) {
        Snackbar.make(sv, "Thanks for your donation, we'll strive to provide you the best of us!", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
        bp.consumePurchase("typenote_donate");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Snackbar.make(sv, "There was an error in completing the Donation process!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPurchaseHistoryRestored() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
