package com.app.rafael.gestaodeviagem;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Configuracoes;
import com.app.rafael.gestaodeviagem.utilidades.System;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearOpcoes, linearGv, linearDespesa, linearCategorias, linearListaRats, linearRelatorio;
    Dml dml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);
        overridePendingTransition(R.anim.activity_open_left_to_right_begin, R.anim.activity_open_left_to_right_end);

        inicialise();
        botoes();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_right_to_left_begin, R.anim.activity_open_right_to_left_end);
    }

    public void inicialise(){
        linearOpcoes = (LinearLayout) findViewById(R.id.mainLinearOpcoes);
        linearGv = (LinearLayout) findViewById(R.id.mainLinearGv);
        linearDespesa = (LinearLayout) findViewById(R.id.mainLinearDespesa);
        linearCategorias = (LinearLayout) findViewById(R.id.mainLinearCategorias);
        linearRelatorio = (LinearLayout) findViewById(R.id.mainLinearRelatorio);
        linearListaRats = (LinearLayout) findViewById(R.id.mainLinearRat);

        dml = new Dml(MainActivity.this);

        if(dml.getCount(Configuracoes.TABELA)<=0){
            //Faz o insert padrão de configuração
            ContentValues valores;
            valores = new ContentValues();
            valores.put("vl_rat", "0");
            valores.put("vl_alimentacao", "0");
            dml.insert(Configuracoes.TABELA, valores);
        }

    }

    public void botoes(){

        linearOpcoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent it = new Intent(MainActivity.this, OpcoesActivity.class);
            startActivity(it);
            finish();
            }
        });

        linearGv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, GestaoDeViagemActivity.class);
                startActivity(it);
                finish();
            }
        });

        linearDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, DespesasActivity.class);
                startActivity(it);
                finish();
            }
        });

        linearCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, CategoriasActivity.class);
                startActivity(it);
                finish();
            }
        });

        linearListaRats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, RatActivity.class);
                startActivity(it);
                finish();
            }
        });

        linearRelatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, RelatorioActivity.class);
                startActivity(it);
                finish();
            }
        });

    }


}
