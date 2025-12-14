package com.tooroq.myapplock;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AppListActivity extends AppCompatActivity {
    ListView listView;
    List<AppItem> apps = new ArrayList<>();
    AppListAdapter adapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle s){
        super.onCreate(s);
        setContentView(R.layout.activity_applist);

        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        loadAppsAsync();
        adapter = new AppListAdapter(this, apps);
        listView.setAdapter(adapter);


    }

    private void loadAppsAsync() {
        new AsyncTask<Void, Void, List<AppItem>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // يمكن وضع ProgressBar إذا بغيت
            }

            @Override
            protected List<AppItem> doInBackground(Void... voids) {
                PackageManager pm = getPackageManager();
                List<ApplicationInfo> installed = pm.getInstalledApplications(PackageManager.GET_META_DATA);
                Set<String> locked = Prefs.getLockedApps(AppListActivity.this);

                List<AppItem> tempList = new ArrayList<>();

                for (ApplicationInfo ai : installed) {
                    if (ai.packageName.equals(getPackageName())) continue;

                    String label = ai.loadLabel(pm).toString();
                    Drawable icon = ai.loadIcon(pm);

                    tempList.add(new AppItem(label, ai.packageName, icon, locked.contains(ai.packageName)));
                }

                return tempList;
            }

            @Override
            protected void onPostExecute(List<AppItem> result) {
                super.onPostExecute(result);
                apps.clear();
                apps.addAll(result);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }



}