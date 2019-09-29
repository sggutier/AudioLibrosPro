package itsur.german.audiolibrospro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Vector;

import itsur.german.audiolibrospro.AdaptadorLibros;
import itsur.german.audiolibrospro.Aplicacion;
import itsur.german.audiolibrospro.Libro;
import itsur.german.audiolibrospro.MainActivity;
import itsur.german.audiolibrospro.R;

public class SelectorFragment extends Fragment {
    private Activity mActividad;
    private RecyclerView mRecyclerView;
    private AdaptadorLibros mAdaptador;
    private Vector<Libro> mVectorLibros;

    @Override
    public void onAttach(Context contexto) {
        super.onAttach(contexto);
        if (contexto instanceof Activity) {
            this.mActividad = (Activity) contexto;
            Aplicacion app = (Aplicacion) mActividad.getApplication();
            mAdaptador = app.getAdaptador();
            mVectorLibros = app.getVectorLibros();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        mRecyclerView = (RecyclerView) vista.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActividad,2));
        mRecyclerView.setAdapter(mAdaptador);
        setHasOptionsMenu(true);
        mAdaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mActividad).mostrarDetalle(mRecyclerView.getChildAdapterPosition(v));
            }
        });

        mAdaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int id = mRecyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(mActividad);
                CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Libro libro = mVectorLibros.elementAt(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar
                                Snackbar.make(v,"¿Estás seguro?", Snackbar.LENGTH_LONG)
                                        .setAction("SI", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                mVectorLibros.remove(id);
                                                mAdaptador.notifyDataSetChanged();
                                            }
                                        }).show();
                                break;
                            case 2: //Insertar
                                mVectorLibros.add(mVectorLibros.elementAt(id));
                                mAdaptador.notifyDataSetChanged();
                                Snackbar.make(v,"Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override public void onClick(View view) { }
                                        })
                                        .show();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });

        return vista;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) mActividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
