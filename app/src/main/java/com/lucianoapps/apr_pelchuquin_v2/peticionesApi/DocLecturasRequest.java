package com.lucianoapps.apr_pelchuquin_v2.peticionesApi;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class DocLecturasRequest  extends StringRequest {

    private static final String  LOGIN_REQUEST_URL="https://apirest.saap.aprpelchuquin.cl/api/DocLecturasPDA/leturas";
    private Map<String,String> params;
    public DocLecturasRequest(String cadenaDocumento,String nombreArchivo, String imei, Response.Listener<String> listener){
        super(Request.Method.POST,LOGIN_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("cadenaDocumento",cadenaDocumento);
        params.put("nombreArchivo",nombreArchivo);
        params.put("imei",imei);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
