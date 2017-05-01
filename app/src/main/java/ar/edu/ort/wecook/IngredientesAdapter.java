package ar.edu.ort.wecook;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 41836435 on 06/05/2016.
 */
public class IngredientesAdapter extends BaseAdapter {

    ArrayList<Ingrediente> ingredientes;
    Context context;


    public IngredientesAdapter(Context context, ArrayList<Ingrediente> ingredientes){
        this.context = context;
        this.ingredientes = ingredientes;

    }
    @Override
    public int getCount() {
        return ingredientes.size();
    }

    @Override
    public Object getItem(int i) {
        return ingredientes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listviewingredientes, viewGroup, false);
        }

        TextView ingrediente = (TextView)view.findViewById(R.id.ingrediente);


        Ingrediente i =  ingredientes.get(position);
        ingrediente.setText(i.getNombre());




        return view;
    }


}





