package com.kkorkmaz.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context,"Login.db",null,1 );
    }

    @Override
    public void onCreate(SQLiteDatabase myDB) {
       myDB.execSQL("create Table users(username Text primary key , password Text)");
        // myDB.execSQL("create Table users(username Text primary key,password Text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase myDB, int oldVersion, int newVersion) {

        myDB.execSQL("drop Table if exists users");
    }

    public  Boolean insertData(String username, String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues(); //ContentValues.put ile eklenecek veriler tanımlanır
        contentValues.put("username",username);
        contentValues.put("password",password);
        long result = myDB.insert("users",null,contentValues); //bundan sonra değeri saklamalıyız
        if(result == -1){ //başarılı olursa true değerini döndürür başarısız olursa -1 değer atar ve result içinde saklanır
            return false;
        }
        else{
            return true;
        }
    }

    //veritabanına ekleme işlemlerimiz bitti şimdi kullanacağımız fonksiyonlarla kullanılıcılarımızın olup olmadığının kontrolünü yapıyoruz

    public  Boolean checkusername(String username){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ?", new String [] {username});

        if(cursor.getCount()>0){ //kullanıcımız varsa true dönder

            return true;
        }
        else{
            return false;
        }

    }

    public Boolean checkusernamePassword(String username,String password){
        SQLiteDatabase myDB = this.getWritableDatabase();
        Cursor cursor = myDB.rawQuery("select * from users where username = ? and password = ? ", new String [] {username, password});

        if(cursor.getCount()>0){ //kullanıcının şifresi kullanıcı adıyla uyuyorsa true dönder
            return true;
        }
        else{
            return false;
        }

    }



}
