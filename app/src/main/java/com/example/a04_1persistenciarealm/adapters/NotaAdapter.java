package com.example.a04_1persistenciarealm.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.a04_1persistenciarealm.R;
import com.example.a04_1persistenciarealm.models.Nota;

import java.text.SimpleDateFormat;
import java.util.List;

public class NotaAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<Nota> lstNota;

    public NotaAdapter(Context context, int layout, List<Nota> lstNota) {
        this.context = context;
        this.layout = layout;
        this.lstNota = lstNota;
    }

    @Override
    public int getCount() {
        return lstNota.size();
    }

    @Override
    public Nota getItem(int position) {
        return lstNota.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            viewHolder = new ViewHolder();
            viewHolder.lblNotaDescripcion = convertView.findViewById(R.id.lblNotaDescripcion);
            viewHolder.lblNotaFecCreacion = convertView.findViewById(R.id.lblNotaFecCreacion);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Nota nota = lstNota.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fecha = sdf.format(nota.getFecCreacion());
        viewHolder.lblNotaDescripcion.setText(nota.getDescripcion());
        viewHolder.lblNotaFecCreacion.setText(fecha);
        return convertView;
    }

    public class ViewHolder {
        TextView lblNotaDescripcion;
        TextView lblNotaFecCreacion;
    }
}
