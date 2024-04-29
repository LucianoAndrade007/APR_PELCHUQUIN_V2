package com.lucianoapps.apr_pelchuquin_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lucianoapps.apr_pelchuquin_v2.db.DbLecturas;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Usuarios;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Suspensiones extends AppCompatActivity {

    private TextView inv_gauges, inv_userid, txt_name, txt_address,txt_mesesImpagos,txt_noContacto,txt_medidor;
    private Usuarios usuario;
    SpinnerDialog spinnerDialog,spinnerDialog2;
    ArrayList<String> items = new ArrayList<>();
    ImageButton ibtn_buscar;
    private boolean rbtControlServicio;
    private Button btn_usuario,btn_usuarioHabilitados;
    private CheckBox chkRealizado;
    private ArrayList<Usuarios> lista = new ArrayList<Usuarios>();
    private RadioButton rbtSuspension,rbtHabilitar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suspensiones);
        txt_name = findViewById(R.id.txt_nombre);
        txt_address = findViewById(R.id.txt_address);
        chkRealizado = findViewById(R.id.chkRealizado);
        txt_mesesImpagos = findViewById(R.id.txt_mesesImpagos);
        inv_gauges = findViewById(R.id.inv_gauges);
        inv_userid = findViewById(R.id.inv_userid);
        //txt_noContacto = findViewById(R.id.txt_noContacto);
        rbtSuspension = findViewById(R.id.rbtSuspension);
        rbtHabilitar = findViewById(R.id.rbtHabilitar);
        txt_medidor = findViewById(R.id.txt_medidor);

        inv_gauges.setVisibility(View.INVISIBLE);
        inv_userid.setVisibility(View.INVISIBLE);



        DbLecturas dbLecturas = new DbLecturas(Suspensiones.this);
        lista = dbLecturas.mostrarUsuariosSuspender();

        for (Usuarios p:lista){
            items.add( p.getCodigo_medidor()+ " - " + p.getNombres() + " " + p.getApellidos());
        }

        spinnerDialog = new SpinnerDialog(Suspensiones.this, items, "Ingrese medidor: ", R.style.DialogAnimations_SmileWindow, "Cerrar");
        spinnerDialog.setItemColor(R.color.colorBlack);
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //String usuarioDatos[]= item.split(" - ");
                //usuario = dbLecturas.verUsuario(usuarioDatos[0]);
                //txt_name.setText(usuario.getNombres() + " " + usuario.getApellidos());
                //txt_address.setText(usuario.getDireccion());
            }
        });

        /*ibtn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.clear();
                if (rbtSuspension.isChecked() ){
                    DbLecturas dbLecturas = new DbLecturas(Suspensiones.this);
                    lista = dbLecturas.mostrarUsuariosSuspender();

                }else if (rbtHabilitar.isChecked()){

                    DbLecturas dbLecturas = new DbLecturas(Suspensiones.this);
                    lista = dbLecturas.mostrarUsuariosHabilitar();
                }
                for (Usuarios p:lista){
                    items.add( p.getCodigo_medidor()+ " - " + p.getNombres() + " " + p.getApellidos());
                }
                spinnerDialog = new SpinnerDialog(Suspensiones.this, items, "Ingrese medidor: ", R.style.DialogAnimations_SmileWindow, "Cerrar");
                spinnerDialog.setItemColor(R.color.colorBlack);

                spinnerDialog.showSpinerDialog();
            }
        });*/

        String id = "";
        String servicio = "";
        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                id = null;
                servicio = null;
            } else {
                id = extras.getString("ID");
                servicio = extras.getString("Servicio");
            }
        } else {
            id =  savedInstanceState.getString("ID");
            servicio =  savedInstanceState.getString("Servicio");
        }

        if(id != null){
            if(servicio.equals("Suspender")){
                txt_medidor.setText(id);
                rbtSuspension.setChecked(true);
                buscarSuspendido();
            }
            else{
                txt_medidor.setText(id);
                rbtHabilitar.setChecked(true);
                buscarReponer();
            }


        }

        txt_medidor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,int before, int count) {

                if (rbtSuspension.isChecked()) {
                    buscarSuspendido();
                }

                if (rbtHabilitar.isChecked()){
                    buscarReponer();
                }
//                for (Usuarios p:lista){
//                    items.add( p.getCodigo_medidor()+ " - " + p.getNombres() + " " + p.getApellidos());
//                }
            }
        });



    }

    public void rbtSuspenderServicio (View v){
        txt_medidor.setText("");
        LimpiarDatos();
    }

    public void rbtHabilitarServicio (View v){
        txt_medidor.setText("");
        LimpiarDatos();
    }

    private void LimpiarDatos(){
        txt_name.setText("");
        txt_address.setText("");
        txt_mesesImpagos.setText("");
        //txt_noContacto.setText("");
        chkRealizado.setChecked(false);
    }

     private void buscarSuspendido(){
         String id_medidor = txt_medidor.getText().toString();
         Usuarios usuario;
         if (rbtSuspension.isChecked() ){
             DbLecturas dbLecturas = new DbLecturas(Suspensiones.this);
             usuario = dbLecturas.verUsuariosSuspender(id_medidor);
             if (usuario != null){

                 txt_name.setText(usuario.getNombres() + " " + usuario.getApellidos());
                 txt_name.setTextColor(Color.BLACK);
                 txt_address.setText(usuario.getDireccion());
                 txt_mesesImpagos.setText("Meses Impagos: "+usuario.getMeses_impagos() +"" );
                 if(usuario.getLectura_tomada() == 1){chkRealizado.setChecked(true);}

             }else{
                 LimpiarDatos();
             }
         }
     }

     private void buscarReponer (){
         String id_medidor = txt_medidor.getText().toString();
         DbLecturas dbLecturas = new DbLecturas(Suspensiones.this);
         usuario = dbLecturas.verUsuarioReponer(id_medidor);
         if (usuario != null){

             txt_name.setText(usuario.getNombres() + " " + usuario.getApellidos());
             txt_name.setTextColor(Color.BLACK);
             txt_address.setText(usuario.getDireccion());
             txt_mesesImpagos.setText("Meses Impagos: " + usuario.getMeses_impagos() +"" );
             if(usuario.getLectura_tomada() == 1){chkRealizado.setChecked(true);}

         }else{
             LimpiarDatos();
         }
     }

    public void onClick_verServicios(View v){
        Intent goToCuts = new Intent(this, VerLecturas.class);
        if(rbtSuspension.isChecked()){
            goToCuts.putExtra("Servicio", "Suspendidos");
        }else{
            goToCuts.putExtra("Servicio", "Reponer");
        }
        startActivity(goToCuts);

    }

    public void GuardaControl_onClick(View v){

        String fechaActual = obtenerFechaActual();
        DbLecturas dbLecturas = new DbLecturas(Suspensiones.this);
        boolean correcto, checked = chkRealizado.isChecked();
        int realizado;
        if(checked){realizado = 1;}else{realizado = 2;}
        if(rbtSuspension.isChecked()){
            correcto=dbLecturas.suspensionTomada(txt_medidor.getText().toString(), realizado,fechaActual);

        }else{
            correcto=dbLecturas.reposicionTomada(txt_medidor.getText().toString(), realizado,fechaActual);
        }
        Toast.makeText(Suspensiones.this, " "+correcto, Toast.LENGTH_LONG).show();


    }

    private String obtenerFechaActual (){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);
        return fecha;
    }

}