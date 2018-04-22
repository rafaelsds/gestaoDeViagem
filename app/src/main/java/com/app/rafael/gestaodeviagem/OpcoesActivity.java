package com.app.rafael.gestaodeviagem;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rafael.gestaodeviagem.db.Backup;
import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Configuracoes;
import com.app.rafael.gestaodeviagem.utilidades.RegraCampo;
import com.app.rafael.gestaodeviagem.utilidades.SnackBar;
import com.app.rafael.gestaodeviagem.utilidades.System;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

public class OpcoesActivity extends AppCompatActivity {

    private LinearLayout linearOpcoesRat, linearOpcoesAlimentacao, linenarOpcoesBackupImpInterno, linenarOpcoesBackupRestInterno;
    private Dml dml;
    private Toolbar toolbar;
    private TextView toolbarTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes);

        inicialise();
        ajusteToolbar();
        botoes();

    }

    public void inicialise(){
        linearOpcoesRat = (LinearLayout)findViewById(R.id.linenarOpcoesRat);
        linearOpcoesAlimentacao = (LinearLayout)findViewById(R.id.linenarOpcoesAlimentacao);
        linenarOpcoesBackupImpInterno = (LinearLayout)findViewById(R.id.linenarOpcoesBackupImpInterno);
        linenarOpcoesBackupRestInterno = (LinearLayout)findViewById(R.id.linenarOpcoesBackupRestInterno);
        dml = new Dml(OpcoesActivity.this);
        toolbar = (Toolbar) findViewById(R.id.opcoesToolbar);
        toolbarTxtTitle = (TextView)findViewById(R.id.opcoesToolbarTxtTitle);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public void ajusteToolbar(){

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.postDelayed(new Runnable()
        {
            @Override
            public void run ()
            {
                int maxWidth = toolbar.getWidth();
                int titleWidth = toolbarTxtTitle.getWidth();
                int iconWidth = maxWidth - titleWidth;

                if (iconWidth > 0)
                {
                    int width = maxWidth - iconWidth * 2;
                    toolbarTxtTitle.setMinimumWidth(width);
                    toolbarTxtTitle.getLayoutParams().width = width;
                }
            }
        }, 0);
    }

    public void botoes(){
        linearOpcoesRat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarValorFixoAlert("RAT");
            }
        });

        linearOpcoesAlimentacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarValorFixoAlert("ALIMENTACAO");
            }
        });

        linenarOpcoesBackupImpInterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.permissions(OpcoesActivity.this);

                new FancyAlertDialog.Builder(OpcoesActivity.this)
                        .setTitle(getString(R.string.atencaoE))
                        .setBackgroundColor(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                        .setMessage(getString(R.string.desejaRealizarBackup))
                        .setPositiveBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))  //Don't pass R.color.colorvalue
                        .setPositiveBtnText(getString(R.string.sim))
                        .setNegativeBtnText(getString(R.string.nao))
                        .setNegativeBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_error_outline_black_24dp, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                if(Backup.exportar(OpcoesActivity.this)) {
                                    new SnackBar(getString(R.string.backupRealizado), linenarOpcoesBackupImpInterno, Snackbar.LENGTH_LONG);
                                    return;
                                }
                                new SnackBar(getString(R.string.backupRealizadoErro), linenarOpcoesBackupRestInterno, Snackbar.LENGTH_LONG);
                            }
                        })
                        .OnNegativeClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                            }
                        })
                        .build();
            }
        });

        linenarOpcoesBackupRestInterno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.permissions(OpcoesActivity.this);

                new FancyAlertDialog.Builder(OpcoesActivity.this)
                        .setTitle(getString(R.string.atencaoE))
                        .setBackgroundColor(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                        .setMessage(getString(R.string.desejaRestaurarbackup))
                        .setPositiveBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))  //Don't pass R.color.colorvalue
                        .setPositiveBtnText(getString(R.string.sim))
                        .setNegativeBtnText(getString(R.string.nao))
                        .setNegativeBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.ic_error_outline_black_24dp, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                                if(Backup.importar(OpcoesActivity.this)) {
                                    new SnackBar(getString(R.string.backupRestaurado), linenarOpcoesBackupRestInterno, Snackbar.LENGTH_LONG);
                                    return;
                                }
                                new SnackBar(getString(R.string.backupRestauradoErro), linenarOpcoesBackupRestInterno, Snackbar.LENGTH_LONG);
                            }
                        })
                        .OnNegativeClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {
                            }
                        })
                        .build();

            }
        });

    }


    public void adicionarValorFixoAlert(final String tipoValor){

        final EditText edtValor;
        final View viewInserirValorFixo;
        final AlertDialog.Builder builder;
        final AlertDialog alert;
        final TextView txtTitulo;

        viewInserirValorFixo = getLayoutInflater().inflate(R.layout.inserir_valor_fixo, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewInserirValorFixo);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationUpToBottom;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        txtTitulo = (TextView)viewInserirValorFixo.findViewById(R.id.inserirValorFixoTxtDescricao);
        edtValor = (EditText) viewInserirValorFixo.findViewById(R.id.edtVlLancamento);
        edtValor.setText("");

        if(tipoValor.equals("RAT")){
            txtTitulo.setText(getString(R.string.horaRat));
        }else{
            txtTitulo.setText(getString(R.string.alimentacao));
        }

        String[] campos =  {Configuracoes.VLALIMENTACAO, Configuracoes.VLRAT};
        Cursor cursor = dml.getAll(Configuracoes.TABELA, campos, null,null,null);


        if(cursor != null) {
            if(cursor.moveToFirst()){
                if(tipoValor.equals("RAT")){
                    edtValor.setText(cursor.getString(cursor.getColumnIndexOrThrow(Configuracoes.VLRAT)));
                }else{
                    edtValor.setText(cursor.getString(cursor.getColumnIndexOrThrow(Configuracoes.VLALIMENTACAO)));
                }
            }
        }

        viewInserirValorFixo.findViewById(R.id.bttSalvarInserirValor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegraCampo.obrigatorio(edtValor, getApplicationContext())){

                    ContentValues valores;
                    valores = new ContentValues();

                    if(tipoValor.equals("RAT")){
                        valores.put(Configuracoes.VLRAT, edtValor.getText().toString());
                    }else{
                        valores.put(Configuracoes.VLALIMENTACAO, edtValor.getText().toString());
                    }

                    dml.update(Configuracoes.TABELA, valores,"");

                    new SnackBar(getString(R.string.registroAlterado), linearOpcoesRat, Snackbar.LENGTH_LONG);

                    alert.dismiss();

                }
            }
        });

        viewInserirValorFixo.findViewById(R.id.bttCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }
}
