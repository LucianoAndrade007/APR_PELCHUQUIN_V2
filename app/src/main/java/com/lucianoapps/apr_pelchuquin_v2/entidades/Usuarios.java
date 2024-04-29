package com.lucianoapps.apr_pelchuquin_v2.entidades;

public class Usuarios {
    private String nombres;
    private String apellidos;
    private String direccion;
    private String id_medidor;
    private String id_usuario;
    private String codigo_medidor;
    private String consumo_anterior;
    private int lectura_tomada;
    private String id_medidor_nombre;
    private String fono;
    private int mesesImpagos;
    private String fecha_modificado;
    private String lectura_pendiente;


    public String getNombres() { return nombres; }

    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellidos() { return apellidos; }

    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getId_medidor() { return id_medidor; }

    public void setId_medidor(String id_medidor) { this.id_medidor = id_medidor; }

    public String getId_usuario() { return id_usuario; }

    public void setId_usuario(String id_usuario) { this.id_usuario = id_usuario; }

    public String getCodigo_medidor() { return codigo_medidor; }

    public void setCodigo_medidor(String codigo_medidor) { this.codigo_medidor = codigo_medidor; }

    public String getConsumo_anterior() { return consumo_anterior; }

    public void setConsumo_anterior(String consumo_anterior) { this.consumo_anterior = consumo_anterior; }

    public int getLectura_tomada() { return lectura_tomada; }

    public void setLectura_tomada(int lectura_tomada) { this.lectura_tomada = lectura_tomada; }

    public String getId_medidor_nombre() { return id_medidor_nombre; }

    public void setId_medidor_nombre(String id_medidor_nombre) { this.id_medidor_nombre = id_medidor_nombre; }

    public String getFono() { return fono; }

    public void setFono(String fono) { this.fono = fono; }

    public int getMeses_impagos() { return mesesImpagos; }

    public void setMeses_Impagos(int mesesImpagos) { this.mesesImpagos = mesesImpagos; }

    public String getfecha_modificado() { return fecha_modificado; }

    public void setfecha_modificado(String fecha_modificado) { this.fecha_modificado = fecha_modificado; }

   public String getlectura_pendiente() { return lectura_pendiente; }

    public void setlectura_pendiente(String lectura_pendiente) { this.lectura_pendiente = lectura_pendiente; }
}

