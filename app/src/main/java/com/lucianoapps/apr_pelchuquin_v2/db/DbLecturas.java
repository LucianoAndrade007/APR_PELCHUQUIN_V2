package com.lucianoapps.apr_pelchuquin_v2.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.lucianoapps.apr_pelchuquin_v2.InfoGlobal;
import com.lucianoapps.apr_pelchuquin_v2.entidades.LecturaEnviada;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Usuarios;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Lectura;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import static android.os.Build.ID;

//import java.util.ArrayList;

public class DbLecturas extends DbHelper {

    Context context;

    public DbLecturas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    public long insertarLectura(String cod_usuario, Integer lectura, String foto, String comentario) {

        long id = 0;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String fecha = dateFormat.format(date);
        String codigo_usuario = "", valida_registro = "";

        try {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursorUsuario;
            cursorUsuario = db.rawQuery("SELECT id_usuario FROM " + TABLE_USUARIOS + " where id_medidor = '" + cod_usuario + "' LIMIT 1", null);
            if (cursorUsuario.moveToFirst()) {
                codigo_usuario = cursorUsuario.getString(0);
            }

        } catch (Exception ex) {
            ex.toString();
        }
        if(true){
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                Cursor cursorUsuario;
                cursorUsuario = db.rawQuery("SELECT EXISTS (SELECT cod_usuario FROM t_lecturas where cod_usuario = '" + codigo_usuario + "' LIMIT 1)", null);
                if (cursorUsuario.moveToFirst()) {
                    valida_registro = cursorUsuario.getString(0);
                }

            } catch (Exception ex) {
                ex.toString();
            }finally {
                db.close();
            }
        }


        if (valida_registro.equals("1")) {
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                db.execSQL("DELETE FROM t_lecturas WHERE cod_usuario ='" + codigo_usuario + "'");
            } catch (Exception ex) {
                ex.toString();
            } finally {
                db.close();
            }
        }

        if(true){
            DbHelper dbHelper = new DbHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put("cod_usuario", codigo_usuario);
                values.put("lectura", lectura);
                values.put("foto", foto);
                values.put("comentario", comentario);
                values.put("fecha_lectura", fecha);

                db.execSQL("UPDATE " + TABLE_USUARIOS + " SET lectura_pendiente=0  WHERE id_usuario='" + codigo_usuario + "' ");
                id = db.insert(TABLE_LECTURAS, null, values);
            } catch (Exception ex) {
                ex.toString();
            }
            finally {
                db.close();
            }
        }



        return id;
    }

    public long insertarUsuarios(String nombres, String apellidos, String direccion, String codigo_casa, String id_usuario, String codigo_medidor, Integer consumo_anterior,String lectura_pendiente) {

        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_USUARIOS + " WHERE id_usuario = "+ codigo_casa);
            ContentValues values = new ContentValues();
            values.put("nombres", nombres);
            values.put("apellidos", apellidos);
            values.put("direccion", direccion);
            values.put("id_medidor", codigo_casa);
            values.put("id_usuario", id_usuario);
            values.put("codigo_medidor", codigo_medidor);
            values.put("consumo_anterior", consumo_anterior);
            values.put("lectura_pendiente", lectura_pendiente);

            id = db.insert(TABLE_USUARIOS, null, values);
        } catch (Exception ex) {
            ex.toString();
        }finally {
            db.close();
        }

        return id;
    }

    public Usuarios verUsuario(String id_medidor) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Usuarios usuarios = null;
        Usuarios usuarios2 = null;
        Cursor cursorUsuarios;
        Cursor cursorlecturaSubida;

        cursorUsuarios = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS + " where id_medidor = '" + id_medidor + "' LIMIT 1", null);
        //cursorlecturaSubida = db.rawQuery("SELECT * FROM " + TABLE_LECTURAS_TOMADAS + " WHERE codigo_casa = '" + id_medidor  + "' LIMIT 1", null);

        if (cursorUsuarios.moveToFirst()) {
            usuarios = new Usuarios();
            usuarios.setNombres(cursorUsuarios.getString(1));
            usuarios.setApellidos(cursorUsuarios.getString(2));
            usuarios.setId_usuario(cursorUsuarios.getString(5));
            usuarios.setConsumo_anterior(cursorUsuarios.getString(7));
            usuarios.setLectura_tomada(cursorUsuarios.getInt(8));
            usuarios.setlectura_pendiente(cursorUsuarios.getString(9));

        }
        /*if (cursorlecturaSubida.moveToFirst() && cursorlecturaSubida != null){
            usuarios = new Usuarios();
            usuarios.setNombres(cursorlecturaSubida.getString(1));
            usuarios.setApellidos(cursorlecturaSubida.getString(2));
            usuarios.setId_usuario(cursorlecturaSubida.getString(5));
            usuarios.setConsumo_anterior(cursorlecturaSubida.getString(8));
            usuarios.setLectura_tomada(2);
        }*/


        cursorUsuarios.close();
        db.close();
        return usuarios;
    }


    public boolean eliminarContacto() {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_USUARIOS);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public boolean eliminarSuspendidosMedidor(String id_medidor) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM t_usuariosSuspender where codigo='"+id_medidor + "'");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }


    //motodo para ingresar un estado a la lectura del usuario que tendra 3 tipos diferentes
    // 0 : usuario sin lectura tomada
    // 1 : usuario con lectua almacenada en la app movil
    // 2 : usuario con lectura ya sincronizada en servidor
    public boolean lecturaIngresada(String id_medidor,int estado_lectura) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_USUARIOS + " SET lectura_tomada=" + estado_lectura + " WHERE id_medidor='" + id_medidor + "' ");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }

    public List<Integer> datosLista() {

        List<Integer> datos = new ArrayList<>();
        Cursor cursorUsuarios= null;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {


            cursorUsuarios = db.rawQuery("SELECT (SELECT COUNT(*) FROM " + TABLE_USUARIOS + " WHERE lectura_tomada=1) as lecturas_actuales,(SELECT COUNT(*) FROM " + TABLE_USUARIOS + " WHERE lectura_tomada=0 and  lectura_pendiente = 0 ) as lecturas_totales FROM " + TABLE_USUARIOS, null);
            if (cursorUsuarios.moveToFirst()) {
                datos.add(cursorUsuarios.getInt(0)) ;
                datos.add(cursorUsuarios.getInt(1)) ;

            }else{
                datos.add(0) ;
                datos.add(0) ;
            }


        } catch (Exception ex) {
            ex.toString();
        } finally {
            db.close();
        }
        return datos;
    }

    public ArrayList<Usuarios> mostrarUsuarios() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Usuarios> listaContactos = new ArrayList<>();
        Usuarios usuarios;
        Cursor cursorUsuarios;

        cursorUsuarios = db.rawQuery("SELECT * FROM t_usuarios where  lectura_pendiente = 0  ORDER BY nombres ", null);
        //linea de abajo se usara para pruebas, limit marca la cantidad de usaurios para probar
        //cursorUsuarios = db.rawQuery("SELECT * FROM t_usuarios ORDER BY nombres ASC LIMIT 50", null);

        if (cursorUsuarios.moveToFirst()) {
            do {
                usuarios = new Usuarios();
                usuarios.setNombres(cursorUsuarios.getString(1));
                usuarios.setApellidos(cursorUsuarios.getString(2));
                usuarios.setDireccion(cursorUsuarios.getString(3));
                usuarios.setId_medidor(cursorUsuarios.getString(4));
                usuarios.setCodigo_medidor(cursorUsuarios.getString(6));
                usuarios.setId_usuario(cursorUsuarios.getString(5));
                usuarios.setLectura_tomada(cursorUsuarios.getInt(8));
                usuarios.setId_medidor_nombre(cursorUsuarios.getString(4) + " - " + cursorUsuarios.getString(1) + " - " + cursorUsuarios.getString(2)+ " - " + cursorUsuarios.getString(3));
                listaContactos.add(usuarios);
            } while (cursorUsuarios.moveToNext());
        }

        cursorUsuarios.close();
        db.close();
        return listaContactos;
    }

    public Lectura verLectura(String id_medidor) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Lectura lectura = null;
        Cursor cursorlectura;
        Cursor cursorlecturaSubida;

        cursorlectura = db.rawQuery("SELECT * FROM " + TABLE_LECTURAS + " WHERE cod_usuario = '" + id_medidor  + "' LIMIT 1", null);

        if (cursorlectura.moveToFirst()) {
            lectura = new Lectura();
            lectura.setId(cursorlectura.getInt(0));
            lectura.setCod_usuario(cursorlectura.getString(1));
            lectura.setLectura(cursorlectura.getInt(2));
            lectura.setFoto(cursorlectura.getString(3));
            lectura.setComentario((cursorlectura.getString(4) == null)?"":cursorlectura.getString(4));

        }

        cursorlectura.close();
        db.close();
        return lectura;
    }


    public long insertarUsuariosHabilitados(Integer id_user, String nombres, String apellidos, String codigo, String direccion, Integer mesesImpagos, String fono) {

        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("id_user", id_user);
            values.put("nombres", nombres);
            values.put("apellidos", apellidos);
            values.put("codigo", codigo);
            values.put("direccion", direccion);
            values.put("mesesImpagos", mesesImpagos);
            values.put("fono", fono);

            id = db.insert("t_usuariosReponer", null, values);
        } catch (Exception ex) {
            ex.toString();
        }finally {
            db.close();
        }

        return id;
    }

    public long insertarUsuariosSuspendidos(Integer id_user, String nombres, String apellidos, String codigo, String direccion, Integer mesesImpagos, String fono) {

        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("id_user", id_user);
            values.put("nombres", nombres);
            values.put("apellidos", apellidos);
            values.put("codigo", codigo);
            values.put("direccion", direccion);
            values.put("mesesImpagos", mesesImpagos);
            values.put("fono", fono);

            id = db.insert("t_usuariosSuspender", null, values);
        } catch (Exception ex) {
            ex.toString();
        }finally {
            db.close();
        }

        return id;
    }

    public Usuarios verUsuariosSuspender(String id_medidor) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Usuarios usuarios = null;
        Cursor cursorUsuarios;

        cursorUsuarios = db.rawQuery("SELECT * FROM t_usuariosSuspender where codigo = '" + id_medidor  + "' LIMIT 1", null);

        if (cursorUsuarios.moveToFirst()) {
            usuarios = new Usuarios();
            usuarios.setNombres(cursorUsuarios.getString(1));
            usuarios.setApellidos(cursorUsuarios.getString(2));
            usuarios.setDireccion(cursorUsuarios.getString(4));
            usuarios.setMeses_Impagos(cursorUsuarios.getInt(5));
            usuarios.setFono(cursorUsuarios.getString(6));
            usuarios.setLectura_tomada(cursorUsuarios.getInt(7));
        }


        cursorUsuarios.close();
        db.close();
        return usuarios;
    }

    public ArrayList<Usuarios> mostrarUsuariosSuspender() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Usuarios> listaContactos = new ArrayList<>();
        Usuarios usuarios;
        Cursor cursorUsuarios;

        cursorUsuarios = db.rawQuery("SELECT * FROM t_usuariosSuspender ORDER BY nombres ASC", null);

        if (cursorUsuarios.moveToFirst()) {
            do {
                usuarios = new Usuarios();
                usuarios.setNombres(cursorUsuarios.getString(1));
                usuarios.setApellidos(cursorUsuarios.getString(2));
                usuarios.setDireccion(cursorUsuarios.getString(4));
                usuarios.setId_medidor(cursorUsuarios.getString(3));
                usuarios.setCodigo_medidor(cursorUsuarios.getString(0));
                usuarios.setLectura_tomada(cursorUsuarios.getInt(7));
                usuarios.setfecha_modificado(cursorUsuarios.getString(8));
                usuarios.setId_medidor_nombre(cursorUsuarios.getString(4) + " - " + cursorUsuarios.getString(1) + " - " + cursorUsuarios.getString(2)+ " - " + cursorUsuarios.getString(3));
                listaContactos.add(usuarios);
            } while (cursorUsuarios.moveToNext());
        }

        cursorUsuarios.close();
        db.close();
        return listaContactos;
    }

    public Usuarios verUsuarioReponer(String id_medidor) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Usuarios usuarios = null;
        Cursor cursorUsuarios;

        cursorUsuarios = db.rawQuery("SELECT * FROM t_usuariosReponer where codigo ='" + id_medidor  + "' ORDER BY nombres ASC", null);

        if (cursorUsuarios.moveToFirst()) {
            usuarios = new Usuarios();
            usuarios.setNombres(cursorUsuarios.getString(1));
            usuarios.setApellidos(cursorUsuarios.getString(2));
            usuarios.setDireccion(cursorUsuarios.getString(4));
            usuarios.setMeses_Impagos(cursorUsuarios.getInt(5));
            usuarios.setFono(cursorUsuarios.getString(6));
            usuarios.setLectura_tomada(cursorUsuarios.getInt(7));
        }
        cursorUsuarios.close();
        db.close();
        return usuarios;
    }

    public ArrayList<Usuarios> mostrarUsuariosReponer() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Usuarios> listaContactos = new ArrayList<>();
        Usuarios usuarios;
        Cursor cursorUsuarios;

        cursorUsuarios = db.rawQuery("SELECT * FROM t_usuariosReponer ORDER BY nombres ASC", null);

        if (cursorUsuarios.moveToFirst()) {
            do {
                usuarios = new Usuarios();
                usuarios.setNombres(cursorUsuarios.getString(1));
                usuarios.setApellidos(cursorUsuarios.getString(2));
                usuarios.setDireccion(cursorUsuarios.getString(4));
                usuarios.setId_medidor(cursorUsuarios.getString(3));
                usuarios.setCodigo_medidor(cursorUsuarios.getString(0));
                usuarios.setLectura_tomada(cursorUsuarios.getInt(7));
                //usuarios.setLectura_tomada(cursorUsuarios.getInt(8));
                usuarios.setId_medidor_nombre(cursorUsuarios.getString(4) + " - " + cursorUsuarios.getString(1) + " - " + cursorUsuarios.getString(2)+ " - " + cursorUsuarios.getString(3));
                listaContactos.add(usuarios);
            } while (cursorUsuarios.moveToNext());
        }

        cursorUsuarios.close();
        db.close();
        return listaContactos;
    }

    public ArrayList<Usuarios> usuarioPorReponer(String id_medidor) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Usuarios> listaContactos = new ArrayList<>();
        Usuarios usuarios;
        Cursor cursorUsuarios;

        cursorUsuarios = db.rawQuery("SELECT * FROM t_usuariosReponer where id_medidor='" + id_medidor  + "' ORDER BY nombres ASC", null);

        if (cursorUsuarios.moveToFirst()) {
            do {
                usuarios = new Usuarios();
                usuarios.setNombres(cursorUsuarios.getString(1));
                usuarios.setApellidos(cursorUsuarios.getString(2));
                usuarios.setDireccion(cursorUsuarios.getString(4));
                usuarios.setMeses_Impagos(cursorUsuarios.getInt(5));
                usuarios.setFono(cursorUsuarios.getString(6));
            } while (cursorUsuarios.moveToNext());
        }

        cursorUsuarios.close();
        db.close();
        return listaContactos;
    }

    public ArrayList<LecturaEnviada> listadoEnvioLecturas() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<LecturaEnviada> listadoLecturasParaEnviar = new ArrayList<>();
        LecturaEnviada lecturaEnviar;
        Cursor cursorLecturasEnviar;

        cursorLecturasEnviar = db.rawQuery("select tu.id, tl.cod_usuario, tl.lectura, tl.foto ,tl.comentario , tl.fecha_lectura ,tu.nombres, tu.apellidos ,tu.direccion, tu.id_medidor, tu.id_usuario, tu.codigo_medidor ,tu.consumo_anterior from t_usuarios tu inner join t_lecturas tl on tl.cod_usuario = tu.id_usuario", null);
        cursorLecturasEnviar.getCount();
        if (cursorLecturasEnviar.moveToFirst()) {
            do {
                lecturaEnviar = new LecturaEnviada();
                lecturaEnviar.setId(cursorLecturasEnviar.getInt(0));
                lecturaEnviar.setCod_usuario(cursorLecturasEnviar.getString(1));
                lecturaEnviar.setLectura(cursorLecturasEnviar.getInt(2));
                lecturaEnviar.setFoto(cursorLecturasEnviar.getString(3));
                lecturaEnviar.setComentario(cursorLecturasEnviar.getString(4));
                lecturaEnviar.setFecha_lectura(cursorLecturasEnviar.getString(5));
                lecturaEnviar.setNombres(cursorLecturasEnviar.getString(6));
                lecturaEnviar.setApellidos(cursorLecturasEnviar.getString(7));
                lecturaEnviar.setDireccion(cursorLecturasEnviar.getString(8));
                lecturaEnviar.setId_medidor(cursorLecturasEnviar.getString(9));
                lecturaEnviar.setId_usuario(cursorLecturasEnviar.getString(10));
                lecturaEnviar.setCodigo_medidor(cursorLecturasEnviar.getString(11));
                lecturaEnviar.setConsumo_anterior(cursorLecturasEnviar.getString(12));
                listadoLecturasParaEnviar.add(lecturaEnviar);
            } while (cursorLecturasEnviar.moveToNext());
        }

        cursorLecturasEnviar.close();
        db.close();
        return listadoLecturasParaEnviar;
    }

    public ArrayList<LecturaEnviada> listadoEnvioLecturasIdMedidor(String id_medidor) {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<LecturaEnviada> listadoLecturasParaEnviar = new ArrayList<>();
        LecturaEnviada lecturaEnviar;
        Cursor cursorLecturasEnviar;

        cursorLecturasEnviar = db.rawQuery("select tu.id, tl.cod_usuario, tl.lectura, tl.foto ,tl.comentario , tl.fecha_lectura ,tu.nombres, tu.apellidos ,tu.direccion, tu.id_medidor, tu.id_usuario, tu.codigo_medidor ,tu.consumo_anterior from t_usuarios tu inner join t_lecturas tl on tl.cod_usuario = tu.id_usuario WHERE tu.id_usuario ='" + id_medidor + "' ",null);
        cursorLecturasEnviar.getCount();
        if (cursorLecturasEnviar.moveToFirst()) {
            do {
                lecturaEnviar = new LecturaEnviada();
                lecturaEnviar.setId(cursorLecturasEnviar.getInt(0));
                lecturaEnviar.setCod_usuario(cursorLecturasEnviar.getString(1));
                lecturaEnviar.setLectura(cursorLecturasEnviar.getInt(2));
                lecturaEnviar.setFoto(cursorLecturasEnviar.getString(3));
                lecturaEnviar.setComentario(cursorLecturasEnviar.getString(4));
                lecturaEnviar.setFecha_lectura(cursorLecturasEnviar.getString(5));
                lecturaEnviar.setNombres(cursorLecturasEnviar.getString(6));
                lecturaEnviar.setApellidos(cursorLecturasEnviar.getString(7));
                lecturaEnviar.setDireccion(cursorLecturasEnviar.getString(8));
                lecturaEnviar.setId_medidor(cursorLecturasEnviar.getString(9));
                lecturaEnviar.setId_usuario(cursorLecturasEnviar.getString(10));
                lecturaEnviar.setCodigo_medidor(cursorLecturasEnviar.getString(11));
                lecturaEnviar.setConsumo_anterior(cursorLecturasEnviar.getString(12));
                listadoLecturasParaEnviar.add(lecturaEnviar);
            } while (cursorLecturasEnviar.moveToNext());
        }

        cursorLecturasEnviar.close();
        db.close();
        return listadoLecturasParaEnviar;
    }

    public long insertarCalendario(String nombre,String descargaListaUsuarios,String descargaListaSuspender,String descargaListaReponer, String fecha) {

        long id = 0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put("nombre", nombre);
            values.put("descargaListaUsuarios", descargaListaUsuarios);
            values.put("descargaListaSuspender", descargaListaSuspender);
            values.put("descargaListaReponer", descargaListaReponer);
            values.put("fechaCalendario", fecha);

            id = db.insert("t_calendario", null, values);
        } catch (Exception ex) {
            ex.toString();
        }finally {
            db.close();
        }

        return id;
    }

    public boolean suspensionTomada(String id_medidor,int realizado, String fechaActual) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE t_usuariosSuspender SET suspension_tomada=" + realizado + ", fecha_Suspension='" + fechaActual + "' WHERE codigo='" + id_medidor + "' ");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }

    public boolean reposicionTomada(String id_medidor,int realizado , String fechaActual) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE t_usuariosReponer SET reposicion_tomada =" + realizado + ", fecha_Reposicion='" + fechaActual + "' WHERE codigo='" + id_medidor + "' ");
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }
        return correcto;
    }


    public long lecturasPrueba(String cod_usuario){
        long id =0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {

            String a = InfoGlobal.IMG_PRUEBA_BASE64_P1 ;
            String b = a + InfoGlobal.IMG_PRUEBA_BASE64_P2 ;
            String c = b + InfoGlobal.IMG_PRUEBA_BASE64_P3;
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String fecha = dateFormat.format(date);

            ContentValues values = new ContentValues();

                values.put("cod_usuario", cod_usuario);
                values.put("lectura", 100);
                //values.put("foto", c);
                values.put("foto", "1");
                //values.put("comentario", cod_usuario + "112312312332"+".jpg" );
                values.put("comentario", "" );
                values.put("fecha_lectura",fecha );
                id = db.insert(TABLE_LECTURAS, null, values);


        } catch (Exception ex) {
            ex.toString();
        } finally {
            db.close();
        }

        return id;
    }

    public boolean eliminarLecturaId(String cod_usuario) {

        boolean correcto ;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_LECTURAS + " WHERE cod_usuario = "+ cod_usuario);
            //db.execSQL("DELETE FROM " + TABLE_USUARIOS + " WHERE id_usuario = "+ cod_usuario);
            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public int verificaLecturas() {
        int valida_registro=0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        try {

            Cursor cursorUsuario;
            cursorUsuario = db.rawQuery("SELECT EXISTS (SELECT * FROM t_lecturas )", null);
            if (cursorUsuario.moveToFirst()) {
                valida_registro = cursorUsuario.getInt(0);
            }

        } catch (Exception ex) {
            ex.toString();
        }finally {
            db.close();
        }

        return valida_registro;
    }

    //
    // ************************************************
    // Funcion que valida codigo de usuario y retorna un 1 en el caso que exista o 0 en caso contrario
    //************************************************
    public int validaUsuario(String id_medidor) {
        int valida_registro=0;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            Cursor cursorUsuario;
            cursorUsuario = db.rawQuery("SELECT EXISTS (SELECT * FROM t_usuarios WHERE id_medidor = "+ id_medidor +")", null);
            if (cursorUsuario.moveToFirst()) {
                valida_registro = cursorUsuario.getInt(0);
            }
            db.close();
        } catch (Exception ex) {
            ex.toString();
        }finally {
            db.close();
        }

        return valida_registro;
    }


    public boolean eliminarUsuarioId(String cod_usuario) {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if(validaUsuario(cod_usuario)==1){

            try {
                db.execSQL("DELETE FROM " + TABLE_USUARIOS + " WHERE id_usuario = "+ cod_usuario);
                correcto = true;
            } catch (Exception ex) {
                ex.toString();
                correcto = false;
            } finally {
                db.close();
            }
        }


        return correcto;
    }


    public ArrayList<Lectura> listarIdLecturas() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<Lectura> listadoLecturas = new ArrayList<>();
        Lectura lectura;
        Cursor cursorLecturasEnviar;

        cursorLecturasEnviar = db.rawQuery("SELECT b.id_medidor FROM t_lecturas a, t_usuarios b where a.cod_usuario = b.id_usuario", null);

        if (cursorLecturasEnviar.moveToFirst()) {
            do {
                lectura = new Lectura();
                lectura.setId(cursorLecturasEnviar.getInt(0));

                listadoLecturas.add(lectura);
            } while (cursorLecturasEnviar.moveToNext());
        }

        cursorLecturasEnviar.close();
        db.close();
        return listadoLecturas;
    }


    public long insertaLecturasTomadas(String user_id, int number_consumption){
        long id =0;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("UPDATE " + TABLE_USUARIOS + " SET lectura_pendiente=1, consumo_anterior='" + number_consumption + "' WHERE id_usuario='" + user_id + "' ");


        } catch (Exception ex) {
            ex.toString();
        } finally {
            db.close();
        }

        return id;
    }

    public ArrayList<LecturaEnviada> lecturasDoc() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<LecturaEnviada> listadoLecturasParaEnviar = new ArrayList<>();
        LecturaEnviada lecturaEnviar;
        Cursor cursorLecturasEnviar;

        cursorLecturasEnviar = db.rawQuery("select cod_usuario,lectura,fecha_lectura from t_lecturas", null);
        cursorLecturasEnviar.getCount();
        if (cursorLecturasEnviar.moveToFirst()) {
            do {
                lecturaEnviar = new LecturaEnviada();
                lecturaEnviar.setCod_usuario(cursorLecturasEnviar.getString(0));
                lecturaEnviar.setLectura(cursorLecturasEnviar.getInt(1));
                lecturaEnviar.setFecha_lectura(cursorLecturasEnviar.getString(2));
                listadoLecturasParaEnviar.add(lecturaEnviar);
            } while (cursorLecturasEnviar.moveToNext());
        }

        cursorLecturasEnviar.close();
        db.close();
        return listadoLecturasParaEnviar;
    }

    public boolean eliminarTablas() {

        boolean correcto = false;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("DELETE FROM " + TABLE_LECTURAS);
            db.execSQL("DELETE FROM " + TABLE_USUARIOS );
            db.execSQL("DELETE FROM t_usuariosSuspender");
            db.execSQL("DELETE FROM t_usuariosReponer");
            //db.execSQL("DELETE FROM t_calendario");
            db.execSQL("DELETE FROM " + TABLE_LECTURAS_TOMADAS);

            correcto = true;
        } catch (Exception ex) {
            ex.toString();
            correcto = false;
        } finally {
            db.close();
        }

        return correcto;
    }

    public String consultaFechaDescarga() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String fecha = "";

        try {
            Cursor cursor = db.rawQuery("SELECT fechaCalendario FROM t_calendario ORDER BY fechaCalendario DESC LIMIT 1", null);
            if (cursor.moveToFirst()) {
                fecha = cursor.getString(0);
            }
            cursor.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            db.close();
        }

        return fecha;
    }


    public Integer contadorLecturas() {

        int cantidad_lecturas = 0;
        Cursor cursorUsuarios= null;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            cursorUsuarios = db.rawQuery("select count(*) from t_usuarios tu inner join t_lecturas tl on tl.cod_usuario = tu.id_usuario", null);
            if (cursorUsuarios.moveToFirst()) {
                cantidad_lecturas=cursorUsuarios.getInt(0);

            }
        } catch (Exception ex) {
            ex.toString();
        } finally {
            db.close();
        }
        return cantidad_lecturas;
    }

    public ArrayList<LecturaEnviada> lecturaEnviar() {

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<LecturaEnviada> listadoLecturasParaEnviar = new ArrayList<>();
        LecturaEnviada lecturaEnviar;
        Cursor cursorLecturasEnviar;

        cursorLecturasEnviar = db.rawQuery("SELECT tu.id, tl.cod_usuario, tl.lectura, tl.foto, tl.comentario, tl.fecha_lectura, tu.nombres, tu.apellidos, tu.direccion, tu.id_medidor, tu.id_usuario, tu.codigo_medidor, tu.consumo_anterior FROM t_usuarios tu INNER JOIN t_lecturas tl ON tl.cod_usuario = tu.id_usuario ORDER BY tl.fecha_lectura DESC LIMIT 1", null);
        cursorLecturasEnviar.getCount();
        if (cursorLecturasEnviar.moveToFirst()) {
            do {
                lecturaEnviar = new LecturaEnviada();
                lecturaEnviar.setId(cursorLecturasEnviar.getInt(0));
                lecturaEnviar.setCod_usuario(cursorLecturasEnviar.getString(1));
                lecturaEnviar.setLectura(cursorLecturasEnviar.getInt(2));
                lecturaEnviar.setFoto(cursorLecturasEnviar.getString(3));
                lecturaEnviar.setComentario(cursorLecturasEnviar.getString(4));
                lecturaEnviar.setFecha_lectura(cursorLecturasEnviar.getString(5));
                lecturaEnviar.setNombres(cursorLecturasEnviar.getString(6));
                lecturaEnviar.setApellidos(cursorLecturasEnviar.getString(7));
                lecturaEnviar.setDireccion(cursorLecturasEnviar.getString(8));
                lecturaEnviar.setId_medidor(cursorLecturasEnviar.getString(9));
                lecturaEnviar.setId_usuario(cursorLecturasEnviar.getString(10));
                lecturaEnviar.setCodigo_medidor(cursorLecturasEnviar.getString(11));
                lecturaEnviar.setConsumo_anterior(cursorLecturasEnviar.getString(12));
                listadoLecturasParaEnviar.add(lecturaEnviar);
            } while (cursorLecturasEnviar.moveToNext());
        }

        cursorLecturasEnviar.close();
        db.close();
        return listadoLecturasParaEnviar;
    }

}
