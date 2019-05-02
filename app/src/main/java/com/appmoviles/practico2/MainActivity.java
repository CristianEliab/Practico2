package com.appmoviles.practico2;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;

import com.appmoviles.practico2.db.DBHandler;
import com.appmoviles.practico2.modelo.AdapterAmigos;
import com.appmoviles.practico2.modelo.Amigo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements AdapterAmigos.OnItemClickListener{

    private RecyclerView lista_amigos;
    private Button btn_agregar;
    DBHandler localdb;
    private AdapterAmigos adapterAmigos;
    FirebaseAuth auth;
    private Button btn_signout;


    FirebaseDatabase rtdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rtdb = FirebaseDatabase.getInstance();


        //Get Reference --> la raiz de la base de datos
        //child hijos de esa DB
        //rtdb.getReference().child("alfa").child("bea").child("gamma").setValue("Mi primer valor");


        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CALL_PHONE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, 0);

        localdb = DBHandler.getInstance(this);
        auth = FirebaseAuth.getInstance();


        //Si no hay usuario loggeado
        if (auth.getCurrentUser() == null) {

            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();

            return;
        }

        btn_signout = findViewById(R.id.btn_signout);
        lista_amigos = findViewById(R.id.lista_amigos);
        btn_agregar = findViewById(R.id.btn_agregar);
        adapterAmigos = new AdapterAmigos();
        adapterAmigos.setListener(this);
        lista_amigos.setLayoutManager(new LinearLayoutManager(this));
        lista_amigos.setAdapter(adapterAmigos);
        lista_amigos.setHasFixedSize(true);


        btn_agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AgregarAmigoActivity.class);
                startActivity(i);
            }
        });

        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapterAmigos.showAllAmigos(localdb.getAllAmigosOfUser(auth.getCurrentUser().getUid()));
    }

    @Override
    public void onItemClick(Amigo amigo) {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + amigo.getTelefono()));
        startActivity(i);
    }
}
