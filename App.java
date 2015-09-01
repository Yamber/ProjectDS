package com.example.darren.testapp;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Darren on 28/08/2015.
 * App.java is a class designed to hold data on each app.
 */
public class App implements Comparable<App>{

    private String title; //App's Name
    private ArrayList<String> permissions; //App's permissions
    public Drawable icon; //App's Icon
    public File file; //Directory to apk file
    public boolean sysApp; //True or false to is it a system app. Otherwise, it is downloaded app
    private int positionInPM; //It's position in Package Manger
    private String packageName;

    App (){

        title = "";
        permissions = new ArrayList<String>();
        icon = null;
        file = null;
        sysApp = false;
        positionInPM = 0;
        packageName = "";

    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPositionInPM() {
        return positionInPM;
    }

    public void setPositionInPM(int positionInPM) {
        this.positionInPM = positionInPM;
    }

    App (String name, ArrayList<String> perm, Drawable pic, File apk, Boolean systemApp, int position, String pm){

        title = name;
        if (perm.isEmpty() == true){
            perm.add("This app does not use permissions");
            permissions = perm;
        }
        else{
            permissions = perm;
        }

        icon = pic;
        file = apk;
        sysApp = systemApp;
        positionInPM = position;
        packageName = pm;


    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSysApp() {
        return sysApp;
    }

    public void setSysApp(boolean sysApp) {
        this.sysApp = sysApp;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int compareTo(App compareApp){

        int comparePermission = ((App) compareApp).getPermissions().size();

        return comparePermission -this.permissions.size();
    }

}
