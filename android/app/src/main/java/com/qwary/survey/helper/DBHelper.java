package com.qwary.survey.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qwary.survey.model.SurveyModel;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "com.qwary.survey";
    public static final String TABLE_SURVEY = "surveys";

    public static final String DOMAIN = "DOMAIN";
    public static final String TOKEN = "TOKEN";

    public static final String LOADER = "LOADER";
    public static final String MODAL = "MODAL";

    public static final String IS_ALREADY_TAKEN = "IS_ALREADY_TAKEN";

    public static final String START_AFTER = "START_AFTER";
    public static final String REPEAT_INTERVAL = "REPEAT_INTERVAL";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table " + TABLE_SURVEY + "(id integer primary key, " + DOMAIN + " text," + TOKEN + " text," + LOADER + " text," + MODAL + " text," + IS_ALREADY_TAKEN + " text," + START_AFTER + " text," + REPEAT_INTERVAL + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SURVEY + "");
        onCreate(db);
    }

    public boolean insertSurvey(SurveyModel mSurveyObj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOKEN, mSurveyObj.getToken());
        contentValues.put(DOMAIN, mSurveyObj.getDomain());
        contentValues.put(LOADER, mSurveyObj.isLoader() ? "true" : "false");
        contentValues.put(MODAL, mSurveyObj.isModal() ? "true" : "false");
        contentValues.put(IS_ALREADY_TAKEN, mSurveyObj.isAlreadyTaken() ? "true" : "false");
        contentValues.put(START_AFTER, mSurveyObj.getStartAfter()+"");
        contentValues.put(REPEAT_INTERVAL, mSurveyObj.getRepeatInterval());
        db.insert(TABLE_SURVEY, null, contentValues);
        return true;
    }

    public boolean updateSurveyTaken(String token, boolean isAlreadyTaken) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(IS_ALREADY_TAKEN, isAlreadyTaken ? "true" : "false");
        db.update(TABLE_SURVEY, contentValues, TOKEN + " = ? ", new String[]{token});
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from contacts where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_SURVEY);
        return numRows;
    }

    public Integer deleteData(String tableName, String name, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(tableName, "NAME = ? AND TYPE = ?", new String[]{name, type});
    }

    public Integer deleteSurvey(String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_SURVEY, TOKEN + " = ? ", new String[]{token});
    }

    public boolean surveyExist(String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_SURVEY + " WHERE " + TOKEN + "=?", new String[]{token});
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    public boolean surveyExist(String domain, String token) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_SURVEY + " WHERE " + DOMAIN + "=? AND " + TOKEN + "=?", new String[]{domain, token});
        if (cursor.getCount() <= 0) {
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

}

