package dsic.online.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {
    public MyHelper(Context context) {
        super(context,"myfile.db",null,1);
    }
//, String name, SQLiteDatabase.CursorFactory factory, int version
    //, "myfile.db", null, 1
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tblContact ("
                + " name text not null PRIMARY KEY , "
                + " email text, "
                + " phone text); ");
/*
        ContentValues values = new ContentValues();
        values.put("name", "alex");
        values.put("email", "asdfasdf@adasd.mna");
        values.put("phone", "777889998");
        db.insert("tblContact", null, values);
        db.close();
*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
