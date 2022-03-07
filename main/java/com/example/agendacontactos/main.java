package com.example.agendacontactos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link main#newInstance} factory method to
 * create an instance of this fragment.
 */
public class main extends Fragment implements OnClickAdapterListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    RecyclerView rv_contactos;
    SqLiteDatos sql_datos;

    String orderByNewest = "ADDED_TIME_STAMP" + " DESC";
    String currentOrderState = orderByNewest;

    private OnClickMainListener listener;

    public main() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment main.
     */
    // TODO: Rename and change types and number of parameters
    public static main newInstance(String param1, String param2) {
        main fragment = new main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_contactos = view.findViewById(R.id.mainRV);
        init(view);
    }

    public void init(View view) {

        view.findViewById(R.id.fab).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Activity_agregar_contacto.class);
            intent.putExtra("isEditMode",false);
            startActivity(intent);
        });

        sql_datos = new SqLiteDatos(getContext());
        cargarContactos(orderByNewest);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
    public void cargarContactos(String orderBy) {
        currentOrderState = orderBy;
        ContactoAdapter contactoAdapter = new ContactoAdapter(getContext(), sql_datos.GetAllContacts(orderBy), this);
        rv_contactos.setAdapter(contactoAdapter);
    }

    public void SearchDatabase(String query) {
        ContactoAdapter contactoAdapter = new ContactoAdapter(getContext(), sql_datos.SearchContacts(query), this);
        rv_contactos.setAdapter(contactoAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            listener = (OnClickMainListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " Debe Implementar OnListItemSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener =null;

    }
        @Override
    public void DataTransfer(String id) {
      listener.transferId(id);
    }
}