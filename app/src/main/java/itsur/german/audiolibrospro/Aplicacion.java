package itsur.german.audiolibrospro;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Vector;

public class Aplicacion extends Application {
    private Vector<Libro> vectorLibros;
    private AdaptadorLibrosFiltro adaptador;

    @Override
    public void onCreate() {
        super.onCreate();
        vectorLibros = Libro.ejemploLibros();
        adaptador = new AdaptadorLibrosFiltro (this, vectorLibros);
    }

    public AdaptadorLibrosFiltro getAdaptador() {
        return adaptador;
    }
    public Vector<Libro> getVectorLibros() {
        return vectorLibros;
    }

}
