package com.example.darren.testapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class DisplayPermissions extends AppCompatActivity {

    private ListView permissionsListView;
    private ArrayAdapter<String> permissionAdapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_permissions);

        permissionsListView = (ListView) findViewById(R.id.permissionsListView);

        Bundle b = getIntent().getExtras();
        Log.d("hey", b.toString());

        ArrayList<String> permissions = (ArrayList<String>) b.getSerializable("key");

        Log.d("permission", permissions.get(0));

        Log.d("debug", "ufdhfhu");
        permissionAdapter = new ArrayAdapter<String>(this, R.layout.permissionlist, permissions);

        Log.d("debug1", "gfadga");

        permissionsListView.setAdapter(permissionAdapter);

        Log.d("debug", "huhd;af");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_permissions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
