package com.example.darren.testapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Darren on 30/08/2015.
 * Adapted from http://www.androprogrammer.com/2013/10/get-installed-application-in-list-view.html
 */
public class ListAdapter extends BaseAdapter {

    public ArrayList<App> apps;
    Activity context;

    public ListAdapter(Activity context, ArrayList<App> apps) {
        super();
        this.context = context;
        this.apps = apps;
    }

        private class ViewHolder {
            TextView apkName;
        }

        public int getCount() {
            return apps.size();
        }

        public Object getItem(int position) {
            return apps.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            Log.d("dddd", "addf");

            LayoutInflater inflater = context.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list, null);
                holder = new ViewHolder();

                holder.apkName = (TextView) convertView
                        .findViewById(R.id.textView1);

                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            App packageInfo = (App) getItem(position);

            Drawable appIcon = apps.get(position).getIcon();
            String appName = apps.get(position).getTitle();
            appIcon.setBounds(0, 0, 40, 40);
            holder.apkName.setCompoundDrawables(appIcon, null, null, null);
            holder.apkName.setCompoundDrawablePadding(15);
            holder.apkName.setText(appName);

            return convertView;

        }


    }
