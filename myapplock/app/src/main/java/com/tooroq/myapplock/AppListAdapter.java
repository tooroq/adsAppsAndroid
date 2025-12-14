package com.tooroq.myapplock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class AppListAdapter extends BaseAdapter {
    Context ctx;
    List<AppItem> items;
    LayoutInflater inf;

    public AppListAdapter(Context c, List<AppItem> items){
        ctx = c; this.items = items; inf = LayoutInflater.from(c);
    }

    @Override public int getCount(){return items.size();}
    @Override public Object getItem(int p){return items.get(p);}
    @Override public long getItemId(int p){return p;}

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        if(convertView==null) convertView = inf.inflate(R.layout.row_app_item, parent, false);
        ImageView iv = convertView.findViewById(R.id.icon);
        TextView tv = convertView.findViewById(R.id.name);
        ImageButton btnLock = convertView.findViewById(R.id.btnLock);

        AppItem it = items.get(pos);
        iv.setImageDrawable(it.icon);
        tv.setText(it.name);
        btnLock.setImageResource(it.locked ? R.drawable.ic_lock : R.drawable.ic_unlock);

        btnLock.setOnClickListener(v -> {
            it.locked = !it.locked;
            // تعديل مجموعة المقفولين في SharedPreferences
            Set<String> locked = Prefs.getLockedApps(ctx);
            if(it.locked) locked.add(it.packageName);
            else locked.remove(it.packageName);
            Prefs.setLockedApps(ctx, locked);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
