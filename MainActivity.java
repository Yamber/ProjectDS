package com.example.darren.testapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView alpha;

    public final static String EXTRA_MESSAGE = "com.example.darren.testapp.MESSAGE";
    private static final String TAG = "TEST";
    public static ArrayList<App> allApps;
    //public static ArrayList<App> allApps = new ArrayList<>();
    public  static  ArrayList<String> appNames = new ArrayList<>();
    public static ListAdapter Adapter;


    //This method runs once the app starts
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        allApps = new ArrayList<>(); //Creates a new ArrayList. Also "flushes" it when the user goes from viewing permissions and back.

        alpha = (ListView) findViewById(R.id.ListView1);

        alpha.setOnCreateContextMenuListener(this);

        //Handles creating APK storage
        File appBlockDir = new File(Environment.getExternalStorageDirectory() + "/APKStorage"); //Place to store APKs.

        if(appBlockDir.exists()){
            //No need to do anything if it already exists
        }
        else{
            appBlockDir.mkdirs(); // Makes directory at mnt/sdcard/APKStorage
        }

        //Belows uses package manager to get all installed packages on the device
        PackageManager pack = getPackageManager();
        List<ApplicationInfo> packages = pack.getInstalledApplications(PackageManager.GET_META_DATA); //Gets Metadata of all apps. Name, location, file size, etc.

        int counter = 0; //oounter is used to store an apps location in the package manger

        for (ApplicationInfo applicationInfo : packages) {
            counter = counter + 1;
            String a;
            a = applicationInfo.loadLabel(getPackageManager()).toString(); //Name of app

            String packageName = applicationInfo.packageName;
            appNames.add(a);

            //Drawable here
            Drawable icon;
            icon = applicationInfo.loadIcon(getPackageManager());

            try {
                PackageInfo packageInfo = pack.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS); //Gets permission by using package name

                //Get Permissions
                ArrayList<String> appsPermissions = new ArrayList<>();

                File pleaseBeAPK = new File(applicationInfo.publicSourceDir); //Gets the source of the apk

                boolean sysApp = true;
                if (pleaseBeAPK.toString().contains("data/app")){
                    sysApp = false;
                }


                //Log.d("Debugging", applicationInfo.publicSourceDir.toString());

                //if (pleaseBeAPK.exists()) {
                    //Log.d("APK", pleaseBeAPK.getAbsolutePath());
                //}

                String[] requestedPermissions = packageInfo.requestedPermissions; //String[] of permissions

                if(requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        //Log.d("test", requestedPermissions[i]);
                        appsPermissions.add(requestedPermissions[i]); //Necessary as the types of string lists do not match
                    }
                }

                App app;
                app = new App (a, appsPermissions, icon, pleaseBeAPK, sysApp, counter, packageName); //"creates" an app
                allApps.add(app); //adds it the list of all apps

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(allApps);

        for (int g = 0; g < allApps.size(); g++){
            if (allApps.get(g).isSysApp() == true){
                Log.d("SystemApp", allApps.get(g).getTitle());
            }
            else{
                Log.d("DownloadedApp", allApps.get(g).getTitle());
            }
        }

        Adapter = new ListAdapter(this,allApps);
        alpha.setAdapter(Adapter); //Creates the custom Listview
        registerForContextMenu(alpha);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Option");
        menu.add(0, v.getId(), 0, "Back-up APK");
        menu.add(0, v.getId(), 0, "Uninstall");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position; //position is the position of the app in the element. Id is something else.

        //Read user selected options
        if(item.getTitle()=="Back-up APK"){
            backUpAPK(item.getItemId(), listPosition);
        }
        else if(item.getTitle()=="Uninstall"){
            uninstallApp(item.getItemId(), listPosition);
        }
        else {
            return false;
        }
        return true;
    }

    public void backUpAPK(int id, int position){
        //Toast.makeText(this, "function 1 called", Toast.LENGTH_SHORT).show();
        File apkFile = allApps.get(position).getFile();
        File destination = new File(Environment.getExternalStorageDirectory() + "/APKStorage" +"/" + allApps.get(position).getTitle() + ".apk");
        try{
            copyFileUsingFileStreams(apkFile, destination);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Toast.makeText(this, "APK created at mnt/sdcard/APKStorage", Toast.LENGTH_SHORT).show();

    }

    /**
     *
     * @param source
     * @param dest
     * @throws IOException
     * Copies the APK. Method taken from: http://examples.javacodegeeks.com/core-java/io/file/4-ways-to-copy-file-in-java/
     */
    private static void copyFileUsingFileStreams(File source, File dest)
            throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public void uninstallApp(int id,int position){
        String b = allApps.get(position).getTitle();
        Log.d("aaaaa", b);
        Toast.makeText(this, "function 2 called", Toast.LENGTH_SHORT).show();

        String pn = allApps.get(position).getPackageName();

        Uri packageUri = Uri.parse("package:" + pn);
        Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
        startActivity(uninstallIntent);
        allApps.remove(position);
        Adapter.notifyDataSetChanged();
    }

}
