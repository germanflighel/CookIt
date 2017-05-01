package ar.edu.ort.wecook;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import android.view.View;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ListaRecetas extends AppCompatActivity {


    ArrayList<Paso> Parseado;
    Receta miRecetaParseada;
    String url;
    private RecyclerView miRecyclerView;
    private ArrayList<Receta> recetasTraidas;
    RVAdapter miAdaptador;

    static int idReceta;
    static Context miContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        miContext =getApplicationContext();

        recetasTraidas= (ArrayList<Receta>)getIntent().getSerializableExtra("lista");
        Log.d("recetas", recetasTraidas.toString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_recetas);


        miRecyclerView= (RecyclerView)findViewById(R.id.recyclerView);

        LinearLayoutManager miManagerLinear = new LinearLayoutManager(this);
        miRecyclerView.setLayoutManager(miManagerLinear);
        miRecyclerView.setHasFixedSize(true);
        iniciarAdapter();


    }

    private void iniciarAdapter(){
        miAdaptador = new RVAdapter(recetasTraidas);
        miRecyclerView.setAdapter(miAdaptador);
    }




    public void cocinar(View vista){

        Log.d("Cocinar","El id del CardView y La Receta es:" + vista.getId());
        url= "http://wecook.hol.es/traeunareceta.php?idReceta="+vista.getId();
        idReceta = vista.getId();
        new TraeMiReceta().execute(url);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    private class TraeMiReceta extends AsyncTask<String, Void, Receta> {

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(Receta miReceta) {
            Log.d("TraeMiReceta", "PostExecute");
            super.onPostExecute(miReceta);

            Intent miIntent = new Intent(ListaRecetas.this,DetalleReceta.class);
            miIntent.putExtra("lista", miReceta);


            startActivity(miIntent);


        }

        protected Receta doInBackground(String... params) {
            String url = params[0];
            Log.d("TraeMiReceta", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                Log.d("TraeMiReceta", paraParsear);
                miRecetaParseada = parsearResultado(paraParsear);
                Log.d("TraeMiReceta", "Parseo");
                return miRecetaParseada;


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());
                return new Receta();
            }
        }

        Receta parsearResultado(String JSONstr) throws JSONException {
            Receta miReceta = new Receta();
            ArrayList<Ingrediente> misIngredientes = new ArrayList<>();
            Ingrediente miIngrediente;
            JSONArray json = new JSONArray(JSONstr);
            for (int i = 0; i < json.length(); i++) {
                if(i==0) {
                    JSONObject jsonResultado = json.getJSONObject(i);
                    String nombreReceta = jsonResultado.getString("nombre");
                    Log.d("TraeMiReceta", nombreReceta);
                    int idReceta = jsonResultado.getInt("idRecetas");
                    String imagenreceta = jsonResultado.getString("rutaMedia");
                    String duracion = jsonResultado.getString("duracion");

                    miReceta.setNombre(nombreReceta);
                    miReceta.setIdReceta(idReceta);
                    miReceta.setImagen(imagenreceta);
                    miReceta.setDuracion(duracion);


                    Log.d("TraeMiReceta", "El Nombre de miReceta es: " + miReceta.getNombre());
                }
                else if(i>0){

                    miIngrediente= new Ingrediente();
                    JSONObject jsonResultado = json.getJSONObject(i);

                    String nombreIngrediente = jsonResultado.getString("Nombre");
                    String idIngrediente = jsonResultado.getString("idIngredientes");
                    //String Unidad = jsonResultado.getString("Unidad");
                    //int cantidad = jsonResultado.getInt("Cantidad");

                    miIngrediente.Id= idIngrediente;
                    miIngrediente.setNombre(nombreIngrediente);
                    //Log.d("TraeMiReceta", ""+cantidad);
                    miIngrediente.setCantidad(0);
                    miIngrediente.setUnidad("Kg");

                    misIngredientes.add(miIngrediente);

                    Log.d("TraeMiReceta", miIngrediente.getNombre());
                }

            }
            miReceta.setIngredientes(misIngredientes);
            return miReceta;
        }
    }

}
