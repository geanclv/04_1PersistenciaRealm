package com.example.a04_1persistenciarealm.app;

import android.app.Application;

import com.example.a04_1persistenciarealm.models.Nota;
import com.example.a04_1persistenciarealm.models.Pizarra;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class MyApplication extends Application {
    //Extendiendo la clase Application hacemos que esta clase se ejecute ni bien inicia la aplicación
    //esto permite que se ejecute antes que el MainActivity. Además de esto debemos escribir
    //en el Manifest la propiedad "name"

    public static AtomicInteger PizarraID = new AtomicInteger();
    public static AtomicInteger NotaID = new AtomicInteger();

    //Al sobreescribir este método podemos realizar configuraciones iniciales de la app
    @Override
    public void onCreate() {
        super.onCreate();

        setUpRealmConfig();

        //Configurando la BD
        Realm realm = Realm.getDefaultInstance();

        //Obteniendo los ID de las clases
        PizarraID = getIdByTable(realm, Pizarra.class);
        NotaID = getIdByTable(realm, Nota.class);

        //Cerrando la BD
        realm.close();
    }

    //Configuracion de Realm
    private void setUpRealmConfig(){
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    //En este método se obtiene el último ID creado
    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0)
                ? new AtomicInteger(results.max("id").intValue())
                : new AtomicInteger();
    }
}
