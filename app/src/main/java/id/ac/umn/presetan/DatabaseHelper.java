package id.ac.umn.presetan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, "Register.db", null, 1);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user(username varchar2 primary key, password varchar2)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
    }

    public Boolean insert(String username, String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        long inserting = db.insert("user", null, contentValues);
        if(inserting==-1){
            return false;
        }
        else{
            return true;
        }
    }

    //Check username if exists
    public Boolean checkUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user where username=?", new String[]{username});
        if(cursor.getCount() > 0 ){
            return false;
        }
        else{
            return true;
        }
    }

    //Check username & password
    public Boolean userpassword(String username, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor curs = db.rawQuery("select * from  user where username=? and password=?", new String[]{username,password});
        if(curs.getCount() > 0){
            return true;
        }
        else{
            return false;
        }
    }
}