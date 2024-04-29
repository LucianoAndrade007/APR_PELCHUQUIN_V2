package com.lucianoapps.apr_pelchuquin_v2.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;


public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 26;
    private static final String DATABASE_NOMBRE = "saap.db";
    public static final String TABLE_LECTURAS = "t_lecturas";
    public static final String TABLE_USUARIOS = "t_usuarios";
    public static final String TABLE_LECTURAS_TOMADAS = "t_lecturas_tomadas";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_LECTURAS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cod_usuario TEXT NOT NULL," +
                "lectura INTEGER NOT NULL," +
                "foto TEXT," +
                "comentario TEXT," +
                "fecha_lectura TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_USUARIOS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombres TEXT NOT NULL," +
                "apellidos TEXT ," +
                "direccion TEXT ," +
                "id_medidor TEXT NOT NULL," +
                "id_usuario TEXT NOT NULL," +
                "codigo_medidor TEXT NOT NULL," +
                "consumo_anterior INTEGER NOT NULL," +
                "lectura_tomada INTEGER DEFAULT 0," +
                "lectura_pendiente TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE t_usuariosSuspender(" +
                "id_user INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombres TEXT NOT NULL," +
                "apellidos TEXT ," +
                "codigo TEXT ," +
                "direccion TEXT NOT NULL," +
                "mesesImpagos INTEGER NOT NULL," +
                "fono TEXT NOT NULL," +
                "suspension_tomada INTEGER DEFAULT 0," +
                "fecha_Suspension TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE t_usuariosReponer(" +
                "id_user INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombres TEXT NOT NULL," +
                "apellidos TEXT ," +
                "codigo TEXT ," +
                "direccion TEXT NOT NULL," +
                "mesesImpagos INTEGER NOT NULL," +
                "fono TEXT NOT NULL," +
                "reposicion_tomada INTEGER DEFAULT 0," +
                "fecha_Reposicion TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE t_calendario(" +
                "id_calendario INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT ," +
                "descargaListaUsuarios TEXT ," +
                "descargaListaSuspender TEXT ," +
                "descargaListaReponer TEXT ,"+
                "fechaCalendario TEXT)");


        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_LECTURAS_TOMADAS + "(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "firstname TEXT NOT NULL," +
                "surnames TEXT ," +
                "address TEXT ," +
                "codigo_casa TEXT ," +
                "user_id TEXT ," +
                "code TEXT ," +
                "fecha_lectura TEXT ," +
                "number_consumption INTEGER )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURAS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS t_usuariosSuspender");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS t_usuariosReponer");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS t_calendario");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_LECTURAS_TOMADAS);
        onCreate(sqLiteDatabase);

    }
}
