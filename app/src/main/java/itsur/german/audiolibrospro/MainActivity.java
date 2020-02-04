package itsur.german.audiolibrospro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import itsur.german.audiolibrospro.fragments.DetalleFragment;
import itsur.german.audiolibrospro.fragments.PreferenciasFragment;
import itsur.german.audiolibrospro.fragments.SelectorFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AdaptadorLibrosFiltro mAdaptador;
    private AppBarLayout mAppBarLayout;
    private TabLayout mTabs;
    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("AudioLibros", "oncreate");
        boolean startANew = false;
        if(savedInstanceState == null) {
            startANew = true;
        }
        else {
            int orientation = getResources().getConfiguration().orientation;
            int lastOrientation = savedInstanceState.getInt("lastOri");
            if(orientation != lastOrientation) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                startANew = true;
            }
        }
        mAdaptador = ((Aplicacion) getApplicationContext()).getAdaptador();
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irUltimoVisitado();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Pestañas
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.addTab(mTabs.newTab().setText("Todos"));
        mTabs.addTab(mTabs.newTab().setText("Nuevos"));
        mTabs.addTab(mTabs.newTab().setText("Leidos"));
        mTabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: //Todos
                        mAdaptador.setNovedad(false);
                        mAdaptador.setLeido(false);
                        break;
                    case 1: //Nuevos
                        mAdaptador.setNovedad(true);
                        mAdaptador.setLeido(false);
                        break;
                    case 2: //Leidos
                        mAdaptador.setNovedad(false);
                        mAdaptador.setLeido(true);
                        break;
                }
                mAdaptador.notifyDataSetChanged();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Navigation Drawer
        mDrawer = (DrawerLayout) findViewById(
                R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,
                mDrawer, toolbar, R.string.drawer_open, R.string. drawer_close);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();
        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(
                R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(startANew) {
            createSubFragments();
        }
    }

    private void createSubFragments() {
        int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ?
                R.id.contenedor_pequeno : R.id.contenedor_izquierdo;
        SelectorFragment primerFragment = new SelectorFragment();
        getSupportFragmentManager().beginTransaction()
                .add(idContenedor, primerFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(
                "lastOri",
                getResources().getConfiguration().orientation
        );
        super.onSaveInstanceState(outState);
    }

    public void abrePreferencias() {
        int idContenedor = (findViewById(R.id.contenedor_pequeno) != null) ?
                R.id.contenedor_pequeno : R.id.contenedor_izquierdo;
        PreferenciasFragment prefFragment = new PreferenciasFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(idContenedor, prefFragment)
                .addToBackStack(null)
                .commit();
    }

    public void mostrarDetalle(final int id) {
        DetalleFragment detalleFragment = (DetalleFragment)
                getSupportFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment != null && (findViewById(R.id.contenedor_pequeno) == null) ) {
            detalleFragment.ponInfoLibro(id);
        } else {
            DetalleFragment nuevoFragment = new DetalleFragment();
            Bundle args = new Bundle();
            args.putInt(DetalleFragment.ARG_ID_LIBRO, id);
            nuevoFragment.setArguments(args);
            FragmentTransaction transaccion = getSupportFragmentManager()
                    .beginTransaction();
            transaccion.replace(R.id.contenedor_pequeno, nuevoFragment);
            transaccion.addToBackStack(null);
            transaccion.commit();
            Log.d("AudioLibros","Fragment añadido");
        }
        SharedPreferences pref = getSharedPreferences(
                "itsur.german.audiolibrospro", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("ultimo", id);
        editor.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_preferencias) {
            abrePreferencias();
            return true;
        }
        else if (id == R.id.menu_acerca) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Mensaje de Acerca De");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void irUltimoVisitado() {
        SharedPreferences pref = getSharedPreferences(
                "itsur.german.audiolibrospro", MODE_PRIVATE);
        int id = pref.getInt("ultimo", -1);
        if (id >= 0) {
            mostrarDetalle(id);
        } else {
            Toast.makeText(this,"Sin última vista",Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_todos) {
            mAdaptador.setGenero("");
            mAdaptador.notifyDataSetChanged();
        } else if (id == R.id.nav_epico) {
            mAdaptador.setGenero(Libro.G_EPICO);
            mAdaptador.notifyDataSetChanged();
        } else if (id == R.id.nav_XIX) {
            mAdaptador.setGenero(Libro.G_S_XIX);
            mAdaptador.notifyDataSetChanged();
        } else if (id == R.id.nav_suspense) {
            mAdaptador.setGenero(Libro.G_SUSPENSE);
            mAdaptador.notifyDataSetChanged();
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void mostrarElementos(boolean mostrar) {
        mAppBarLayout.setExpanded(mostrar);
        mToggle.setDrawerIndicatorEnabled(mostrar);
        if (mostrar) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mTabs.setVisibility(View.VISIBLE);
        } else {
            mTabs.setVisibility(View.GONE);
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }


}
