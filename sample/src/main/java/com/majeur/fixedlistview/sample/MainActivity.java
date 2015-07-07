package com.majeur.fixedlistview.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.majeur.fixedlistview.FixedListView;
import com.majeur.fixedlistview.FixedListViewAdapter;
import com.majeur.fixedlistview.OnItemClickListener;
import com.majeur.fixedlistview.OnItemLongClickListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public static final String[] WORDS = {"Current", "Last Day", "Last Week", "Last Month", "Last Year", "Last 10 Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        TextView messageView = (TextView) findViewById(R.id.message);

        // Get message from raw folder
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.message);
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String message = scanner.hasNext() ? scanner.next() : "";
            inputStream.close();

            messageView.setText(Html.fromHtml(message));
        } catch (IOException e) {
            messageView.setText(e.toString());
        }

        FixedListView fixedListView = (FixedListView) findViewById(R.id.fixedListView);

        // Adapter
        fixedListView.setAdapter(new FixedListViewAdapter() {
            @Override
            public int getCount() {
                return WORDS.length;
            }

            @Override
            public View getView(@NonNull FixedListView parent, int position) {
                View view = getLayoutInflater().inflate(R.layout.list_item, parent, false);
                TextView label = (TextView) view.findViewById(R.id.text);
                label.setText(WORDS[position]);

                return view;
            }
        });

        // Item click listener
        fixedListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull FixedListView parent, @NonNull View v, int position) {
                Toast.makeText(MainActivity.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

        // Item long click listener
        fixedListView.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(@NonNull FixedListView parent, @NonNull View view, int position) {
                Toast.makeText(MainActivity.this, "Item " + position + " long clicked", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
