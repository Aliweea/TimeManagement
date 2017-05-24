package com.mycompany.timemanagement;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AppLockActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private Context context;
    private AppInfoDao infoDao;
    private List<AppInfo> unLockedDatas = new ArrayList<>();
    private List<AppInfo> lockedDatas = new ArrayList<>();
    private List<AppInfo> adapterDatas = new ArrayList<>();
    private TextView tvTitle;
    private LockAdapter adapter;
    private WatchDogDao watchDogDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_app_lock);
        Intent intent = new Intent(this, WatchDogService.class);
        startService(intent);
        watchDogDao = new WatchDogDao(context);
        updateData();
        initTitle();
        initListView();
    }

    private void initTitle() {
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("未加锁");
        adapterDatas = unLockedDatas;
        tvTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (tvTitle.getText().toString().equals("未加锁")) {
                    tvTitle.setText("已加锁");
                    // TODO: 去 已加锁界面
                    adapterDatas = lockedDatas;
                } else {
                    tvTitle.setText("未加锁");
                    // TODO：去 未加锁界面
                    adapterDatas = unLockedDatas;
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void updateData() {
        infoDao = new AppInfoDao();
        unLockedDatas = infoDao.getAllApps(context);
        List<String> lockPackageNames = watchDogDao.queryAllInfos();

        for (String text : lockPackageNames) {
            for(int i=0; i<unLockedDatas.size(); i++) {
                AppInfo info = unLockedDatas.get(i);
                if (info.packageName.equals(text)) {
                    unLockedDatas.remove(info);
                    lockedDatas.add(info);
                    break;
                }
            }
        }
    }

    private void initListView() {
        setTitle("程序锁功能");
        listView = (ListView) findViewById(R.id.lock_list_view);
        adapter = new LockAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private class LockAdapter extends BaseAdapter {
        /*private boolean isLock;*    //用于区分已加锁和未加锁应用的标示 true已加锁数据适配器 false未加锁数据适配器

        public LockAdapter(boolean isLock){
            this.isLock = isLock;
        }*/

        @Override
        public int getCount() {
            return adapterDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return adapterDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.app_list_row, null);
                holder = new ViewHolder();
                holder.appIcon = (ImageView) convertView.findViewById(R.id.app_icon);
                holder.appName = (TextView) convertView.findViewById(R.id.app_name);
                holder.appLock = (ImageView) convertView.findViewById(R.id.app_lock);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            AppInfo appInfo = (AppInfo) getItem(position);

            holder.appIcon.setBackground(appInfo.appIcon);
            holder.appName.setText(appInfo.appName);
            return convertView;
        }

        private class ViewHolder{
            ImageView appIcon;
            TextView appName;
            ImageView appLock;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        System.out.println("position： " + position);
        System.out.println("id： " + id);
        vtoast("position： " + position + "\n id： " + id);
        if (tvTitle.getText().toString().equals("未加锁")) {
            AppInfo removedAppInfo = unLockedDatas.remove(position);
            watchDogDao.insert(removedAppInfo.packageName);
            lockedDatas.add(removedAppInfo);
            adapter.notifyDataSetInvalidated();
        } else {
            AppInfo removedAppInfo = lockedDatas.remove(position);
            watchDogDao.delete(removedAppInfo.packageName);
            unLockedDatas.add(removedAppInfo);
            adapter.notifyDataSetInvalidated();
        }

    }

    protected void vtoast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
