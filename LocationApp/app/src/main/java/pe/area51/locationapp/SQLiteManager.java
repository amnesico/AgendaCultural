package pe.area51.locationapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by USER on 17/08/2016.
 */
public class SQLiteManager extends SQLiteOpenHelper {

    private static SQLiteManager INSTANCE;

    private static final int VERSION = 2;

    public static SQLiteManager getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SQLiteManager(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public SQLiteManager(final Context context) {
        super(context, "lugares", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql = "CREATE TABLE places (_id INTEGER PRIMARY KEY, namePlace TEXT, district TEXT, address TEXT, latitude DOUBLE(9,6), longitude DOUBLE(9,6))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS places");
        onCreate(db);

    }

    public boolean existsTable (SQLiteDatabase db, String table) {
        try {
            db.execSQL("SELECT * FROM " + table);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public long insertPlace(final Place place) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put("namePlace", place.getName_place());
        contentValues.put("district", place.getDistrict());
        contentValues.put("address", place.getAddress());
        contentValues.put("latitude", place.getLatitude());
        contentValues.put("longitude", place.getLongitude());
        return getWritableDatabase().insert("places", null, contentValues);
    }

    public ArrayList<Place> getPlaces(final String districtAsked) {
        String query = "SELECT * FROM places WHERE district LIKE '" + districtAsked + "'";
        final Cursor queryCursor = getReadableDatabase().rawQuery(query, null);
        final ArrayList<Place> places = new ArrayList<>();
        while (queryCursor.moveToNext()) {
            final long id = queryCursor.getLong(queryCursor.getColumnIndex("_id"));
            final String namePlace = queryCursor.getString(queryCursor.getColumnIndex("namePlace"));
            final String district = queryCursor.getString(queryCursor.getColumnIndex("district"));
            final String address = queryCursor.getString(queryCursor.getColumnIndex("address"));
            final double latitude = queryCursor.getDouble(queryCursor.getColumnIndex("latitude"));
            final double longitude = queryCursor.getDouble(queryCursor.getColumnIndex("longitude"));
            final Place place = new Place(id, namePlace, district, address, latitude, longitude);
            places.add(place);
        }
        queryCursor.close();
        return places;
    }


}
