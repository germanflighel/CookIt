package ar.edu.ort.wecook;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.github.fcannizzaro.materialstepper.style.DotStepper;

import java.io.IOException;
import java.io.LineNumberInputStream;
import java.util.ArrayList;
import java.util.List;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ivb.com.materialstepper.progressMobileStepper;

public class
Stepper extends DotStepper {


    private int paso;
    Paso miPaso;





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ArrayList<Paso> recibido = (ArrayList<Paso>)getIntent().getExtras().get("lista");
        setErrorTimeout(1500);

        setTitle("Cocinando!");
        setDarkPrimaryColor(999999);
        for (paso = 0;paso<recibido.size();paso++) {

            miPaso=  recibido.get(paso);
            Log.d("Stepper", miPaso.get_Paso());
            addStep(createFragment(new StepSample()));
        }


        super.onCreate(savedInstanceState);
    }

    private AbstractStep createFragment(AbstractStep fragment) {

        Bundle b = new Bundle();
        b.putSerializable("paso",miPaso);
        fragment.setArguments(b);
        return fragment;
    }


    @Override
    public void onComplete() {
        super.onComplete();
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Completaste la receta!")
                .setContentText("¨Presiona OK para volver a las recetas")
                .setConfirmText("OK")
                .setCancelText("Volver a la receta")
                .showCancelButton(true)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener(){
                    @Override
                    public void onClick(SweetAlertDialog sDialog){
                        sDialog.dismissWithAnimation();
                        final Dialog rankDialog = new Dialog(Stepper.this, R.style.FullHeightDialog);
                        rankDialog.setContentView(R.layout.rank_dialog);
                        rankDialog.setCancelable(true);
                        final RatingBar miRatingBar = (RatingBar)rankDialog.findViewById(R.id.dialog_ratingbar);

                        Button updateButton = (Button) rankDialog.findViewById(R.id.enviarRating);
                        updateButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("Stepper","Se seleccionaron "+miRatingBar.getRating()+" estrellas");

                                float cantEstrellas = miRatingBar.getRating();

                                String url = "http://wecook.hol.es/inserta_rating.php?";

                                url+="rating="+cantEstrellas;

                                url+="&idreceta="+ ListaRecetas.idReceta;

                                Log.d("url","La url a mandar es: "+ url);

                                PoneRatingM(url);

                                rankDialog.dismiss();

                                Log.d("Stepper", "Cancelo Timer");
                            }
                        });
                        //Mostrar el dialog
                        rankDialog.show();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener(){
                    @Override
                    public  void onClick(SweetAlertDialog sDialog){
                        sDialog.cancel();
                    }
                })
                .show();

    }

    public void PoneRatingM(String url){

        new PoneRating().execute(url);
    }


    private class PoneRating extends AsyncTask<String, Void, String> {

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(String recetasRecibidas){
            Log.d("Stepper","PostExecute");
            super.onPostExecute(recetasRecibidas);

            Intent miIntent = new Intent(Stepper.this, elegirIngredientes.class);
            startActivity(miIntent);


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
