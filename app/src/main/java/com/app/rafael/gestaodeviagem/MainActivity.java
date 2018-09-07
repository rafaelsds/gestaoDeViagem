package com.app.rafael.gestaodeviagem;

import android.content.ContentValues;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Configuracoes;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.System;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearOpcoes, linearGv, linearDespesa, linearCategorias, linearListaRats, linearRelatorio;
    private Dml dml;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private TextView toolbarTitle;
    private ImageView imgFiltro;
    private int itemMenuChecado;
    Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v3);
//        overridePendingTransition(R.anim.activity_open_left_to_right_begin, R.anim.activity_open_left_to_right_end);
        inicialise();
        insertPadrao();
        botoes();
        ajusteToolbar(getString(R.string.gestaoDeViagem));
    }

    public void inicialise(){
        toolbar = (Toolbar) findViewById(R.id.content_toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = (TextView) findViewById(R.id.content_toolbar_title);
        linearOpcoes = (LinearLayout) findViewById(R.id.mainLinearOpcoes);
        linearGv = (LinearLayout) findViewById(R.id.mainLinearGv);
        linearDespesa = (LinearLayout) findViewById(R.id.mainLinearDespesa);
        linearCategorias = (LinearLayout) findViewById(R.id.mainLinearCategorias);
        linearRelatorio = (LinearLayout) findViewById(R.id.mainLinearRelatorio);
        linearListaRats = (LinearLayout) findViewById(R.id.mainLinearRat);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        imgFiltro =(ImageView)findViewById(R.id.imgFiltro);
        dml = new Dml(MainActivity.this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fragment = new HomeActivity();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

    }

    public void insertPadrao(){
        if(dml.getCount(Configuracoes.TABELA)<=0){
            //Faz o insert padrão de configuração
            ContentValues valores;
            valores = new ContentValues();
            valores.put(Configuracoes.VLRAT, "50");
            valores.put(Configuracoes.VLALIMENTACAO, "59");
            dml.insert(Configuracoes.TABELA, valores);
        }
        if(dml.getCount(Categorias.TABELA)<=0){
            //Faz o insert padrão de configuração
            ContentValues valores;
            valores = new ContentValues();

            valores.put(Categorias.DESCRICAO, "Uber");
            valores.put(Categorias.TIPO, "0");
            dml.insert(Categorias.TABELA, valores);

            valores.put(Categorias.DESCRICAO, "Alimentação");
            valores.put(Categorias.TIPO, "0");
            dml.insert(Categorias.TABELA, valores);

            valores.put(Categorias.DESCRICAO, "Almoço");
            valores.put(Categorias.TIPO, "1");
            dml.insert(Categorias.TABELA, valores);
        }
    }

    public void botoes(){
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener(){
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                boolean fragmentTransaction = false;
                imgFiltro.setVisibility(View.GONE);

                switch (menuItem.getItemId()) {
                    case R.id.nav_categorias:
                        fragment = new CategoriasActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.categorias));
                        break;

                    case R.id.nav_opcoes:
                        fragment = new OpcoesActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.opcoes));
                        break;

                    case R.id.nav_relatorio:
                        fragment = new RelatorioActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.relatorio));
                        imgFiltro.setVisibility(View.VISIBLE);
                        break;

                    case R.id.nav_rat:
                        fragment = new RatActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.rat));
                        imgFiltro.setVisibility(View.VISIBLE);
                        break;

                    case R.id.nav_despesas:
                        fragment = new DespesasActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.despesas));
                        imgFiltro.setVisibility(View.VISIBLE);
                        break;

                    case R.id.nav_gv:
                        fragment = new GestaoDeViagemActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.gv));
                        imgFiltro.setVisibility(View.VISIBLE);
                        break;

                    case R.id.nav_home:
                        fragment = new HomeActivity();
                        fragmentTransaction = true;
                        toolbarTitle.setText(getString(R.string.home));
                        break;
                }

                if(fragmentTransaction){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .commit();

                    menuItem.setChecked(true);
                    itemMenuChecado = menuItem.getItemId();
                }

                drawer.closeDrawers();

                return true;
                }
            });

        imgFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                LinearLayout linear;
                switch(itemMenuChecado){
                    case R.id.nav_relatorio:
                        linear =(LinearLayout)findViewById(R.id.relLinearFiltros);
                        if (linear.getVisibility() == View.GONE){
                            linear.setVisibility(View.VISIBLE);
                        }else{
                            linear.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.nav_rat:
                        linear=(LinearLayout)findViewById(R.id.RatLinearFiltro);
                        if (linear.getVisibility() == View.GONE){
                            linear.setVisibility(View.VISIBLE);
                        }else{
                            linear.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.nav_gv:
                        linear=(LinearLayout)findViewById(R.id.GvLinearFiltro);
                        if (linear.getVisibility() == View.GONE){
                            linear.setVisibility(View.VISIBLE);
                        }else{
                            linear.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.nav_despesas:
                        linear=(LinearLayout)findViewById(R.id.DespesaLinearFiltro);
                        if (linear.getVisibility() == View.GONE){
                            linear.setVisibility(View.VISIBLE);
                        }else{
                            linear.setVisibility(View.GONE);
                        }
                        break;

                }
            }
        });
    }


    public void ajusteToolbar(String title){
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(title);

        toolbar.postDelayed(new Runnable()
        {
            @Override
            public void run ()
            {
                int maxWidth = toolbar.getWidth();
                int titleWidth = toolbarTitle.getWidth();
                int iconWidth = maxWidth - titleWidth;

                if (iconWidth > 0)
                {
                    int width = maxWidth - iconWidth * 2;
                    toolbarTitle.setMinimumWidth(width);
                    toolbarTitle.getLayoutParams().width = width;
                }
            }
        }, 0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.activity_open_right_to_left_begin, R.anim.activity_open_right_to_left_end);
//    }

}
