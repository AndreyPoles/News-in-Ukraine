package com.example.andreygalashko.mynews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.andreygalashko.mynews.settings.SettingsActivity;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NewsListActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
    }

    private String ur;
    private TextView textView;
    private ImageView imageView;
    private ActionBar actionBar;
    private SharedPreferences def_pref;
    private ArrayList<String> arrayList_text = new ArrayList<>();
    private ArrayList<String> arrayList_img = new ArrayList<>();
    private ArrayList<String> arrayList_title = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        actionBar = getSupportActionBar();
        def_pref = PreferenceManager.getDefaultSharedPreferences(this);

        Intent intent = getIntent();

        ur = intent.getStringExtra("siteText");

        String text = def_pref.getString("main_text_size", "Средний");
        if (text != null) {
            switch (text) {
                case "Большой":
                    textView.setTextSize(24);
                    break;
                case "Средний":
                    textView.setTextSize(18);
                    break;
                case "Маленький":
                    textView.setTextSize(14);
                    break;
            }
        }

        init();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            Intent i = new Intent(NewsListActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getWeb();
            }
        };
        Thread secThread = new Thread(runnable);
        secThread.start();
    }

    private void getWeb() {
        try {
            Document doc = Jsoup.connect("https://strana.ua" + ur).get();
            Elements aloneElement = doc.select("div.article-text");
            Elements title = doc.select("h1.article");
            arrayList_img.clear();
            for (Element el : aloneElement) {
                Elements element = el.getElementsByTag("img");
                arrayList_img.add(element.attr("src").toString());
                arrayList_text.add(aloneElement.eachText().get(0));
                arrayList_title.add(title.eachText().get(0));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(arrayList_text.get(0));
                String b = "https://strana.ua" + arrayList_img.get(0);
                Picasso.get().load(b).into(imageView);
                actionBar.setTitle(arrayList_title.get(0));

            }
        });
    }
}
