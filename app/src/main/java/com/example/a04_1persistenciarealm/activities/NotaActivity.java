package com.example.a04_1persistenciarealm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a04_1persistenciarealm.R;
import com.example.a04_1persistenciarealm.adapters.NotaAdapter;
import com.example.a04_1persistenciarealm.models.Nota;
import com.example.a04_1persistenciarealm.models.Pizarra;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class NotaActivity extends AppCompatActivity
        implements RealmChangeListener<Pizarra> //En este caso lo implementamos a un objeto
{

    private FloatingActionButton fabNota;
    private ListView listView;

    private NotaAdapter adapter;

    private RealmList<Nota> lstNota;
    private Realm realm;

    private int pizarraId;
    private Pizarra pizarra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nota);

        initDB();
        initControls();

        if (getIntent().getExtras() != null) {
            pizarraId = getIntent().getExtras().getInt("id");
        }

        //Obteniendo las notas de la pizarra
        obtenerPizarraPorID(pizarraId);
        obtenerNotaPorPizarra();

        //Cambiando el titulo del activity
        this.setTitle(pizarra.getTitulo());

        //Listener para que refresque la pizarra cuando se agregue una nota
        pizarra.addChangeListener(this);

        //Iniciando el adaptador
        adapter = new NotaAdapter(this, R.layout.list_view_nota_item, lstNota);
        listView.setAdapter(adapter);

        //Dando acción al botón
        fabNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarAlertaParaCrearNota("Crear nota");
            }
        });

        //Anexando el menu contextual
        registerForContextMenu(listView);
    }

    //Obtengo la pizarra para obtener todas las notas de la pizarra
    private void obtenerPizarraPorID(int id) {
        pizarra = realm.where(Pizarra.class).equalTo("id", id).findFirst();
    }

    private void obtenerNotaPorPizarra(){
        lstNota = pizarra.getLstNota();
    }

    private void crearNota(final String descripcion) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Nota no = new Nota(descripcion);
                realm.copyToRealm(no);
                //Agregando la relación con la pizarra
                pizarra.getLstNota().add(no);
            }
        });
    }

    private void borrarNotaPorId(Nota nota) {
        realm.beginTransaction();
        nota.deleteFromRealm();
        realm.commitTransaction();
    }

    private void borrarTodoPorPizarra() {
        realm.beginTransaction();
        pizarra.getLstNota().deleteAllFromRealm();
        realm.commitTransaction();
    }

    private void actualizarNotaPorId(String descripcion, Nota nota){
        realm.beginTransaction();
        nota.setDescripcion(descripcion);
        realm.copyToRealmOrUpdate(nota);
        realm.commitTransaction();
    }

    //Creando el popup para crear nueva Nota
    private void mostrarAlertaParaCrearNota(String titulo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (titulo != null) builder.setTitle(titulo);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_nota, null);
        builder.setView(viewInflated);

        final EditText txtDescripcion = viewInflated.findViewById(R.id.txtNotaDescripcion);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String notaDescripcion = txtDescripcion.getText().toString().trim();
                if (notaDescripcion.length() > 0)
                    crearNota(notaDescripcion);
                else
                    Toast.makeText(getApplicationContext(),
                            "Debe ingresar una descripcion", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Creando el popup para actualizar Nota
    private void mostrarAlertaParaActualizarNota(String titulo, final Nota nota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (titulo != null) builder.setTitle(titulo);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_crear_nota, null);
        builder.setView(viewInflated);

        final EditText txtDescripcion = viewInflated.findViewById(R.id.txtNotaDescripcion);
        txtDescripcion.setText(nota.getDescripcion());

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String notaDescripcion = txtDescripcion.getText().toString().trim();
                if (notaDescripcion.length() == 0)
                    Toast.makeText(getApplicationContext(),
                            "La descripcion no puede ser vacía", Toast.LENGTH_LONG).show();
                else if(notaDescripcion.equals(nota.getDescripcion()))
                    Toast.makeText(getApplicationContext(),
                            "Debe ingresar una nueva descripcion", Toast.LENGTH_LONG).show();
                else
                    actualizarNotaPorId(notaDescripcion, nota);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Creando el menu de opciones de la barra de tareas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pizarra, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Cuando se selecciona alguna opción del menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnuEliminarTodo:
                borrarTodoPorPizarra();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Creando el menu contextual
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(lstNota.get(info.position).getDescripcion());
        getMenuInflater().inflate(R.menu.context_menu_pizarra, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.menuBorrar:
                borrarNotaPorId(lstNota.get(info.position));
                return true;
            case R.id.menuEditar:
                mostrarAlertaParaActualizarNota("Actualizar nota", lstNota.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void initDB() {
        realm = Realm.getDefaultInstance();
    }

    private void initControls() {
        fabNota = findViewById(R.id.fabNota);
        listView = findViewById(R.id.listViewNota);
    }

    @Override
    public void onChange(Pizarra pizarra) {
        adapter.notifyDataSetChanged();
    }
}
