package com.leanderli.dev.cardlauncherpro.model;

import android.content.Intent;

import com.leanderli.dev.cardlauncherpro.common.AppConstants;
import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 应用数据库类
 * @author leanderli
 * @description
 * @date 2018/3/6 00062057
 */
@Database(name = AppDataBase.DATABASE_NAME, version = AppDataBase.DATABASE_VERSION)
public class AppDataBase {

    public static final String DATABASE_NAME = "db_cardlauncherpro";

    public static final int DATABASE_VERSION = 1;
}
