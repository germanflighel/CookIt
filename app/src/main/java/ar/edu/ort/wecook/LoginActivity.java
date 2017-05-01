package ar.edu.ort.wecook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {


    String url = "http://wecook.hol.es/cargaWeb/login.php?";
    EditText edtMail,edtPassword;
    TextView Invitado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void IniciarComoInvitado(View vista){
        Intent miIntent = new Intent(LoginActivity.this, elegirIngredientes.class);
        miIntent.putExtra("idUserLogged", 784);
        startActivity(miIntent);
    }

    public void login(View miView){
        edtMail = (EditText) findViewById(R.id.input_email);
        edtPassword = (EditText)findViewById(R.id.input_password);
        String miMail = edtMail.getText().toString();
        String miPassWord = edtPassword.getText().toString();
        Log.d("Login","UserName: "+ miMail + " Password: "+ miPassWord);
        url+="usuario="+miMail + "&password="+miPassWord;
        Log.d("Login",url);
        new Login().execute(url);
    }

    public void registracion(View miView){
        String urlString="http://wecook.hol.es/cargaWeb/registracion.php";
        Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException ex) {

            intent.setPackage(null);
            getApplicationContext().startActivity(intent);
        }
    }

    private class Login extends AsyncTask<String, Void, Integer>{

        private OkHttpClient client = new OkHttpClient();

        protected void onPostExecute(Integer verificar){
            Log.d("TraeRecetas","PostExecute");
            super.onPostExecute(verificar);

            Log.d("Login", "Verificar: "+ verificar);
            if (verificar==0) {

                edtMail.setText("");
                edtPassword.setText("");
                edtMail.requestFocus();
                Toast.makeText(getApplicationContext(), "Datos Incorrectos, Intente de Nuevo", Toast.LENGTH_SHORT).show();


            }else{
                Intent miIntent = new Intent(LoginActivity.this, elegirIngredientes.class);

                miIntent.putExtra("idUserLogged", verificar);


                startActivity(miIntent);

            }


        }

        protected Integer doInBackground(String... params) {
            String url = params[0];
            Log.d("break", url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                String paraParsear = response.body().string();
                Log.d("TraeRecetas", paraParsear);
                int  Parseado = parsearResultado(paraParsear);
                Log.d("TraeRecetas", "Parseo");
                return Parseado;


            } catch (IOException | JSONException e) {
                Log.d("Error",e.getMessage());
                return 0;
            }
        }

        int parsearResultado(String JSONstr) throws JSONException {
            int verifico=0;

            JSONObject json = new JSONObject(JSONstr);

            verifico = json.getInt("verfico");
            Log.d("Verifico", verifico+"");

            return Integer.valueOf(verifico);



        }

    }
}
