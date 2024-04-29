package com.lucianoapps.apr_pelchuquin_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    static final Integer PHONESTATS = 0x1;

    private TextInputEditText EditUser, txt_pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditUser = findViewById(R.id.txt_EditUser);
        txt_pass = findViewById(R.id.txt_EditPass);

    }


    public void GoToMenu(View view) {

        String user = EditUser.getText().toString();
        String pass = txt_pass.getText().toString();

        if(user.equals(InfoGlobal.ACC_USUARIO) && pass.equals(InfoGlobal.ACC_PASSWORD) ) {
        //if (true == true) {
            EditUser.setText("");
            txt_pass.setText("");
            Intent GoToMenu = new Intent(this, MenuActivity.class);
            startActivity(GoToMenu);
        } else {
            //Es falso
            Toast.makeText(this, "Verificar cuenta", Toast.LENGTH_LONG).show();
        }
    //}

    }


    public void GoLink (View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(InfoGlobal.URL_SIASUR)));
    }


}