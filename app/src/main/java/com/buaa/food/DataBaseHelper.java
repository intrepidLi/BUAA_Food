package com.buaa.food;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.Nullable;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class DataBaseHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "BuaaFood.db";
    private Context context;

    private ByteArrayOutputStream byteArrayOutputStream;
    private byte[] imageInByte;

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
        db.execSQL("drop Table if exists historyDishes");
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
                " phone varchar(20) unique, " +
                "image BLOB)" );

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
                "image BLOB, " +
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

        // 新建历史记录表 （id（主键），用户，菜品，时间）
        myDataBase.execSQL("create table if not exists historyDishes(" +
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
        initSuperAdmin(myDataBase);
        initTestUser1(myDataBase);
        initTestFavoriteDish(myDataBase);
        initTestHistoryDish(myDataBase);
        initTestComment(myDataBase);
        initTestSecondComment(myDataBase);
    }

    public void initSuperAdmin(SQLiteDatabase myDataBase) {
        ContentValues values = new ContentValues();
        values.put("username", "admin");
        values.put("password", "123456");
        values.put("phone", "12345678901");
        values.put("image", new byte[0]);

        long newRowId = myDataBase.insert("users", null, values);

        if (newRowId != -1) {
            Timber.tag("initSuperAdmin").d("initSuperAdmin Success!!!");
        } else {
            Timber.tag("initSuperAdmin").d("initSuperAdmin Failed!!!");
        }
    }

    public void initTestUser1(SQLiteDatabase myDataBase) {
        ContentValues values = new ContentValues();
        values.put("username", "user1");
        values.put("password", "12345678");
        values.put("phone", "12345678902");
        values.put("image", new byte[0]);

        long newRowId = myDataBase.insert("users", null, values);

        if (newRowId != -1) {
            Timber.tag("initTestUser1").d("initTestUser1 Success!!!");
        } else {
            Timber.tag("initTestUser1").d("initTestUser1 Failed!!!");
        }
    }

    public void initTestFavoriteDish(SQLiteDatabase myDataBase) {
        ContentValues values = new ContentValues();
        values.put("userId", 2);
        values.put("dishId", 2);

        long newRowId = myDataBase.insert("favoriteDishes", null, values);

        if (newRowId != -1) {
            Timber.tag("initTestFavoriteDish").d("initTestFavoriteDish Success!!!");
        } else {
            Timber.tag("initTestFavoriteDish").d("initTestFavoriteDish Failed!!!");
        }
    }

    public void initTestHistoryDish(SQLiteDatabase myDataBase) {
        ContentValues values = new ContentValues();
        values.put("userId", 2);
        values.put("dishId", 3);

        long newRowId = myDataBase.insert("historyDishes", null, values);

        if (newRowId != -1) {
            Timber.tag("initTestHistoryDish").d("initTestHistoryDish Success!!!");
        } else {
            Timber.tag("initTestHistoryDish").d("initTestHistoryDish Failed!!!");
        }
    }

    public void initTestComment(SQLiteDatabase myDataBase) {
        ContentValues values = new ContentValues();
        values.put("userId", 2);
        values.put("dishId", 4);
        values.put("comment", "好吃！！！");
        // 获取当前时间
        Date currentDate = new Date();

        // 定义时间格式
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = dateFormat.format(currentDate);

        values.put("time", formattedTime);

        long newRowId = myDataBase.insert("comments", null, values);

        if (newRowId != -1) {
            Timber.tag("initTestComment").d("initTestComment Success!!!");
        } else {
            Timber.tag("initTestComment").d("initTestComment Failed!!!");
        }
    }

    public void initTestSecondComment(SQLiteDatabase myDataBase) {
        ContentValues values = new ContentValues();
        values.put("userId", 1);
        values.put("commentId", 1);
        values.put("secondComment", "你说的对！！！");
        // 获取当前时间
        Date currentDate = new Date();

        // 定义时间格式
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = dateFormat.format(currentDate);

        values.put("time", formattedTime);

        long newRowId = myDataBase.insert("secondComments", null, values);

        if (newRowId != -1) {
            Timber.tag("initTestSecondComment").d("initTestSecondComment Success!!!");
        } else {
            Timber.tag("initTestSecondComment").d("initTestSecondComment Failed!!!");
        }
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
                    (context).getAssets().open("dishes2.csv")
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
                    // TODO: 数据库里的图片初始化除了4张时都为new byte[0]
                    if (!Objects.equals(line[8], "default")) {
                        String base64ImageData = line[8];
                        byte[] imageData = new byte[0];
                        Timber.tag("uploadDishImage").d(base64ImageData);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            imageData = Base64.getDecoder().decode(base64ImageData);
                        }
                        contentValues.put("image", imageData);
                    } else {
                        contentValues.put("image", new byte[0]);
                    }
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

    public boolean insertInitUser(String username, String password, String phone){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        // ContentValues contentValues = new ContentValues();
        contentValues.put("image", new byte[0]);
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        long result = db.insert("users", null, contentValues);
        return result != -1;
    }

    public boolean updateUser(String username, String password, String phone){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("phone", phone);
        long result = db.update("users", contentValues, "username=?", new String[]{username});
        return result != -1;
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
            cursor.close();
            db.close();
            return true;
        }else{
            cursor.close();
            db.close();
            return false;
        }
    }

    public void insertComment(int dishId, String content, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("dishId", dishId);
        contentValues.put("comment", content);
        // 获取当前时间
        Date currentDate = new Date();

        // 定义时间格式
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = dateFormat.format(currentDate);

        contentValues.put("time", formattedTime);

        long result = db.insert("comments", null, contentValues);
        if(result == -1){
            Toast.makeText(context, "Failed to insert comment", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Failed to insert comment");
        }else{
            Toast.makeText(context, "Successfully inserted comment", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Successfully inserted comment");
           // db.close();
        }
    }

    public void insertSecondComment(int commentId, String content, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("commentId", commentId);
        contentValues.put("comment", content);
        // 获取当前时间
        Date currentDate = new Date();

        // 定义时间格式
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat
                = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = dateFormat.format(currentDate);

        contentValues.put("time", formattedTime);

        long result = db.insert("secondComments", null, contentValues);
        if(result == -1){
            Toast.makeText(context, "Failed to insert comment", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Failed to insert comment");
        }else{
            Toast.makeText(context, "Successfully inserted comment", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Successfully inserted comment");
            // db.close();
        }
    }

    public boolean checkPhonePassword(String phone, String password){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=? and password=?",
                new String[]{phone, password});
        if(cursor.getCount() > 0){
            cursor.close();
            // db.close();
            return true;
        }else{
            cursor.close();
            // db.close();
            return false;
        }
    }

    public String getUsername(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex("username"));
            cursor.close();
            // db.close();
            return username;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public String getUsernameById(int userId){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where id=?",
                new String[]{String.valueOf(userId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String username = cursor.getString(cursor.getColumnIndex("username"));
            cursor.close();
            // db.close();
            return username;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public int getUserId(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            cursor.close();
            // db.close();
            return Integer.parseInt(id);
        } else {
            cursor.close();
            // db.close();
            return -1;
        }
    }

    public String getUserPassword(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String password = cursor.getString(cursor.getColumnIndex("password"));
            cursor.close();
            // db.close();
            return password;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public byte[] getUserAvatar(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
            cursor.close();
            // db.close();
            return image;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public int getCommentId(int dishId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from comments where dishId=?",
                new String[]{String.valueOf(dishId)});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int commentId = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            // db.close();
            return commentId;
        } else {
            return -1;
        }
    }

    public int getCommentUserId(int dishId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from comments where dishId=?",
                new String[]{String.valueOf(dishId)});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int userId = cursor.getInt(cursor.getColumnIndex("userId"));
            cursor.close();
            // db.close();
            return userId;
        } else {
            return -1;
        }
    }

    public String getCommentContent(int dishId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from comments where dishId=?",
                new String[]{String.valueOf(dishId)});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String comment = cursor.getString(cursor.getColumnIndex("comment"));
            cursor.close();
            // db.close();
            return comment;
        } else {
            return null;
        }
    }

    public String getCommentTime(int dishId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from comments where dishId=?",
                new String[]{String.valueOf(dishId)});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String time = cursor.getString(cursor.getColumnIndex("time"));
            cursor.close();
            // db.close();
            return time;
        } else {
            return null;
        }
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void updateUserAvatar(String phone, Bitmap bitmapImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInByte = byteArrayOutputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put("image", imageInByte);

        long result = db.update("users", contentValues, "phone=?", new String[]{phone});
        if(result == -1){
            Toast.makeText(context, "Failed to update avatar", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Failed to update avatar");
        }else{
            Toast.makeText(context, "Successfully updated avatar", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Successfully updated avatar");
            db.close();
        }
    }

    public void updateUsername(String phone, String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);

        long result = db.update("users", contentValues, "phone=?", new String[]{phone});
        if(result == -1){
            Toast.makeText(context, "Failed to update username", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Failed to update username");
        }else{
            Toast.makeText(context, "Successfully updated username", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Successfully updated username");
            // db.close();
        }
    }

    public boolean updatePassword(String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("password", password);

        long result = db.update("users", contentValues, "phone=?", new String[]{phone});
        if(result == -1){
            Toast.makeText(context, "Failed to update password", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Failed to update password");
            return false;
        }else{
            Toast.makeText(context, "Successfully updated password", Toast.LENGTH_SHORT).show();
            Timber.tag("DatabaseHelper").d("Successfully updated password");

            // db.close();
            return true;
        }
    }

    public List<DishPreview> fetchCanteenDishes(String canteenName) {
        List<DishPreview> dishPreviews = new ArrayList<>();

        // 使用数据库帮助类获取数据库实例
        SQLiteDatabase db = getReadableDatabase();

        // 构建查询语句，使用 JOIN 语句连接 dishes 和 canteens 表
        String query = "SELECT d.id, d.name, d.price, d.image, d.ordered, d.viewed FROM dishes d " +
                "INNER JOIN canteens c ON d.canteenId = c.id " +
                "WHERE c.address = ?";

        // 执行查询
        Cursor cursor = db.rawQuery(query, new String[]{canteenName});

        // 处理查询结果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));

                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                int ordered = cursor.getInt(cursor.getColumnIndex("ordered"));
                int viewed = cursor.getInt(cursor.getColumnIndex("viewed"));
                // 根据查询结果创建 DishPreview 对象并添加到数据列表
                dishPreviews.add(new DishPreview(id, name, price, image, ordered, viewed));
            } while (cursor.moveToNext());

            // 关闭 cursor
            cursor.close();
        }

        return dishPreviews;
    }

    public List<DishPreview> fetchAllDishes() {
        List<DishPreview> dishPreviews = new ArrayList<>();

        // 使用数据库帮助类获取数据库实例
        SQLiteDatabase db = getReadableDatabase();

        // 构建查询语句，使用 JOIN 语句连接 dishes 和 canteens 表
        String query = "SELECT id, name, price, image, ordered, viewed FROM dishes";

        // 执行查询
        Cursor cursor = db.rawQuery(query, null);

        // 处理查询结果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));

                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));

                int ordered = cursor.getInt(cursor.getColumnIndex("ordered"));
                int viewed = cursor.getInt(cursor.getColumnIndex("viewed"));
                // 根据查询结果创建 DishPreview 对象并添加到数据列表
                dishPreviews.add(new DishPreview(id, name, price, image, ordered, viewed));
            } while (cursor.moveToNext());

            // 关闭 cursor
            cursor.close();
        }

        return dishPreviews;
    }

    public String getDishName(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            cursor.close();
            // db.close();
            return name;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public float getDishPrice(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            float price = cursor.getFloat(cursor.getColumnIndex("price"));
            cursor.close();
            // db.close();
            return price;
        } else {
            cursor.close();
            // db.close();
            return -1f;
        }
    }

    public int getDishRemaining(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int remaining = cursor.getInt(cursor.getColumnIndex("remain"));
            cursor.close();
            // db.close();
            return remaining;
        } else {
            cursor.close();
            // db.close();
            return -1;
        }
    }

    public byte[] getDishImage(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
            cursor.close();
            // db.close();
            return image;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public String getDishCanteen(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int canteenId = cursor.getInt(cursor.getColumnIndex("canteenId"));
            cursor.close();
            // db.close();
            return getCanteenName(canteenId);
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public String getDishWindow(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int windowId = cursor.getInt(cursor.getColumnIndex("windowId"));
            cursor.close();
            // db.close();
            return getWindowName(windowId);
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public String getCanteenName(int canteenId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from canteens where id=?", new String[]{String.valueOf(canteenId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            cursor.close();
            // db.close();
            return name;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public String getWindowName(int windowId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from windows where id=?", new String[]{String.valueOf(windowId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String name = cursor.getString(cursor.getColumnIndex("name"));
            cursor.close();
            // db.close();
            return name;
        } else {
            cursor.close();
            // db.close();
            return null;
        }
    }

    public int getDishViewed(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int viewed = cursor.getInt(cursor.getColumnIndex("viewed"));
            cursor.close();
            // db.close();
            return viewed;
        } else {
            cursor.close();
            // db.close();
            return -1;
        }
    }

    public int getDishOrdered(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from dishes where id=?", new String[]{String.valueOf(dishId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int ordered = cursor.getInt(cursor.getColumnIndex("ordered"));
            cursor.close();
            // db.close();
            return ordered;
        } else {
            cursor.close();
            // db.close();
            return -1;
        }
    }

    public boolean updateDishName(int dishId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish name");
            return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish name");
            // db.close();
            return true;
        }
    }

    public boolean updateDishPrice(int dishId, float price) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("price", price);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish price");
            return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish price");
            // db.close();
            return true;
        }
    }

    public boolean checkDishRemaining(int dishId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT remain FROM dishes WHERE id=?", new String[]{String.valueOf(dishId)});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int curRemain = cursor.getInt(cursor.getColumnIndex("remain"));
            cursor.close();
            if (curRemain <= 0) {
                Timber.tag("DatabaseHelper").d("Dish remaining is already less than 0. No buying needed.");
                cursor.close();
                // db.close();
                return false;
            }
            return true;
        } else {
            cursor.close();
            Timber.tag("DatabaseHelper").d("Update Dish remaining without this dish.");
            return false;
        }
    }

    public void updateDishRemaining(int dishId, int remaining) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("remain", remaining);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish remaining");
           // return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish remaining");
            // db.close();
           // return true;
        }
    }

    public void updateDishOrdered(int dishId, int orders) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("ordered", orders);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish ordered");
            // return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish ordered");
            // db.close();
            // return true;
        }
    }

    public void updateDishViewed(int dishId, int viewed) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("viewed", viewed);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish remaining");
            // return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish remaining");
            // db.close();
            // return true;
        }
    }

    public boolean updateDishImage(int dishId, Bitmap bitmapImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInByte = byteArrayOutputStream.toByteArray();

        ContentValues contentValues = new ContentValues();
        contentValues.put("image", imageInByte);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("updateDishImage").d("Failed to update dish image");
            return false;
        }else{
            Timber.tag("updateDishImage").d("Successfully updated dish image");
            // db.close();
            return true;
        }
    }

    public boolean updateDishCanteen(int dishId, int canteenId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("canteenId", canteenId);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish canteen");
            return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish canteen");
            // db.close();
            return true;
        }
    }

    public boolean updateDishWindow(int dishId, int windowId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("windowId", windowId);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish window");
            return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish window");
            // db.close();
            return true;
        }
    }

    public boolean insertDish(String name, float price, int remaining, int windowId, int canteenId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("price", price);
        contentValues.put("remain", remaining);
        contentValues.put("windowId", windowId);
        contentValues.put("canteenId", canteenId);
        contentValues.put("image", new byte[0]);

        long result = db.insert("dishes", null, contentValues);
        return result != -1;
    }

    public boolean updateDish(int id, String name, float price, int remaining, int windowId, int canteenId){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", name);
        contentValues.put("price", price);
        contentValues.put("remain", remaining);
        contentValues.put("windowId", windowId);
        contentValues.put("canteenId", canteenId);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(id)});
        return result != -1;
    }

    public int getCanteenId(String canteenName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from canteens where name=?", new String[]{canteenName});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            // db.close();
            return id;
        } else {
            cursor.close();
            // db.close();
            return -1;
        }
    }

    public int getWindowId(String windowName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from windows where name=?", new String[]{windowName});

        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            // db.close();
            return id;
        } else {
            cursor.close();
            // db.close();
            return -1;
        }
    }

    public boolean isFavorite(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        Cursor cursor = db.rawQuery("select * from favoriteDishes where userId=? and dishId=?",
                new String[]{String.valueOf(userId), String.valueOf(dishId)});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    public void uploadFavorite(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        Cursor cursor = db.rawQuery("select * from favoriteDishes where userId=? and dishId=?",
                new String[]{String.valueOf(userId), String.valueOf(dishId)});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Toast.makeText(context, "已收藏", Toast.LENGTH_SHORT).show();
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("userId", userId);
            contentValues.put("dishId", dishId);

            long result = db.insert("favoriteDishes", null, contentValues);
            if (result != -1) {
                Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "收藏失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadHistory(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        Cursor cursor = db.rawQuery("select * from historyDishes where userId=? and dishId=?",
                new String[]{String.valueOf(userId), String.valueOf(dishId)});

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String whereClause = "userId=? AND dishId=?";
            String[] whereArgs = {String.valueOf(userId), String.valueOf(dishId)};

            // 执行删除操作
            int deletedRows = db.delete("historyDishes", whereClause, whereArgs);
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("userId", userId);
        contentValues.put("dishId", dishId);

        long result = db.insert("historyDishes", null, contentValues);
        if (result != -1) {
            // Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();
            Timber.tag("uploadHistory").d("购买记录添加成功");
        } else {
            Timber.tag("uploadHistory").d("购买记录添加失败");
        }
    }

    public void deleteCollection(int dishId) {
        SQLiteDatabase db = getWritableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        // 定义删除的条件
        String whereClause = "userId=? AND dishId=?";
        String[] whereArgs = {String.valueOf(userId), String.valueOf(dishId)};

        // 执行删除操作
        int deletedRows = db.delete("favoriteDishes", whereClause, whereArgs);

        // 检查是否删除成功
        if (deletedRows > 0) {
           Toast.makeText(context, "取消收藏成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "取消收藏失败", Toast.LENGTH_SHORT).show();
        }
    }

    public List<DishPreview> fetchFavorites() {
        List<DishPreview> dishPreviews = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        Cursor cursor = db.rawQuery("SELECT d.id, d.name, d.price, d.image, d.ordered, d.viewed " +
                        "FROM dishes d " +
                        "INNER JOIN favoriteDishes f ON d.id = f.dishId " +
                        "WHERE f.userId = ?",
                new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));

                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                int ordered = cursor.getInt(cursor.getColumnIndex("ordered"));
                int viewed = cursor.getInt(cursor.getColumnIndex("viewed"));
                dishPreviews.add(new DishPreview(id, name, price, image, ordered, viewed));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return dishPreviews;
    }

    public List<DishPreview> fetchHistorys() {
        List<DishPreview> dishPreviews = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        Cursor cursor = db.rawQuery("SELECT d.id, d.name, d.price, d.image, d.ordered, d.viewed " +
                        "FROM dishes d " +
                        "INNER JOIN historyDishes h ON d.id = h.dishId " +
                        "WHERE h.userId = ?",
                new String[]{String.valueOf(userId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));

                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));

                int ordered = cursor.getInt(cursor.getColumnIndex("ordered"));
                int viewed = cursor.getInt(cursor.getColumnIndex("viewed"));
                dishPreviews.add(new DishPreview(id, name, price, image, ordered, viewed));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return dishPreviews;
    }

    public void clearFavoriteDishes() {
        SQLiteDatabase db = getReadableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        int deletedRows = db.delete("favoriteDishes",
                "userId = ?", new String[]{String.valueOf(userId)});

        Toast.makeText(context, "收藏夹已请空", Toast.LENGTH_SHORT).show();
        // 输出删除的行数，以便进行调试
        Timber.tag("DatabaseHelper").d("Deleted " + deletedRows + " rows from favoriteDishes table");
    }

    public void clearHistoryDishes() {
        SQLiteDatabase db = getReadableDatabase();
        int userId = getUserId(UserAuth.getLocalUserPhone());

        int deletedRows = db.delete("historyDishes",
                "userId = ?", new String[]{String.valueOf(userId)});

        Toast.makeText(context, "历史记录已请空", Toast.LENGTH_SHORT).show();
        // 输出删除的行数，以便进行调试
        Timber.tag("DatabaseHelper").d("Deleted " + deletedRows + " rows from historyDishes table");
    }

    public List<CommentPreview> fetchComments(int dishId) {
        List<CommentPreview> commentPreviews = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT c.id, c.userId, c.comment, c.time " +
                        "FROM comments c " +
                        "WHERE c.dishId = ?",
                new String[]{String.valueOf(dishId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int userId = cursor.getInt(cursor.getColumnIndex("userId"));
                String comment = cursor.getString(cursor.getColumnIndex("comment"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String userName = getUsernameById(userId);

                commentPreviews.add(new CommentPreview(dishId, id, userName, comment, time));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return commentPreviews;
    }

    public List<CommentPreview> fetchSecondComments(int commentId) {
        List<CommentPreview> commentPreviews = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT s.id, s.userId, s.comment, s.time " +
                        "FROM secondComments s " +
                        "WHERE s.commentId = ?",
                new String[]{String.valueOf(commentId)});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                int userId = cursor.getInt(cursor.getColumnIndex("userId"));
                String comment = cursor.getString(cursor.getColumnIndex("comment"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String userName = getUsernameById(userId);

                commentPreviews.add(new CommentPreview(-1, id, userName, comment, time));
            } while (cursor.moveToNext());

            cursor.close();
        }

        return commentPreviews;
    }

}
