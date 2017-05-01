package ar.edu.ort.wecook;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by 41914608 on 27/05/2016.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

    public static class PersonViewHolder extends RecyclerView.ViewHolder {

        CardView miCardView;
        TextView NombreReceta;
        TextView Ingredientes, Rating, Duracion;
        ImageView imagenReceta;
        Context miContext;
        ToggleButton Favoritear;




        PersonViewHolder(View itemView) {
            super(itemView);
            miCardView = (CardView)itemView.findViewById(R.id.card_view);
            NombreReceta = (TextView)itemView.findViewById(R.id.Titulo_Receta);
            Ingredientes = (TextView)itemView.findViewById(R.id.Ingredientes);
            imagenReceta = (ImageView)itemView.findViewById(R.id.imageView);
            Rating = (TextView)itemView.findViewById(R.id.txtRating);
            Duracion = (TextView)itemView.findViewById(R.id.txtDuracion);
            Favoritear = (ToggleButton)itemView.findViewById(R.id.myToggleButton);
            Log.d("break", "Crea CardView");

        }



    }

    ArrayList<Receta> recetasTraidas;

    RVAdapter (ArrayList<Receta> misRecetas){
        recetasTraidas = misRecetas;

    }

    @Override
    public int getItemCount() {
        return recetasTraidas.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_item, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int posicion) {
        Log.d("RVAdapter", "Posicion es: " + posicion);
        Log.d("RVAdapter", "Id del CardView es: " + personViewHolder.miCardView.getId());

        personViewHolder.NombreReceta.setText(recetasTraidas.get(posicion).Nombre);
        personViewHolder.miCardView.setId(recetasTraidas.get(posicion).IdReceta);
        String urlimage = "http://www.wecook.hol.es/images/" + recetasTraidas.get(posicion).imagen;
        Log.d("imagen", urlimage);
        Picasso.with(personViewHolder.miCardView.getContext()).load(urlimage).fit().into(personViewHolder.imagenReceta);
        Log.d("RVAdapter", "Id del CardView es: " + personViewHolder.miCardView.getId());

        String ratingConEstrella = recetasTraidas.get(posicion).Rating + "★";
        personViewHolder.Rating.setText(ratingConEstrella);

        String duracion = recetasTraidas.get(posicion).getDuracion() + "⏰";
        personViewHolder.Duracion.setText(duracion);


        final Context miContext = personViewHolder.miCardView.getContext();


        personViewHolder.Favoritear.setBackgroundDrawable(ContextCompat.getDrawable(personViewHolder.miCardView.getContext(), R.drawable.btn_star_pink));
        final ToggleButton Favoritear = personViewHolder.Favoritear;
        personViewHolder.Favoritear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("isChecked", "Faveo");
                    Favoritear.setBackgroundDrawable(ContextCompat.getDrawable(miContext, R.drawable.btn_star));
                    String url ="http://wecook.hol.es/favea_receta.php?idUser="+ elegirIngredientes.idUserLogged+ "&idReceta= "+ recetasTraidas.get(posicion).IdReceta;
                    new HacerFavorita().execute(url);

                }
                else {
                    Log.d("isChecked", "DesFaveo");
                    Favoritear.setBackgroundDrawable(ContextCompat.getDrawable(miContext, R.drawable.btn_star_grey));
                    String url ="http://wecook.hol.es/unfavea_receta.php?idUser="+ elegirIngredientes.idUserLogged+ "&idReceta= "+ recetasTraidas.get(posicion).IdReceta;
                    new DesHacerFavorita().execute(url);
                }
            }
        });

        if (!elegirIngredientes._recetasFavoritas.isEmpty()){
            try {
                for (Receta receta : recetasTraidas) {
                    for (Receta recipe : elegirIngredientes._recetasFavoritas)

                        if (receta.getIdReceta() == recipe.getIdReceta()) {
                            Log.d("Foreach", "Va a setear que si");
                            personViewHolder.Favoritear.setBackgroundDrawable(ContextCompat.getDrawable(miContext, R.drawable.btn_star));
                        } else {
                            Log.d("Foreach", "Va a setear que no");
                            //personViewHolder.Favoritear.setBackgroundDrawable(ContextCompat.getDrawable(miContext, R.drawable.btn_star_grey));
                        }
                }
            }
            catch (Exception e){
                Log.d("Error", e.getMessage());
            }
        }
        if (elegirIngredientes.idUserLogged==784){
            personViewHolder.Favoritear.setEnabled(false);
        }




    }


    private class HacerFavorita extends AsyncTask<String, Void, String> {

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(String recetasRecibidas){
            Log.d("Stepper","PostExecute");
            super.onPostExecute(recetasRecibidas);


        }

        protected String doInBackground(String... params) {
            String url = params[0];
            Log.d("url", "A punto de mandar: " +url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String respuesta = response.body().string();
                Log.d("Stepper", respuesta);

                return respuesta;


            } catch (IOException e) {
                Log.d("Error",e.getMessage());
                return e.getMessage();
            }
        }



    }
    private class DesHacerFavorita extends AsyncTask<String, Void, String> {

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(String recetasRecibidas){
            Log.d("Stepper","PostExecute");
            super.onPostExecute(recetasRecibidas);


        }

        protected String doInBackground(String... params) {
            String url = params[0];
            Log.d("url", "A punto de mandar: " +url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String respuesta = response.body().string();
                Log.d("Stepper", respuesta);

                return respuesta;


            } catch (IOException e) {
                Log.d("Error",e.getMessage());
                return e.getMessage();
            }
        }



    }

}
