package com.appmoviles.practico2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.appmoviles.practico2.db.DBHandler;
import com.appmoviles.practico2.modelo.Amigo;
import com.appmoviles.practico2.util.UtilDomi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

public class AgregarAmigoActivity extends AppCompatActivity {
    private static final int CAMERA_CALLBACK_ID = 100;
    private static final int GALLERY_CALLBACK_ID = 101;
    private EditText et_nombre;
    private EditText et_edad;
    private EditText et_correo;
    private EditText et_telefono;
    private Button btn_agregar_amigo;
    private Button btn_open_gal;
    FirebaseAuth auth;
    FirebaseDatabase rtdb;
    DBHandler db;

    private ImageView img_amigo;
    private Button btn_take_pic;
    private File photoFile;
    private boolean agrego_datos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_amigos);


        //Si no pongo esto entonces va a salir el URIFileExposedException


        db = DBHandler.getInstance(this);
        auth = FirebaseAuth.getInstance();
        rtdb = FirebaseDatabase.getInstance();


        et_nombre = findViewById(R.id.et_nombre);
        et_edad = findViewById(R.id.et_edad);
        et_correo = findViewById(R.id.et_correo);
        et_telefono = findViewById(R.id.et_telefono);
        btn_agregar_amigo = findViewById(R.id.btn_agregar_amigo);
        img_amigo = findViewById(R.id.im_photo);
        btn_take_pic = findViewById(R.id.btn_take_photo);

        btn_open_gal = findViewById(R.id.btn_open_gal);

        btn_agregar_amigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Amigo amigo = new Amigo(UUID.randomUUID().toString(),
                        et_nombre.getText().toString(),
                        et_edad.getText().toString(),
                        et_telefono.getText().toString(),
                        et_correo.getText().toString(), auth.getCurrentUser().getUid());
                //Agregar amigo a DB local
                db.createAmigo(amigo);

                rtdb.getReference().child("amigos").child(auth.getCurrentUser().getUid()).push().setValue(amigo);

                ArrayList<Amigo> lista = db.getAllAmigosOfUser(auth.getCurrentUser().getUid());
                for (int i = 0; i < lista.size(); i++) {
                    Log.e(">>>", lista.get(i).getNombre());
                }

                agrego_datos = true;
                finish();
            }
        });

        btn_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = new File(Environment.getExternalStorageDirectory() + "/" + UUID.randomUUID().toString() + ".png");
                Uri uri = FileProvider.getUriForFile(AgregarAmigoActivity.this, getPackageName(), photoFile);
                i.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(i, CAMERA_CALLBACK_ID);
            }
        });

        btn_open_gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_CALLBACK_ID);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Luego de tomar la foto y guardarla
        if (requestCode == CAMERA_CALLBACK_ID && resultCode == RESULT_OK) {
            Bitmap imagen = BitmapFactory.decodeFile(photoFile.toString());
            img_amigo.setImageBitmap(imagen);
        }
        if (requestCode == GALLERY_CALLBACK_ID && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            photoFile = new File(UtilDomi.getPath(this, uri));
            Bitmap imagen = BitmapFactory.decodeFile(photoFile.toString());
            img_amigo.setImageBitmap(imagen);
        }
    }

    //Cargamos
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String nombre = sp.getString("nombre", "");
        String edad = sp.getString("edad", "");
        String correo = sp.getString("correo", "");
        String telefono = sp.getString("telefono", "");

        et_nombre.setText(nombre);
        et_edad.setText(edad);
        et_correo.setText(correo);
        et_telefono.setText(telefono);
    }

    //Guardamos
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putString("nombre", et_nombre.getText().toString())
                .putString("edad", et_edad.getText().toString())
                .putString("correo", et_correo.getText().toString())
                .putString("telefono", et_telefono.getText().toString())
                .apply();
    }

    @Override
    protected void onDestroy() {
        if (agrego_datos) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            sp.edit().remove("nombre")
                    .remove("edad")
                    .remove("correo")
                    .remove("telefono")
                    .apply();
            super.onDestroy();
        }
    }

    // onSaveInstanceState
}
