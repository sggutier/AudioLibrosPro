package itsur.german.audiolibrospro.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.io.IOException;

import itsur.german.audiolibrospro.Aplicacion;
import itsur.german.audiolibrospro.Libro;
import itsur.german.audiolibrospro.MainActivity;
import itsur.german.audiolibrospro.R;
import itsur.german.audiolibrospro.ServicioReproduccion;
import itsur.german.audiolibrospro.ServicioReproduccion.BinderReproduccion;

public class DetalleFragment extends Fragment implements View.OnTouchListener, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {
    public static String ARG_ID_LIBRO = "id_libro";
    ServicioReproduccion mServicioReproduccion;
    MediaPlayer mediaPlayer;
    MediaController mediaController;

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            mediaPlayer.start();
        }
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView());
        mediaController.setPadding(0, 0, 0, 110);
        mediaController.setEnabled(true);
        mediaController.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_detalle,
                contenedor, false);
        Bundle args = getArguments();

        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        return vista;
    }

    private void ponInfoLibro(int id, View vista) {
        final Libro libro = ((Aplicacion) getActivity().getApplication()).getVectorLibros().elementAt(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((ImageView) vista.findViewById(R.id.portada)).setImageResource(libro.recursoImagen);
        vista.setOnTouchListener(this);
//        if (mediaPlayer != null){
//            mediaPlayer.release();
//        }
//        mediaPlayer = new MediaPlayer();

        ServiceConnection gestionConexion = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("woof", "Servicio conectadezco");
                BinderReproduccion binder = (BinderReproduccion) service;
                mServicioReproduccion = binder.obtenServicio();
                mServicioReproduccion.reloadMediaPlayer();
                Log.d("aosnth", "Se conecto al servicio");

                mediaPlayer = mServicioReproduccion.getMediaPlayer();
                mediaPlayer.setOnPreparedListener(DetalleFragment.this);
                mediaController = new MediaController(getActivity());
                Uri audio = Uri.parse(libro.urlAudio);
                try {
                    mediaPlayer.setDataSource(getActivity(), audio);
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    Log.e("Audiolibros", "ERROR: No se puede reproducir "+audio,e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("aosnth", "Se desconecto del servicio");
            }
        };
        Context contexto = this.getContext();
        Intent intent = new Intent(this.getContext(), ServicioReproduccion.class);
        contexto.bindService(intent, gestionConexion, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onResume(){
        SelectorFragment selectorFragment = (SelectorFragment) getFragmentManager().findFragmentById(R.id.recycler_view);
        if (selectorFragment == null ) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }


    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Log.i("AudioLibros", "Me tocaron");
        mediaController.show();
        return false;
    }

    @Override
    public void onStop() {
        mediaController.hide();
        super.onStop();
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public void onDetach() {
        Log.d("aoeu", "OnDetach called");
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
            Log.e("Audiolibros", "Error en mediaPlayer.stop()");
        }
        super.onDetach();
    }
}
