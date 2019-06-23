package hit.TechNews.Db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "TechNews.db";
    private String CREATE_TABLE_NEWS =
            "create table news(" +
                    "articleid integer primary key," +
                    "imgurl VARCHAR(100)," +
                    "webid int,"+
                    "date datetime,"+
                    "title VARCHAR(100)," +
                    "url VARCHAR(100)," +
                    "source VARCHAR(32)," +
                    "fever float," +
                    "watch int)" ;
    private String CREATE_TABLES_USERS="" +
            "create table users(" +
                "userid int," +
                "email VARCHAR(255)," +
                "phone VARCHAR(20)," +
                "nickname varchar(20)," +
                "token varchar(255)," +
                "is_signin tinyint default 0," +
                "sort int default 0)";

    private String CREATE_TABLE_VIDEOS =
            "create table video(" +
                    "videoid integer primary key," +
                    "imgurl VARCHAR(100)," +
                    "webid int,"+
                    "date datetime,"+
                    "title VARCHAR(100)," +
                    "url VARCHAR(100)," +
                    "source VARCHAR(32)," +
                    "fever int)" ;
    private String DEFAULT_USER=
            "insert into users(userid,nickname,is_signin,token) values(0,\"未登录\",1,\"\")" ;
    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DbHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NEWS);
        db.execSQL(CREATE_TABLES_USERS);
        db.execSQL(CREATE_TABLE_VIDEOS);
        db.execSQL(DEFAULT_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
