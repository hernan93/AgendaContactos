package com.example.agendacontactos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnClickMainListener {

    RecyclerView mContacts;
    SqLiteDatos sqLiteDatos;
    private main mainFragment;

    String orderByNewest = "ADDED_TIME_STAMP" + " DESC";
    String orderByOldest = "ADDED_TIME_STAMP" + " ASC";
    String orderByNameAsc = "Nombre" + " ASC";
    String orderByNameDesc = "Nombre" + " DESC ";

    String currentOrderState = orderByNewest;
    private static final int STORAGE_IMPORT = 50;
    private static final int STORAGE_EXPORT = 60;
    boolean sdDisponible =	false;          // Bool que indica si la SD esta disponible
    boolean sdAccesoEscritura =	false;
    String textoLecturaFin = "";            // String asociada al contenido de la lectura de la SD

    String [] StoragePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContacts = findViewById(R.id.mainRV);
        StoragePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        mostrarFragmentos();
    }

    private void mostrarFragmentos() {
        FragmentManager fm = getSupportFragmentManager();
        mainFragment = (main) fm.findFragmentById(R.id.fragmentContainerView);

        cargarPrimerFragmento();
    }

    private void cargarPrimerFragmento() {

        sqLiteDatos = new SqLiteDatos( this);
        ArrayList<Contacto> contactArrayList = sqLiteDatos.GetAllContacts(currentOrderState);

    }
    @Override
    protected void onResume() {
        super.onResume();
        mainFragment.cargarContactos(currentOrderState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.main_menu_buscar);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mainFragment.SearchDatabase(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                mainFragment.SearchDatabase(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public boolean CheckStoragePermission(){
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void RequestStoragePermissionImport(){
        ActivityCompat.requestPermissions(this, StoragePermissions, STORAGE_IMPORT);
    }

    public void RequestStoragePermissionExport(){
        ActivityCompat.requestPermissions(this, StoragePermissions, STORAGE_EXPORT);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.main_menu_ordenar)
        {
            Sorting();
        }else if (id == R.id.main_menu_eliminar)
        {
            sqLiteDatos.DeleteAllData();
            onResume();
        }else if (id == R.id.main_menu_llamar)
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            startActivity(intent);
        }
        else if(id == R.id.main_menu_exportarJs) {

            if(CheckStoragePermission()) {
                //Permission allowed
                exportarContatosJson();
            }else{
                RequestStoragePermissionExport();
            }

        }
        else if(id == R.id.main_menu_exportarcnt) {

            if(CheckStoragePermission()) {
                //Permission allowed
                exportarContatosCNT();
            }else{
                RequestStoragePermissionExport();
            }
        }
        else if(id == R.id.main_menu_ImportarJS) {
            if(CheckStoragePermission()) {
                //Permission allowed
                importaFileJson();
                onResume();
            }else{
                RequestStoragePermissionImport();
            }

        }
        else if(id == R.id.main_menu_Importarcnt) {
            if(CheckStoragePermission()) {
                //Permission allowed
                importaFileCnt();
                onResume();
            }else{
                RequestStoragePermissionImport();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void Sorting() {

        String [] options = {"Ascendente","Descendente","Nuevo ","Antiguo"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Ordenar por");

        builder.setItems(options, (dialogInterface, i) -> {

            switch (i){
                case 0:
                    mainFragment.cargarContactos(orderByNameAsc);
                    break;
                case 1:
                    mainFragment.cargarContactos(orderByNameDesc);
                    break;
                case 2:
                    mainFragment.cargarContactos(orderByNewest);
                    break;
                case 3:
                    mainFragment.cargarContactos(orderByOldest);
                    break;
            }
        }).show();
    }
    private void comprobarSD(){
        String estado	=	Environment.getExternalStorageState();

        if (estado.equals(Environment.MEDIA_MOUNTED)) {
            sdDisponible =	true;
            sdAccesoEscritura =	true;
        }
        else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            sdDisponible =	true;
            sdAccesoEscritura =	false;
        }
        else {
            ActivityCompat.requestPermissions(this, StoragePermissions, STORAGE_IMPORT);
            sdDisponible =	false;
            sdAccesoEscritura =	false;
        }
    }

    private void exportarContatosJson() {

        // Comprobamos si tenemos una SD montada.
        comprobarSD();

        if (sdAccesoEscritura) {
            crearFileJson();

        } else {
            Context context = getApplicationContext();
            CharSequence text = "NO se pudo exportar";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private void exportarContatosCNT() {

        // Comprobamos si tenemos una SD montada.
        comprobarSD();

        if (sdAccesoEscritura) {
            crearFileCNT();

        } else {
            Context context = getApplicationContext();
            CharSequence text = "NO se pudo exportar";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    public void crearFileJson(){

        // Establecemos la ruta donde guardar el archivo .JSON
        File ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        // Creamos el archivo
        File file = new File(ruta.getAbsolutePath(), "Contactos.json");
        // Creamos un JSArray
        JSONArray contactos = new JSONArray();
        ArrayList<Contacto> contactArrayList;
        contactArrayList = sqLiteDatos.GetAllContacts(orderByOldest);
        // Necesario para acceder a los atributos del contacto

        try{
            // Para cada registro de la lista, añadimos sus valores en el objeto Json
            // y lo añadimos al array previamente creado.
            for (int i=0; i < contactArrayList.size(); i++){
                JSONObject root = new JSONObject();

                root.put("IMAGE",contactArrayList.get(i).getImage()); //1
                root.put("NOMBRE",contactArrayList.get(i).getNombre());
                root.put("APELLIDO",contactArrayList.get(i).getApellido());
                root.put("EMAIL",contactArrayList.get(i).getEmail());
                root.put("DIRECCION",contactArrayList.get(i).getDireccion());
                root.put("NUMERO_MOVIL",contactArrayList.get(i).getNumeroMovil());
                root.put("ADDED_TIME_STAMP",contactArrayList.get(i).getAddedTime());
                root.put("UPDATED_TIME_STAMP",contactArrayList.get(i).getUpdateTime());
                contactos.put(root);
            }

        }catch (Exception e){e.printStackTrace();}


        JSONObject contacto_final = new JSONObject();
        try{
            contacto_final.put("Contactos",contactos);
        }catch (Exception e){e.printStackTrace();}


        // Abrimos el archivo como y añadimos la cadena en formato JSON
        try {

            OutputStreamWriter f_out = new OutputStreamWriter(new FileOutputStream(file));
            f_out.write(contacto_final.toString());
            f_out.close();

            Context context = getApplicationContext();
            CharSequence text = "Datos JSON creado en: "+ruta;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (Exception e) {
            Context context = getApplicationContext();
            CharSequence text = "No se escribieron datos en el archivo";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }
    }
    // Crea el fichero Contactos.txt con los datos obtenidos de la lista

    public void crearFileCNT(){

        File ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        //File ruta = Environment.getExternalStorageDirectory();
        // Creamos el archivo
        File file = new File(ruta.getAbsolutePath(), "Contactos.cnt");
        // String que contiene toda la informacion
        StringBuilder textoFin = new StringBuilder();
        // Necesario para acceder a los atributos del contacto
        ArrayList<Contacto> contactArrayList;
        contactArrayList = sqLiteDatos.GetAllContacts(orderByOldest);

        // Si no hay usuarios le mostramos un error
        if(contactArrayList.size() == 0){
            Context context = getApplicationContext();
            CharSequence text = "No tienes datos a exportar";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else{
            // Cargamos los datos
            for (int i=0; i<contactArrayList.size(); i++){

                String aux =
                        "IMAGE:" + contactArrayList.get(i).getImage() + ", " +
                        "NOMBRE:" + contactArrayList.get(i).getNombre() + ", " +
                        "APELLIDO:" + contactArrayList.get(i).getApellido() + ", " +
                        "EMAIL:" + contactArrayList.get(i).getEmail() + ", " +
                        "DIRECCION:" + contactArrayList.get(i).getDireccion() + ", " +
                        "NUMERO_MOVIL:"  + contactArrayList.get(i).getNumeroMovil()  +", " +
                                "ADDED_TIME_STAMP:"  + contactArrayList.get(i).getAddedTime()  +", " +
                                "UPDATED_TIME_STAMP:"  + contactArrayList.get(i).getUpdateTime()  +
                                "\n";
                textoFin.append(aux);
            }

            Log.i("Texto",""+textoFin);

            // Abrimos el archivo y escribimos los datos cargados previamente
            try {
                // Establecemos la ruta donde guardar el archivo .cnt

                OutputStreamWriter f_out = new OutputStreamWriter(new FileOutputStream(file));
                f_out.write(textoFin.toString());
                f_out.close();

                Context context = getApplicationContext();
                CharSequence text = "Archivo CNT creado en: " + ruta;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            } catch (Exception e) {
                Context context = getApplicationContext();
                CharSequence text = "No se escribieron datos en el archivo";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                }
        }

    }

    public void importaFileCnt(){

        File ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(ruta.getAbsolutePath(), "Contactos.cnt");
        // ArrayList para obtener los registros del File y añadirlos a la BD
        ArrayList<String> ImageFromFile =  new ArrayList<>();
        ArrayList<String> nombreFromFile =  new ArrayList<>();
        ArrayList<String> apellidoFromFile =  new ArrayList<>();
        ArrayList<String> movilFromFile =  new ArrayList<>();
        ArrayList<String> direccionFromFile =  new ArrayList<>();
        ArrayList<String> mailFromFile =  new ArrayList<>();
        ArrayList<String> addedTimeFromFile =  new ArrayList<>();
        ArrayList<String> upDateTimeFromFile =  new ArrayList<>();
        // Si no encuentra el archivo
        if (!file.exists()) {
            Context context = getApplicationContext();
            CharSequence text = "No se Encuentra datos";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } else {
            try {
                // Borramos los contactos actuales
                sqLiteDatos.DeleteAllData();
                // Clase para leer lineas desde un archivo en Java
                BufferedReader f_in = new BufferedReader(new
                        InputStreamReader(new FileInputStream(file)));
                // Mientras lea una linea del archivo
                while ((textoLecturaFin = f_in.readLine()) != null) {
                    Log.i("Inf"," "+ textoLecturaFin);
                    String[] sep = textoLecturaFin.split(", ");

                    //Imagen
                    String[] ni = sep[0].split(":");
                    ImageFromFile.add(ni[1]);
                    // Nombre
                    String[] nm = sep[1].split(":");
                    nombreFromFile.add(nm[1]);

                    // Apellido
                    String[] mv = sep[2].split(":");
                    apellidoFromFile.add(mv[1]);

                    // Email
                    String[] ml = sep[3].split(":");
                    mailFromFile.add(ml[1]);

                    // direccion
                    String[] dr = sep[4].split(":");
                    direccionFromFile.add(dr[1]);

                    // numero
                    String[] num = sep[5].split(":");
                    movilFromFile.add(num[1]);

                    // add
                    String[] addi = sep[6].split(":");
                    addedTimeFromFile.add(addi[1]);
                    // in
                    String[] in = sep[7].split(":");
                    upDateTimeFromFile.add(in[1]);


                }

                // Insertamos los nuevos campos
                for (int j=0; j<ImageFromFile.size() ;j++){
                    sqLiteDatos.InsertRecord(
                            ImageFromFile.get(j) ,
                            nombreFromFile.get(j),
                            apellidoFromFile.get(j),
                            mailFromFile.get(j),
                            direccionFromFile.get(j),
                            movilFromFile.get(j),
                            addedTimeFromFile.get(j),
                            upDateTimeFromFile.get(j));
                }

                f_in.close(); // Cerramos el file
                mostrarFragmentos(); // Recargamos los datos para ver la lista

                Context context = getApplicationContext();
                CharSequence text = "Datos importados "+ruta;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public void importaFileJson(){

        File ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(ruta.getAbsolutePath(), "Contactos.json");

        // ArrayList para obtener los registros del File y añadirlos a la BD
        ArrayList<String> ImageFromFile =  new ArrayList<>();
        ArrayList<String> nombreFromFile =  new ArrayList<>();
        ArrayList<String> apellidoFromFile =  new ArrayList<>();
        ArrayList<String> movilFromFile =  new ArrayList<>();
        ArrayList<String> direccionFromFile =  new ArrayList<>();
        ArrayList<String> mailFromFile =  new ArrayList<>();
        ArrayList<String> addedTimeFromFile =  new ArrayList<>();
        ArrayList<String> upDateTimeFromFile =  new ArrayList<>();

        // Si no encuentra el archivo
        if (!file.exists()) {
            Context context = getApplicationContext();
            CharSequence text = "No se Encuentra datos" + ruta;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        } else {
            try {
                //Borramos los contactos actuales
                sqLiteDatos.DeleteAllData();
                // Clase para leer lineas desde un archivo en Java
                BufferedReader f_in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                // Pasamos el contenido del archvio a un string
                textoLecturaFin = f_in.readLine();
                // Parseamos el contenido a JSON
                JSONObject root = new JSONObject(textoLecturaFin);
                JSONArray jsonArray = root.getJSONArray("Contactos");

                for(int i=0; i<jsonArray.length(); i++) {

                    // Obtenemos el objeto contacto y los diferentes campos
                    JSONObject json_data = jsonArray.getJSONObject(i);
                    ImageFromFile.add(json_data.getString("IMAGE"));
                    nombreFromFile.add(json_data.getString("NOMBRE"));
                    apellidoFromFile.add(json_data.getString("APELLIDO"));
                    mailFromFile.add(json_data.getString("EMAIL"));
                    direccionFromFile.add(json_data.getString("DIRECCION"));
                    movilFromFile.add(json_data.getString("NUMERO_MOVIL"));
                    addedTimeFromFile.add(json_data.getString("ADDED_TIME_STAMP"));
                    upDateTimeFromFile.add(json_data.getString("UPDATED_TIME_STAMP"));


                }
                // Insertamos los nuevos campos
                for (int j=0; j<nombreFromFile.size() ;j++){
                    sqLiteDatos.InsertRecord(
                            ImageFromFile.get(j) ,
                            nombreFromFile.get(j),
                            apellidoFromFile.get(j),
                            mailFromFile.get(j),
                            direccionFromFile.get(j),
                            movilFromFile.get(j),
                            addedTimeFromFile.get(j),
                            upDateTimeFromFile.get(j));
                }

                f_in.close(); // Cerramos el archivo
                mostrarFragmentos(); // Cargamos de nuevo la vista con los datos importados
                Context context = getApplicationContext();
                CharSequence text = "Datos importados " + ruta;
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            } catch (Exception e) {
                Context context = getApplicationContext();
                CharSequence text = "No se importaron los datos";
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                e.printStackTrace();
            }
        }
    }
    // Revision de permisos para APIs superiores a 23
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_IMPORT:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    importaFileJson();
                    importaFileCnt();
                }
                break;
            case STORAGE_EXPORT:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportarContatosJson();
                    exportarContatosCNT();
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void transferId(String id) {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Intent intent = new Intent(this, DetallesActivity.class);
            intent.putExtra("detailID", id);
            startActivity(intent);

        } else {
            Intent intent = new Intent(this, DetallesActivity.class);
            intent.putExtra("detailID", id);
            startActivity(intent);
        }
    }
}