package com.lucianoapps.apr_pelchuquin_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.KeyEvent;
import android.widget.SearchView;

import android.os.Bundle;

import com.lucianoapps.apr_pelchuquin_v2.adaptadores.ListaReposicionAdapter;
import com.lucianoapps.apr_pelchuquin_v2.adaptadores.ListaSuspendidosAdapter;
import com.lucianoapps.apr_pelchuquin_v2.adaptadores.ListaUsuariosAdapter;
import com.lucianoapps.apr_pelchuquin_v2.db.DbLecturas;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Usuarios;

import java.util.ArrayList;

public class VerLecturas extends AppCompatActivity implements SearchView.OnQueryTextListener {

        SearchView txtBuscar;
        RecyclerView listaContactos;
        ArrayList<Usuarios> listaArrayContactos;
        ListaUsuariosAdapter adapter;
        ListaSuspendidosAdapter adapterSuspendidos;
        ListaReposicionAdapter adapterReposiciones;
        String servicio = "";
        //Spinner spinner;
        //private int selecteditem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_lecturas);

        txtBuscar = findViewById(R.id.txtBuscar);
        listaContactos = findViewById(R.id.listaContactos);
        listaContactos.setLayoutManager(new LinearLayoutManager(this));

        DbLecturas dbUsuarios = new DbLecturas(VerLecturas.this);

        //listaArrayContactos = new ArrayList<>();

        //adapter = new ListaUsuariosAdapter(dbUsuarios.mostrarUsuarios());
        //listaContactos.setAdapter(adapter);

        if(savedInstanceState == null){
            Bundle extras = getIntent().getExtras();
            if(extras == null){
                servicio = null;
            } else {
                servicio = extras.getString("Servicio");
            }
        } else {
            servicio =  savedInstanceState.getString("Servicio");
        }
        if(servicio == null){
            adapter = new ListaUsuariosAdapter(dbUsuarios.mostrarUsuarios());
            listaContactos.setAdapter(adapter);
        }
        else if(servicio.equals("Suspendidos")){
            adapterSuspendidos = new ListaSuspendidosAdapter(dbUsuarios.mostrarUsuariosSuspender());
            listaContactos.setAdapter(adapterSuspendidos);
        }else if(servicio.equals("Reponer")){
            adapterReposiciones = new ListaReposicionAdapter(dbUsuarios.mostrarUsuariosReponer());
            listaContactos.setAdapter(adapterReposiciones);
        }



        txtBuscar.setOnQueryTextListener(this);

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        //adapter.filtroLista(selecteditem);
        if(servicio == null){
            adapter.filtrado(s);
        }
        else if(servicio.equals("Suspendidos")){
            adapterSuspendidos.filtrado(s);
        }else if(servicio.equals("Reponer")){
            adapterReposiciones.filtrado(s);
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event ) {
        if(keyCode == event.KEYCODE_BACK){
            Intent goToCuts = new Intent(this, MenuActivity.class);
            startActivity(goToCuts);
        }
        return super.onKeyDown(keyCode, event);
    }

}