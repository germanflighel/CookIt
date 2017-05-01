package ar.edu.ort.wecook;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class DetalleReceta extends AppCompatActivity {


    View miView1,miView2,miView3,miView4,miView5,miView6;
    TextView miNombreRecetaTxt, miDuracionRecetaTxt;
    TextView miIngrediente1,miIngrediente2,miIngrediente3,miIngrediente4,miIngrediente5,miIngrediente6;
    ArrayList<Paso> Parseado;
    String url;
    Receta miReceta;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_receta);
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
        miReceta = (Receta) getIntent().getExtras().get("lista");
        Log.d("DetalleReceta", "Recibi la Receta");

        miNombreRecetaTxt = (TextView)findViewById(R.id.miNombreReceta);
        miNombreRecetaTxt.setText(miReceta.getNombre());

        miDuracionRecetaTxt = (TextView)findViewById(R.id.miDuracion);
        Log.d("DetalleReceta","Duracion: "+ miReceta.getDuracion());
        miDuracionRecetaTxt.setText("Duracion: "+miReceta.getDuracion()+ "⏰");

        miIngrediente1 = (TextView) findViewById(R.id.miIngrediente1);
        miIngrediente2 = (TextView) findViewById(R.id.miIngrediente2);
        miIngrediente3 = (TextView) findViewById(R.id.miIngrediente3);
        miIngrediente4 = (TextView) findViewById(R.id.miIngrediente4);
        miIngrediente5 = (TextView) findViewById(R.id.miIngrediente5);
        miIngrediente6 = (TextView) findViewById(R.id.miIngrediente6);

        miView1 = (View) findViewById (R.id.view1);
        miView2 = (View) findViewById (R.id.view2);
        miView3 = (View) findViewById (R.id.view3);
        miView4 = (View) findViewById (R.id.view4);
        miView5 = (View) findViewById (R.id.view5);
        miView6 = (View) findViewById (R.id.view6);

        int NumeroDeIngredientes=0;
        for (Ingrediente miIngrediente : miReceta.getIngredientes()){
            NumeroDeIngredientes++;
            switch (NumeroDeIngredientes){

                case 1:
                    Log.d("AsignoIngredientes", miIngrediente.getNombre());
                    miIngrediente1.setText(miIngrediente.getNombre());
                    miView1.setBackgroundColor(getResources().getColor(R.color.black));
                    break;
                case 2:
                    Log.d("AsignoIngredientes", miIngrediente.getNombre());
                    miIngrediente2.setText(miIngrediente.getNombre());
                    miView2.setBackgroundColor(getResources().getColor(R.color.black));
                    break;
                case 3:
                    Log.d("AsignoIngredientes", miIngrediente.getNombre());
                    miView3.setBackgroundColor(getResources().getColor(R.color.black));

                    miIngrediente3.setText(miIngrediente.getNombre());
                    break;
                case 4:
                    miView4.setBackgroundColor(getResources().getColor(R.color.black));

                    Log.d("AsignoIngredientes", miIngrediente.getNombre());

                    miIngrediente4.setText(miIngrediente.getNombre());
                    break;
                case 5:
                    miView5.setBackgroundColor(getResources().getColor(R.color.black));

                    Log.d("AsignoIngredientes", miIngrediente.getNombre());

                    miIngrediente5.setText(miIngrediente.getNombre());
                    break;
                case 6:
                    miView6.setBackgroundColor(getResources().getColor(R.color.black));

                    Log.d("AsignoIngredientes", miIngrediente.getNombre());

                    miIngrediente6.setText(miIngrediente.getNombre());
                    break;

            }


        }

    }


    public void ACocinar(View vista){

        url = "http://wecook.hol.es/traepasos.php?id="+ miReceta.getIdReceta();
        Log.d("TraePasos", url);
        new TraePasos().execute(url);

    }

    private class TraePasos extends AsyncTask<String, Void, ArrayList<Paso>> {

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(ArrayList<Paso> pasosRecibidos) {
            Log.d("TraePasos", "PostExecute");
            super.onPostExecute(pasosRecibidos);

            //Collections.reverse(pasosRecibidos);

            Intent miIntent = new Intent(DetalleReceta.this,Stepper.class);
            miIntent.putExtra("lista", Parseado);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

}
