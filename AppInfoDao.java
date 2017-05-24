package com.mycompany.timemanagement;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2017/5/23.
 */

public class AppInfoDao {

     /*
     * 查询手机内非系统应用
     * @param context
     * @return app信息的集合
     */

    public static List<AppInfo> getAllApps(Context context) {
        List<AppInfo> appList = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();

        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for(int i=0; i<packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            //确认是非系统应用
            if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) <= 0){
                AppInfo tmpInfo =new AppInfo();
                tmpInfo.appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
                tmpInfo.packageName = packageInfo.packageName;
                tmpInfo.versionName = packageInfo.versionName;
                tmpInfo.versionCode = packageInfo.versionCode;
                tmpInfo.appIcon = pm.getApplicationIcon(packageInfo.applicationInfo);
                appList.add(tmpInfo);
            }
        }
        return appList;
    }


}

class AppInfo {
    public String appName="";
    public String packageName="";
    public String versionName="";
    public int versionCode=0;
    public Drawable appIcon=null;
}
