package com.example.agendacontactos;

import static com.example.agendacontactos.SqLiteDatos.C_APELLIDO;
import static com.example.agendacontactos.SqLiteDatos.C_DIRECCION;
import static com.example.agendacontactos.SqLiteDatos.C_EMAIL;
import static com.example.agendacontactos.SqLiteDatos.C_ID;
import static com.example.agendacontactos.SqLiteDatos.C_IMAGE;
import static com.example.agendacontactos.SqLiteDatos.C_NOMBRE;
import static com.example.agendacontactos.SqLiteDatos.C_NUMERO_MOVIL;
import static com.example.agendacontactos.SqLiteDatos.Nombre_Tabla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;


public class DetallesActivity extends AppCompatActivity {

    TextView Tvnombre, Tvemail, Tvdireccion, Tvtelefonomovil;
    ImageView foto_perfil;
    SqLiteDatos sqLiteDatos;

    String contactID="";
    String[] phonePermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        inicio();

    }

    private void inicio() {
        sqLiteDatos = new SqLiteDatos(this);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   //show back button
        getSupportActionBar().setTitle("Información");
        Tvnombre = findViewById(R.id.tv_nombre);
        Tvemail = findViewById(R.id.tv_email);
        Tvdireccion = findViewById(R.id.tv_direccion);
        Tvtelefonomovil = findViewById(R.id.tv_numero_movil);
        foto_perfil = findViewById(R.id.IV_FotoDetalles);
        phonePermission = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.SEND_SMS};

        Intent intent = getIntent();
        if( getIntent().getExtras() != null)
        {
            contactID = intent.getStringExtra("detailID");
            if (contactID != null && !contactID.isEmpty()) {
                MostrarDetalles();
            }
        }

    }

    private void MostrarDetalles() {

        String selectQuery = "SELECT * FROM " + Nombre_Tabla + " WHERE " + C_ID + " =\"" + contactID + "\"";
        SQLiteDatabase db = sqLiteDatos.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String id = "" + cursor.getInt(cursor.getColumnIndexOrThrow(C_ID));
                String image = "" + cursor.getString(cursor.getColumnIndexOrThrow(C_IMAGE));
                String nombre = "" + cursor.getString(cursor.getColumnIndexOrThrow(C_NOMBRE));
                String apellido = "" + cursor.getString(cursor.getColumnIndexOrThrow(C_APELLIDO));
                String email = "" + cursor.getString(cursor.getColumnIndexOrThrow(C_EMAIL));
                String direccion = "" + cursor.getString(cursor.getColumnIndexOrThrow(C_DIRECCION));
                String telefonomovil = "" + cursor.getString(cursor.getColumnIndexOrThrow(C_NUMERO_MOVIL));




                Tvnombre.setText(String.format("Nombre : %s%s", nombre, apellido));
                Tvemail.setText(String.format("Email: %s", email));
                Tvdireccion.setText(String.format("Dirección: %s", direccion));
                Tvtelefonomovil.setText(String.format("Móvil: %s", telefonomovil));

                if (image.equals("null")) {
                    foto_perfil.setImageResource(R.drawable.perfil_del_usuario);
                } else {
                    foto_perfil.setImageURI(Uri.parse(image));
                }



            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detalles_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.detail_menu_call:
                RequestCallingPermission();
                if (CheckForCallPermission()) {
                    //Permission is already granted
                    PhoneCall();
                    }
                 break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void PhoneCall() {

        String[] num = Tvtelefonomovil.getText().toString().split(":");
        String phone = num[1];

        Intent phoneCallIntent = new Intent(Intent.ACTION_CALL);
        phoneCallIntent.setData(Uri.parse("tel:" + phone));
        startActivity(phoneCallIntent);



    }

    private void RequestCallingPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CALL_PHONE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para realizar llamadas telefónicas.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 225);
        } else {
            Log.i("Mensaje", "Se tiene permiso!");
        }

    }

    private boolean CheckForCallPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}