package ar.edu.ort.wecook;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;

/**
 * Created by 41914608 on 20/05/2016.
 */
public class Paso implements java.io.Serializable{


    String _Paso;
    String _RutaMedia;
    String _TimerString;
    Calendar _TimerTiempo;
    public Paso() {

    }

    public void set_Paso(String _Paso) {
        this._Paso = _Paso;
    }

    public String get_Paso() {
        return _Paso;
    }


    public String get_RutaMedia() {
        return _RutaMedia;
    }

    public void set_RutaMedia(String RutaMedia) {
        this._RutaMedia = RutaMedia;
    }


    public String get_TimerString() {
        return _TimerString;
    }

    public void set_TimerString(String TimerString) {
        this._TimerString = TimerString;

    }
}
