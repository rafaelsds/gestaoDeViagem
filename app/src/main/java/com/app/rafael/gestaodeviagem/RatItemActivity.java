package com.app.rafael.gestaodeviagem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Vibrator;
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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Configuracoes;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagens;
import com.app.rafael.gestaodeviagem.db.tabelas.Rats;
import com.app.rafael.gestaodeviagem.db.tabelas.RatsItens;
import com.app.rafael.gestaodeviagem.entidades.GestaoDeViagem;
import com.app.rafael.gestaodeviagem.entidades.Rat;
import com.app.rafael.gestaodeviagem.entidades.RatItem;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.AlertDynamic;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.RegraCampo;
import com.app.rafael.gestaodeviagem.utilidades.SnackBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RatItemActivity extends AppCompatActivity {

    private Intent intent;

    private FloatingActionButton btnAdicionar;
    private AlertDialog alertRatItem, alertaCalendar;
    private RecyclerView recyclerView;
    private Integer diaCalendar, mesCalendar, anoCalendar;
    private String dataCalendar;
    private Rat rat;
    private TextView toolbarTxtTitle, txtData, txtValor;
    private Calendar calendar;
    private Toolbar toolbar;
    private LinearLayout ratItemLinear;
    Dml dml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rat_item);
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

    private void Vibrar(){
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 50;//'30' é o tempo em milissegundos, é basicamente o tempo de duração da vibração. portanto, quanto maior este numero, mais tempo de vibração você irá ter
        rr.vibrate(milliseconds);
    }

    public void inicialise(){

        intent = getIntent();
        if(intent.getSerializableExtra("rat") != null){
            rat = (Rat) intent.getSerializableExtra("rat");
        }

        dml = new Dml(RatItemActivity.this);
        btnAdicionar = (FloatingActionButton)findViewById(R.id.ratItemBttAdd);
        recyclerView = (RecyclerView)findViewById(R.id.ratItemRecyclerView);
        toolbarTxtTitle = (TextView) findViewById(R.id.ratItemTxtNrRat);
        txtData = (TextView) findViewById(R.id.ratItemTxtPeriodo);
        txtValor = (TextView) findViewById(R.id.ratItemTxtVlTotal);
        calendar = Calendar.getInstance();
        txtData.setText(rat.getDtInicial()+" até "+rat.getDtFinal());
        toolbar = (Toolbar) findViewById(R.id.ratItemToolbar);
        ratItemLinear = (LinearLayout) findViewById(R.id.ratItemLinear);
    }


    public void botoes(){
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarRegistro("","","");
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
        toolbarTxtTitle.setText("RAT "+rat.getNrRat());

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
    public boolean onCreateOptionsMenu(Menu menu){
        String item="Vincular GV";

        if(dml.getCount(Rats.TABELA, Rats.IDGV+" is not null and "+Rats.IDGV+" >0 and "+
                Rats.ID+"="+rat.getId())>0){
            item="Desvincular GV";
        }

        menu.add(item);
        getMenuInflater().inflate(R.menu.main_rat_item, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
//            Intent myIntent = new Intent(getApplicationContext(), RatActivity.class);
//            startActivityForResult(myIntent, 0);
            finish();
            return false;
        }

        if(item.getTitle().equals("Vincular GV")){
            final Spinner spnGv;
            View v;
            AlertDialog.Builder builder;
            final AlertDialog alert;

            v = getLayoutInflater().inflate(R.layout.vincular_gv_rat, null);
            builder = new AlertDialog.Builder(this);
            builder.setView(v);
            alert = builder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.AnimationTopRightDiagonal;
            alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            spnGv = (Spinner) v.findViewById(R.id.ratVincularGvSpnNrGv);
            ArrayList<GestaoDeViagem> list = obterGvDb();
            montaAdapterSpinner(spnGv, list);
            alert.show();

            v.findViewById(R.id.ratVincularGvBttCancelar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });

            v.findViewById(R.id.ratVincularGvBttSalvar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GestaoDeViagem spnSelecionado = ((GestaoDeViagem)spnGv.getSelectedItem());
                    ContentValues valores;
                    valores = new ContentValues();
                    valores.put(Rats.IDGV, spnSelecionado.getId().toString());
                    dml.update(Rats.TABELA, valores, Rats.ID + "=" + rat.getId().toString());
                    alert.dismiss();
                    RatItemActivity.this.invalidateOptionsMenu();
                    new SnackBar(getString(R.string.gvVinculada), ratItemLinear, Snackbar.LENGTH_LONG);
                }
            });
        }else{

            dml.updateDynamic(Rats.TABELA, Rats.IDGV+"=null", Rats.ID +"="+rat.getId().toString());
            RatItemActivity.this.invalidateOptionsMenu();
            new SnackBar(getString(R.string.gvDesvinculada), ratItemLinear, Snackbar.LENGTH_LONG);
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<GestaoDeViagem> obterGvDb(){
        String[] camposSelect={GestaoDeViagens.ID, GestaoDeViagens.NRGV};
        System.out.println(dml.getItem(GestaoDeViagens.TABELA, "DATE("+GestaoDeViagens.DTINICIAL+")","1=1"));
        String where =
                " DATE('"+ConverterDate.StrToStr(rat.getDtInicial(),"dd/MM/yyyy","yyyy-MM-dd")+"') >= DATE("+GestaoDeViagens.DTINICIAL+")"+
                " and DATE('"+ConverterDate.StrToStr(rat.getDtFinal(),"dd/MM/yyyy","yyyy-MM-dd")+"') <= DATE("+GestaoDeViagens.DTFINAL+")";

        Cursor cursor = dml.getAll(GestaoDeViagens.TABELA, camposSelect, where, null,null);

        ArrayList<GestaoDeViagem> list = new ArrayList<GestaoDeViagem>();

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    list.add(new GestaoDeViagem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(GestaoDeViagens.ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagens.NRGV))));
                    cursor.moveToNext();
                }
            }
        }

        return list;
    }


    @Override
    public void onBackPressed(){
//        Intent myIntent = new Intent(getApplicationContext(), RatActivity.class);
//        startActivityForResult(myIntent, 0);
        finish();
    }


    public void montaAdapterSpinner(Spinner spinnger, ArrayList list){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,R.layout.spinner_item, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnger.setAdapter(spinnerArrayAdapter);
    }


    public void adicionarRegistro(final String idP, final String dataP, String horasP){

        final EditText edtDtLancamento, edtHoras;
        final View viewInserirRatItem;
        final AlertDialog.Builder builder;
        final AlertDialog alert;

        viewInserirRatItem = getLayoutInflater().inflate(R.layout.inserir_rat_item, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewInserirRatItem);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightDiagonal;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtDtLancamento = (EditText) viewInserirRatItem.findViewById(R.id.ratItemInserirEdtData);
        edtDtLancamento.setText(dataP);

        edtHoras = (EditText) viewInserirRatItem.findViewById(R.id.ratItemInserirEdtHoras);
        edtHoras.setText(horasP);
        

        edtDtLancamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtLancamento);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewInserirRatItem.findViewById(R.id.ratItemInserirEdtData).getWindowToken(), 0);
            }
        });


        viewInserirRatItem.findViewById(R.id.ratItemInserirBttSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegraCampo.obrigatorio(edtDtLancamento, getApplicationContext())){
                    if(RegraCampo.obrigatorio(edtHoras, getApplicationContext())){

                        if(!compareDate(rat.getDtInicial(), edtDtLancamento.getText().toString())){
                            Alert a = new Alert(RatItemActivity.this, getString(R.string.atencao),"A data de lançamento não pode estar fora do período!");
                            return;
                        }

                        if(!compareDate(edtDtLancamento.getText().toString(), rat.getDtFinal())){
                            Alert a = new Alert(RatItemActivity.this, getString(R.string.atencao),"A data de lançamento não pode estar fora do período!");
                            return;
                        }
                        
                        //Inclui no banco
                        ContentValues valores;
                        valores = new ContentValues();
                        valores.put(RatsItens.IDRAT, rat.getId().toString());
                        valores.put(RatsItens.DATA, ConverterDate.StrToStr(edtDtLancamento.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                        valores.put(RatsItens.HORAS, edtHoras.getText().toString());
                        valores.put(RatsItens.VALOR, (Float.parseFloat(edtHoras.getText().toString()) *
                                Float.parseFloat(dml.getItem(Configuracoes.TABELA, Configuracoes.VLRAT, "1=1"))));
                        
                        
                        if (idP.trim().isEmpty()) {
                            dml.insert(RatsItens.TABELA, valores);
                            new SnackBar(getString(R.string.registroInserido), ratItemLinear, Snackbar.LENGTH_LONG);
                        } else {
                            dml.update(RatsItens.TABELA, valores, Categorias.ID + "=" + idP);
                            new SnackBar(getString(R.string.registroAlterado), ratItemLinear, Snackbar.LENGTH_LONG);
                        }

                        alert.dismiss();
                        carregarCard();

                    }
                }
            }
        });

        viewInserirRatItem.findViewById(R.id.ratItemInserirBttCancelar).setOnClickListener(new View.OnClickListener() {
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
        String[] camposSelect =  {RatsItens.ID, RatsItens.IDRAT, RatsItens.HORAS, RatsItens.DATA, RatsItens.VALOR};
        String camposWhere = RatsItens.IDRAT+" = ?";
        String[] valorCamposWhere = {rat.getId().toString()};

        Cursor cursor = dml.getAll(RatsItens.TABELA, camposSelect, camposWhere, valorCamposWhere,RatsItens.DATA+" ASC");

        ArrayList<RatItem> list = new ArrayList<RatItem>();

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    list.add(new RatItem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(RatsItens.ID)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(RatsItens.IDRAT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(RatsItens.HORAS)),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(RatsItens.DATA)),"yyyy-MM-dd","dd/MM/yyyy"),
                            cursor.getString(cursor.getColumnIndexOrThrow(RatsItens.VALOR))));
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
        recyclerView.setAdapter(new CardAdapter(RatItemActivity.this,list));

        atualizarTotal();
    }


    public void atualizarTotal(){
        float valorRat = dml.getSum(RatsItens.TABELA, RatsItens.VALOR, RatsItens.IDRAT+"="+rat.getId().toString());
        txtValor.setText("R$ Valor: "+String.valueOf(valorRat));
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

        private List<RatItem> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<RatItem> notes) {
            mList = notes;
            mContext = context;
        }


        @Override
        public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View notesView = inflater.inflate(R.layout.card_rat_item, parent, false);

            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }


        private void alert_opcoes_card(final String idP, final String dataP, final String horasP, final String valorP) {
//            View viewOpcoesCard = getLayoutInflater().inflate(R.layout.card_opcoes,null);
//
//            viewOpcoesCard.findViewById(R.id.bttEditarOpcoesCard).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v){
//                    adicionarRegistro(idP, dataP, horasP);
//                    alertRatItem.dismiss();
//                }
//            });
//
//            viewOpcoesCard.findViewById(R.id.bttExcluirOpcoesCard).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v){
//                    dml.delete(RatsItens.TABELA, RatsItens.ID +"="+idP);
//                    carregarCard();
//                    alertRatItem.dismiss();
//                    new SnackBar(getString(R.string.registroExcluido), ratItemLinear, Snackbar.LENGTH_LONG);
//                }
//            });
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setView(viewOpcoesCard);
//            alertRatItem = builder.create();
//            alertRatItem.show();

            new AlertDynamic.Builder(RatItemActivity.this)
                    .setTitle(getString(R.string.escolhaUmaOpcao))
                    .setBackgroundColor(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setPositiveBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))  //Don't pass R.color.colorvalue
                    .setPositiveBtnText(getString(R.string.excluir))
                    .setNegativeBtnText(getString(R.string.editar))
                    .setNegativeBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setAnimation(AlertDynamic.Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_error_outline_white_24dp, AlertDynamic.Icon.Visible)
                    .OnPositiveClicked(new AlertDynamic.AlertDynamicDialogListener() {
                        @Override
                        public void OnClick() {
                            // ACAO EXCLUIR
                            dml.delete(RatsItens.TABELA, RatsItens.ID +"="+idP);
                            carregarCard();
                            new SnackBar(getString(R.string.registroExcluido), ratItemLinear, Snackbar.LENGTH_LONG);
                        }
                    })
                    .OnNegativeClicked(new AlertDynamic.AlertDynamicDialogListener() {
                        @Override
                        public void OnClick() {
                            // ACAO EDITAR
                            adicionarRegistro(idP, dataP, horasP);
                        }
                    })
                    .build();
        }


        private Context getContext() {
            return mContext;
        }

        @Override
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {

            final RatItem obj = mList.get(position);

            TextView data = viewHolder.data;
            data.setText(obj.getDtLancamento());

            TextView horas = viewHolder.horas;
            horas.setText(obj.getHoras());

            TextView valor = viewHolder.valor;
            valor.setText(obj.getValor());

            viewHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Vibrar();
                    alert_opcoes_card(obj.getId().toString(),
                            obj.getDtLancamento(),
                            obj.getHoras(),
                            obj.getValor());
                    return false;
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView data, horas, valor;
            CardView card;

            public ViewHolder(View itemView) {
                super(itemView);
                data = (TextView)itemView.findViewById(R.id.ratItemCardTxtData);
                horas = (TextView)itemView.findViewById(R.id.ratItemCardTxtHoras);
                valor = (TextView)itemView.findViewById(R.id.ratItemCardTxtValor);
                card = (CardView)itemView.findViewById(R.id.ratItemCard);
            }
        }
    }

}
