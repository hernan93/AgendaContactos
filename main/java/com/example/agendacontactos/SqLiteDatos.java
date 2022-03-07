package com.example.agendacontactos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

class SqLiteDatos extends SQLiteOpenHelper {

    private static final String Etiqueta = "Hernan" ;
    private static final String Nombre_DB = "Contactos.db";        //Nombre de base de datos
    private static final int Version_DB = 2;                    //Versión de base de datos

    public static final String DROP = "DROP TABLE IF EXISTS";
    public static final String Nombre_Tabla = "MisContactos";   //Nombre de la tabla

    //Campos de datos en columnas
    public static final String C_ID = "ID";
    public static final String C_IMAGE = "IMAGE";
    public static final String C_NOMBRE = "Nombre";
    public static final String C_APELLIDO = "Apellido";
    public static final String C_EMAIL = "EMAIL";
    public static final String C_DIRECCION = "Direccion";
    public static final String C_NUMERO_MOVIL = "Numero_Telefono";
    public static final String C_ADDED_TIMESTAMP = "ADDED_TIME_STAMP";
    public static final String C_UPDATED_TIMESTAMP = "UPDATED_TIME_STAMP";

    //Crear tabla
    public static final String CREAR_TABLA = "CREATE TABLE " + Nombre_Tabla + "("
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + C_IMAGE + " TEXT,"
            + C_NOMBRE + " TEXT,"
            + C_APELLIDO + " TEXT,"
            + C_EMAIL + " TEXT,"
            + C_DIRECCION + " TEXT,"
            + C_NUMERO_MOVIL + " TEXT,"
            + C_ADDED_TIMESTAMP + " TEXT,"
            + C_UPDATED_TIMESTAMP + " TEXT"
            + ")";

    public SqLiteDatos(@Nullable Context context) {
        super(context, Nombre_DB, null, Version_DB);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREAR_TABLA);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP + " " + Nombre_Tabla);
        onCreate(db);
    }


    public long InsertRecord (String image, String Nombre, String Apellido, String Email, String Direccion,
                          String Numero_movil, String addedTime, String updatedTime)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(C_IMAGE, image);
        values.put(C_NOMBRE, Nombre);
        values.put(C_APELLIDO, Apellido);
        values.put(C_EMAIL, Email);
        values.put(C_DIRECCION, Direccion);
        values.put(C_NUMERO_MOVIL, Numero_movil);
        values.put(C_ADDED_TIMESTAMP, addedTime);
        values.put(C_UPDATED_TIMESTAMP, updatedTime);

        long id = db.insert(Nombre_Tabla, null, values);

        if (id != -1){
            Log.e(Etiqueta, "InsertRecord: " + "Insertado");
        }
        db.close();
        return id;
    }

    //Obtener datos desde la base SQLite

    public ArrayList<Contacto> GetAllContacts(String orderBy){

        ArrayList<Contacto> contactArrayList = new ArrayList<>();
        //Query to select records
        String selectQuery = "SELECT * FROM " + Nombre_Tabla + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst())
            do {
                Contacto contact = new Contacto(

                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(C_ID)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_NOMBRE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_APELLIDO)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_DIRECCION)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_NUMERO_MOVIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_UPDATED_TIMESTAMP))
                );
                contactArrayList.add(contact);
            }while (cursor.moveToNext());

        //close the connection

        db.close();

        return contactArrayList;
    }

    //Buscar en la Base de datos por nombre
    public ArrayList<Contacto> SearchContacts(String query){

        ArrayList<Contacto> contactArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Nombre_Tabla + " WHERE " + C_NOMBRE + " LIKE '%" + query +"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        //Loop thought everything
        if (cursor.moveToFirst())
            do {
                Contacto contact = new Contacto(
                        ""+cursor.getInt(cursor.getColumnIndexOrThrow(C_ID)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_IMAGE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_NOMBRE)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_APELLIDO)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_EMAIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_DIRECCION)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_NUMERO_MOVIL)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_ADDED_TIMESTAMP)),
                        ""+cursor.getString(cursor.getColumnIndexOrThrow(C_UPDATED_TIMESTAMP))

                );
                contactArrayList.add(contact);
            }while (cursor.moveToNext());

        db.close(); //Cerrando base de datos
        return contactArrayList;
    }

    //Obtener el contenido de los datos guardados
    public int GetRecordsCount(){
        String countQuery = "SELECT * FROM " + Nombre_Tabla;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery,null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    //Update data
    public void UpdateRecord(String id, String image, String nombre, String apellido,
                             String email, String direccion, String telefonoMovil,
                             String addedTime, String updatedTime){

        //Get the writable database, because we want to add into it
        SQLiteDatabase db = this.getWritableDatabase();

        //Insert data
        ContentValues values = new ContentValues();

        values.put(C_IMAGE, image);
        values.put(C_NOMBRE, nombre);
        values.put(C_APELLIDO, apellido);
        values.put(C_EMAIL, email);
        values.put(C_DIRECCION, direccion);
        values.put(C_NUMERO_MOVIL, telefonoMovil);
        values.put(C_ADDED_TIMESTAMP, addedTime);
        values.put(C_UPDATED_TIMESTAMP, updatedTime);
        db.update(Nombre_Tabla, values, C_ID + " = ?", new String[]{id});
        db.close();
    }

    //Eliminar usando el Identificador
    public void DeleteData(String id){
        SQLiteDatabase db = getReadableDatabase();
        db.delete(Nombre_Tabla, C_ID +" = ?", new String[]{id});
        db.close();
    }
    //Eliminar toda la tabla, No recomendable

    public void DeleteAllData(){
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + Nombre_Tabla);
        db.close();
    }




}
