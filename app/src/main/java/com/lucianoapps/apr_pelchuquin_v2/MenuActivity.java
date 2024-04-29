package com.lucianoapps.apr_pelchuquin_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.lucianoapps.apr_pelchuquin_v2.db.DbLecturas;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Lectura;
import com.lucianoapps.apr_pelchuquin_v2.entidades.LecturaEnviada;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Usuarios;
import com.lucianoapps.apr_pelchuquin_v2.peticionesApi.LecturasRequest;
import com.lucianoapps.apr_pelchuquin_v2.peticionesApi.ReposicionesRequest;
import com.lucianoapps.apr_pelchuquin_v2.peticionesApi.SuspendidosRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {

    private TextView lv_cantidadLecturas,lv_ultimaDescarga;
    private int ContadorPantalla = 0;
    private RequestQueue mQueue;
    private ProgressBar progressBar;
    private ArrayList<LecturaEnviada> lista = new ArrayList<LecturaEnviada>();
    private ArrayList<LecturaEnviada> listaAux = new ArrayList<LecturaEnviada>();
    private ArrayList<Usuarios> listaServicios = new ArrayList<Usuarios>();

    Button btnLecturas,btnSuspensiones,btnDescargar,btnSincronizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        lv_cantidadLecturas = findViewById(R.id.lv_cantidadLecturas);
        lv_ultimaDescarga = findViewById(R.id.lv_ultimaDescarga);
        progressBar = findViewById(R.id.progressBar);
        btnLecturas = findViewById(R.id.imageButton6);
        btnSuspensiones = findViewById(R.id.imageButton64);
        btnDescargar = findViewById(R.id.btn_Descargar);
        btnSincronizar = findViewById(R.id.btn_Up);

        if(!validacionListaUsuarios()){}
        DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        lv_ultimaDescarga.setText(dbLecturas.consultaFechaDescarga().replace("_", " "));

    }

    //Función que llamara a la pantalla de Estados
    public void GoToEstados(View view) {

        if(!validacionListaUsuarios()){
            Intent goToEstados = new Intent(this, LecturasActivity.class);
            startActivity(goToEstados);
        }
    }

    //Función que llamara a la pantalla de información, esta se activara luego de hacer mas de 9 clics
    public void GoToPantallaInformacion(View view){

        ContadorPantalla ++;
        if (ContadorPantalla > 9){
            Toast.makeText(this, "Pantalla Información", Toast.LENGTH_LONG).show();
            ContadorPantalla = 0;
            /*Intent goToReporte = new Intent(this, PantallaInformacion.class);
            startActivity(goToReporte);*/
        }
    }

    public void GoToCuts(View view) {

        if(!validacionListaUsuarios()){
            Intent goToCuts = new Intent(this, Suspensiones.class);
            startActivity(goToCuts);
        }

    }

    public void GoToVerLecturas(View view) {

        if(!validacionListaUsuarios()){
            Intent goToCuts = new Intent(this, VerLecturas.class);
            startActivity(goToCuts);
        }
    }

    private void cantidadLecturas(){
        final DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        int lecturasTomadas = dbLecturas.datosLista().get(0);
        int lecturasTotales = dbLecturas.datosLista().get(1);

        lv_cantidadLecturas.setText("Ver lecturas: "+ lecturasTomadas +"/"+ lecturasTotales);
    }

    public boolean validacionListaUsuarios() {
        final DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        int lecturasTomadas = dbLecturas.datosLista().get(0);
        int lecturasTotales = dbLecturas.datosLista().get(1);
        boolean correcto = false;

        lv_cantidadLecturas.setText("Ver lecturas: "+ lecturasTomadas +"/"+ lecturasTotales);
        if(lecturasTomadas == 0 && lecturasTotales==0){
            new AlertDialog.Builder(this)
                    .setTitle("Alerta!")
                    .setMessage("No se encuentra lista de usuarios.")
                    .setPositiveButton("Descargar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever...
                            final DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
                            dbLecturas.eliminarContacto();
                            ExtraerUsuariosApi();


                        }})
                    //.setNeutralButton("Cancelar", null)
                    .show();
            correcto = true;
        }else{
            correcto = false;
        }
        return correcto;
    }

    //llamada a api para traer lista de usuarios de la APISAAP
    private void ExtraerUsuariosApi() {
        progressBar.setVisibility(View.VISIBLE);
        desabilitarBotones(false);
        mQueue = Volley.newRequestQueue(MenuActivity.this);
        String url1 = InfoGlobal.URL_LECTURAS_ESTADO;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //Log.d("respuesta response:", response.getString("users"));


                            if (response.getString("users").equals("sin_datos")) {
                                Toast.makeText(MenuActivity.this, "Sin datos", Toast.LENGTH_LONG).show();
                            }else {
                                JSONArray jsonArray = response.getJSONArray("users");
                                //Log.d("respuesta jsonArray:", jsonArray.getString(0));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject Usuario = jsonArray.getJSONObject(i);
                                    String name = Usuario.getString("firstname");
                                    String surname = Usuario.getString("surnames");
                                    String address = Usuario.getString("address");
                                    String codigo_casa = Usuario.getString("codigo_casa");
                                    String id_user = Usuario.getString("user_id");
                                    String code = Usuario.getString("code");
                                    String number_consumption = Usuario.getString("number_consumption");
                                    String lectura_pendiente = Usuario.getString("lectura_pendiente");
                                    DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
                                    dbLecturas.insertarUsuarios(name, surname, address, codigo_casa, id_user, code, Integer.parseInt(number_consumption),lectura_pendiente);
                                }
                            }

                            Toast.makeText(MenuActivity.this, "Datos escritos en memoria", Toast.LENGTH_LONG).show();
                            recreate();



                            progressBar.setVisibility(View.INVISIBLE);
                            desabilitarBotones(true);
                            final DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
                            ArrayList<Lectura> listaLecturas = new ArrayList<Lectura>();
                            listaLecturas = dbLecturas.listarIdLecturas();
                            for (Lectura p:listaLecturas){
                                dbLecturas.lecturaIngresada(p.getId()+"",1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressBar.setVisibility(View.VISIBLE);
                            desabilitarBotones(true);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        //Se agrega codigo para indicar que debe tener mas tiempo de espera para descagar lista de usuarios
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
        cantidadLecturas();
    }

    //Descarga de lista de Habilitados
    private void DescargarHabilitados() {
        mQueue = Volley.newRequestQueue(MenuActivity.this);
        String url1 = InfoGlobal.URL_USUARIOS_POR_REPONER;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject estados = jsonArray.getJSONObject(i);
                                String id = estados.getString("id_user");
                                String firstname = estados.getString("firstname");
                                String surnames = estados.getString("surnames");
                                String code = estados.getString("codigo_casa");
                                String address = estados.getString("address");
                                String MesesImpagos = estados.getString("MesesImpagos");
                                String mobile_no = estados.getString("mobile_no");

                                DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
                                dbLecturas.insertarUsuariosHabilitados(Integer.parseInt(id),firstname,surnames,code,address,Integer.parseInt(MesesImpagos),mobile_no);

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    //Descarga de lista de Habilitados
    private void DescargarSuspendidos() {
        mQueue = Volley.newRequestQueue(MenuActivity.this);
        String url1 = InfoGlobal.URL_USUARIOS_POR_SUSPENDER;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("users");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject estados = jsonArray.getJSONObject(i);
                                String id = estados.getString("id_user");
                                String firstname = estados.getString("firstname");
                                String surnames = estados.getString("surnames");
                                String code = estados.getString("codigo_casa");
                                String address = estados.getString("address");
                                String MesesImpagos = estados.getString("MesesImpagos");
                                String mobile_no = estados.getString("mobile_no");

                                DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
                                dbLecturas.insertarUsuariosSuspendidos(Integer.parseInt(id),firstname,surnames,code,address,Integer.parseInt(MesesImpagos),mobile_no);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    public void onClick_Descargar(View view){
        final DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        if(dbLecturas.verificaLecturas()==1){
            new AlertDialog.Builder(this)
                    .setTitle("Alerta!")
                    .setMessage("Se reecomienda no tener lecturas pendientes para subir, ¿Desea continuar?")
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Whatever...
                            progressBar.setVisibility(View.VISIBLE);

                            dbLecturas.eliminarContacto();
                            ExtraerUsuariosApi();

                        }})
                    .setNeutralButton("Cancelar", null)
                    .show();
        }else {
            progressBar.setVisibility(View.VISIBLE);

            dbLecturas.eliminarContacto();
            ExtraerUsuariosApi();
        }
        guardaFecha("ExtraerUsuariosApi","1","0","0",obtenerFechaActual());


    }

    public void onClick_Sincronizar(View view){
        final DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        //btnUp.setClickable(true);


        new AlertDialog.Builder(this)
                .setTitle("Alerta!")
                .setMessage("La sincronizacion debe realizarze con WIFI para no prentar lentitud en la transferencia de datos. ¿Desea continuar?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Whatever...
                        //DescargarCalendario();
                        //DescargarHabilitados();
                        //DescargarSuspendidos();
                        //ExtraerUsuariosApi();
                        //envio_lecturas();
                        envio_lecturas_nuevo();
                        //envio_Repuestos();
                        //envio_suspendidos();
                    }})
                .setNeutralButton("Cancelar", null)
                .show();

    }

    public void onClick_Opciones(View view){
        Intent goToCuts = new Intent(this, Opciones.class);
        startActivity(goToCuts);

    }


    //Envio de datos a la aplicacion en internet
    private void envio_lecturas(){
        desabilitarBotones(false);
        DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        lista = dbLecturas.listadoEnvioLecturas();
        for (LecturaEnviada p:lista){
            Response.Listener<String> respoListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    Log.d("respuesta api:", jsonResponse.getString("id_medidor"));
                    boolean success = jsonResponse.getBoolean("exito");
                    if (success) {
                        String cadena = jsonResponse.getString("id_medidor");
                        int lecturasTomadas = dbLecturas.datosLista().get(0);
                        int lecturasTotales = dbLecturas.datosLista().get(1);
                        listaAux = dbLecturas.listadoEnvioLecturasIdMedidor(cadena);
                        Log.d("listaAux:"+listaAux.get(0).getId_usuario(),listaAux.get(0).getLectura()+"");
                        almacenaLecturaEnviada(listaAux.get(0).getId_usuario(),listaAux.get(0).getLectura());
                        // firstname,surnames,address,codigo_casa,user_id,code,number_consumption
                        lv_cantidadLecturas.setText("Ver lecturas: "+ lecturasTomadas +"/"+ lecturasTotales);
                        dbLecturas.eliminarLecturaId(cadena);
                        if(dbLecturas.verificaLecturas() == 0){
                            progressBar.setVisibility(View.INVISIBLE);
                            desabilitarBotones(true);
                        }
                    }
                    else {
                        Toast.makeText(MenuActivity.this, "Fallo al enviar registros", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            //String imei=consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
            String imei="869806050235092";
            SystemClock.sleep(5000);
            LecturasRequest lecturaRequest = new LecturasRequest(p.getCod_usuario(),Integer.toString(p.getLectura()) , p.getFecha_lectura(), p.getComentario(), "Enviado desde PDA", p.getCod_usuario(), p.getId_usuario(), p.getFoto(),imei, respoListener);
            RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
            queue.add(lecturaRequest);
        }
        if(lista.size() < 1){
            //dbLecturas.eliminarContacto();
            //ExtraerUsuariosApi();
            progressBar.setVisibility(View.INVISIBLE);
            desabilitarBotones(true);
        }
    }

    // Con este método mostramos en un Toast con un mensaje que el usuario ha concedido los permisos a la aplicación
    private String consultarPermiso(String permission, Integer requestCode) {
        String imei ="";
        if (ContextCompat.checkSelfPermission(MenuActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuActivity.this, permission)) {

                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = obtenerIMEI();
            //Toast.makeText(this,permission + " El permiso a la aplicación esta concedido.", Toast.LENGTH_SHORT).show();
        }
        return  imei;
    }

    private String obtenerIMEI() {
        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Hacemos la validación de métodos, ya que el método getDeviceId() ya no se admite para android Oreo en adelante, debemos usar el método getImei()
              return telephonyManager.getImei();
        }
        else {
            return telephonyManager.getDeviceId();
        }

    }

    //Descarga de Calendario
    private void DescargarCalendario() {
        mQueue = Volley.newRequestQueue(MenuActivity.this);
        String url1 = InfoGlobal.URL_CALENDARIO;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("calendario");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject estados = jsonArray.getJSONObject(i);
                                String nombre = estados.getString("Nombre");
                                String fecha = estados.getString("Fecha");

                                DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
                                //dbLecturas.insertarCalendario(nombre,fecha);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event ) {
        if(keyCode == event.KEYCODE_BACK) {
            Intent goToCuts = new Intent(this, LoginActivity.class);
            startActivity(goToCuts);
        }
        return super.onKeyDown(keyCode, event);
    }


    //Envio de datos a la aplicacion en internet
    private void envio_suspendidos(){
        DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        listaServicios = dbLecturas.mostrarUsuariosSuspender();
        for (Usuarios p:listaServicios){
            Response.Listener<String> respoListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("exito");
                    String user_id = jsonResponse.getString("user_id");
                    if (success) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        dbLecturas.eliminarSuspendidosMedidor(user_id);


                    } else {
                        Toast.makeText(MenuActivity.this, "Fallo al enviar registros", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            if(p.getLectura_tomada()==1){
                //String imei=consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
                SuspendidosRequest suspendidosRequest = new SuspendidosRequest(p.getId_medidor(),p.getfecha_modificado(), respoListener);
                RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
                queue.add(suspendidosRequest);
            }

        }
    }



    //Envio de datos a la aplicacion en internet
    private void envio_Repuestos(){
        DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        listaServicios = dbLecturas.mostrarUsuariosReponer();
        for (Usuarios p:listaServicios){
            Response.Listener<String> respoListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("exito");
                    String user_id = jsonResponse.getString("user_id");
                    if (success) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

                        //registerDelete(id, Boolean.parseBoolean(status.toString()), "BitacoraControlHabilitados.txt");
                        Toast.makeText(MenuActivity.this, "Datos enviados :" +user_id , Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MenuActivity.this, "Fallo al enviar registros", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            if(p.getLectura_tomada()==1) {
                //String imei = consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
                ReposicionesRequest reposicionesRequest = new ReposicionesRequest(p.getId_medidor(), p.getfecha_modificado(), respoListener);
                RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
                queue.add(reposicionesRequest);
            }
        }
    }

    private void desabilitarBotones(boolean valida){
        if(valida){
            lv_cantidadLecturas.setClickable(true);
            btnLecturas.setClickable(true);
            btnSuspensiones.setClickable(true);
            btnDescargar.setClickable(true);
            btnSincronizar.setClickable(true);
        }
        else{
            lv_cantidadLecturas.setClickable(false);
            btnLecturas.setClickable(false);
            btnSuspensiones.setClickable(false);
            btnDescargar.setClickable(false);
            btnSincronizar.setClickable(false);
        }
    }

        private void almacenaLecturaEnviada(String user_id, int number_consumption){
            DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
            dbLecturas.insertaLecturasTomadas( user_id, number_consumption);
        }

        private String obtenerFechaActual (){

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            String fecha = dateFormat.format(date);
            return fecha;
        }


        private void saveLectuasDoc(String nombreArchivo){
            String archivos[] = fileList();
            if (ArchivoExiste(archivos, nombreArchivo)) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(nombreArchivo));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();

                    while (linea != null) {
                        //bitacoraCompleta = bitacoraCompleta + linea;
                        //mylist.add(linea);
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();

                } catch (IOException e) {
                    Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
                }
            }
        }
    private boolean ArchivoExiste(String archivos[], String NombreArchivo) {
        for (int i = 0; i < archivos.length; i++)
            if (NombreArchivo.equals(archivos[i]))
                return true;
        return false;
    }

    private void guardaFecha(String nombre,String lista1,String lista2,String lista3, String fecha){
        DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        dbLecturas.insertarCalendario(nombre,lista1,lista2,lista3,fecha);
    }

    /*
    Nuevo Envio de datos a la aplicacion en internet
    El envio de lecturas se hara tomando un registro a la vez y enviado a la API
    */
    private void envio_lecturas_nuevo(){
        DbLecturas dbLecturas = new DbLecturas(MenuActivity.this);
        lista = dbLecturas.lecturaEnviar();

        for (LecturaEnviada p:lista){
            progressBar.setVisibility(View.VISIBLE);
            desabilitarBotones(false);
            Response.Listener<String> respoListener = response -> {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    //Log.d("respuesta api:", jsonResponse.getString("id_medidor"));
                    boolean success = jsonResponse.getBoolean("exito");
                    if (success) {
                        String cadena = jsonResponse.getString("id_medidor");
                        int lecturasTomadas = dbLecturas.datosLista().get(0);
                        int lecturasTotales = dbLecturas.datosLista().get(1);
                        listaAux = dbLecturas.listadoEnvioLecturasIdMedidor(cadena);
                        //Log.d("listaAux:"+listaAux.get(0).getId_usuario(),listaAux.get(0).getLectura()+"");
                        almacenaLecturaEnviada(listaAux.get(0).getId_usuario(),listaAux.get(0).getLectura());
                        // firstname,surnames,address,codigo_casa,user_id,code,number_consumption
                        lv_cantidadLecturas.setText("Ver lecturas: "+ lecturasTomadas +"/"+ lecturasTotales);
                        dbLecturas.eliminarLecturaId(cadena);
                        Log.d("contadorLecturas():", dbLecturas.contadorLecturas()+"");
                        if(dbLecturas.contadorLecturas() > 0){
                            envio_lecturas_nuevo();
                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            desabilitarBotones(true);
                        }

                        /*if(dbLecturas.verificaLecturas() == 0){
                            progressBar.setVisibility(View.INVISIBLE);
                            desabilitarBotones(true);
                        }*/
                    }
                    else {
                        Toast.makeText(MenuActivity.this, "Fallo al enviar registros", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            };
            //String imei=consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
            String imei="869806050235092";
            SystemClock.sleep(5000);
            LecturasRequest lecturaRequest = new LecturasRequest(p.getCod_usuario(),Integer.toString(p.getLectura()) , p.getFecha_lectura(), p.getComentario(), "Enviado desde PDA", p.getCod_usuario(), p.getId_usuario(), p.getFoto(),imei, respoListener);
            RequestQueue queue = Volley.newRequestQueue(MenuActivity.this);
            queue.add(lecturaRequest);
        }


    }

}