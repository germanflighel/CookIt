package ar.edu.ort.wecook;

/**
 * Created by 41557360 on 06/05/2016.
 */
public class Ingrediente implements java.io.Serializable{

    String Nombre;
    String Id;
    String Unidad;
    int Cantidad;

    public String getNombre() {
        return Nombre;
    }

    public String getId() {return Id;}

    public void setNombre(String nombre) {Nombre = nombre;}
    public void setId(String nombre) {Id = nombre;}

    public String getUnidad() {
        return Unidad;
    }

    public void setUnidad(String duracion) {
        Unidad = duracion;
    }

    public int getCantidad() {
        return Cantidad;
    }

    public void setCantidad(int cantidad) {
        Cantidad = cantidad;
    }
}
