package com.buaa.food;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "SignUp.db";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "SignUp.db", null, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("drop Table if exists users");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase myDataBase) {
        myDataBase.execSQL("create table if not exists users(" +
                "username varchar(20), password varchar(20), phone varchar(20) primary key) ");
    }

    public boolean insert(String username, String password, String phone){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        long result = db.insert("users", null, contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean update(String username, String password, String phone){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        long result = db.update("users", contentValues, "username=?", new String[]{username});
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean checkUsername(String username){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where username=?",
                new String[]{username});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkPhone(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkPhonePassword(String phone, String password){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=? and password=?",
                new String[]{phone, password});
        if(cursor.getCount() > 0){
            return true;
        }else{
            return false;
        }
    }

}
