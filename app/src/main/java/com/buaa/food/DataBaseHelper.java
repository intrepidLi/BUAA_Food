package com.buaa.food;
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
import java.util.ArrayList;
import java.util.Base64;
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

    public String getUserId(String phone){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from users where phone=?",
                new String[]{phone});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex("id"));
            cursor.close();
            // db.close();
            return id;
        } else {
            cursor.close();
            // db.close();
            return null;
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
        String query = "SELECT d.id, d.name, d.price, d.image FROM dishes d " +
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
                // 根据查询结果创建 DishPreview 对象并添加到数据列表
                dishPreviews.add(new DishPreview(id, name, price, image));
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
        String query = "SELECT id, name, price, image FROM dishes";

        // 执行查询
        Cursor cursor = db.rawQuery(query, null);

        // 处理查询结果
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String price = cursor.getString(cursor.getColumnIndex("price"));

                byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                // 根据查询结果创建 DishPreview 对象并添加到数据列表
                dishPreviews.add(new DishPreview(id, name, price, image));
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

    public boolean updateDishRemaining(int dishId, int remaining) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("remain", remaining);

        long result = db.update("dishes", contentValues, "id=?", new String[]{String.valueOf(dishId)});
        if(result == -1){
            Timber.tag("DatabaseHelper").d("Failed to update dish remaining");
            return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish remaining");
            // db.close();
            return true;
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
            Timber.tag("DatabaseHelper").d("Failed to update dish image");
            return false;
        }else{
            Timber.tag("DatabaseHelper").d("Successfully updated dish image");
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

}
