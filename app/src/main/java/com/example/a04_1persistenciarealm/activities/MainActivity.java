package com.example.a04_1persistenciarealm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a04_1persistenciarealm.R;
import com.example.a04_1persistenciarealm.adapters.MyAdapter;
import com.example.a04_1persistenciarealm.models.Pizarra;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity
        implements RealmChangeListener<RealmResults<Pizarra>>, //En este caso implementamos a una lista
        AdapterView.OnItemClickListener // interfaz para ir de un activity a otro al hacerle clic
{

    private FloatingActionButton fabAgregarPizarra;
    private ListView listViewPizarra;

    private MyAdapter adapter;
    private RealmResults<Pizarra> lstPizarra;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initDBRealmConfig();
        initComponets();
        listarPizarra();

        //Listener que permitirá refrescar el dato en base a sus hijos
        //Listener Realm: Forma 1
        /*lstPizarra.addChangeListener(new RealmChangeListener<RealmResults<Pizarra>>() {
            @Override
            public void onChange(RealmResults<Pizarra> pizarras) {

            }
        });*/
        //Listener Realm: Forma 2, se implementa la interfaz RealmChangeListener
        lstPizarra.addChangeListener(this);

        adapter = new MyAdapter(this, R.layout.list_view_pizarra_item, lstPizarra);
        listViewPizarra.setAdapter(adapter);

        //Añexando el evento onItemClick al listView
        listViewPizarra.setOnItemClickListener(this);

        fabAgregarPizarra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAlertParaCrearPizarra("Nueva Pizarra", "Ingrese un nombre");
            }
        });

        //Añadiendo el menú contextual
        registerForContextMenu(listViewPizarra);
    }

    //Borrando todas las pizarras
    private void borrarTodo(){
        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();
    }

    //Borrando una pizarra
    private void borrarPizarraPorId(Pizarra pizarra){
        realm.beginTransaction();
        pizarra.deleteFromRealm();
        realm.commitTransaction();
    }

    //Creando pizarra
    private void crearNuevaPizarra(String titulo) {
        //Se debe iniciar la transacción Realm
        realm.beginTransaction();
        Pizarra pizarra = new Pizarra(titulo);
        realm.copyToRealm(pizarra);
        //Cerrando la transaccion
        realm.commitTransaction();
    }

    //Obteniendo las pizarras
    private void listarPizarra() {
        lstPizarra = realm.where(Pizarra.class).findAll();
    }

    //Actualizando pizarra
    private void actualizarPizarraPorId(String titulo, Pizarra pizarra){
        realm.beginTransaction();
        pizarra.setTitulo(titulo);
        realm.copyToRealmOrUpdate(pizarra);
        realm.commitTransaction();
    }

    //Creando un dialogo para el ingreso de datos -- Crear Pizarra
    private void mostrarAlertParaCrearPizarra(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Para crear este popup creamos un layout llamado dialog_crear_pizarra
        if (titulo != null) builder.setTitle(titulo);
        if (mensaje != null) builder.setMessage(mensaje);

        //Inflando la vista
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_pizarra, null);
        builder.setView(viewInflated);

        //Capturando el valor del layout
        final EditText txtPizarraTitulo = viewInflated.findViewById(R.id.txtPizarraTitulo);

        //Configurando la acción del botón del popup
        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tituloPizarra = txtPizarraTitulo.getText().toString().trim();

                if (tituloPizarra.length() > 0) {
                    crearNuevaPizarra(tituloPizarra);
                } else {
                    Toast.makeText(getApplicationContext(), "No ha escrito un titulo",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //Mostrando el popup
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Creando un dialogo para la actualización de datos - Actualizando pizarra
    private void mostrarAlertParaActualizarPizarra(String titulo, String mensaje,
                                                   final Pizarra pizarra) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Para crear este popup creamos un layout llamado dialog_crear_pizarra
        if (titulo != null) builder.setTitle(titulo);
        if (mensaje != null) builder.setMessage(mensaje);

        //Inflando la vista
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_pizarra, null);
        builder.setView(viewInflated);

        //Capturando el valor del layout
        final EditText txtPizarraTitulo = viewInflated.findViewById(R.id.txtPizarraTitulo);
        txtPizarraTitulo.setText(pizarra.getTitulo());

        //Configurando la acción del botón del popup
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String tituloPizarra = txtPizarraTitulo.getText().toString().trim();

                if (tituloPizarra.length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            "El titulo es obligatorio para editar la pizarra",
                            Toast.LENGTH_LONG).show();
                } else if (tituloPizarra.equals(pizarra.getTitulo())) {
                    Toast.makeText(getApplicationContext(), "El titulo no ha cambiado",
                            Toast.LENGTH_LONG).show();
                } else {
                    actualizarPizarraPorId(tituloPizarra, pizarra);
                }
            }
        });

        //Mostrando el popup
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Menu en la parte superior de la barra de opciones
    //Debemos haber creado el layout del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pizarra, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Controlando los clic en las opciones de menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case  R.id.mnuEliminarTodo:
                borrarTodo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Creando menu contextual para editar los items
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(lstPizarra.get(info.position).getTitulo());
        getMenuInflater().inflate(R.menu.context_menu_pizarra, menu);
    }

    //Controlando los clic en el menu contextual
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.menuBorrar:
                borrarPizarraPorId(lstPizarra.get(info.position));
                return true;
            case R.id.menuEditar:
                mostrarAlertParaActualizarPizarra("Actualizar pizarra",
                        "Cambie el titulo", lstPizarra.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //Listener Realm: Complemento de la forma 2
    // Si existiera la necesidad de tener varios Listener, el parametro de entrada debería
    // ser general e internamente el método debería validar a qué objeto debe atender,
    // o utilizamos la Forma 1
    @Override
    public void onChange(RealmResults<Pizarra> pizarras) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(MainActivity.this, NotaActivity.class);
        intent.putExtra("id", lstPizarra.get(position).getId());
        startActivity(intent);
    }

    private void initComponets() {
        fabAgregarPizarra = findViewById(R.id.fabAgregarPizarra);
        listViewPizarra = findViewById(R.id.listView);
    }

    private void initDBRealmConfig() {
        realm = Realm.getDefaultInstance();
    }
}
