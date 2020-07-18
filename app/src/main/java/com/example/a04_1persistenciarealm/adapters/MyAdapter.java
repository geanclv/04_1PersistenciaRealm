package com.example.a04_1persistenciarealm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a04_1persistenciarealm.R;
import com.example.a04_1persistenciarealm.models.Pizarra;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MyAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Pizarra> lstPizarra;

    public MyAdapter(Context context, int layout, List<Pizarra> lstPizarra) {
        this.context = context;
        this.layout = layout;
        this.lstPizarra = lstPizarra;
    }

    @Override
    public int getCount() {
        return lstPizarra.size();
    }

    @Override
    public Object getItem(int position) {
        return lstPizarra.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.lblPizarraTitulo = convertView.findViewById(R.id.lblPizarraTitulo);
            viewHolder.lblPizarraNotas = convertView.findViewById(R.id.lblPizarraNotas);
            viewHolder.lblPizarraFecCreacion = convertView.findViewById(R.id.lblPizarraFecCreacion);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Pizarra pizarra = lstPizarra.get(position);
        int nroNotas = pizarra.getLstNota().size();
        String textoNota = (nroNotas == 1) ? nroNotas + " nota" : nroNotas + " notas";
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fecFormateada = df.format(pizarra.getFecCreacion());
        viewHolder.lblPizarraTitulo.setText(pizarra.getTitulo());
        viewHolder.lblPizarraNotas.setText(textoNota);
        viewHolder.lblPizarraFecCreacion.setText(fecFormateada);

        return convertView;
    }

    public class ViewHolder {
        public TextView lblPizarraTitulo;
        public TextView lblPizarraNotas;
        public TextView lblPizarraFecCreacion;
    }
}
