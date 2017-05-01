package ar.edu.ort.wecook;

import android.content.ClipData;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Sherminator3000 on 10/07/2016.
 */
public class RecetasTopAdapter extends BaseAdapter {
    private ArrayList<Receta> _recetas;
    private Context miContext;


    public RecetasTopAdapter(Context context, ArrayList<Receta> recetas){
        this.miContext = context;
        this._recetas = recetas;

    }
    @Override
    public int getCount() {
        return _recetas.size();
    }

    @Override
    public Receta getItem(int position) {
        return _recetas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Log.d("getView", "ArrancaGetView");

        LayoutInflater inflater = (LayoutInflater) miContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.listviewirecetastop, parent, false);
        Log.d("getView", "Infle View");

        TextView miReceta = (TextView)view.findViewById(R.id.mireceta);
        Receta i =  _recetas.get(position);
        miReceta.setText(i.getNombre());
        Log.d("getView", "Puse Nombre de Receta");
        TextView miRating= (TextView)view.findViewById(R.id.miRating);
        miRating.setText(i.getRating() + "★");





        return view;
    }

}
