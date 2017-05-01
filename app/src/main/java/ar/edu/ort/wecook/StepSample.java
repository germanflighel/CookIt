package ar.edu.ort.wecook;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.fcannizzaro.materialstepper.AbstractStep;
import com.squareup.picasso.Picasso;



/**
 * Created by 41914608 on 5/8/2016.
 */

public class StepSample extends AbstractStep {


    boolean startTimer=true;
    static CountDownTimer miTimer;
    private TextView miTextView;
    private int i = 1;
    private Button button;
    private TextView miTxt;
    private ImageView miImageView;
    private final static String CLICK = "click";
    private final static String NEXT_DATA = "next";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.paso_de_texto, container, false);

        miImageView =(ImageView) v.findViewById(R.id.miImageView);

        Paso miPaso = (Paso) getArguments().get("paso");

        if (savedInstanceState != null)
            i = savedInstanceState.getInt(CLICK, 0);

        miTxt = (TextView) v.findViewById(R.id.txtPaso);
        miTxt.setText(miPaso.get_Paso());
        miTxt.setTextColor(Color.BLACK);

        String urlimage = "http://www.wecook.hol.es/images/" + miPaso.get_RutaMedia();
        Log.d("url", urlimage);
        Picasso.with(getContext()).load(urlimage).into(miImageView);
        String tiempo = miPaso.get_TimerString();
        Log.d("Tiempo", "El tiempo es: "+tiempo);

        if (tiempo.contains("ay")){
            button = (Button) v.findViewById(R.id.btnIniciarTimer);
            button.setEnabled(false);
            button.setText("No hay Timer");

            Log.d("Tiempo","No Tiene Timer");
        }
        else {
            button = (Button) v.findViewById(R.id.btnIniciarTimer);
            button.setText("Iniciar Timer");

            long Segundos = Long.valueOf(tiempo.substring(6,8))*1000;
            Log.d("Tiempo","Segundos: "+ Segundos);
            long Minutos = Long.valueOf(tiempo.substring(3, 5)) * 1000*60;
            Log.d("Tiempo","Minutos: "+ Minutos);
            long TiempoTotal = Minutos+Segundos;
            Log.d("Tiempo", "En Milisegundos es: "+ Minutos);
            miTimer = new CountDownTimer(TiempoTotal,1000) {
                @Override
                public void onTick(long l) {
                    Log.d("Tiempo", "Tick");

                }

                @Override
                public void onFinish() {
                    Log.d("Tiempo", "Finished");
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                        Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
                        r.play();
                    }
                    catch (Exception e){
                        Log.d("Error", e.getMessage());

                    }

                }
            };

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (startTimer) {
                    startTimer=false;
                    miTimer.start();
                    Log.d("Tiempo", "Inicio Timer");
                    button.setText("Frenar Timer");

                }else{
                    startTimer=true;
                    miTimer.cancel();
                    Log.d("Tiempo", "Freno Timer");
                    button.setText("Iniciar Timer");

                }
            }
        });



        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(CLICK, i);
    }

    @Override
    public String name() {
        return "";
    }

    @Override
    public boolean isOptional() {
        return true;
    }


    @Override
    public void onStepVisible() {
        if (getStepData() != null && button != null)
            button.setText(Html.fromHtml("Tap <b>" + getStepData().getInt(NEXT_DATA) + "</b>"));
    }

    @Override
    public void onNext() {
        System.out.println("onNext");
    }

    @Override
    public void onPrevious() {
        System.out.println("onPrevious");
    }

    @Override
    public String optional() {
        return "You can skip";
    }

    @Override
    public boolean nextIf() {
        return i > 1;
    }

    @Override
    public String error() {
        return "<b>You must click!</b> <small>this is the condition!</small>";
    }


}
