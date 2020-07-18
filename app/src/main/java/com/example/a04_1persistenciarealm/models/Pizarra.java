package com.example.a04_1persistenciarealm.models;

import com.example.a04_1persistenciarealm.app.MyApplication;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Pizarra extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String titulo;
    @Required
    private Date fecCreacion;
    //Relacionando Pizarra con Nota
    private RealmList<Nota> lstNota;

    //Realm necesita un construtor vacío
    public Pizarra(){}

    public Pizarra(String titulo){
        this.id = MyApplication.PizarraID.incrementAndGet();
        this.titulo = titulo;
        this.fecCreacion = new Date();
        this.lstNota = new RealmList<Nota>();
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Date getFecCreacion() {
        return fecCreacion;
    }

    public RealmList<Nota> getLstNota() {
        return lstNota;
    }
}
