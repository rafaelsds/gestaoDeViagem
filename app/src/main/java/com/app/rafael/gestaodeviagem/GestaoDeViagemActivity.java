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
import android.support.v4.app.Fragment;
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
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagens;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagensItens;
import com.app.rafael.gestaodeviagem.entidades.GestaoDeViagem;
import com.app.rafael.gestaodeviagem.entidades.Status;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.AlertDynamic;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.FormatNumber;
import com.app.rafael.gestaodeviagem.utilidades.GetDate;
import com.app.rafael.gestaodeviagem.utilidades.RegraCampo;
import com.app.rafael.gestaodeviagem.utilidades.SnackBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GestaoDeViagemActivity extends Fragment {

    private FloatingActionButton btnAdicionar;
    private AlertDialog alertaCalendar;
    private RecyclerView recyclerView;
    private Integer diaCalendar, mesCalendar, anoCalendar;
    private String dataCalendar;
    private LinearLayout linearFiltro;
    private EditText edtFiltroDtFinal, edtFiltroDtInicial;
    private Spinner spnFiltroStatus;
    private Calendar calendar;
    private Dml lancamentoDao;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_gv, container, false);
        inicialise();
        botoes();
        return view;

    }

    @Override
    public void onResume() {
        carregarCard();
        super.onResume();
    }

    private void Vibrar(){
        Vibrator rr = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        long milliseconds = 50;
        rr.vibrate(milliseconds);
    }

    public void inicialise(){
        lancamentoDao = new Dml(getContext());
        btnAdicionar = (FloatingActionButton)view.findViewById(R.id.bttAddGv);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerViewGv);
        linearFiltro = (LinearLayout)view.findViewById(R.id.GvLinearFiltro);
        edtFiltroDtInicial = (EditText)view.findViewById(R.id.GvFiltroEdtDtInicial);
        edtFiltroDtInicial.setText(GetDate.today(true,-1,0).toString());
        edtFiltroDtFinal = (EditText)view.findViewById(R.id.GvFiltroEdtDtFinal);
        edtFiltroDtFinal.setText(GetDate.today(false,0,0).toString());

        spnFiltroStatus = (Spinner)view.findViewById(R.id.GvFiltroSpnStatus);
        ArrayAdapter<Status> spinnerArrayAdapter = new ArrayAdapter<Status>(
                getContext(), android.R.layout.simple_spinner_item ,Status.values());
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spnFiltroStatus.setAdapter(spinnerArrayAdapter);
        spnFiltroStatus.setSelection(1);
        calendar = Calendar.getInstance();
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

    public void adicionarRegistro(final String idP, final String nrGvP, final String dtInicialP, String dtFinalP, final String TipoP){

        final EditText edtNrGv, edtDtInicial, edtDtFinal;
        final Spinner spnStatus;
        final View viewInserirGv;
        final AlertDialog.Builder builder;
        final AlertDialog alert;

        viewInserirGv = getLayoutInflater().inflate(R.layout.inserir_gv, null);
        builder = new AlertDialog.Builder(getContext());
        builder.setView(viewInserirGv);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightDiagonal;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtNrGv = (EditText) viewInserirGv.findViewById(R.id.edtNrGv);
        edtNrGv.setText(nrGvP);

        edtDtInicial = (EditText) viewInserirGv.findViewById(R.id.edtGvDtInicial);
        edtDtInicial.setText(dtInicialP);

        edtDtFinal = (EditText) viewInserirGv.findViewById(R.id.edtGvDtFinal);
        edtDtFinal.setText(dtFinalP);

        spnStatus = (Spinner) viewInserirGv.findViewById(R.id.spnStatusGv);

        ArrayAdapter<Status> spinnerArrayAdapter = new ArrayAdapter<Status>(
                getContext(), android.R.layout.simple_spinner_item,Status.values());
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
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewInserirGv.findViewById(R.id.edtGvDtInicial).getWindowToken(), 0);
            }
        });

        edtDtFinal.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtFinal);
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(viewInserirGv.findViewById(R.id.edtGvDtFinal).getWindowToken(), 0);
            }
        });

        viewInserirGv.findViewById(R.id.bttSalvarGv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(RegraCampo.obrigatorio(spnStatus, getContext())){
                if(RegraCampo.obrigatorio(edtDtInicial, getContext())){
                    if(RegraCampo.obrigatorio(edtDtFinal, getContext())){

                        if(!compareDate(edtDtInicial.getText().toString(), edtDtFinal.getText().toString())){
                            Alert a = new Alert(getContext(), getString(R.string.atencao),"A data inicial não pode ser superior a data final!");
                            return;
                        }

                        //Inclui no banco
                        ContentValues valores;
                        valores = new ContentValues();
                        valores.put(GestaoDeViagens.NRGV, edtNrGv.getText().toString());
                        valores.put(GestaoDeViagens.DTINICIAL, ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                        valores.put(GestaoDeViagens.DTFINAL, ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"));
                        valores.put(GestaoDeViagens.STATUS, Status.getId(
                                String.valueOf(spnStatus.getAdapter().getItem(spnStatus.getSelectedItemPosition()))));

                        if (idP.trim().isEmpty()) {
                            lancamentoDao.insert(GestaoDeViagens.TABELA, valores);
                            new SnackBar(getString(R.string.registroInserido), linearFiltro, Snackbar.LENGTH_LONG);
                        } else {
                            lancamentoDao.update(GestaoDeViagens.TABELA, valores, Categorias.ID + "=" + idP);
                            new SnackBar(getString(R.string.registroAlterado), linearFiltro, Snackbar.LENGTH_LONG);
                        }

                        alert.dismiss();
                        carregarCard();

                    }
                }
            }

            }
        });

        viewInserirGv.findViewById(R.id.bttCancelarGv).setOnClickListener(new View.OnClickListener() {
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
            Alert a = new Alert(getContext(), getString(R.string.atencao),"A data inicial não pode ser superior a data final!");
            return;
        }

        String where = "("+GestaoDeViagens.DTINICIAL+" between DATE(?) and DATE(?) "+
            " or "+GestaoDeViagens.DTFINAL+" between DATE(?) and DATE(?))"+
            " and ("+GestaoDeViagens.STATUS+" = ? or ? = '0')";

        String[] valorWhere = new String[]{
                ConverterDate.StrToStr(edtFiltroDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtFiltroDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtFiltroDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtFiltroDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                String.valueOf(spnFiltroStatus.getSelectedItemPosition()),
                String.valueOf(spnFiltroStatus.getSelectedItemPosition())
        };

        //Faz o select de todos os dados passando por parametros a tabela, os campos e a ordem
        String[] campos =  {GestaoDeViagens.ID, GestaoDeViagens.NRGV, GestaoDeViagens.STATUS, GestaoDeViagens.DTINICIAL, GestaoDeViagens.DTFINAL};
        Cursor cursor = lancamentoDao.getAll(GestaoDeViagens.TABELA, campos, where, valorWhere, GestaoDeViagens.DTINICIAL+" asc, "+GestaoDeViagens.DTFINAL+" asc");

        ArrayList<GestaoDeViagem> list = new ArrayList<GestaoDeViagem>();
        Float valorGv;

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {

                    valorGv = lancamentoDao.getSum(GestaoDeViagensItens.TABELA, GestaoDeViagensItens.VALOR, GestaoDeViagensItens.IDGV+"="+cursor.getInt(cursor.getColumnIndexOrThrow(GestaoDeViagens.ID)));

                    list.add(new GestaoDeViagem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(GestaoDeViagens.ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagens.NRGV)),
                            Status.getDescricao(cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagens.STATUS))),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagens.DTINICIAL)),"yyyy-MM-dd","dd/MM/yyyy"),
                            ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagens.DTFINAL)),"yyyy-MM-dd","dd/MM/yyyy"),
                            valorGv));

                    valorGv=0f;
                    cursor.moveToNext();
                }
            }else{
                Toast.makeText(getContext(), getString(R.string.nenhumRegistro),
                        Toast.LENGTH_SHORT).show();
            }
        }

        if(btnAdicionar.getVisibility() == View.GONE)
            btnAdicionar.show();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CardAdapter(getContext(),list));
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


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(v);
        alertaCalendar = builder.create();
        alertaCalendar.show();
    }


    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        private List<GestaoDeViagem> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<GestaoDeViagem> notes) {
            mList = notes;
            mContext = context;
        }

        @Override
        public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View notesView = inflater.inflate(R.layout.card_gvs, parent, false);

            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }

        private void alert_opcoes_card(final String idP, final String nrGvP, final String dtInicialP, final String dtFinalP, final String tipoP) {

            new AlertDynamic.Builder(getContext())
                    .setTitle(getString(R.string.escolhaUmaOpcao))
                    .setBackgroundColor(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setPositiveBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setPositiveBtnText(getString(R.string.excluir))
                    .setNegativeBtnText(getString(R.string.editar))
                    .setNegativeBtnBackground(Color.parseColor(getResources().getString(0+R.color.greyDark)))
                    .setAnimation(AlertDynamic.Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_error_outline_white_24dp, AlertDynamic.Icon.Visible)
                    .OnPositiveClicked(new AlertDynamic.AlertDynamicDialogListener(){
                        @Override
                        public void OnClick() {
                            // ACAO EXCLUIR
                            lancamentoDao.delete(GestaoDeViagens.TABELA, GestaoDeViagens.ID +"="+idP);
                            carregarCard();
                            new SnackBar(getString(R.string.registroInserido), linearFiltro, Snackbar.LENGTH_LONG);
                        }
                    })
                    .OnNegativeClicked(new AlertDynamic.AlertDynamicDialogListener() {
                        @Override
                        public void OnClick() {
                            // ACAO EDITAR
                            adicionarRegistro(idP, nrGvP, dtInicialP, dtFinalP, tipoP);
                        }
                    })
                    .build();
        }


        private Context getContext() {
            return mContext;
        }

        @Override
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {

            final GestaoDeViagem obj = mList.get(position);

            TextView nrGv = viewHolder.nrGv;
            nrGv.setText(obj.getNrGv());

            TextView status = viewHolder.status;
            status.setText(obj.getStatus());

            TextView dtInicial = viewHolder.dtInicial;
            dtInicial.setText(obj.getDtInicial());

            TextView dtFinal = viewHolder.dtFinal;
            dtFinal.setText(obj.getDtFinal());

            TextView valor = viewHolder.vlTotal;
            valor.setText(FormatNumber.numToString(obj.getValorTotal(),"###,##0.00"));

            viewHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Vibrar();
                    alert_opcoes_card(obj.getId().toString(),
                            obj.getNrGv(),
                            obj.getDtInicial(),
                            obj.getDtFinal(),
                            obj.getStatus());
                    return false;
                }
            });

            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(getContext(), GestaoDeViagemItemActivity.class);
                    it.putExtra("gv", obj);
                    startActivity(it);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView nrGv, status, dtInicial, dtFinal, vlTotal;
            CardView card;

            public ViewHolder(View itemView) {
                super(itemView);

                nrGv = (TextView)itemView.findViewById(R.id.cardGvNr);
                status = (TextView)itemView.findViewById(R.id.cardGvStatus);
                dtInicial = (TextView)itemView.findViewById(R.id.cardGvDtInicial);
                dtFinal = (TextView)itemView.findViewById(R.id.cardGvDtFinal);
                vlTotal = (TextView)itemView.findViewById(R.id.cardGvTotal);
                card = (CardView)itemView.findViewById(R.id.cardGv);
            }
        }
    }

}
