package com.lucianoapps.apr_pelchuquin_v2.peticionesApi;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.lucianoapps.apr_pelchuquin_v2.InfoGlobal;

import java.util.HashMap;
import java.util.Map;

public class LecturasRequest extends StringRequest{
    //private static final String  LOGIN_REQUEST_URL="https://apirest.saap.aprpelchuquin.cl/api/LecturaEstado";
    private Map<String,String> params;
    public LecturasRequest(String id_medidor,String lectura,String date,String photoComent, String coment, String user,String code,String file,String imei,
                           Response.Listener<String> listener){
        super(Request.Method.POST, InfoGlobal.LOGIN_REQUEST_URL,listener,null);
        params=new HashMap <>();
        params.put("id_medidor",id_medidor);
        params.put("lectura",lectura);
        params.put("date",date);
        params.put("photoComent",photoComent);
        params.put("coment",coment);
        params.put("user",user);
        params.put("code",code);
        params.put("filename",file);
        params.put("imei",imei);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
