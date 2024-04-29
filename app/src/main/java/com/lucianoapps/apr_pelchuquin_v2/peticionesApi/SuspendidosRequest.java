package com.lucianoapps.apr_pelchuquin_v2.peticionesApi;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
public class SuspendidosRequest extends StringRequest  {
    private static final String  LOGIN_REQUEST_URL="https://apirest.saap.aprpelchuquin.cl/api/UsuariosPorSuspenderPDA";
    private Map<String,String> params;
    public SuspendidosRequest(String id,String fecha,Response.Listener<String> listener){
        super(Request.Method.POST,LOGIN_REQUEST_URL,listener,null);
        params=new HashMap<>();
        params.put("user_id",id);
        params.put("FechaCreacion",fecha);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
