package com.example.taskso;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Arrays;
import java.util.List;

public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "taskso_final.db";
    private static final String TABLE_GROUP = "GROUP_LIST";
    private static final String TABLE_TASK = "TASK_LIST";
    private static final String GROUP_ID = "gId";
    private static final String GROUP_NAME = "gName";
    private static final String GROUP_COLOR = "gColor";
    private static final String TASK_ID = "tId";
    private static final String TASK_NAME = "tName";
    private static final String TASK_CHECK = "tCheck";
    private static final String TASK_STAR = "tStar";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_GROUP +
                " ( " + GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + GROUP_NAME + " TEXT NOT NULL, " + GROUP_COLOR + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TASK +
                " ( " + TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK_NAME + " TEXT NOT NULL, " +
                TASK_CHECK + " TEXT NOT NULL, " + TASK_STAR + " TEXT NOT NULL, " + GROUP_ID + " INTEGER NOT NULL )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
        onCreate(db);
    }

    public Cursor getGroupList() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GROUP, null);
    }

    public int createGroup(String groupName, String groupColor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GROUP_NAME, groupName);
        cv.put(GROUP_COLOR, groupColor);
        if (db.insert(TABLE_GROUP, null, cv) != -1) {
            Cursor c = db.rawQuery("SELECT LAST_INSERT_ROWID()", null);
            c.moveToNext();
            return c.getInt(0);
        }

        return -1;
    }

    public boolean deleteGroup(String groupId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASK, GROUP_ID + " = ?", new String[] { groupId });
        return db.delete(TABLE_GROUP, GROUP_ID + " = ?", new String[] { groupId }) >= 1;
    }

    public String getGroupColor(String groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + GROUP_COLOR + " FROM " + TABLE_GROUP + " WHERE " + GROUP_ID + " = ?", new String[] { groupId });
        c.moveToNext();
        return c.getString(0);
    }

    public boolean setGroupColor(String groupId, String groupColor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(GROUP_COLOR, groupColor);
        return db.update(TABLE_GROUP, args, GROUP_ID + " = ?", new String[] { groupId }) >= 1;
    }

    public Cursor getTaskList(String groupId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TASK + " WHERE " + GROUP_ID + " = ?", new String[] { groupId });
    }

    public Cursor getAllTasks() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TASK, null);
    }

    // Return Task ID
    public int createTask(String groupId, String taskName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASK_NAME, taskName);
        cv.put(TASK_CHECK, "0");
        cv.put(TASK_STAR, "0");
        cv.put(GROUP_ID, groupId);
        if (db.insert(TABLE_TASK, null, cv) != -1) {
            Cursor c = db.rawQuery("SELECT LAST_INSERT_ROWID()", null);
            c.moveToNext();
            return c.getInt(0);
        }

        return -1;
    }

    public boolean deleteTask(String taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TASK, TASK_ID + " = ?", new String[] { taskId }) >= 1;
    }

    public boolean getTaskChecked(String taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + TASK_CHECK + " FROM " + TABLE_TASK + " WHERE " + TASK_ID + " = ?", new String[] { taskId });
        c.moveToNext();
        return c.getString(0).equals("1");
    }

    public boolean setTaskChecked(String taskId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        String checked = isChecked ? "1" : "0";
        ContentValues args = new ContentValues();
        args.put(TASK_CHECK, checked);
        return db.update(TABLE_TASK, args, TASK_ID + " = ?", new String[] { taskId }) >= 1;
    }

    public boolean getTaskStarred(String taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + TASK_STAR + " FROM " + TABLE_TASK + " WHERE " + TASK_ID + " = ?", new String[] { taskId });
        c.moveToNext();
        return c.getString(0).equals("1");
    }

    public boolean setTaskStarred(String taskId, boolean isStarred) {
        SQLiteDatabase db = this.getWritableDatabase();
        String starred = isStarred ? "1" : "0";
        ContentValues args = new ContentValues();
        args.put(TASK_STAR, starred);
        return db.update(TABLE_TASK, args, TASK_ID + " = ?", new String[] { taskId }) >= 1;
    }

    public List<String> getGroupNameAndColorByTaskId(String taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(
                "SELECT " + GROUP_NAME + ", " + GROUP_COLOR +
                        " FROM " + TABLE_TASK +
                        " INNER JOIN " + TABLE_GROUP + " ON " + TABLE_TASK + "." + GROUP_ID + " = " + TABLE_GROUP + "." + GROUP_ID +
                        " WHERE " + TASK_ID + " = ?", new String[] { taskId }
                );
        c.moveToNext();
        return Arrays.asList(c.getString(0), c.getString(1));
    }
}
