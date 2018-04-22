package com.app.rafael.gestaodeviagem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Rats;
import com.app.rafael.gestaodeviagem.db.tabelas.RatsItens;
import com.app.rafael.gestaodeviagem.entidades.Rat;
import com.app.rafael.gestaodeviagem.entidades.Status;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.FormatNumber;
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

public class RatActivity extends AppCompatActivity {

    private FloatingActionButton btnAdicionar;
    private AlertDialog alertRat, alertaCalendar;
    private AlertDialog.Builder builder;
    private RecyclerView recyclerView;
    private Integer diaCalendar, mesCalendar, anoCalendar;
    private String dataCalendar;
    private LinearLayout linearFiltro;
    private EditText edtFiltroDtFinal, edtFiltroDtInicial;
    private Spinner spnFiltroStatus;
    private ImageView imgFiltro;
    private Calendar calendar;
    private Toolbar toolbar;
    private TextView toolbarTxtTitle;
    Dml dml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rat);

        inicialise();
        ajusteToolbar();
        botoes();
        carregarCard();

    }

    private void Vibrar(){
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 50;//'30' é o tempo em milissegundos, é basicamente o tempo de duração da vibração. portanto, quanto maior este numero, mais tempo de vibração você irá ter
        rr.vibrate(milliseconds);
    }

    public void inicialise(){
        dml = new Dml(RatActivity.this);
        btnAdicionar = (FloatingActionButton)findViewById(R.id.bttAddRat);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewRat);
        linearFiltro = (LinearLayout)findViewById(R.id.RatLinearFiltro);
        toolbarTxtTitle = (TextView) findViewById(R.id.ratTxtRats);
        edtFiltroDtInicial = (EditText)findViewById(R.id.RatFiltroEdtDtInicial);
        edtFiltroDtInicial.setText(GetDate.today(true,-1,0).toString());
        edtFiltroDtFinal = (EditText)findViewById(R.id.RatFiltroEdtDtFinal);
        edtFiltroDtFinal.setText(GetDate.today(false,0,0).toString());
        spnFiltroStatus = (Spinner)findViewById(R.id.RatFiltroSpnStatus);
        spnFiltroStatus.setAdapter(new ArrayAdapter<Status>(this, android.R.layout.simple_spinner_item, Status.values()));
        spnFiltroStatus.setSelection(1);
        imgFiltro = (ImageView)findViewById(R.id.imgRatFiltro);
        calendar = Calendar.getInstance();
        toolbar = (Toolbar) findViewById(R.id.ratToolbar);
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
        btnAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarRegistro("","","", "", "");
            }
        });

        edtFiltroDtInicial.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtFiltroDtInicial);
            }
        });

        edtFiltroDtInicial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                carregarCard();
            }
        });

        edtFiltroDtFinal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtFiltroDtFinal);
            }
        });

        edtFiltroDtFinal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                carregarCard();
            }
        });

        imgFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (linearFiltro.getVisibility() == View.GONE){
                    linearFiltro.setVisibility(View.VISIBLE);
                }else{
                    linearFiltro.setVisibility(View.GONE);
                }
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

        spnFiltroStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carregarCard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }


    public void adicionarRegistro(final String idP, final String nrRatP, final String dtInicialP, String dtFinalP, final String TipoP){

        final EditText edtNrRat, edtDtInicial, edtDtFinal;
        final Spinner spnStatus;
        final View viewInserirRat;
        final AlertDialog.Builder builder;
        final AlertDialog alert;

        viewInserirRat = getLayoutInflater().inflate(R.layout.inserir_rat, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewInserirRat);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightDiagonal;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtNrRat = (EditText) viewInserirRat.findViewById(R.id.edtNrRat);
        edtNrRat.setText(nrRatP);

        edtDtInicial = (EditText) viewInserirRat.findViewById(R.id.edtRatDtInicial);
        edtDtInicial.setText(dtInicialP);


        edtDtFinal = (EditText) viewInserirRat.findViewById(R.id.edtRatDtFinal);
        edtDtFinal.setText(dtFinalP);

        spnStatus = (Spinner) viewInserirRat.findViewById(R.id.spnStatusRat);

        ArrayAdapter<Status> spinnerArrayAdapter = new ArrayAdapter<Status>(
                this,R.layout.spinner_item,Status.values());
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spnStatus.setAdapter(spinnerArrayAdapter);

        if(!TipoP.trim().isEmpty()){
            spnStatus.setSelection(Integer.parseInt(Status.getId(TipoP)));
        }

        edtDtInicial.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtInicial);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewInserirRat.findViewById(R.id.edtRatDtInicial).getWindowToken(), 0);
            }
        });

        edtDtFinal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtFinal);
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewInserirRat.findViewById(R.id.edtRatDtFinal).getWindowToken(), 0);
            }
        });

        viewInserirRat.findViewById(R.id.bttSalvarRat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegraCampo.obrigatorio(spnStatus, getApplicationContext())){
                    if(RegraCampo.obrigatorio(edtDtInicial, getApplicationContext())){
                        if(RegraCampo.obrigatorio(edtDtFinal, getApplicationContext())){

                            if(!compareDate(edtDtInicial.getText().toString(), edtDtFinal.getText().toString())){
                                Alert a = new Alert(RatActivity.this, getString(R.string.atencao),"A data inicial não pode ser superior a data final!");
                                return;
                            }

                            //Inclui no banco
                            ContentValues valores;
                            valores = new ContentValues();
                            valores.put(Rats.NRRAT, edtNrRat.getText().toString());
                            valores.put(Rats.DTINICIAL, ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                            valores.put(Rats.DTFINAL, ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                            valores.put(Rats.STATUS, Status.getId(
                                    String.valueOf(spnStatus.getAdapter().getItem(spnStatus.getSelectedItemPosition()))));

                            if (idP.trim().isEmpty()) {
                                dml.insert(Rats.TABELA, valores);
                                new SnackBar(getString(R.string.registroInserido), linearFiltro, Snackbar.LENGTH_LONG);
                            } else {
                                dml.update(Rats.TABELA, valores, Categorias.ID + "=" + idP);
                                new SnackBar(getString(R.string.registroAlterado), linearFiltro, Snackbar.LENGTH_LONG);
                            }

                            alert.dismiss();
                            carregarCard();
                        }
                    }
                }
            }
        });

        viewInserirRat.findViewById(R.id.bttCancelarRat).setOnClickListener(new View.OnClickListener() {
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

    private void carregarCard(){

        if(!compareDate(edtFiltroDtInicial.getText().toString(), edtFiltroDtFinal.getText().toString())){
            Alert a = new Alert(RatActivity.this, getString(R.string.atencao),"A data inicial não pode ser superior a data final!");
            return;
        }

        String where = "("+Rats.DTINICIAL+" between DATE(?) and DATE(?) "+
            " or "+Rats.DTFINAL+" between DATE(?) and DATE(?))"+
            " and ("+Rats.STATUS+" = ? or ? = '0')";

        String[] valorWhere = new String[]{
                ConverterDate.StrToStr(edtFiltroDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtFiltroDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtFiltroDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtFiltroDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                String.valueOf(spnFiltroStatus.getSelectedItemPosition()),
                String.valueOf(spnFiltroStatus.getSelectedItemPosition())
        };

        //Faz o select de todos os dados passando por parametros a tabela, os campos e a ordem
        String[] campos =  {Rats.ID, Rats.NRRAT, Rats.STATUS, Rats.DTINICIAL, Rats.DTFINAL};
        Cursor cursor = dml.getAll(Rats.TABELA, campos, where, valorWhere, Rats.DTINICIAL+" asc, "+Rats.DTFINAL+" asc");

        ArrayList<Rat> list = new ArrayList<Rat>();
        Float valorRat, horas;

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {

                    valorRat = dml.getSum(RatsItens.TABELA, RatsItens.VALOR, RatsItens.IDRAT+"="+cursor.getInt(cursor.getColumnIndexOrThrow(Rats.ID)));
                    horas = dml.getSum(RatsItens.TABELA, RatsItens.HORAS, RatsItens.IDRAT+"="+cursor.getInt(cursor.getColumnIndexOrThrow(Rats.ID)));

                    list.add(new Rat(
                            cursor.getInt(cursor.getColumnIndexOrThrow(Rats.ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(Rats.NRRAT)),
                            Status.getDescricao(cursor.getString(cursor.getColumnIndexOrThrow(Rats.STATUS))),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(Rats.DTINICIAL)),"yyyy-MM-dd","dd/MM/yyyy"),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(Rats.DTFINAL)),"yyyy-MM-dd","dd/MM/yyyy"),
                            valorRat,
                            horas)
                    );

                    valorRat=0f;
                    horas=0f;

                    cursor.moveToNext();
                }
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.nenhumRegistro),
                        Toast.LENGTH_SHORT).show();
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CardAdapter(RatActivity.this,list));
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

        private List<Rat> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<Rat> notes) {
            mList = notes;
            mContext = context;
        }

        @Override
        public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View notesView = inflater.inflate(R.layout.card_rats, parent, false);

            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }

        private void alert_opcoes_card(final String idP, final String nrRatP, final String dtInicialP, final String dtFinalP, final String tipoP) {
//            View viewOpcoesCard = getLayoutInflater().inflate(R.layout.card_opcoes,null);
//
//            viewOpcoesCard.findViewById(R.id.bttEditarOpcoesCard).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v){
//                    adicionarRegistro(idP, nrRatP, dtInicialP, dtFinalP, tipoP);
//                    alertRat.dismiss();
//                }
//            });
//
//            viewOpcoesCard.findViewById(R.id.bttExcluirOpcoesCard).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v){
//                    dml.delete(Rats.TABELA, Rats.ID +"="+idP);
//                    carregarCard();
//                    alertRat.dismiss();
//                    new SnackBar(getString(R.string.registroExcluido), linearFiltro, Snackbar.LENGTH_LONG);
//                }
//            });
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//            builder.setView(viewOpcoesCard);
//            alertRat = builder.create();
//            alertRat.show();


            new FancyAlertDialog.Builder(RatActivity.this)
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
                            dml.delete(Rats.TABELA, Rats.ID +"="+idP);
                            carregarCard();
                            new SnackBar(getString(R.string.registroExcluido), linearFiltro, Snackbar.LENGTH_LONG);
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            // ACAO EDITAR
                            adicionarRegistro(idP, nrRatP, dtInicialP, dtFinalP, tipoP);
                        }
                    })
                    .build();

        }


        private Context getContext() {
            return mContext;
        }

        @Override
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {

            final Rat obj = mList.get(position);

            TextView nrRat = viewHolder.nrRat;
            nrRat.setText(obj.getNrRat());

            TextView status = viewHolder.status;
            status.setText(obj.getStatus());

            TextView dtInicial = viewHolder.dtInicial;
            dtInicial.setText(obj.getDtInicial());

            TextView dtFinal = viewHolder.dtFinal;
            dtFinal.setText(obj.getDtFinal());

            TextView valor = viewHolder.vlTotal;
            valor.setText(FormatNumber.numToString(obj.getValorTotal(),"###,##0.00"));

            TextView horas = viewHolder.horas;
            horas.setText(obj.getHoras().toString());

            viewHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Vibrar();
                    alert_opcoes_card(obj.getId().toString(),
                            obj.getNrRat(),
                            obj.getDtInicial(),
                            obj.getDtFinal(),
                            obj.getStatus());
                    return false;
                }
            });

            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(RatActivity.this, RatItemActivity.class);
                    it.putExtra("rat", obj);
                    startActivity(it);
                    finish();
                }
            });

        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nrRat, status, dtInicial, dtFinal, vlTotal, horas;
            CardView card;

            public ViewHolder(View itemView) {
                super(itemView);

                nrRat = (TextView)itemView.findViewById(R.id.cardRatNr);
                status = (TextView)itemView.findViewById(R.id.cardRatStatus);
                dtInicial = (TextView)itemView.findViewById(R.id.cardRatDtInicial);
                dtFinal = (TextView)itemView.findViewById(R.id.cardRatDtFinal);
                vlTotal = (TextView)itemView.findViewById(R.id.cardRatTotal);
                horas = (TextView)itemView.findViewById(R.id.cardRatHoras);
                card = (CardView)itemView.findViewById(R.id.cardRat);
            }
        }
    }

}
