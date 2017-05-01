package ar.edu.ort.wecook;

import java.util.ArrayList;

/**
 * Created by 41914608 on 20/05/2016.
 */
public class Receta  implements java.io.Serializable {

    String Nombre;
    String Duracion;
    ArrayList<Paso> Pasos;
    ArrayList<Ingrediente> Ingredientes;
    double Rating;
    int IdReceta;
    String imagen;


    //Seteos de Atributos
    public void setIdReceta(int miIdReceta){ IdReceta= miIdReceta;}
    public void setImagen (String miImagen){ imagen= miImagen;}
    public void setNombre(String miNombre){
        Nombre = miNombre;
    }
    public void setDuracion(String miDuracion){
        Duracion = miDuracion;
    }
    public void setPasos(ArrayList<Paso> misPasos){
        Pasos = misPasos;
    }
    public void setIngredientes(ArrayList<Ingrediente> misIngredientes){ Ingredientes = misIngredientes; }
    public void setRating(double miRating){
        Rating = miRating;
    }

    //Geteos de Atributos
    public int getIdReceta(){return IdReceta;}
    public String getImagen(){return imagen;}
    public String getNombre(){return Nombre;}
    public String getDuracion(){return Duracion;}
    public ArrayList<Paso> getPasos(){return Pasos;}
    public ArrayList<Ingrediente> getIngredientes(){return Ingredientes;}
    public double getRating(){return Rating;}

    //Constructor
    public Receta(){

    }
}

