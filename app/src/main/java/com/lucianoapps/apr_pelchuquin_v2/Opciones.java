package com.lucianoapps.apr_pelchuquin_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.lucianoapps.apr_pelchuquin_v2.db.DbLecturas;
import com.lucianoapps.apr_pelchuquin_v2.entidades.LecturaEnviada;
import com.lucianoapps.apr_pelchuquin_v2.peticionesApi.DocLecturasRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Opciones extends AppCompatActivity {


    private ArrayList<LecturaEnviada> lista = new ArrayList<LecturaEnviada>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);
    }

    public void GoTo_SubirDocLecturas(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Alerta!")
                .setMessage(R.string.txt_historiaDecLecturas)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                    crearArchivo();


                    }})
                .setNeutralButton("Cancelar", null)
                .show();

    }

    public void GoTo_EliminarTablas(View view) {

        new AlertDialog.Builder(this)
                .setTitle("Alerta!")
                .setMessage(R.string.txt_historiaBorrarData)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                        DbLecturas dbLecturas = new DbLecturas(Opciones.this);
                        boolean correcto = dbLecturas.eliminarTablas();
                        if(correcto) {
                            Toast.makeText(Opciones.this, "Tablas borraras con exito", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(Opciones.this, "Ocurrió un problema, favor contactarse con administradores.", Toast.LENGTH_SHORT).show();

                        }


                    }})
                .setNeutralButton("Cancelar", null)
                .show();


    }


    //Crea archivo d lcturas para luego ser enviado al servidor como una seegunda opcion de subida
    private void crearArchivo() {

        String nombreArchivo = "nuevo_archivo_"+obtenerFechaActual()+".txt";
        String cadenaLecturas = "";
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(nombreArchivo, Context.MODE_PRIVATE));

            DbLecturas dbLecturas = new DbLecturas(Opciones.this);
            lista = dbLecturas.lecturasDoc();
            for (LecturaEnviada p:lista){
                cadenaLecturas = cadenaLecturas + p.getCod_usuario() + ";;" + p.getLectura() + ";;" + p.getFecha_lectura()+ "||";
            }
            Log.d("cadenaLecturas:", cadenaLecturas);
            archivo.write(cadenaLecturas);
            archivo.close();
            Toast.makeText(this, "Archivo creado correctamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al crear el archivo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        convertirArchivoBase64(nombreArchivo);

    }

    private void convertirArchivoBase64(String nombreArchivo) {
        try {
            FileInputStream fis = openFileInput(nombreArchivo);
            Log.d("nombreArchivo:", nombreArchivo);
            // Leer el contenido del archivo en un byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            fis.close();

            // Codificar el byte array en Base64
            byte[] fileBytes = baos.toByteArray();
            String base64String = Base64.encodeToString(fileBytes, Base64.DEFAULT);
            Log.d("base64String:", base64String);

            deleteFile(nombreArchivo);

            //String imei=consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
            String imei="869806050235092";

            Response.Listener<String> respoListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("exito");
                    if(success)
                        Toast.makeText(this, "Documento enviado con exito", Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            DocLecturasRequest docLecturasRequest = new DocLecturasRequest(base64String, nombreArchivo, imei, respoListener);
            RequestQueue queue = Volley.newRequestQueue(Opciones.this);
            queue.add(docLecturasRequest);

        } catch (IOException e) {
            Toast.makeText(this, "Error al convertir el archivo a Base64: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String obtenerFechaActual(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        return fecha;
    }
}