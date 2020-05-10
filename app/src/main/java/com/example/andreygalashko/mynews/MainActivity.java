package com.example.andreygalashko.mynews;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity {

    public Elements content;
    public ArrayList<String> titleList = new ArrayList<>();
    public ArrayList<String> titleList2 = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.listViev);
        new NewThread().execute();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.pro_item, titleList);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                titleList.get(position);

                Intent intent = new Intent(MainActivity.this, NewsListActivity.class);
                intent.putExtra("siteText", titleList2.get(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    public class NewThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg) {
            Document document;
            try {
                document = Jsoup.connect("https://strana.ua/news.html").get();
                content = document.select(".title");
                titleList.clear();
                for (Element contents : content) {
                    titleList.add(contents.text());
                    Element element = contents.child(0);
                    titleList2.add(element.attr("href"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            lv.setAdapter(adapter);
        }
    }
}
