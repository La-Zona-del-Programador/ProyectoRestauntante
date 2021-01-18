package com.example.proyectorestauntante.Actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectorestauntante.MainActivity;
import com.example.proyectorestauntante.R;
import com.example.proyectorestauntante.dominio.Personal;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import dmax.dialog.SpotsDialog;

public class IniciarSesion extends AppCompatActivity {
    EditText edtCorreo,edtContrasenia;
    Button btnIniciarSesion, btnRegistrarse;
    AlertDialog dialog;
    Personal personal;
    TextView tvMensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicar_sesion);

        /*
        Inicializamos los TextView, eEditText y Button
        */
        tvMensaje=(TextView) findViewById(R.id.tvMensajeAviso);
        edtCorreo=(EditText) findViewById(R.id.edtCorreo);
        edtContrasenia=(EditText) findViewById(R.id.edtContrasenia);
        btnIniciarSesion=(Button) findViewById(R.id.btnIniciarSesion);
        btnRegistrarse=(Button) findViewById(R.id.btnRegistrarse);

        /*
        Inicializamos AlertDialog con la configuracion y mensaje
        */
        dialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un Momento")
                .setCancelable(false)
                .build();

        /*
        Metodo btnIniciarSesion.setOnClickListener:
        Validar si lo campos estan llenos.
        Validar si el correo y contraseña es correcto.
        Mostrar el mensaje de acuerdo a la ejecucion.
        */
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(edtCorreo.getText().toString().equals("")||edtContrasenia.getText().toString().equals("")) {
                            tvMensaje.setText("Falta llenar campos.");
                            //tvMensaje.setBackgroundColor(Color.GREEN);
                            tvMensaje.setTextColor(Color.GREEN);
                            dialog.dismiss();
                        }else{
                        try {
                            String url="http://192.168.42.59:8080/RESTAURANTE/controles/controlIniciarSesion.php";
                            StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        if(response.equals("[]")){
                                            tvMensaje.setText("Correo o contraseña incorrectos.");
                                            tvMensaje.setTextColor(Color.RED);
                                            //tvMensaje.setBackgroundColor(Color.RED);
                                        }else{
                                            JSONArray jsonArray = new JSONArray(response);
                                            String estado;
                                            estado =jsonArray.getJSONObject(0).getString("v2");

                                            Intent intent=new Intent(getBaseContext(), MainActivity.class);
                                            startActivity(intent);
                                        }
                                        dialog.dismiss();
                                    }catch (JSONException e){
                                        Log.i("Error1", e.getMessage());
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    Toast.makeText(getBaseContext(), "Verificar si tiene Acceso a Internet.", Toast.LENGTH_LONG).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> parametros =new HashMap<String,String>();
                                    parametros.put("correo",edtCorreo.getText().toString());
                                    parametros.put("contrasenia",edtContrasenia.getText().toString());
                                    return parametros;
                                }
                            };
                            RequestQueue request= Volley.newRequestQueue(getBaseContext());
                            request.add(stringRequest);
                            }catch (Exception e){
                            Log.i("Error2", e.getMessage());
                            }
                        }
                    }
                },3000);

            }
        });

        /*
        Metodo btnRegistrarse.setOnClickListener:
        Enviar ala Actividad registrarse
        */
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),Registrarse.class);
                startActivity(intent);
            }
        });

    }


}
