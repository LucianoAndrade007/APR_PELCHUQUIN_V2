package com.lucianoapps.apr_pelchuquin_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.lucianoapps.apr_pelchuquin_v2.db.DbLecturas;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Lectura;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Usuarios;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class LecturasActivity extends AppCompatActivity implements View.OnFocusChangeListener{

    private EditText txtMedidor,txtLectura;
    private TextView txtNombre,txtConsumoAnterior,txtApellidos;
    private ImageButton ibtnCheckFoto;
    private Button btnGuardar;
    Usuarios usuario;
    Lectura lectura;
    ///variables para toma de fotos del estado
    private static final int PICTURE_RESULT = 122;
    private ContentValues values;
    private Uri imageUri;
    private Bitmap thumbnail;
    String foto;
    String imageurl;
    private Boolean banderaIntent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturas);
        txtMedidor = findViewById(R.id.txt_medidor);
        txtNombre = findViewById(R.id.txt_nombre);
        txtApellidos = findViewById(R.id.txt_apellidos);
        txtLectura = findViewById(R.id.txt_lectura);
        ibtnCheckFoto = findViewById(R.id.ibtn_check_foto);
        txtConsumoAnterior = findViewById(R.id.txt_numberConsumption);
        btnGuardar = findViewById(R.id.imageButton6);
        txtMedidor.setOnFocusChangeListener(this);
        //ibtnCheckFoto.setImageResource(R.drawable.ic_baseline_check);

        txtNombre.setVisibility(View.INVISIBLE);
        txtApellidos.setVisibility(View.INVISIBLE);
        txtConsumoAnterior.setVisibility(View.INVISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Verifica permisos para Android 6.0+
            if (!checkExternalStoragePermission()) {
                return;
            }
        }
        String id = "";
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                id = null;
            } else {
                id = extras.getString("ID");
            }
        } else {
            id =  savedInstanceState.getString("ID");
        }
        if(id!=null){
            banderaIntent = true;
            final DbLecturas dbLecturas = new DbLecturas(LecturasActivity.this);

            usuario = dbLecturas.verUsuario(id);
            lectura = dbLecturas.verLectura(usuario.getId_usuario());
            txtMedidor.setText(id);
            if(usuario != null){
                txtNombre.setText(usuario.getNombres());
                txtApellidos.setText(usuario.getApellidos());
                txtConsumoAnterior.setText("Lectura Anterior: "+usuario.getConsumo_anterior());
                //Toast.makeText(LecturasActivity.this, " "+"Lectura Anterior: "+usuario.getConsumo_anterior(), Toast.LENGTH_LONG).show();
                txtNombre.setVisibility(View.VISIBLE);
                txtNombre.setTextColor(Color.BLACK);
                txtApellidos.setVisibility(View.VISIBLE);
                txtApellidos.setTextColor(Color.BLACK);
                txtConsumoAnterior.setVisibility(View.VISIBLE);
                if(lectura != null){
                    txtLectura.setText( lectura.getLectura()+"");
                    if(lectura.getFoto() != null){
                        ibtnCheckFoto.setImageResource(R.drawable.ic_baseline_check);
                    }else{
                        ibtnCheckFoto.setImageResource(R.drawable.ic_launcher_camara);
                    }

                }
            }
        }


        txtLectura.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {
                showSoftKeyboard();
            }
        });
    }

    public void showSoftKeyboard() {
        View view = this.getCurrentFocus();
        if(view != null){
            view.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtLectura.getWindowToken(), 0);
        }

    }


    //funcion que se activa cuando el lector de codigos del medidor pierde el focus y busca los datos del usuario
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        String id_medidor = txtMedidor.getText().toString();
        int estado_lectura;
        String estado_subida;
        btnGuardar.setClickable(true);
        if(!id_medidor.isEmpty()){
            if (!hasFocus){
                final DbLecturas dbLecturas = new DbLecturas(LecturasActivity.this);


                    usuario = dbLecturas.verUsuario(id_medidor);
                    //consulta si medidor ya tiene lectura tomada
                    if(usuario != null) {
                        lectura = dbLecturas.verLectura(usuario.getId_usuario());
                        estado_lectura = usuario.getLectura_tomada();
                        estado_subida = usuario.getlectura_pendiente();
                        if (estado_lectura == 1) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Alerta!")
                                    .setMessage("El usuario ya tiene la lectura tomada. ¿Desea volver a ingresar?")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Whatever...
                                                txtNombre.setText(usuario.getNombres());
                                                txtApellidos.setText(usuario.getApellidos());
                                                txtConsumoAnterior.setText("Lectura Anterior: " + usuario.getConsumo_anterior());
                                                txtNombre.setVisibility(View.VISIBLE);
                                                txtNombre.setTextColor(Color.BLACK);
                                                txtApellidos.setVisibility(View.VISIBLE);
                                                txtApellidos.setTextColor(Color.BLACK);
                                                txtConsumoAnterior.setVisibility(View.VISIBLE);
                                                if (lectura != null) {
                                                    txtLectura.setText(lectura.getLectura() + "");
                                                    if (!lectura.getFoto().equals("1")) {
                                                        ibtnCheckFoto.setImageResource(R.drawable.ic_baseline_check);
                                                    } else {
                                                        ibtnCheckFoto.setImageResource(R.drawable.ic_launcher_camara);
                                                    }
                                                }

                                        }
                                    })

                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            limpiar();
                                        }
                                    })
                                    .show();
                        } else if (estado_subida.equals("1") ){
                            new AlertDialog.Builder(this)
                                    .setTitle("Alerta!")
                                    .setMessage("El usuario ya tiene su lectura sincronizada. ¿Desea volver a ingresar?")
                                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Whatever...
                                            txtNombre.setText(usuario.getNombres());
                                            txtApellidos.setText(usuario.getApellidos());
                                            txtConsumoAnterior.setText("Lectura Anterior: " + usuario.getConsumo_anterior());
                                            txtNombre.setVisibility(View.VISIBLE);
                                            txtNombre.setTextColor(Color.BLACK);
                                            txtApellidos.setVisibility(View.VISIBLE);
                                            txtApellidos.setTextColor(Color.BLACK);
                                            txtConsumoAnterior.setVisibility(View.VISIBLE);
                                            ibtnCheckFoto.setImageResource(R.drawable.ic_launcher_camara);

                                        }
                                    })

                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            limpiar();
                                        }
                                    })
                                    .show();

                        } else {
                            //consulta si medidor ya tiene lectura tomada
                                txtNombre.setText(usuario.getNombres());
                                txtApellidos.setText(usuario.getApellidos());
                                txtConsumoAnterior.setText("Lectura Anterior: " + usuario.getConsumo_anterior());
                                txtNombre.setVisibility(View.VISIBLE);
                                txtNombre.setTextColor(Color.BLACK);
                                txtApellidos.setVisibility(View.VISIBLE);
                                txtApellidos.setTextColor(Color.BLACK);
                                txtConsumoAnterior.setVisibility(View.VISIBLE);

                        }
                    }else {
                        txtNombre.setVisibility(View.VISIBLE);
                        txtNombre.setText("USUARIO NO ENCONTRADO");
                        txtNombre.setTextColor(Color.RED);
                        txtApellidos.setVisibility(View.VISIBLE);
                        txtApellidos.setText("");
                        txtApellidos.setTextColor(Color.RED);
                        txtConsumoAnterior.setVisibility(View.INVISIBLE);
                        btnGuardar.setClickable(false);
                    }
            }
        }else{
            Toast.makeText(LecturasActivity.this, "Ingrese un codigo de medidor", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event ) {
        if(event.getAction()==1){

        }
        boolean bandera = false;
        if(keyCode==287 ||keyCode==285 || keyCode==286){

            Toast.makeText(LecturasActivity.this, "Tecla presionada", Toast.LENGTH_LONG).show();
            bandera=true;
        }
        return bandera;
    }

    public void onClick_Guardar(View v) {
        if(!txtNombre.getText().toString().equals("") && !txtLectura.getText().toString().equals("")) {


            int lecturaAnterior, nuevaLectura;
            lecturaAnterior =  Integer.parseInt(txtConsumoAnterior.getText().toString().replace("Lectura Anterior: ",""));
            nuevaLectura =  Integer.parseInt(txtLectura.getText().toString());

            final DbLecturas dbLecturas = new DbLecturas(LecturasActivity.this);

            if (lecturaAnterior > nuevaLectura){
                new AlertDialog.Builder(this)
                        .setTitle("Alerta!")
                        .setMessage("La nueva lectura es menor a la anterior. ¿Desea continuar?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                                ingresoLectura();
                            }})
                        .setNeutralButton("Cancelar", null)
                        .show();
            }else if(nuevaLectura-lecturaAnterior>99){
                new AlertDialog.Builder(this)
                        .setTitle("Alerta!")
                        .setMessage("La nueva lectura es mayor en 100m³. ¿Desea continuar?")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Whatever...
                                ingresoLectura();
                            }})
                        .setNeutralButton("Cancelar", null)
                        .show();
            }else{
                ingresoLectura();
            }



        } else {
            Toast.makeText(LecturasActivity.this, "DEBE LLENAR LOS CAMPOS OBLIGATORIOS", Toast.LENGTH_LONG).show();
        }

    }


    private void limpiar() {
        txtNombre.setText("");
        txtApellidos.setText("");
        txtConsumoAnterior.setText("");
        txtMedidor.setText("");
        txtLectura.setText("");
        foto = null;
        ibtnCheckFoto.setImageResource(R.drawable.ic_launcher_camara);
        txtMedidor.requestFocus();
    }

    public void onClick_tomaFoto(View v) {

        if (txtMedidor.getText().length() > 0){
            values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "MyPicture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Photo taken on " + System.currentTimeMillis());
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, PICTURE_RESULT);
        }
        else{
            Toast.makeText(LecturasActivity.this, "Debe seleccionar un medidor", Toast.LENGTH_LONG).show();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICTURE_RESULT:
                if (requestCode == PICTURE_RESULT)
                    if (resultCode == Activity.RESULT_OK) {
                        try {
                            thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                            float aspectRatio = thumbnail.getWidth() / (float) thumbnail.getHeight();
                            int width = 120;
                            //int width = 148;
                            int height = Math.round(width / aspectRatio);
                            thumbnail = Bitmap.createScaledBitmap(thumbnail, width, height, false);

                            //myImageView.setImageBitmap(thumbnail);
                            //Obtiene la ruta donde se encuentra guardada la imagen.
                            imageurl = getRealPathFromURI(imageUri);
                            foto = imageurl;
                            if(foto != null){ibtnCheckFoto.setImageResource(R.drawable.ic_baseline_check);}
                            String[] option = imageurl.split("/");
                            String picture = option[5];
                            String urlImg = imageurl.replace(picture, "");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
        }
    }
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private boolean checkExternalStoragePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        } else {
            return true;
        }
        return false;
    }

    private void ingresoLectura(){
        String foto64;
        String picture;
            DbLecturas dbLecturas = new DbLecturas(LecturasActivity.this);
            if(foto == null){
                foto64 = "1";
                picture = "";
            }else{
                foto64= encodeImages(foto);
                String[] option = foto.split("/");
                picture = option[5];
                EliminarArchivos(new File(foto));
            }




            long id = dbLecturas.insertarLectura(txtMedidor.getText().toString(), Integer.parseInt(txtLectura.getText().toString()) , foto64,picture);
            if(id>0){
                dbLecturas.lecturaIngresada(txtMedidor.getText().toString(), 1);
            }
            if (id > 0) {
                Toast.makeText(LecturasActivity.this, "REGISTRO GUARDADO", Toast.LENGTH_LONG).show();
                limpiar();
            } else {
                Toast.makeText(LecturasActivity.this, "ERROR AL GUARDAR REGISTRO", Toast.LENGTH_LONG).show();
            }

    }

    void EliminarArchivos(File ArchivoDirectorio) { /* Parametro File (Ruta) */
        if (ArchivoDirectorio.isDirectory()) /* Si es Directorio */
        {
            /* Recorremos sus Hijos y los ELiminamos */
            for (File hijo : ArchivoDirectorio.listFiles())
                EliminarArchivos(hijo); /*Recursividad Para Saber si no hay Subcarpetas */
        }
        else
            ArchivoDirectorio.delete(); /* Si no, se trata de un File ,solo lo Eliminamos*/
    }

    public void onClick_verUsuarios(View v){

        Intent goToCuts = new Intent(this, VerLecturas.class);
        startActivity(goToCuts);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event ) {
        if(keyCode == event.KEYCODE_BACK){
            limpiar();
            if(banderaIntent == true){
                Intent goToCuts = new Intent(this, VerLecturas.class);
                startActivity(goToCuts);
            }
            else {
                Intent goToCuts = new Intent(this, MenuActivity.class);
                startActivity(goToCuts);
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private String encodeImages(String path) {
        String imgDecodableString;
        Log.d("path:", path);
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] b = baos.toByteArray();
        imgDecodableString = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return imgDecodableString;

    }
}