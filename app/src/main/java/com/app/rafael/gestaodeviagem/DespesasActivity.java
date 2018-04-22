package com.app.rafael.gestaodeviagem;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Despesas;
import com.app.rafael.gestaodeviagem.entidades.Categoria;
import com.app.rafael.gestaodeviagem.entidades.GestaoDeViagemItem;
import com.app.rafael.gestaodeviagem.entidades.TipoCategoria;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.RegraCampo;
import com.app.rafael.gestaodeviagem.utilidades.SnackBar;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DespesasActivity extends AppCompatActivity {

    private Intent intent;
    private FloatingActionButton btnAdicionar;
    private AlertDialog alertDespesa, alertaCalendar, alertAlimentacao;
    private RecyclerView recyclerView;
    private Integer diaCalendar, mesCalendar, anoCalendar;
    private String dataCalendar;
    private TextView toolbarTxtTitle, txtData, txtValor;
    private Calendar calendar;
    private Toolbar toolbar;
    private Dml dml;
    private CoordinatorLayout despesaCoordinator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        inicialise();
        ajusteToolbar();
        botoes();
        carregarCard();

    }

    
    public void inicialise(){
        dml = new Dml(DespesasActivity.this);
        btnAdicionar = (FloatingActionButton)findViewById(R.id.despesaBttAdd);
        recyclerView = (RecyclerView)findViewById(R.id.despesaRecyclerView);
        toolbarTxtTitle = (TextView) findViewById(R.id.despesaTxtTitle);
        calendar = Calendar.getInstance();
        toolbar = (Toolbar) findViewById(R.id.despesaToolbar);
        despesaCoordinator = (CoordinatorLayout) findViewById(R.id.despesaCoordinator);
    }
    
    
    public void botoes(){
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarRegistro("","","", "", "");
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && btnAdicionar.getVisibility() == View.VISIBLE) {
                    btnAdicionar.hide();
                } else if (dy < 0 && btnAdicionar.getVisibility() != View.VISIBLE) {
                    btnAdicionar.show();
                }
            }
        });
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
    

    private void Vibrar(){
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 50;
        rr.vibrate(milliseconds);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }


    public ArrayList<Categoria> obterCategoriaDb(){
        String[] camposSelect={Categorias.ID, Categorias.DESCRICAO, Categorias.TIPO};
        String[] valorCamposWhere = { TipoCategoria.getId("Despesa") };
        Cursor cursor = dml.getAll(Categorias.TABELA, camposSelect, Categorias.TIPO+"=?", valorCamposWhere,null);

        ArrayList<Categoria> list = new ArrayList<Categoria>();

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    list.add(new Categoria(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Categorias.ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Categorias.DESCRICAO)),
                            TipoCategoria.getDescricao(cursor.getString(cursor.getColumnIndexOrThrow(Categorias.TIPO)))));
                    cursor.moveToNext();
                }
            }
        }

        return list;
    }

    
    public void montaAdapterSpinner(Spinner spinnger, ArrayList list){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,R.layout.spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnger.setAdapter(spinnerArrayAdapter);
    }


    public void adicionarRegistro(final String idP, final String categoriaP, final String dataP, String valorP, String observacaoP){

        final EditText edtDtLancamento, edtValor, edtObservacao;
        final Spinner spnCategoria;
        final View viewInserirDespesa;
        final AlertDialog.Builder builder;
        final AlertDialog alert;
        final TextView txtTitulo;

        viewInserirDespesa = getLayoutInflater().inflate(R.layout.inserir_gv_item, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewInserirDespesa);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightDiagonal;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtDtLancamento = (EditText) viewInserirDespesa.findViewById(R.id.gvItemInserirEdtData);
        edtDtLancamento.setText(dataP);

        edtValor = (EditText) viewInserirDespesa.findViewById(R.id.gvItemInserirEdtValor);
        edtValor.setText(valorP);

        edtObservacao = (EditText) viewInserirDespesa.findViewById(R.id.gvItemInserirEdtObservacao);
        edtObservacao.setText(observacaoP);

        txtTitulo = (TextView) viewInserirDespesa.findViewById(R.id.inserirGvItemTxtTitulo);
        txtTitulo.setText(getString(R.string.despesa));

        spnCategoria = (Spinner) viewInserirDespesa.findViewById(R.id.gvItemInserirSpnCategoria);
        ArrayList<Categoria> list = obterCategoriaDb();
        montaAdapterSpinner(spnCategoria, list);

        if(!categoriaP.isEmpty()){
            int i=0;
            for(Categoria c : list){
                if(categoriaP.equals(c.getDescricao())){
                    spnCategoria.setSelection(i);
                    break;
                }

                i++;
            }
        }

        edtDtLancamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtLancamento);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewInserirDespesa.findViewById(R.id.gvItemInserirEdtData).getWindowToken(), 0);
            }
        });


        viewInserirDespesa.findViewById(R.id.gvItemInserirBttSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegraCampo.obrigatorio(spnCategoria, getApplicationContext())){
                    if(RegraCampo.obrigatorio(edtDtLancamento, getApplicationContext())){
                        if(RegraCampo.obrigatorio(edtValor, getApplicationContext())){

                            Categoria spnSelecionado = ((Categoria)spnCategoria.getSelectedItem());
                            //Inclui no banco
                            ContentValues valores;
                            valores = new ContentValues();
                            valores.put(Despesas.DATA, ConverterDate.StrToStr(edtDtLancamento.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                            valores.put(Despesas.VALOR, edtValor.getText().toString());
                            valores.put(Despesas.CATEGORIA, spnSelecionado.getId());
                            valores.put(Despesas.OBSERVACAO, edtObservacao.getText().toString());

                            if (idP.trim().isEmpty()) {
                                dml.insert(Despesas.TABELA, valores);
                                new SnackBar(getString(R.string.registroInserido), despesaCoordinator, Snackbar.LENGTH_LONG);
                            } else {
                                dml.update(Despesas.TABELA, valores, Categorias.ID + "=" + idP);
                                new SnackBar(getString(R.string.registroAlterado), despesaCoordinator, Snackbar.LENGTH_LONG);
                            }

                            alert.dismiss();
                            carregarCard();
                        }
                    }
                }
            }
        });

        viewInserirDespesa.findViewById(R.id.gvItemInserirBttCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }


    private void carregarCard() {
        //Faz o select de todos os dados passando por parametros a tabela, os camposSelect e a ordem
        String[] camposSelect =  {Despesas.ID, Despesas.CATEGORIA, Despesas.DATA, Despesas.VALOR, Despesas.OBSERVACAO};

        Cursor cursor = dml.getAll(Despesas.TABELA, camposSelect, null, null,Despesas.DATA+" ASC, "+Despesas.CATEGORIA+" ASC");

        ArrayList<GestaoDeViagemItem> list = new ArrayList<GestaoDeViagemItem>();

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    list.add(new GestaoDeViagemItem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Despesas.ID)),
                            null,
                            dml.getItem(Categorias.TABELA, Categorias.DESCRICAO, Categorias.ID+"="+
                                    cursor.getString(cursor.getColumnIndexOrThrow(Despesas.CATEGORIA))),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(Despesas.DATA)),"yyyy-MM-dd","dd/MM/yyyy"),
                            cursor.getString(cursor.getColumnIndexOrThrow(Despesas.VALOR)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Despesas.OBSERVACAO))));
                    cursor.moveToNext();
                }

            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.nenhumRegistro),
                        Toast.LENGTH_SHORT).show();
            }
        }

        if(btnAdicionar.getVisibility() == View.GONE)
            btnAdicionar.show();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DespesasActivity.CardAdapter(DespesasActivity.this,list));

    }


    public Boolean compareDate(String dtInicialP, String dtFinalP){
        if(dtInicialP.equals(dtFinalP))
            return true;

        return ConverterDate.StrToDate(dtInicialP).before(ConverterDate.StrToDate(dtFinalP));
    }


    public void obterDataCalendar(final EditText edt){
        View v = getLayoutInflater().inflate(R.layout.calendar, null);
        final DatePicker datePicker;
        datePicker = (DatePicker) v.findViewById(R.id.calenderPickerClock);

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                diaCalendar = datePicker.getDayOfMonth();
                mesCalendar = datePicker.getMonth()+1;
                anoCalendar = datePicker.getYear();

                if(diaCalendar <= 9){
                    dataCalendar = "0"+diaCalendar.toString();
                }else{
                    dataCalendar = diaCalendar.toString();
                }

                if(mesCalendar <= 9){
                    dataCalendar += "/0"+mesCalendar.toString();
                }else{
                    dataCalendar += "/"+mesCalendar.toString();
                }

                dataCalendar +="/"+anoCalendar;

                if(dataCalendar != null && !dataCalendar.toString().isEmpty()){
                    edt.setText(dataCalendar);
                }else{
                    edt.setText("");
                }

                calendar.setTime(ConverterDate.StrToDate(dataCalendar));

                alertaCalendar.dismiss();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        alertaCalendar = builder.create();
        alertaCalendar.show();
    }


    public class CardAdapter extends RecyclerView.Adapter<DespesasActivity.CardAdapter.ViewHolder> {

        private List<GestaoDeViagemItem> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<GestaoDeViagemItem> notes) {
            mList = notes;
            mContext = context;
        }


        @Override
        public DespesasActivity.CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View notesView = inflater.inflate(R.layout.card_gv_item, parent, false);

            DespesasActivity.CardAdapter.ViewHolder viewHolder = new DespesasActivity.CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }


        private void alert_opcoes_card(final String idP, final String categoriaP, final String dataP, final String valorP, final String observacaoP) {

            new FancyAlertDialog.Builder(DespesasActivity.this)
                    .setTitle(getString(R.string.escolhaUmaOpcao))
                    .setBackgroundColor(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setPositiveBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText(getString(R.string.excluir))
                    .setNegativeBtnText(getString(R.string.editar))
                    .setNegativeBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_error_outline_white_24dp, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            // ACAO EXCLUIR
                            dml.delete(Despesas.TABELA, Despesas.ID +"="+idP);
                            carregarCard();
                            new SnackBar(getString(R.string.registroExcluido), despesaCoordinator, Snackbar.LENGTH_LONG);
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            // ACAO EDITAR
                            adicionarRegistro(idP, categoriaP, dataP, valorP, observacaoP);
                        }
                    })
                    .build();

        }


        private Context getContext() {
            return mContext;
        }

        @Override
        public void onBindViewHolder(DespesasActivity.CardAdapter.ViewHolder viewHolder, final int position) {

            final GestaoDeViagemItem obj = mList.get(position);

            TextView data = viewHolder.data;
            data.setText(obj.getDtLancamento());

            TextView categoria = viewHolder.categoria;
            categoria.setText(obj.getCategoria());

            TextView valor = viewHolder.valor;
            valor.setText(obj.getValor());

            viewHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Vibrar();
                    alert_opcoes_card(obj.getId().toString(),
                            obj.getCategoria(),
                            obj.getDtLancamento(),
                            obj.getValor(),
                            obj.getObservacao());
                    return false;
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView categoria, data, valor;
            CardView card;

            public ViewHolder(View itemView) {
                super(itemView);
                categoria = (TextView)itemView.findViewById(R.id.gvItemCardTxtCategoria);
                data = (TextView)itemView.findViewById(R.id.gvItemCardTxtData);
                valor = (TextView)itemView.findViewById(R.id.gvItemCardTxtValor);
                card = (CardView)itemView.findViewById(R.id.gvItemCard);
            }
        }
    }

}
