package com.leanderli.dev.cardlauncherpro.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leanderli.dev.cardlauncherpro.common.AppConstants;

/**
 * @author leanderli
 * @description
 * @date 2018/3/3 00030233
 */

public class MyDBHelper extends SQLiteOpenHelper {

    /**
     *
     * @param context
     * @param name
     * @param factory 游标工厂实例，设置为 null 代表获取默认工厂实例
     * @param version
     */
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists tb_app_info(package_name text primary key, " +
                "activity_name text not null, app_name text not null, icon_path text not null);");
        db.execSQL("create table if not exists tb_fav_app(package_name text primary key, " +
                "activity_name text not null, app_name text not null, icon_path text not null);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
