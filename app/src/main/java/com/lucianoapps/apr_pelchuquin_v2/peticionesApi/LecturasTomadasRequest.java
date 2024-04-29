package com.lucianoapps.apr_pelchuquin_v2.peticionesApi;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.lucianoapps.apr_pelchuquin_v2.InfoGlobal;

import java.util.HashMap;
import java.util.Map;

public class LecturasTomadasRequest extends StringRequest {
    private Map<String,String> params;
    public LecturasTomadasRequest(String imei,
                           Response.Listener<String> listener){
        super(Request.Method.POST, InfoGlobal.URL_LECTURAS_TOMADAS,listener,null);
        params=new HashMap<>();
        params.put("imei",imei);
    }
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

