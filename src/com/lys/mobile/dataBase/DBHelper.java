package com.lys.mobile.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dvillanueva on 01/07/2016.
 */
public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context, ConstasDB.DB_NAME, null, ConstasDB.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(ConstasDB.TABLA_MTP_ACCESO_SQL);
        sqLiteDatabase.execSQL(ConstasDB.TABLA_MTP_MENUS_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ConstasDB.TABLA_MTP_ACCESO_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ConstasDB.TABLA_MTP_MENUS_NAME);

        //onCreate(sqLiteDatabase);
    }
}
