package com.example.a04_1persistenciarealm.models;

import com.example.a04_1persistenciarealm.app.MyApplication;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Nota extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String descripcion;
    @Required
    private Date fecCreacion;

    //Realm necesita un construtor vacío
    public Nota(){}

    public Nota(String descripcion) {
        this.id = MyApplication.NotaID.incrementAndGet();
        this.descripcion = descripcion;
        this.fecCreacion = new Date();
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecCreacion() {
        return fecCreacion;
    }
}
