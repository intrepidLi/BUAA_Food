package com.buaa.food;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import androidx.annotation.Nullable;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;

public class DataBaseHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "BuaaFood.db";
    private Context context;

    public DataBaseHelper(@Nullable Context context) {
        super(context, "BuaaFood.db", null, 1);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        db.execSQL("drop Table if exists favoriteDishes");
        db.execSQL("drop Table if exists favoriteWindows");
        db.execSQL("drop Table if exists favoriteCanteens");
        db.execSQL("drop Table if exists dinner");
        db.execSQL("drop Table if exists lunch");
        db.execSQL("drop Table if exists breakfast");
        db.execSQL("drop Table if exists secondComments");
        db.execSQL("drop Table if exists comments");
        db.execSQL("drop Table if exists orders");
        db.execSQL("drop Table if exists viewedDishes");
        db.execSQL("drop Table if exists dishes");
        db.execSQL("drop Table if exists windows");
        db.execSQL("drop Table if exists canteens");
        db.execSQL("drop Table if exists users");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase myDataBase) { // 新建数据库时需要建表并插入数据
        // 新建用户表
        myDataBase.execSQL("create table if not exists users(" +
                "id integer primary key autoincrement," +
                "username varchar(20)," +
                " password varchar(20)," +
                " phone varchar(20) unique)" );

        // 需要新增菜品表 （id(主键)，名字，窗口id，食堂id，被点餐的量，被浏览的量，菜品剩余量，图片URL）
        myDataBase.execSQL("create table if not exists dishes(" +
                "id integer primary key autoincrement," +
                " name varchar(40)," +
                " windowId integer, " +
                " canteenId integer," +
                " ordered integer," +
                " viewed integer," +
                " remain integer, " +
                " price float, " +
                "image varchar(100), " +
                "foreign key(windowId) references windows(id), " +
                "foreign key(canteenId) references canteens(id))");

        // 新建食堂表 （id（主键），名字，地址，图片URL）
        myDataBase.execSQL("create table if not exists canteens(" +
                "id integer primary key autoincrement," +
                " name varchar(20)," +
                " address varchar(50)," +
                " image varchar(100))");

        // 新建窗口表 （id（主键），名字，食堂）
        myDataBase.execSQL("create table if not exists windows(" +
                "id integer primary key autoincrement," +
                " name varchar(20)," +
                " canteenId integer," +
                "foreign key(canteenId) references canteens(id))");

        // 新建订单表 （id（主键），用户id，菜品id，数量，时间，状态）
        myDataBase.execSQL("create table if not exists orders(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " dishId integer," +
                " number integer," +
                " time varchar(20)," +
                " status integer," +
                "foreign key(userId) references users(id)," +
                "foreign key(dishId) references dishes(id))");

        // 新建评论菜品表 （id（主键），用户id，菜品id，评论，时间）
        myDataBase.execSQL("create table if not exists comments(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " dishId integer," +
                " comment varchar(100)," +
                " time varchar(20)," +
                "foreign key(userId) references users(id)," +
                "foreign key(dishId) references dishes(id))");

        // 新建二级评论表 （id（主键），用户，评论，二级评论，时间）
        myDataBase.execSQL("create table if not exists secondComments(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " commentId integer," +
                " secondComment varchar(100)," +
                " time varchar(20)," +
                "foreign key(userId) references users(id)," +
                "foreign key(commentId) references comments(id))");

        // 新建收藏菜品表 （id（主键），用户，菜品，时间）
        myDataBase.execSQL("create table if not exists favoriteDishes(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " dishId integer," +
                " time varchar(20)," +
                "foreign key(userId) references users(id)," +
                "foreign key(dishId) references dishes(id))");

        // 新建收藏窗口表 （id（主键），用户，窗口，时间）
        myDataBase.execSQL("create table if not exists favoriteWindows(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " windowId integer," +
                " time varchar(20)," +
                "foreign key(userId) references users(id)," +
                "foreign key(windowId) references windows(id))");

        // 新建收藏食堂表 （id（主键），用户，食堂，时间）
        myDataBase.execSQL("create table if not exists favoriteCanteens(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " canteenId integer," +
                " time varchar(20)," +
                "foreign key(userId) references users(id)," +
                "foreign key(canteenId) references canteens(id))");

        // 新建浏览记录表 （id（主键），用户，菜品，时间）
        myDataBase.execSQL("create table if not exists viewedDishes(" +
                "id integer primary key autoincrement," +
                " userId integer," +
                " dishId integer," +
                " time varchar(20)," +
                "foreign key(userId) references users(id)," +
                "foreign key(dishId) references dishes(id))");

        // 新建早餐表
        myDataBase.execSQL("create table if not exists breakfast(" +
                "id integer primary key autoincrement," +
                " dishId integer," +
                "foreign key(dishId) references dishes(id))");

        // 新建午餐表
        myDataBase.execSQL("create table if not exists lunch(" +
                "id integer primary key autoincrement," +
                " dishId integer," +
                "foreign key(dishId) references dishes(id))");

        // 新建晚餐表
        myDataBase.execSQL("create table if not exists dinner(" +
                "id integer primary key autoincrement," +
                " dishId integer," +
                "foreign key(dishId) references dishes(id))");

        initAllTables(myDataBase);
    }

    public void initAllTables(SQLiteDatabase myDataBase) {
        // initInsertUser(myDataBase, context);
        initInsertCanteen(myDataBase);
        initInsertWindow(myDataBase);
        initInsertDish(myDataBase);
        initInsertBreakfast(myDataBase);
    }

    public void initInsertCanteen(SQLiteDatabase myDataBase) {
        try {
            // 打开CSV文件输入流
            CSVReader reader = new CSVReader(new InputStreamReader(
                    (context).getAssets().open("canteens.csv")
            ));
            // 跳过CSV文件的标题行
            String[] header = reader.readNext();
            String[] line;
            ContentValues contentValues = new ContentValues();
            // 逐行读取CSV文件并插入数据
            while ((line = reader.readNext()) != null) {
                if (line.length == 4) {
                    contentValues.put("name", line[1]);
                    contentValues.put("address", line[2]);
                    contentValues.put("image", line[3]);

                    // 使用占位符插入数据，防止SQL注入
                    myDataBase.insert("canteens", null, contentValues);
                }
            }
        } catch (IOException e) {
            Timber.tag("DatabaseInitializer").e(e, "Error reading canteens.csv");
        }
    }

    public void initInsertWindow(SQLiteDatabase myDataBase) {
        try {
            // 打开CSV文件输入流
            CSVReader reader = new CSVReader(new InputStreamReader(
                    (context).getAssets().open("windows.csv")
            ));
            // 跳过CSV文件的标题行
            String[] header = reader.readNext();
            String[] line;
            ContentValues contentValues = new ContentValues();
            // 逐行读取CSV文件并插入数据
            while ((line = reader.readNext()) != null) {
                if (line.length == 3) {
                    contentValues.put("name", line[1]);
                    contentValues.put("canteenId", Integer.parseInt(line[2]));
                    // String image = values[3];

                    // 使用占位符插入数据，防止SQL注入
                    myDataBase.insert("windows", null, contentValues);
                }
            }

        } catch (IOException e) {
            Timber.tag("DatabaseInitializer").e(e, "Error reading windows.csv");
        }
    }

    public void initInsertDish(SQLiteDatabase myDataBase) {
        try {
            // 打开CSV文件输入流
            CSVReader reader = new CSVReader(new InputStreamReader(
                    (context).getAssets().open("dishes1.csv")
            ));
            // 跳过CSV文件的标题行
            String[] header = reader.readNext();
            String[] line;

            ContentValues contentValues = new ContentValues();
            // 逐行读取CSV文件并插入数据
            while ((line = reader.readNext()) != null) {
                if (line.length == 9) {
                    contentValues.put("name", line[1]);
                    contentValues.put("windowId", Integer.parseInt(line[2]));
                    contentValues.put("canteenId", Integer.parseInt(line[3]));
                    contentValues.put("ordered", Integer.parseInt(line[4]));
                    contentValues.put("viewed", Integer.parseInt(line[5]));
                    contentValues.put("remain", Integer.parseInt(line[6]));
                    contentValues.put("price", Float.parseFloat(line[7]));
                    contentValues.put("image", line[8]);
                    // String image = values[3];

                    // 使用占位符插入数据，防止SQL注入
                    myDataBase.insert("dishes", null, contentValues);
                }
            }

        } catch (IOException e) {
            Timber.tag("DatabaseInitializer").e(e, "Error reading dishes.csv");
        }
    }

    public void initInsertBreakfast(SQLiteDatabase myDataBase) {
        try {
            // 打开CSV文件输入流
            CSVReader reader = new CSVReader(new InputStreamReader(
                    (context).getAssets().open("breakfast.csv")
            ));
            // 跳过CSV文件的标题行
            String[] header = reader.readNext();
            String[] line;
            ContentValues contentValues = new ContentValues();
            // 逐行读取CSV文件并插入数据
            while ((line = reader.readNext()) != null) {
                if (line.length == 2) {
                    // String name = values[1];
                    contentValues.put("dishId", Integer.parseInt(line[1]));

                    // 使用占位符插入数据，防止SQL注入
                    myDataBase.insert("breakfast", null, contentValues);
                }
            }

        } catch (IOException e) {
            Timber.tag("DatabaseInitializer").e(e, "Error reading breakfast.csv");
        }
    }

    public boolean insertUser(String username, String password, String phone){
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

    public String getUsername(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("username"));
        } else {
            return null;
        }
    }

    public String getUserId(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("id"));
        } else {
            return null;
        }
    }

}
