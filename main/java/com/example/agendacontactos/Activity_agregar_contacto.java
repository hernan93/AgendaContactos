package com.example.agendacontactos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Activity_agregar_contacto extends AppCompatActivity {

    ImageView foto_perfil;
    EditText ETnombre, ETapellido, ETemail, ETdireccion, ETnumero_movil;
    Button BTguadar, BTcancelar;

    Uri ImageFileUri;
    String id, nombre, apellido, email, direccion, numero_movil, addedTime, updatedTime;


    boolean isEditMode = false;

    SqLiteDatos sqlDatos;
    String[] cameraPermission, storagePermission;

    private void Init() {
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("Nuevo Contacto"); //Setting the title

        //Initialising array permissions
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        foto_perfil = findViewById(R.id.imageView2);
        ETnombre = findViewById(R.id.editTextTextPersonName);
        ETapellido = findViewById(R.id.editTextTextPersonName2);
        ETemail = findViewById(R.id.editTextTextEmailAddress);
        ETdireccion = findViewById(R.id.editTextTextPersonName3);
        ETnumero_movil = findViewById(R.id.editTextPhone);

        Intent intent = getIntent();
        if( getIntent().getExtras() != null)
        {
            isEditMode = intent.getBooleanExtra("isEditMode", false);
        }
        isEditMode = intent.getBooleanExtra("isEditMode", false);
        if (isEditMode) {
            getSupportActionBar().setTitle("Actualizar");
            id = intent.getStringExtra("ID");
            nombre = intent.getStringExtra("NOMBRE");
            apellido = intent.getStringExtra("APELLIDO");
            email = intent.getStringExtra("EMAIL");
            direccion = intent.getStringExtra("DIRECCION");
            numero_movil = intent.getStringExtra("NUMERO_MOVIL");
            addedTime = intent.getStringExtra("ADDED_TIME");
            updatedTime = intent.getStringExtra("UPDATE_TIME");
            ImageFileUri = Uri.parse(intent.getStringExtra("IMAGE"));
            ETnombre.setText(nombre);
            ETapellido.setText(apellido);
            ETemail.setText(email);
            ETdireccion.setText(direccion);
            ETnumero_movil.setText(numero_movil);

            if (ImageFileUri.toString().equals("null")) {
                foto_perfil.setImageResource(R.drawable.perfil_del_usuario);
            } else {
                foto_perfil.setImageURI(ImageFileUri);
            }
        }
        BTcancelar = findViewById(R.id.BtCancelar);
        BTcancelar.setOnClickListener(v -> finish());

        BTguadar = findViewById(R.id.BtGuardar);
        BTguadar.setOnClickListener(v -> InputData());

        sqlDatos = new SqLiteDatos(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_contacto);
        Init();
    }

    private void InputData() {
        nombre = ETnombre.getText().toString().trim();
        apellido = ETapellido.getText().toString().trim();
        email = ETemail.getText().toString().trim();
        direccion = ETdireccion.getText().toString().trim();
        numero_movil = ETnumero_movil.getText().toString().trim();

        if (isEditMode) {

            String timestanp = " " + System.currentTimeMillis();

            sqlDatos.UpdateRecord(
                    " " + id,
                    " " + ImageFileUri,
                    " " + nombre,
                    " " + apellido,
                    " " + email,
                    " " + direccion,
                    " " + numero_movil,
                    " " + addedTime,
                    " "+ updatedTime
            );

            finish();

        } else {
            String timestanp = "" + System.currentTimeMillis();
            long id = sqlDatos.InsertRecord(
                    "" + ImageFileUri,
                    " " + nombre,
                    " " + apellido,
                    " " + email,
                    " " + direccion,
                    " " + numero_movil,
                    " " + timestanp,
                    " " + timestanp
            );
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}