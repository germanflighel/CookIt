package ar.edu.ort.wecook;

import android.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import android.util.Log;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import com.squareup.okhttp.Response;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class elegirIngredientes extends AppCompatActivity {

    String url;
    Context miContext;

    ArrayList<Ingrediente> ingredientesDeBaseDeDatosConId;

    private int cont = 0;
    ArrayList<Ingrediente> ingredientesingresados;
    ArrayList<Receta> recetasTopRating, recetasFavoritas;
    AutoCompleteTextView atxt;
    ListView listVW,listVWRecetas,listVWRecetasFavoritas;
    IngredientesAdapter adapter;
    RecetasTopAdapter miAdapter;
    RecetasFavoritasAdapter miOtroAdapter;
    Receta miRecetaParseada;


    String[] ingredientes;
    ArrayList<String> ingredientesalist;
    static int idUserLogged;
    static ArrayList<Receta> _recetasFavoritas = new ArrayList<Receta>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("break", "OnCreate");

        try {
            idUserLogged = this.getIntent().getExtras().getInt("idUserLogged");
        }catch (Exception e){
            idUserLogged= 784;
        }

        miContext=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_ingredientes);

        url = "http://wecook.hol.es/traeIngredientes.php";

        ConsultarIngredientes();


        // AUTOCOMPLETE
        atxt = (AutoCompleteTextView)findViewById(R.id.txtingrediente);

        Log.d("test", "arr: " + Arrays.toString(ingredientes));
        atxt.setInputType(1);
        atxt.setThreshold(1);
        atxt.setDropDownHeight(-1);
        atxt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        Log.d("break", "Hizo el AutoComplete");


        // LISTVIEW INGREDIENTES INGRESADOS
        ingredientesingresados = new ArrayList<>();
        listVW = (ListView) findViewById(R.id.listVW);
        adapter = new IngredientesAdapter(this, ingredientesingresados);
        Log.d("break", "Instancio el Adapter");
        listVW.setAdapter(adapter);
        Log.d("break", "Seteo el Adapter");


        listVW.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                Log.d("break", "Listener");
                Ingrediente map = ingredientesingresados.get(position);
                Log.d("break", "ListenerPosition");
                //int id= Integer.parseInt(map.Id);
                Dialog(position);
                Log.d("break", "Termino el Dialog");
                return false;
            }
        });

        // LISTVIEW RECETAS TOP

        recetasTopRating = new ArrayList<>();
        listVWRecetas =(ListView)findViewById(R.id.listVWTopRecetas);
        url = "http://wecook.hol.es/traeTopRecetasPorRating.php";
        ConsultarRecetasTop();

        listVWRecetas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Receta recetaSeleccionada = (Receta)listVWRecetas.getItemAtPosition(position);
                Log.d("IdReceta", ""+recetaSeleccionada.getIdReceta());
                url = "http://wecook.hol.es/traeunareceta.php?idReceta="+recetaSeleccionada.getIdReceta();
                new TraeMiReceta().execute(url);



            }
        });

        // LISTVIEW RECETAS FAVORITAS

        recetasFavoritas = new ArrayList<>();
        listVWRecetasFavoritas =(ListView)findViewById(R.id.listVWRecetasFav);
        listVWRecetasFavoritas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Receta recetaSeleccionada = (Receta)listVWRecetasFavoritas.getItemAtPosition(position);
                Log.d("IdReceta", ""+recetaSeleccionada.getIdReceta());
                url = "http://wecook.hol.es/traeunareceta.php?idReceta="+recetaSeleccionada.getIdReceta();
                new TraeMiReceta().execute(url);



            }
        });
        url = "http://wecook.hol.es/recetasFavoritas.php?idUser="+ idUserLogged;
        recetasFavoritas();







    }




    public void Dialog (final int id)    {
        Log.d("Dialog", "Empezo el Dialog");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        ingredientesingresados.remove(id);
                        adapter.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Remover el ingrediente de la lista?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void recetasFavoritas(){

        new TraeRecetasFavoritas().execute(url);
    }

    public void ConsultarIngredientes()
    {
        new TraeIngredientes().execute(url);

    }

    public void ConsultarRecetasTop()
    {
        new TraeTopRecetasPorRating().execute(url);

    }
    public void recetaAleatoria(View vista){
        url= "http://wecook.hol.es/recetarandom.php";
        Log.d("TraeRecetaAleatoria", url);
        Log.d("TraeRecetaAleatoria","Llamo al AsyncTask");
        new TraeRecetaAleatoria().execute(url);
    }

    public void buscarRecetas(View vista){

        url= "http://wecook.hol.es/listarecetas.php?";
        ArrayList<String> misParametros= new ArrayList<>();




        for (int i= 0; i<ingredientesingresados.size();i++){
            for (int j= 0; j<ingredientesDeBaseDeDatosConId.size();j++){
                if (ingredientesingresados.get(i).getNombre().contains(ingredientesDeBaseDeDatosConId.get(j).getNombre())){
                    Log.d("buscarRecetas",ingredientesingresados.get(i).getNombre() + " "+ ingredientesingresados.get(i).getId());
                    ingredientesingresados.set(i, ingredientesDeBaseDeDatosConId.get(j));
                    Log.d("buscarRecetas",ingredientesingresados.get(i).getNombre() + " "+ ingredientesingresados.get(i).getId());

                }
            }
        }


        Log.d("buscarRecetas","Voy a BuscarRecetas");
        Log.d("buscarRecetas", "Ingredientes ingresados vale: "+ingredientesingresados.toString());

        for (int puntero = 0; puntero < ingredientesingresados.size(); puntero++) {
            misParametros.add(ingredientesingresados.get(puntero).getId());
            Log.d("buscarRecetas", "El id del Ingrediente "+ingredientesingresados.get(puntero).getNombre()+" es : " +ingredientesingresados.get(puntero).getId());
            String sumar= "id"+String.valueOf(puntero)+ "=" + ingredientesingresados.get(puntero).getId();
            url+=sumar;
            url+="&";
        }

        Log.d("buscarRecetas", "La url es: "+ url);

        new TraeRecetas().execute(url);


    }




    // BOTON AGREGAR INGREDIENTE
    public void agregaruno (View vista) {
        Log.d("AgregarUno", "comienza agregaruno");
        Ingrediente ingresado = new Ingrediente();
            if (cont < 8 && Arrays.asList(ingredientes).contains(atxt.getText().toString())) {


                ingresado.Nombre = "- " + atxt.getText().toString();
                ingredientesingresados.add(ingresado);
                Log.d("break", ingredientesingresados.toString());


                adapter.notifyDataSetChanged();
                atxt.setText("");
                atxt.requestFocus();

                cont++;


            } else if (cont > 8) {
                Context context = getApplicationContext();
                CharSequence text = "Alcanzo el limite de ingredientes";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else if (!Arrays.asList(ingredientes).contains(atxt.getText().toString())) {
                Context context = getApplicationContext();
                CharSequence text = "Debe seleccionar un ingrediente de la lista";
                int duration = Toast.LENGTH_LONG;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                atxt.setText("");
                atxt.requestFocus();
            }

        }


    private class TraeIngredientes extends AsyncTask<String, Void, ArrayList<Ingrediente>>{

        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Ingrediente> ingredientesArray) {
            Log.d("TraeIngredientes","PostExecute");
            ArrayList<String> listdeBdd = new ArrayList<>();
            super.onPostExecute(ingredientesArray);
            if (!ingredientesArray.isEmpty()) {
                for (int i=0;i<ingredientesArray.size();i++){
                    listdeBdd.add(ingredientesArray.get(i).Nombre.toString());
                }
                ingredientes= new String[listdeBdd.size()];
                for (int posicion = 0;posicion<ingredientes.length;posicion++){
                    ingredientes[posicion]= listdeBdd.get(posicion);
                    Log.d("break", ingredientes[posicion]);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(elegirIngredientes.this, android.R.layout.simple_dropdown_item_1line, ingredientes);

                atxt.setAdapter(adapter);
                atxt.setHint("Ingrediente");
                atxt.setInputType(1);
                atxt.setThreshold(1);
                atxt.setDropDownHeight(-1);
                atxt.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                ingredientesDeBaseDeDatosConId=ingredientesArray;
                Log.d("break", "termina PostExecute");

            }


        }


        @Override
        protected ArrayList<Ingrediente> doInBackground(String... params) {
            String url = params[0];


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                ArrayList<Ingrediente> Parseado = parsearResultado(paraParsear);
                Log.d("TraeIngredientes", "Parseo");
                return Parseado;                                        // Convierto el resultado en ArrayList<Ingredientes>


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());                          // Error de Network o al parsear JSON
                return new ArrayList<Ingrediente>();
            }
        }

        ArrayList<Ingrediente> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Ingrediente> ingredientesArray = new ArrayList<>();
            JSONArray json = new JSONArray(JSONstr);                    // Convierto el String recibido a JSONObject
            for (int i=0; i<json.length(); i++) {
                // Recorro los resultados recibidos
                JSONObject jsonResultado = json.getJSONObject(i);
                String nombreIngrediente = jsonResultado.getString("Nombre");                 // Obtiene nombre
                String idIngrediente = jsonResultado.getString("idIngredientes");            // Obtiene id

                Ingrediente ing = new Ingrediente();
                ing.Nombre= nombreIngrediente;
                ing.Id = idIngrediente;                                                     // Creo nueva instancia de ingrediente
                ingredientesArray.add(ing);                                                 // Agrego objeto ing al array list
                Log.d("idIngrediente","Ingrediente:" +ing.Nombre+ " " + ing.Id);


            }

            return ingredientesArray;
        }

    }


    private class TraeRecetas extends AsyncTask<String, Void,ArrayList<Receta>>{

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(ArrayList<Receta> recetasRecibidas){
            Log.d("TraeRecetas","PostExecute");
            super.onPostExecute(recetasRecibidas);

            if (!recetasRecibidas.isEmpty()) {
                Intent miIntent = new Intent(elegirIngredientes.this, ListaRecetas.class);
                miIntent.putExtra("lista", recetasRecibidas);

                Log.d("TraeRecetas", "Antes de Mandar: " + recetasRecibidas.toString());
                startActivity(miIntent);
            }
            else {
                Toast.makeText(miContext, "No hay recetas con los ingrendientes seleccionados", Toast.LENGTH_SHORT).show();
            }



        }

        protected ArrayList<Receta> doInBackground(String... params) {
            String url = params[0];
            Log.d("break", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                Log.d("TraeRecetas", paraParsear);
                ArrayList<Receta> Parseado = parsearResultado(paraParsear);     // Convierto el resultado en ArrayList<Ingredientes>
                Log.d("TraeRecetas", "Parseo");
                return Parseado;


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());                                 // Error de Network o al parsear JSON
                return new ArrayList<Receta>();
            }
        }

        ArrayList<Receta> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Receta> recetasArray = new ArrayList<>();
            JSONArray json = new JSONArray(JSONstr);                        // Convierto el String recibido a JSONObject
            for (int i=0; i<json.length(); i++) {


                    JSONObject jsonResultado = json.getJSONObject(i);
                    String nombreReceta = jsonResultado.getString("nombre");
                    Log.d("TraeRecetas", nombreReceta);
                    int idReceta = jsonResultado.getInt("idRecetas");
                    String imagenreceta = jsonResultado.getString("rutaMedia");
                    double ratingReceta = jsonResultado.getDouble("rating");

                    String IntduracionReceta =(jsonResultado.getString("duracion"));

                    String duracionReceta = String.valueOf(IntduracionReceta);

                    Receta miReceta = new Receta();
                    miReceta.Nombre = nombreReceta.trim();
                    miReceta.IdReceta = idReceta;
                    miReceta.imagen = imagenreceta;
                    miReceta.setDuracion(duracionReceta);
                    miReceta.Rating = ratingReceta;

                    recetasArray.add(miReceta);
                    Log.d("TraeRecetas", "Receta:" + miReceta.Nombre + " " + miReceta.IdReceta + " El rating es " + miReceta.Rating + " La ruta de la Imagen es " + miReceta.getImagen());
                    Log.d("TraeRecetas", "Duracion"+ miReceta.getDuracion());



            }

            return recetasArray;
        }

    }

    private class TraeTopRecetasPorRating extends AsyncTask<String, Void, ArrayList<Receta>>{

        private OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPostExecute(ArrayList<Receta> recetasArray) {
            Log.d("TraeTopRecetasPorRating","PostExecute");
            super.onPostExecute(recetasArray);
            recetasTopRating = recetasArray;
            miAdapter = new RecetasTopAdapter(getApplicationContext(), recetasTopRating);
            listVWRecetas= (ListView)findViewById(R.id.listVWTopRecetas);
            listVWRecetas.setAdapter(miAdapter);
            miAdapter.notifyDataSetChanged();
            Log.d("ListViewRecetasTop", "Seteo el Adapter");
        }


        @Override
        protected ArrayList<Receta> doInBackground(String... params) {
            String url = params[0];


            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                ArrayList<Receta> Parseado = parsearResultado(paraParsear);
                Log.d("TraeTopRecetasPorRating", "Parseo");
                return Parseado;


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());
                return new ArrayList<Receta>();
            }
        }

        ArrayList<Receta> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Receta> recetaArrayList = new ArrayList<>();
            JSONArray json = new JSONArray(JSONstr);
            for (int i=0; i<json.length(); i++) {
                // Recorro los resultados recibidos
                JSONObject jsonResultado = json.getJSONObject(i);
                String nombreIngrediente = jsonResultado.getString("nombre");
                String idIngrediente = jsonResultado.getString("idRecetas");
                Double ratingIngrediente = jsonResultado.getDouble("rating");
                //String duracion = jsonResultado.getString("duracion");


                Receta miReceta = new Receta();
                miReceta.setNombre(nombreIngrediente.trim());
                miReceta.setIdReceta(Integer.valueOf(idIngrediente));
                miReceta.setRating(ratingIngrediente);
                //miReceta.setDuracion(duracion);

                recetaArrayList.add(miReceta);
                Log.d("TraeTopRecetasPorRating","Receta:" +miReceta.getNombre()+ " " + miReceta.getIdReceta()+ " "+ miReceta.getRating());


            }

            return recetaArrayList;
        }

    }


    private class TraeRecetaAleatoria extends AsyncTask<String, Void, Receta>{

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(Receta recetaRecibida){
            Log.d("TraeRecetaAleatoria","PostExecute");
            super.onPostExecute(recetaRecibida);
            Log.d("TraeRecetaAleatoria", "Antes de Mandar: "+ recetaRecibida.getNombre() + " Id: "+ recetaRecibida.getIdReceta());
            Log.d("TraeRecetaAleatoria", "Voy A Traer Pasos");
            url = "http://wecook.hol.es/traeunareceta.php?idReceta="+recetaRecibida.getIdReceta();
            new TraeMiReceta().execute(url);




        }

        protected Receta doInBackground(String... params) {
            String url = params[0];
            Log.d("TraeRecetaAleatoria", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                Log.d("TraeRecetaAleatoria", paraParsear);
                Receta Parseado = parsearResultado(paraParsear);
                Log.d("TraeRecetaAleatoria", "Parseo");
                return Parseado;


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());
                return new Receta();
            }
        }

        Receta parsearResultado(String JSONstr) throws JSONException {

            Receta miReceta = new Receta();
            JSONArray json = new JSONArray(JSONstr);
            for (int i=0; i<json.length(); i++) {

                JSONObject jsonResultado = json.getJSONObject(i);
                String nombreReceta = jsonResultado.getString("nombre");
                int idReceta = jsonResultado.getInt("idRecetas");
                String imagenreceta = jsonResultado.getString("rutaMedia");
                String duracion = jsonResultado.getString("duracion");

                miReceta.Nombre= nombreReceta;
                miReceta.IdReceta = idReceta;
                miReceta.imagen = imagenreceta;
                miReceta.setDuracion(duracion);

                Log.d("TraeRecetaAleatoria","Receta: " +miReceta.Nombre+ " " + " La ruta de la Imagen: " + miReceta.getImagen()+ " El idRecetas: "+ miReceta.getIdReceta());

            }
            return miReceta;
        }
    }


    private class TraePasos extends AsyncTask<String, Void, ArrayList<Paso>> {

        ArrayList<Paso> Parseado;
        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(ArrayList<Paso> pasosRecibidos) {
            Log.d("TraePasos", "PostExecute");
            super.onPostExecute(pasosRecibidos);
            //Collections.reverse(Parseado);
            Intent miIntent =new Intent(elegirIngredientes.this, Stepper.class);
            miIntent.putExtra("lista",Parseado);

            Log.d("TraePasos", "Voy a Iniciar ActivityStepper");
            startActivity(miIntent);


        }

        protected ArrayList<Paso> doInBackground(String... params) {
            String url = params[0];
            Log.d("TraePasos", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                Log.d("TraePasos", paraParsear);
                Parseado = parsearResultado(paraParsear);
                Log.d("TraePasos", "Parseo");
                return Parseado;


            } catch (IOException | JSONException e) {
                return Parseado;
            }
        }

        ArrayList<Paso> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Paso> pasosArray = new ArrayList<>();
            JSONArray json = new JSONArray(JSONstr);
            for (int i = 0; i < json.length(); i++) {

                JSONObject jsonResultado = json.getJSONObject(i);
                String vPaso = jsonResultado.getString("Descripcion");
                String vRutaMedia = jsonResultado.getString("media");
                String vTimer = jsonResultado.getString("timer");

                Paso miPaso= new Paso();
                miPaso.set_Paso(vPaso);
                miPaso.set_RutaMedia(vRutaMedia);
                miPaso.set_TimerString(vTimer);

                Log.d("Paso", "El paso es: "+ miPaso.get_Paso() + " y la ruta media: " + miPaso.get_RutaMedia());
                pasosArray.add(miPaso);

            }
            Log.d("TraePasos",pasosArray.toString());

            return pasosArray;
        }
    }

    private class TraeRecetasFavoritas extends AsyncTask<String, Void,ArrayList<Receta>>{

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(ArrayList<Receta> recetasRecibidas){
            Log.d("TraeRecetasFavoritas","PostExecute");
            super.onPostExecute(recetasRecibidas);

            if (!recetasRecibidas.isEmpty()) {

                recetasFavoritas = recetasRecibidas;
                _recetasFavoritas = recetasRecibidas;
                miOtroAdapter = new RecetasFavoritasAdapter(getApplicationContext(), recetasFavoritas);
                listVWRecetasFavoritas = (ListView)findViewById(R.id.listVWRecetasFav);
                listVWRecetasFavoritas.setAdapter(miOtroAdapter);
                miAdapter.notifyDataSetChanged();
            }
            else {
                Toast.makeText(miContext, "No hay recetas favoritas", Toast.LENGTH_SHORT).show();
            }



        }

        protected ArrayList<Receta> doInBackground(String... params) {
            String url = params[0];
            Log.d("break", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                Log.d("TraeRecetasFavoritas", paraParsear);
                ArrayList<Receta> Parseado = parsearResultado(paraParsear);     // Convierto el resultado en ArrayList<Ingredientes>
                Log.d("TraeRecetasFavoritas", "Parseo");
                return Parseado;


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());                                 // Error de Network o al parsear JSON
                return new ArrayList<Receta>();
            }
        }

        ArrayList<Receta> parsearResultado(String JSONstr) throws JSONException {
            ArrayList<Receta> recetasArray = new ArrayList<>();
            JSONArray json = new JSONArray(JSONstr);                        // Convierto el String recibido a JSONObject
            for (int i=0; i<json.length(); i++) {


                JSONObject jsonResultado = json.getJSONObject(i);
                String nombreReceta = jsonResultado.getString("nombre");
                Log.d("TraeRecetasFavoritas", nombreReceta);
                int idReceta = jsonResultado.getInt("idRecetas");






                Receta miReceta = new Receta();
                miReceta.Nombre = nombreReceta.trim();
                miReceta.IdReceta = idReceta;


                recetasArray.add(miReceta);
                Log.d("TraeRecetasFavoritas", "Receta:" + miReceta.Nombre + " " + miReceta.IdReceta);



            }

            return recetasArray;
        }

    }

    private class TraeMiReceta extends AsyncTask<String, Void, Receta> {

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(Receta miReceta) {
            Log.d("TraeMiReceta", "PostExecute");
            super.onPostExecute(miReceta);

            Intent miIntent = new Intent(elegirIngredientes.this,DetalleReceta.class);
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



