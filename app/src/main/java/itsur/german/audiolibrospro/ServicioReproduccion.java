package itsur.german.audiolibrospro;


import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ServicioReproduccion extends Service {
    private MediaPlayer mediaPlayer;
    private final BinderReproduccion binder = new BinderReproduccion();

    public class BinderReproduccion extends Binder {
        public ServicioReproduccion obtenServicio() {
            return ServicioReproduccion.this;
        }
    }

    public void reloadMediaPlayer() {
        mediaPlayer = new MediaPlayer();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void onCreate() {
        Log.d("aoeu", "Servicio creado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }
}
