package com.lucianoapps.apr_pelchuquin_v2.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lucianoapps.apr_pelchuquin_v2.LecturasActivity;
import com.lucianoapps.apr_pelchuquin_v2.MenuActivity;
import com.lucianoapps.apr_pelchuquin_v2.R;
import com.lucianoapps.apr_pelchuquin_v2.entidades.Usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListaUsuariosAdapter extends RecyclerView.Adapter<ListaUsuariosAdapter.ContactoViewHolder> {

    ArrayList<Usuarios> listaContactos;
    ArrayList<Usuarios> listaOriginal;

    public ListaUsuariosAdapter(ArrayList<Usuarios> listaContactos) {
        this.listaContactos = listaContactos;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(listaContactos);
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_item_usuario, null, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        holder.viewMedidor.setText(listaContactos.get(position).getId_medidor());
        holder.viewNombre.setText(listaContactos.get(position).getNombres());
        holder.viewTelefono.setText(listaContactos.get(position).getApellidos());
        holder.viewCorreo.setText(listaContactos.get(position).getDireccion());
        if(listaContactos.get(position).getLectura_tomada() == 1){
            holder.viewEstado.setImageResource(R.drawable.ic_circulo_verde);
        }
        else if(listaContactos.get(position).getLectura_tomada() == 2){
            holder.viewEstado.setImageResource(R.drawable.ic_circulo_azul);
        }else{
            holder.viewEstado.setImageResource(R.drawable.ic_circulo_rojo);
        }


    }

    public void filtrado(final String txtBuscar) {
        int longitud = txtBuscar.length();
        if (longitud == 0) {

            listaContactos.clear();
            listaContactos.addAll(listaOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Usuarios> collecion = listaContactos.stream()
                        .filter(i -> i.getId_medidor_nombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());

                List<Usuarios> collecionCero = listaOriginal.stream()
                        .filter(i -> i.getId_medidor_nombre().toLowerCase().contains(txtBuscar.toLowerCase()))
                        .collect(Collectors.toList());
                if(collecion.size() == collecionCero.size()){
                        listaContactos.clear();
                        listaContactos.addAll(collecion);

                }else if(collecion.size() < collecionCero.size()){
                    listaContactos.clear();
                    listaContactos.addAll(collecionCero);
                }

            } else {
                for (Usuarios c : listaOriginal) {
                    if (c.getId_medidor_nombre().toLowerCase().contains(txtBuscar.toLowerCase())) {
                        listaContactos.add(c);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaContactos.size();
    }

    public class ContactoViewHolder extends RecyclerView.ViewHolder {

        TextView viewMedidor, viewTelefono, viewCorreo, viewNombre;
        ImageView viewEstado;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);

            viewMedidor = itemView.findViewById(R.id.viewMedidor);
            viewNombre = itemView.findViewById(R.id.viewNombre);
            viewTelefono = itemView.findViewById(R.id.viewTelefono);
            viewCorreo = itemView.findViewById(R.id.viewCorreo);
            viewEstado = itemView.findViewById(R.id.viewEstado);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, LecturasActivity.class);
                    intent.putExtra("ID", listaContactos.get(getAdapterPosition()).getId_medidor());
                    context.startActivity(intent);
                }
            });
        }
    }


}
