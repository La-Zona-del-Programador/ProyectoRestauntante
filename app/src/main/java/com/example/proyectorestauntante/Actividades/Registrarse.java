package com.example.proyectorestauntante.Actividades;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectorestauntante.R;
import com.google.android.material.canvas.CanvasCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
public class Registrarse extends AppCompatActivity {
   EditText edtNombres, edtApellidos, edtDni, edtCorreo, edtContrasenia;
    Button btnRegistrar;
    AlertDialog dialog;
    TextView tvMensaje;
    ImageView ivImagen;
    int PICK_IMAGE_REQUEST=1;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        /*
        Inicializamos los eEditText y Button
        */
        edtNombres = (EditText) findViewById(R.id.edtNombres);
        edtApellidos = (EditText) findViewById(R.id.edtApellidos);
        edtDni = (EditText) findViewById(R.id.edtDni);
        edtCorreo = (EditText) findViewById(R.id.edtCorreo1);
        edtContrasenia = (EditText) findViewById(R.id.edtContrasenia1);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        ivImagen = (ImageView) findViewById(R.id.ivImagen);
        tvMensaje = (TextView) findViewById(R.id.tvMensajeAviso1);

        /*
        Inicializamos AlertDialog con la configuracion y mensaje
        */
        dialog= new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un Momento")
                .setCancelable(false)
                .build();

        /*
        Metodo btnRegistrar.setOnClickListener:
        Validar si lo campos estan llenos.
        Validar si el si el dni o correo ya se encuentra registrado.
        Mostrar el mensaje de acuerdo a la ejecucion.
        */
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(edtNombres.getText().toString().equals("")
                                ||edtApellidos.getText().toString().equals("")
                                || edtDni.getText().toString().equals("")
                                ||edtCorreo.getText().toString().equals("")
                                || edtContrasenia.getText().toString().equals("")) {
                            tvMensaje.setText("Falta llenar campos.");
                            //tvMensaje.setBackgroundColor(Color.GREEN);
                            tvMensaje.setTextColor(Color.GREEN);
                            dialog.dismiss();
                        }else{
                            try {
                                String url="http://192.168.1.43:8080/RESTAURANTE/controles/controlMantenimientoPersonal.php";
                                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        System.out.println("qweq"+response);
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            String estado,mensaje;
                                            estado =jsonObject.getString("estado");
                                            mensaje = jsonObject.getString("mensaje");
                                            if (estado.equals("1")){
                                            }else if(estado.equals("0")){
                                                tvMensaje.setText(mensaje);
                                                //tvMensaje.setBackgroundColor(Color.GREEN);
                                                tvMensaje.setTextColor(Color.RED);
                                            }
                                            dialog.dismiss();
                                        }catch (JSONException e){
                                            Log.i("Error1", e.getMessage());
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        System.out.println(error.getMessage());
                                        dialog.dismiss();
                                        Toast.makeText(getBaseContext(), "Verificar si tiene Acceso a Internet.", Toast.LENGTH_LONG).show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        ivImagen.buildDrawingCache();
                                        bitmap = ivImagen.getDrawingCache();
                                        String foto=getStringImagen(bitmap);
                                        Map<String,String> parametros =new HashMap<String,String>();
                                        parametros.put("nombres",edtNombres.getText().toString());
                                        parametros.put("apellidos",edtApellidos.getText().toString());
                                        parametros.put("dni",edtDni.getText().toString());
                                        parametros.put("correo",edtCorreo.getText().toString());
                                        parametros.put("contrasenia",edtContrasenia.getText().toString());
                                        parametros.put("cargo","ADMINISTRADOR");
                                        parametros.put("foto",foto);
                                        return parametros;

                                    }
                                };
                                RequestQueue request= Volley.newRequestQueue(getBaseContext());
                                request.add(stringRequest);
                            }catch (Exception e){
                                Log.i("Error3", e.getMessage());
                            }
                        }
                        dialog.dismiss();
                    }
                },3000);

            }

        });

        ivImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Seleccionar una imagen"),PICK_IMAGE_REQUEST);
            }
        });

    }
    //Convertir la imagen en un String
    public String getStringImagen(Bitmap bitmap){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imaBytes=outputStream.toByteArray();
        String encodedImagen= Base64.encodeToString(imaBytes,Base64.DEFAULT);
        return encodedImagen;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri fileUri=data.getData();
            System.out.println(fileUri+"qweqe");
            try {
                //COMO OBTENER EL MAPA DE BITS DE LA GALERIA
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),fileUri);
                //CONFIGURACION DEL MAPA DE BITS EN Imageview
                ivImagen.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Subir imagen
    public void upLoadImagen(){
        final ProgressDialog cargando=ProgressDialog.show(this,"Subiendo...","Espere por favor");

    }
}