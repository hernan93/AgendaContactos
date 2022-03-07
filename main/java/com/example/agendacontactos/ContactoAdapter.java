package com.example.agendacontactos;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.HolderContact> {

    Context context;
    ArrayList<Contacto> ArrayListContacto;
    SqLiteDatos sqLiteDatos;
    private OnClickAdapterListener listener;

    public ContactoAdapter(Context context, ArrayList<Contacto> arrayListContacto, OnClickAdapterListener listener) {
        this.context = context;
        this.ArrayListContacto = arrayListContacto;
        this.listener = listener;
        sqLiteDatos = new SqLiteDatos(context);
    }


    @Override
    public HolderContact onCreateViewHolder( ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row, parent, false);
        return new HolderContact(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderContact holder, int position) {
    Contacto modeloContacto = ArrayListContacto.get(position);

    String id = modeloContacto.getId();
    String image = modeloContacto.getImage();
    String nombre = modeloContacto.getNombre();
    String apellido = modeloContacto.getApellido();
    String email = modeloContacto.getEmail();
    String direccion = modeloContacto.getDireccion();
    String numero_movil = modeloContacto.getNumeroMovil();

    if(image.equals("null"))
    {
        holder.perfil_imagen.setImageResource(android.R.drawable.btn_star);
    }
    else {
        holder.perfil_imagen.setImageURI(Uri.parse(image));
    }
    holder.Name.setText(nombre +" "+ apellido);
    holder.phone.setText(numero_movil);
    Character letra = nombre.charAt(1);
    holder.tv_Inicial.setText(letra.toString().toUpperCase());
    OvalShape ovalShape = new OvalShape();
    ShapeDrawable shapeDrawable = new ShapeDrawable(ovalShape);
    shapeDrawable.getPaint().setColor(ContextCompat.getColor(context, android.R.color.holo_orange_light));
    holder.tv_Inicial.setBackground(shapeDrawable);


    holder.itemView.setOnClickListener(v -> {
        if (listener != null)
        {
            listener.DataTransfer(id);
        }
    });

    holder.MoreBtn.setOnClickListener(v -> {
        ShowMore(
                ""+position,
                ""+ id,
                ""+image,
                ""+nombre,
                ""+apellido,
                ""+email,
                ""+direccion,
                ""+numero_movil
        );

    });

    }

    private void ShowMore ( String position, String id, String image, String nombre, String apellido,
                            String email, String direccion, String numero_movil) {


        String [] options = {"Editar", "Eliminar"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setItems(options, (dialogInterface, i) -> {

            switch (i){
                case 0:

                    Intent intent = new Intent(context, Activity_agregar_contacto.class);
                    intent.putExtra("ID", id);
                    intent.putExtra("IMAGE", image);
                    intent.putExtra("NOMBRE", nombre);
                    intent.putExtra("APELLIDO", apellido);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("DIRECCION", direccion);
                    intent.putExtra("NUMERO_MOVIL", numero_movil);
                    intent.putExtra("isEditMode", true);

                    context.startActivity(intent);
                    break;

                case 1:
                    sqLiteDatos.DeleteData(id);
                    ((MainActivity)context).onResume();
            }
        }).show();
    }

    @Override
    public int getItemCount() {
        if (ArrayListContacto.size() > 0){
            return ArrayListContacto.size();
        }
        return 0;
    }


    static class HolderContact extends RecyclerView.ViewHolder {

        ImageView perfil_imagen;
        TextView Name, phone, tv_Inicial;
        ImageButton MoreBtn;


        public HolderContact (@NonNull View itemView) {
            super(itemView);

            perfil_imagen = itemView.findViewById(R.id.imageViewrow);
            Name = itemView.findViewById(R.id.row_name);
            phone = itemView.findViewById(R.id.row_phone_number);
            MoreBtn = itemView.findViewById(R.id.row_moreBtn);
            tv_Inicial = itemView.findViewById(R.id.TV_Inicial);
        }
    }
}
