package com.lucianoapps.apr_pelchuquin_v2.entidades;

public class Lectura {
    private int  id;
    private String cod_usuario;
    private int lectura;
    private String foto;
    private String comentario;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCod_usuario() {
        return cod_usuario;
    }

    public void setCod_usuario(String cod_usuario) {
        this.cod_usuario = cod_usuario;
    }

    public int getLectura() {
        return lectura;
    }

    public void setLectura(int lectura) {
        this.lectura = lectura;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
