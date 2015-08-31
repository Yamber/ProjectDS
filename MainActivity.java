package com.example.darren.testapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView alpha;

    public final static String EXTRA_MESSAGE = "com.example.darren.testapp.MESSAGE";
    private static final String TAG = "TEST";
    public static ArrayList<App> allApps = new ArrayList<>();
    public  static  ArrayList<String> appNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alpha = (ListView) findViewById(R.id.ListView1);

        File appBlockDir = new File(Environment.getExternalStorageDirectory() + "/APKStorage"); //Place to store APKs.

        if(appBlockDir.exists()){
            //No need to do anything if it already exists
        }
        else{
            appBlockDir.mkdirs(); // Makes directory at mnt/sdcard/APKStorage
        }
        Log.d("Dir", Environment.getExternalStorageDirectory().toString());

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            //Log.d("test", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);
            String a;
            a = applicationInfo.loadLabel(getPackageManager()).toString(); //Name of app

            appNames.add(a);

            //Drawable here
            Drawable icon;
            icon = applicationInfo.loadIcon(getPackageManager());

            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                //Get Permissions
                ArrayList<String> appsPermissions = new ArrayList<>();

                File pleaseBeAPK = new File(applicationInfo.publicSourceDir);

                boolean sysApp = true;
                if (pleaseBeAPK.toString().contains("data/app")){
                    sysApp = false;
                }

                //String z = pleaseBeAPK.toString();
                //File replica = new File(appBlockDir + "/" + z);


                Log.d("Help", applicationInfo.publicSourceDir.toString());

                if (pleaseBeAPK.exists()) {
                    Log.d("APK", pleaseBeAPK.getAbsolutePath());
                }

                //requestedPermissions = packageInfo.requestedPermissions;

                String[] requestedPermissions = packageInfo.requestedPermissions;



                if(requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        //Log.d("test", requestedPermissions[i]);
                        appsPermissions.add(requestedPermissions[i]); //Necessary as the types of string lists do not match
                    }
                }

                App app;
                app = new App (a, appsPermissions, icon, pleaseBeAPK, sysApp); //"creates" an app
                allApps.add(app); //adds it the list of all apps

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


        for (int g = 0; g < allApps.size(); g++){
            if (allApps.get(g).isSysApp() == true){
                Log.d("SystemApp", allApps.get(g).getTitle());
            }
            else{
                Log.d("DownloadedApp", allApps.get(g).getTitle());
            }
        }

        ListAdapter Adapter = new ListAdapter(this,allApps);
        alpha.setAdapter(Adapter); //Creates the custom Listview

        alpha.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, start new activity
                Intent newIntent = new Intent();
                newIntent.setClass(MainActivity.this, DisplayPermissions.class);

                Bundle bundlePermissions = new Bundle();
                bundlePermissions.putSerializable("key", allApps.get(position).getPermissions());

                newIntent.putExtras(bundlePermissions);
                startActivity(newIntent);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
