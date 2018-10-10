package com.example.user.ipcis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.opencv.core.MatOfKeyPoint;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created by User on 8/3/2018.
 */

public class DatabaseHelperKeypoints extends SQLiteOpenHelper {
    private static final String DB_NAME = "blobDbbbbbb.db";
    private static final int DB_VERSION=1;
    private static final String TABLE_NAME = "blobbbbbbTable";
    private static  final String id_num="idNum";
    private static final String ROWS_FIELD_NAME = "rowsField";
    //  public static final int ROWS_FIELD_POSITION = 1;
    private static final String COLUMNS_FIELD_NAME = "columnsField";
    //  public static final int COLUMNS_FIELD_POSITION = 2;
    private static final String MATTYPE_FIELD_NAME = "mattypeField";
    //  public static final int MATTYPE_FIELD_POSITION = 3;
    private static final String BLOB_FIELD_NAME = "blobField";
    //    private static final int BLOB_FIELD_POSITION = 4;
    SQLiteDatabase db;

    private static final String TABLE_CREATE = "CREATE TABLE blobbbbbbTable (id integer PRIMARY KEY AUTOINCREMENT, " +
            "idNum integer NOT NULL , rowsField integer NOT NULL , columnsField integer NOT NULL , mattypeField integer NOT NULL , blobField blob NOT NULL );";


    public DatabaseHelperKeypoints(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db=db;
    }

    public long insertKeyPoints(int id,MatOfKeyPoint keyPoints)
    {
        db=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        float[] data = new float[(int)keyPoints.total() * keyPoints.channels()]; // make a spot to save the data
        keyPoints.get(0,0,data); // load the data;
        ByteBuffer buffer = ByteBuffer.allocate(data.length * 4);
        for (int i = 0; i < data.length; i++){
            buffer.putFloat(data[i]);
        }
        byte[] byteArray = buffer.array();
        int rows=keyPoints.rows();
        int columns=keyPoints.cols();
        int mattype=keyPoints.type();

        values.put(id_num,id);
        values.put(ROWS_FIELD_NAME,rows);
        values.put(COLUMNS_FIELD_NAME,columns);
        values.put(MATTYPE_FIELD_NAME,mattype);
        values.put(BLOB_FIELD_NAME,byteArray);


        long insert_chck= db.insert(TABLE_NAME , null , values );
        db.close();
        return insert_chck;



    }


    public MatOfKeyPoint getKeyPoints(int id)
    {
        db=this.getReadableDatabase();
        String query = "select idNum , rowsField , columnsField , mattypeField , blobField from " + TABLE_NAME ;
        Cursor cursor =db.rawQuery(query,null);
        int idDb;
        MatOfKeyPoint keyPoints = null;
        int rows;
        int columns;
        int mattype;
        if(cursor.moveToFirst())
        {
            do{

                idDb =cursor.getInt(0);


                if(idDb == id)
                {
                    rows = cursor.getInt(1);
                    columns = cursor.getInt(2);
                    mattype = cursor.getInt(3);
                    keyPoints = new MatOfKeyPoint();
                    keyPoints.create(rows, columns, mattype);
                    byte[] blob = cursor.getBlob(4);
                    ByteBuffer buffer = ByteBuffer.wrap(blob);
                    FloatBuffer floatBuffer = buffer.asFloatBuffer();
                    float[] floatArray = new float[floatBuffer.limit()];
                    floatBuffer.get(floatArray);
                    keyPoints.put(0, 0, floatArray);

                    break;
                }


            }while(cursor.moveToNext());


        }
        return  keyPoints;

    }






    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String query ="DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
}

