package com.lucianoapps.apr_pelchuquin_v2.entidades;

public class LecturaEnviada {
    private int  id;
    private String cod_usuario;
    private int lectura;
    private String foto;
    private String comentario;
    private String fecha_lectura;
    private String nombres;
    private String apellidos;
    private String direccion;
    private String id_medidor;
    private String id_usuario;
    private String codigo_medidor;
    private String consumo_anterior;

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

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getId_medidor() {
        return id_medidor;
    }

    public void setId_medidor(String id_medidor) {
        this.id_medidor = id_medidor;
    }

    public String getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(String id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getCodigo_medidor() {
        return codigo_medidor;
    }

    public void setCodigo_medidor(String codigo_medidor) {
        this.codigo_medidor = codigo_medidor;
    }

    public String getConsumo_anterior() {
        return consumo_anterior;
    }

    public void setConsumo_anterior(String consumo_anterior) {
        this.consumo_anterior = consumo_anterior;
    }

    public String getFecha_lectura() {
        return fecha_lectura;
    }

    public void setFecha_lectura(String fecha_lectura) {
        this.fecha_lectura = fecha_lectura;
    }
}
