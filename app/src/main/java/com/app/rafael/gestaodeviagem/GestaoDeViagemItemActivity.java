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
import android.view.Menu;
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
import com.app.rafael.gestaodeviagem.db.tabelas.Configuracoes;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagensItens;
import com.app.rafael.gestaodeviagem.entidades.Categoria;
import com.app.rafael.gestaodeviagem.entidades.GestaoDeViagem;
import com.app.rafael.gestaodeviagem.entidades.GestaoDeViagemItem;
import com.app.rafael.gestaodeviagem.entidades.TipoCategoria;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.GetDate;
import com.app.rafael.gestaodeviagem.utilidades.RegraCampo;
import com.app.rafael.gestaodeviagem.utilidades.SnackBar;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GestaoDeViagemItemActivity extends AppCompatActivity {

    private Intent intent;
    private FloatingActionButton btnAdicionar;
    private AlertDialog alertGvItem, alertaCalendar, alertAlimentacao;
    private RecyclerView recyclerView;
    private Integer diaCalendar, mesCalendar, anoCalendar;
    private String dataCalendar;
    private GestaoDeViagem gv;
    private TextView toolbarTxtTitle, txtData, txtValor;
    private Calendar calendar;
    private Toolbar toolbar;
    private Dml lancamentoDao;
    private CoordinatorLayout gvItemCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gv_item);
        overridePendingTransition(R.anim.activity_open_right_to_left_begin, R.anim.activity_open_right_to_left_end);

        inicialise();
        ajusteToolbar();
        botoes();
        carregarCard();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_open_left_to_right_begin, R.anim.activity_open_left_to_right_end);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_gv_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_gerar_alimentacao:
                gerarAlimentacaoPadrao();
                break;

            case R.id.action_excluir_itens:
                excluirItens();
                break;
        }


        if (item.getItemId() == android.R.id.home) {
            Intent myIntent = new Intent(getApplicationContext(), GestaoDeViagemActivity.class);
            startActivityForResult(myIntent, 0);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void Vibrar(){
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 50;//'30' é o tempo em milissegundos, é basicamente o tempo de duração da vibração. portanto, quanto maior este numero, mais tempo de vibração você irá ter
        rr.vibrate(milliseconds);
    }

    public void inicialise(){

        intent = getIntent();
        if(intent.getSerializableExtra("gv") != null){
            gv = (GestaoDeViagem) intent.getSerializableExtra("gv");
        }

        lancamentoDao = new Dml(GestaoDeViagemItemActivity.this);
        btnAdicionar = (FloatingActionButton)findViewById(R.id.GvItemBttAdd);
        recyclerView = (RecyclerView)findViewById(R.id.gvItemRecyclerView);
        toolbarTxtTitle = (TextView) findViewById(R.id.gvItemTxtNrGv);
        txtData = (TextView) findViewById(R.id.gvItemTxtPeriodo);
        txtValor = (TextView) findViewById(R.id.gvItemTxtVlTotal);
        calendar = Calendar.getInstance();
        txtData.setText(gv.getDtInicial()+" até "+gv.getDtFinal());
        toolbar = (Toolbar) findViewById(R.id.gvItemToolbar);
        gvItemCoordinator = (CoordinatorLayout) findViewById(R.id.gvItemCoordinator);
    }

    public void excluirItens(){
        lancamentoDao.delete(GestaoDeViagensItens.TABELA, GestaoDeViagensItens.IDGV +"="+gv.getId());
        carregarCard();
    }

    public void gerarAlimentacaoPadrao(){
        final Spinner spnCategoria;
        View v;
        AlertDialog.Builder builder;

        v = getLayoutInflater().inflate(R.layout.inserir_gv_item_alimentacao, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(v);
        alertAlimentacao = builder.create();
        alertAlimentacao.getWindow().getAttributes().windowAnimations = R.style.AnimationTopRightDiagonal;

        spnCategoria = (Spinner) v.findViewById(R.id.gvItemInserirAlimentSpnCategor);
        ArrayList<Categoria> list = obterCategoriaDb();
        montaAdapterSpinner(spnCategoria, list);

        v.findViewById(R.id.gvItemInserirAlimentBttCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertAlimentacao.dismiss();
            }
        });

        v.findViewById(R.id.gvItemInserirAlimentBttSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                Categoria spnSelecionado = ((Categoria)spnCategoria.getSelectedItem());
                //Inclui no banco
                ContentValues valores;
                valores = new ContentValues();
                valores.put(GestaoDeViagensItens.IDGV, gv.getId().toString());
                valores.put(GestaoDeViagensItens.VALOR, lancamentoDao.getItem(Configuracoes.TABELA, Configuracoes.VLALIMENTACAO, "1=1"));
                valores.put(GestaoDeViagensItens.CATEGORIA, spnSelecionado.getId());

                for(int i=0; i<= GetDate.between(gv.getDtInicial(),gv.getDtFinal()); i++){
                    valores = valores;
                    valores.put(GestaoDeViagensItens.DATA, ConverterDate.StrToStr(GetDate.addDay(gv.getDtInicial(),i),"dd/MM/yyyy","yyyy-MM-dd"));
                    lancamentoDao.insert(GestaoDeViagensItens.TABELA, valores);
                }

                new SnackBar(getString(R.string.registroInserido), gvItemCoordinator, Snackbar.LENGTH_LONG);

                alertAlimentacao.dismiss();
                carregarCard();
            }
        });

        alertAlimentacao.show();
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
        toolbarTxtTitle.setText("GV "+gv.getNrGv());

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


    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), GestaoDeViagemActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }


    public ArrayList<Categoria> obterCategoriaDb(){
        String[] camposSelect={Categorias.ID, Categorias.DESCRICAO, Categorias.TIPO};
        String[] valorCamposWhere = { TipoCategoria.getId("Gv") };
        Cursor cursor = lancamentoDao.getAll(Categorias.TABELA, camposSelect, Categorias.TIPO+"=?", valorCamposWhere,null);

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
        final View viewInserirGvItem;
        final AlertDialog.Builder builder;
        final AlertDialog alert;
        final TextView txtTitulo;

        viewInserirGvItem = getLayoutInflater().inflate(R.layout.inserir_gv_item, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewInserirGvItem);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightDiagonal;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtDtLancamento = (EditText) viewInserirGvItem.findViewById(R.id.gvItemInserirEdtData);
        edtDtLancamento.setText(dataP);

        edtValor = (EditText) viewInserirGvItem.findViewById(R.id.gvItemInserirEdtValor);
        edtValor.setText(valorP);

        edtObservacao = (EditText) viewInserirGvItem.findViewById(R.id.gvItemInserirEdtObservacao);
        edtObservacao.setText(observacaoP);

        txtTitulo = (TextView) viewInserirGvItem.findViewById(R.id.inserirGvItemTxtTitulo);
        txtTitulo.setText(getString(R.string.gvItem));

        spnCategoria = (Spinner) viewInserirGvItem.findViewById(R.id.gvItemInserirSpnCategoria);
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
                        .hideSoftInputFromWindow(viewInserirGvItem.findViewById(R.id.gvItemInserirEdtData).getWindowToken(), 0);
            }
        });


        viewInserirGvItem.findViewById(R.id.gvItemInserirBttSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegraCampo.obrigatorio(edtDtLancamento, getApplicationContext())){
                    if(RegraCampo.obrigatorio(edtValor, getApplicationContext())){
                        if(RegraCampo.obrigatorio(spnCategoria, getApplicationContext())){

                            if(!compareDate(gv.getDtInicial(), edtDtLancamento.getText().toString())){
                                Alert a = new Alert(GestaoDeViagemItemActivity.this, getString(R.string.atencao),"A data de lançamento não pode estar fora do período!");
                                return;
                            }

                            if(!compareDate(edtDtLancamento.getText().toString(), gv.getDtFinal())){
                                Alert a = new Alert(GestaoDeViagemItemActivity.this, getString(R.string.atencao),"A data de lançamento não pode estar fora do período!");
                                return;
                            }

                            Categoria spnSelecionado = ((Categoria)spnCategoria.getSelectedItem());
                            //Inclui no banco
                            ContentValues valores;
                            valores = new ContentValues();
                            valores.put(GestaoDeViagensItens.IDGV, gv.getId().toString());
                            valores.put(GestaoDeViagensItens.DATA, ConverterDate.StrToStr(edtDtLancamento.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                            valores.put(GestaoDeViagensItens.VALOR, edtValor.getText().toString());
                            valores.put(GestaoDeViagensItens.CATEGORIA, spnSelecionado.getId());
                            valores.put(GestaoDeViagensItens.OBSERVACAO, edtObservacao.getText().toString());

                            if (idP.trim().isEmpty()) {
                                lancamentoDao.insert(GestaoDeViagensItens.TABELA, valores);
                                new SnackBar(getString(R.string.registroInserido), gvItemCoordinator, Snackbar.LENGTH_LONG);
                            } else {
                                lancamentoDao.update(GestaoDeViagensItens.TABELA, valores, Categorias.ID + "=" + idP);
                                new SnackBar(getString(R.string.registroAlterado), gvItemCoordinator, Snackbar.LENGTH_LONG);
                            }

                            alert.dismiss();
                            carregarCard();
                        }
                    }
                }
            }
        });

        viewInserirGvItem.findViewById(R.id.gvItemInserirBttCancelar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }


    public Boolean compareDate(String dtInicialP, String dtFinalP){
        if(dtInicialP.equals(dtFinalP))
            return true;

        return ConverterDate.StrToDate(dtInicialP).before(ConverterDate.StrToDate(dtFinalP));
    }

    private void carregarCard() {
        //Faz o select de todos os dados passando por parametros a tabela, os camposSelect e a ordem
        String[] camposSelect =  {GestaoDeViagensItens.ID, GestaoDeViagensItens.IDGV, GestaoDeViagensItens.CATEGORIA, GestaoDeViagensItens.DATA, GestaoDeViagensItens.VALOR, GestaoDeViagensItens.OBSERVACAO};
        String camposWhere = GestaoDeViagensItens.IDGV+" = ?";
        String[] valorCamposWhere = {gv.getId().toString()};

        Cursor cursor = lancamentoDao.getAll(GestaoDeViagensItens.TABELA, camposSelect, camposWhere, valorCamposWhere,GestaoDeViagensItens.DATA+" ASC, "+GestaoDeViagensItens.CATEGORIA+" ASC");

        ArrayList<GestaoDeViagemItem> list = new ArrayList<GestaoDeViagemItem>();

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    list.add(new GestaoDeViagemItem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(GestaoDeViagensItens.ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(GestaoDeViagensItens.IDGV)),
                            lancamentoDao.getItem(Categorias.TABELA, Categorias.DESCRICAO, Categorias.ID+"="+
                            cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagensItens.CATEGORIA))),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagensItens.DATA)),"yyyy-MM-dd","dd/MM/yyyy"),
                            cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagensItens.VALOR)),
                            cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagensItens.OBSERVACAO))));
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
        recyclerView.setAdapter(new CardAdapter(GestaoDeViagemItemActivity.this,list));

        atualizarTotal();
    }


    public void atualizarTotal(){
        float valorGv = lancamentoDao.getSum(GestaoDeViagensItens.TABELA, GestaoDeViagensItens.VALOR, GestaoDeViagensItens.IDGV+"="+gv.getId().toString());
        txtValor.setText("Total: "+String.valueOf(valorGv));
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


    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        private List<GestaoDeViagemItem> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<GestaoDeViagemItem> notes) {
            mList = notes;
            mContext = context;
        }


        @Override
        public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View notesView = inflater.inflate(R.layout.card_gv_item, parent, false);

            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }


        private void alert_opcoes_card(final String idP, final String categoriaP, final String dataP, final String valorP, final String observacaoP) {

            new FancyAlertDialog.Builder(GestaoDeViagemItemActivity.this)
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
                            lancamentoDao.delete(GestaoDeViagensItens.TABELA, GestaoDeViagensItens.ID +"="+idP);
                            carregarCard();
                            new SnackBar(getString(R.string.registroExcluido), gvItemCoordinator, Snackbar.LENGTH_LONG);
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
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {

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
