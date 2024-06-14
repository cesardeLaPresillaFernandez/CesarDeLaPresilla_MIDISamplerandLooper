package com.example.nativemidi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText nombre, contra;

    String usuario="root", contraseña="root";
    Button enviar;
    ImageView imagen;
    Drawable d;

    String url = "jdbc:mariadb://185.250.203.195:2323/Cesar";
    String user = "cesar";
    String pssw = "tu_contraseña_segura";

    Connection connection;
    ResultSet rs;
    String userEntrada;
    String psswEntrada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imagen=findViewById(R.id.imageView);
        d=getResources().getDrawable(R.drawable.logo);
        imagen.setImageDrawable(d);
        Button enviar= (Button) findViewById(R.id.btEnviar);
        nombre= (EditText) findViewById(R.id.tv_usuario);
        contra= (EditText) findViewById(R.id.tv_usuario2);
        enviar.setOnClickListener(this);

        new Task().execute();


    }
    class Task extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                connection = DriverManager.getConnection(url, user, pssw);
                Statement stmt = connection.createStatement();
                String sql = "Select * from usuarios";
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    userEntrada = rs.getString("user");
                    psswEntrada = rs.getString("pssw");

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null;
        }
    }
    public void onClick(View view) {
                Log.i("credenciales",userEntrada+" "+psswEntrada );
                if(nombre.getText().toString().equals(userEntrada) && contra.getText().toString().equals(psswEntrada)) {
                    Intent intent1 = new Intent(this, ActivityMenu.class);
                    startActivity(intent1);
                }else{
                    Toast.makeText(this, "CREDENCIALES INCORRECTOS", Toast.LENGTH_SHORT).show();
                }


    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}