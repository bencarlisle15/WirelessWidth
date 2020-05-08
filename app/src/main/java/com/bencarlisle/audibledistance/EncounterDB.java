package com.bencarlisle.audibledistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

class EncounterDB extends SQLiteOpenHelper {

    EncounterDB(Context context) {
        super(context, "encounters.db", null, 1);
//        reset db
//        onUpgrade(getWritableDatabase(), 1, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Encounters (id BIGINT NOT NULL, timestamp BIGINT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE Encounters;");
        onCreate(db);
    }

    void addToDatabase(long id) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteStatement statement = db.compileStatement("INSERT INTO Encounters VALUES (?, ?)");
        statement.bindLong(1, id);
        statement.bindLong(2, System.currentTimeMillis());
        statement.executeInsert();
        db.close();
    }

    ArrayList<Encounter> getAllEncounters() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Encounter> encounters = new ArrayList<>();
        Cursor cursor = db.rawQuery( "SELECT * FROM Encounters;", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(0);
            long timestamp = cursor.getLong(1);
            encounters.add(new Encounter(id, timestamp));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return encounters;
    }

    void deleteAllEntries() {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM Encounters");
    }
}
