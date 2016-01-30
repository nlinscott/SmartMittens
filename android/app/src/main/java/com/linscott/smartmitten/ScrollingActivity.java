package com.linscott.smartmitten;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.linscott.smartmitten.services.BLEService;

public class ScrollingActivity extends AppCompatActivity {

    private ListView list;

    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView)findViewById(R.id.listView);

        String[] values = new String[] {
                "Cat",
                "Dog",
                "Horse",
                "Mouse",
                "Rat",
                "Velociraptor",
                "Snake",
                "Cradily",
                "Tentacool",
                "Mammoth",
                "Lamb",
                "Dusclops",
                "Bear",
                "Shark",
                "Sting ray",
                "Anaconda",
                "Flareon",
                "Monkey",
                "Spider",
                "Dragonite",
                "Gorilla",
                "Hippo",
                "Piggy",
                "Giraffe",
                "Lion",
                "Tiger",
                "Mudkip",
                "Polar Bear",
                "Arctic Fox",
                "Penguin",
                "Pikachu",
                "Charmander",
                "Sandshrew",
                "Alligator",
                "Tortoise",
                "Flygon",
                "Lapras",
                "Vaporeon",
                "Hawk",
                "Grasshopper"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        list.setAdapter(adapter);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateListPosition(11);
            }
        },4000);
    }

    private void updateListPosition(final int number){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                list.smoothScrollToPosition(currentPosition += number);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(gestureReceiver, gestureActionIntentFilter());
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(gestureReceiver);
    }

    private final BroadcastReceiver gestureReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra(BLEService.GESTURE_ACTION)){
                if(intent.hasExtra(BLEService.EXTRA_GESTURE)){
                    String extra = intent.getStringExtra(BLEService.EXTRA_GESTURE);

                    if(extra.equals(BLEService.EXTRA_GESTURE_UP)){
                        updateListPosition(11);
                    }else if(extra.equals(BLEService.EXTRA_GESTURE_DOWN)){
                        updateListPosition(-11);
                    }
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.scan) {
            startActivity(new Intent(this, ScanActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter gestureActionIntentFilter(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(BLEService.GESTURE_ACTION);
        return filter;
    }
}
