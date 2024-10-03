package com.example.myapplication;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button btnGetUsuario;
    private Button btnCambiarUsuario;
    private TextView tvNombre, tvCorreo, tvRut;
    private int usuarioActual = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetUsuario = findViewById(R.id.btn_get_usuario);
        btnCambiarUsuario = findViewById(R.id.btn_cambiar_usuario);
        tvNombre = findViewById(R.id.tv_nombre);
        tvCorreo = findViewById(R.id.tv_correo);
        tvRut = findViewById(R.id.tv_rut);

        btnGetUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerInformacionUsuario();
            }
        });

        btnCambiarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usuarioActual == 1) {
                    usuarioActual = 2;
                    btnCambiarUsuario.setText("Cambiar a usuario 1");
                } else {
                    usuarioActual = 1;
                    btnCambiarUsuario.setText("Cambiar a usuario 2");
                }
                obtenerInformacionUsuario();
            }
        });
    }

    private void obtenerInformacionUsuario() {
        Log.d("MainActivity", "Obteniendo información del usuario...");
        String url = "http://44.216.67.43:8081/usuario/" + usuarioActual;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MainActivity", "Error al obtener información del usuario: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String respuesta = response.body().string();
                    Log.d("MainActivity", "Respuesta recibida: " + respuesta);
                    if (respuesta != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                procesarRespuesta(respuesta);
                            }
                        });
                    } else {
                        Log.e("MainActivity", "La respuesta es null");
                    }
                } else {
                    Log.e("MainActivity", "La respuesta no es exitosa");
                }
            }
        });
    }

    private void procesarRespuesta(String respuesta) {
        try {
            JSONObject jsonObject = new JSONObject(respuesta);
            if (jsonObject != null) {
                String nombre = jsonObject.optString("nombre");
                String correo = jsonObject.optString("correo");
                String rut = jsonObject.optString("rut");

                if (nombre != null && correo != null && rut != null) {
                    tvNombre.setText("Nombre: " + nombre);
                    tvCorreo.setText("Correo: " + correo);
                    tvRut.setText("RUT: " + rut);
                } else {
                    Log.e("MainActivity", "Algunos campos son null");
                }
            } else {
                Log.e("MainActivity", "El JSON es null");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error al procesar la respuesta: " + e.getMessage());
        }
    }
}