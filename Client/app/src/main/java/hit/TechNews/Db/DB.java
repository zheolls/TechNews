package hit.TechNews.Db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import hit.TechNews.Entry.UserEntry;

import static com.bumptech.glide.util.Preconditions.checkNotNull;

public class DB {
    private DbHelper mSqliteOpenHelp;
    private static DB INSTANCE;
    private DB(Context context){
        checkNotNull(context);
        mSqliteOpenHelp=new DbHelper(context);
    }
    public static DB getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DB(context);
        }
        return INSTANCE;
    }
    public void Login(UserEntry userEntry){
        SQLiteDatabase sqLiteDatabase=mSqliteOpenHelp.getWritableDatabase();
        String id=String.valueOf(userEntry.getUserid());
        String SQL="select * from users where userid = ?";
        Cursor cursor=sqLiteDatabase.rawQuery(SQL,new String[]{id});
        ContentValues contentValues=new ContentValues();
        contentValues.put("is_signin",0);
        sqLiteDatabase.update("users",contentValues,"is_signin=?",new String[]{"1"});
        contentValues.clear();
        if (cursor.moveToNext()){
            contentValues.put("is_signin",1);
            contentValues.put("token",userEntry.getToken());
            contentValues.put("phone",userEntry.getPhone());
            contentValues.put("nickname",userEntry.getNickname());
            sqLiteDatabase.update("users",contentValues,"userid=?",new String[]{String.valueOf(userEntry.getUserid())});
        }
        else {
            contentValues.put("userid",userEntry.getUserid());
            contentValues.put("email",userEntry.getEmail());
            contentValues.put("phone",userEntry.getPhone());
            contentValues.put("token",userEntry.getToken());
            contentValues.put("nickname",userEntry.getNickname());
            contentValues.put("is_signin",1);
            sqLiteDatabase.insert("users",null,contentValues);
        }
        cursor.close();
    }
    public void Logout(){
        ContentValues contentValues=new ContentValues();
        contentValues.put("token","");
        contentValues.put("is_signin",0);
        SQLiteDatabase sqLiteDatabase=mSqliteOpenHelp.getWritableDatabase();
        sqLiteDatabase.update("users",contentValues,null,null);
        contentValues.clear();
        contentValues.put("is_signin", 1);
        contentValues.put("sort",0);
        sqLiteDatabase.update("users", contentValues, "userid=0", null);
    }
    public UserEntry getUser(){
        SQLiteDatabase sqLiteDatabase=mSqliteOpenHelp.getWritableDatabase();
        UserEntry userEntry=new UserEntry(0);
        String SQL="select * from users where is_signin = 1";
        Cursor cursor = sqLiteDatabase.rawQuery(SQL,null);
        if (cursor.moveToNext()){
            userEntry.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            userEntry.setUserid(cursor.getInt(cursor.getColumnIndex("userid")));
            userEntry.setToken(cursor.getString(cursor.getColumnIndex("token")));
            userEntry.setNickname(cursor.getString(cursor.getColumnIndex("nickname")));
            userEntry.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        }
        cursor.close();
        return userEntry;
    }
    public String getSortType(){
        SQLiteDatabase sqLiteDatabase=mSqliteOpenHelp.getWritableDatabase();
        int type=0;
        String[] types={"lastest","hot","signedin"};
        String SQL="select * from users where is_signin = 1";
        Cursor cursor = sqLiteDatabase.rawQuery(SQL,null);
        if (cursor.moveToNext()){
            type=cursor.getInt(cursor.getColumnIndex("sort"));
        }
        cursor.close();
        return types[type];
    }
    public int getSort(){
        SQLiteDatabase sqLiteDatabase=mSqliteOpenHelp.getWritableDatabase();
        int type=0;
        String[] types={"lastest","hot","signedin"};
        String SQL="select * from users where is_signin = 1";
        Cursor cursor = sqLiteDatabase.rawQuery(SQL,null);
        if (cursor.moveToNext()){
            type=cursor.getInt(cursor.getColumnIndex("sort"));
        }
        cursor.close();
        return type;
    }
    public void setSortType(int type){
        ContentValues contentValues=new ContentValues();
        contentValues.put("sort",type);
        SQLiteDatabase sqLiteDatabase=mSqliteOpenHelp.getWritableDatabase();
        sqLiteDatabase.update("users",contentValues,"is_signin=1",null);
        contentValues.clear();
    }
}
