package com.app.rafael.gestaodeviagem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Despesas;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagens;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagensItens;
import com.app.rafael.gestaodeviagem.db.tabelas.Rats;
import com.app.rafael.gestaodeviagem.db.tabelas.RatsItens;
import com.app.rafael.gestaodeviagem.entidades.Categoria;
import com.app.rafael.gestaodeviagem.entidades.GestaoDeViagem;
import com.app.rafael.gestaodeviagem.entidades.Relatorio;
import com.app.rafael.gestaodeviagem.entidades.Status;
import com.app.rafael.gestaodeviagem.entidades.TipoCategoria;
import com.app.rafael.gestaodeviagem.utilidades.Alert;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.FormatNumber;
import com.app.rafael.gestaodeviagem.utilidades.GetDate;
import com.hanks.library.AnimateCheckBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RelatorioActivity extends Fragment{
    private TextView relTxtTotal;
    private AnimateCheckBox relCkeckGv, relCkeckRat;
    private LinearLayout linearRat, linearGv, relLinearReceita;
    private Spinner relSpnTipo, relSpnCatGv, relSpnStatus;
    private Dml dml;
    private EditText edtDtInicial, edtDtFinal;
    private AlertDialog alertaCalendar;
    private Calendar calendar;
    private Integer diaCalendar, mesCalendar, anoCalendar;
    private String dataCalendar;
    private ImageView imgOrder;
    private RecyclerView recyclerView;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_relatorio, container, false);

        inicialise();
        botoes();
        gerarSpinnerPadrao();
        carregarCard();
        return view;

    }


    public void inicialise(){
        relTxtTotal = (TextView)view.findViewById(R.id.relTxtTotal);
        relCkeckGv = (AnimateCheckBox)view.findViewById(R.id.relCkeckGv);
        relCkeckGv.setChecked(true);
        relCkeckRat = (AnimateCheckBox)view.findViewById(R.id.relCkeckRat);
        relCkeckRat.setChecked(true);
        linearGv = (LinearLayout)view.findViewById(R.id.relLinearGv);
        linearRat = (LinearLayout)view.findViewById(R.id.relLinearRat);
        relLinearReceita = (LinearLayout)view.findViewById(R.id.relLinearReceita);
        relSpnTipo = (Spinner)view.findViewById(R.id.relSpnTipo);
        relSpnCatGv = (Spinner)view.findViewById(R.id.relSpnCatGv);
        relSpnStatus = (Spinner)view.findViewById(R.id.relSpnStatus);
        dml = new Dml(getContext());
        edtDtInicial = (EditText)view.findViewById(R.id.relEdtDtInicial);
        edtDtInicial.setText(GetDate.today(true,-1,0).toString());
        edtDtFinal = (EditText)view.findViewById(R.id.relEdtDtFinal);
        edtDtFinal.setText(GetDate.today(false,0,0).toString());
        calendar = Calendar.getInstance();
        imgOrder = (ImageView)view.findViewById(R.id.relImgOrder);
        recyclerView = (RecyclerView)view.findViewById(R.id.relRecyclerView);
    }


    public void gerarSpinnerPadrao(){
        montaAdapterSpinner(relSpnTipo, new ArrayList<String>(){{
            add("Receita");
            add("Despesa");
        }});

        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getContext(),R.layout.spinner_item_filter, Status.values());
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        relSpnStatus.setAdapter(spinnerArrayAdapter);
        relSpnStatus.setSelection(0);

        atualizarSpinnerCatGv();
    }


    public void atualizarSpinnerCatGv(){
        if(relSpnTipo.getSelectedItem().equals("Despesa")){
            montaAdapterSpinner(relSpnCatGv, obterCategoriaDb());
            return;
        }
        montaAdapterSpinner(relSpnCatGv, obterGvDb());
    }


    public ArrayList<Categoria> obterCategoriaDb(){
        String[] camposSelect={Categorias.ID, Categorias.DESCRICAO, Categorias.TIPO};
        String[] valorCamposWhere = { TipoCategoria.getId("Despesa") };
        Cursor cursor = dml.getAll(Categorias.TABELA, camposSelect, Categorias.TIPO+"=?", valorCamposWhere,null);

        ArrayList<Categoria> list = new ArrayList<Categoria>();
        list.add(new Categoria());

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


    public ArrayList<GestaoDeViagem> obterGvDb(){
        String[] camposSelect={GestaoDeViagens.ID, GestaoDeViagens.NRGV};

        String where = "("+GestaoDeViagens.DTINICIAL+" between DATE(?) and DATE(?) "+
                " or "+GestaoDeViagens.DTFINAL+" between DATE(?) and DATE(?))";

        String[] valorWhere = new String[]{
                ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd")
        };

        Cursor cursor = dml.getAll(GestaoDeViagens.TABELA, camposSelect, where, valorWhere,null);

        ArrayList<GestaoDeViagem> list = new ArrayList<GestaoDeViagem>();
        list.add(new GestaoDeViagem());

        if(cursor != null) {
            if (cursor.moveToFirst()){
                while (!cursor.isAfterLast()) {
                    list.add(new GestaoDeViagem(
                            cursor.getInt(cursor.getColumnIndexOrThrow(GestaoDeViagens.ID)),
                            "GV "+cursor.getString(cursor.getColumnIndexOrThrow(GestaoDeViagens.NRGV))));
                    cursor.moveToNext();
                }
            }
        }
        return list;
    }


    public void montaAdapterSpinner(Spinner spinnger, ArrayList list){
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(getContext(), R.layout.spinner_item_filter, list);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnger.setAdapter(spinnerArrayAdapter);
    }


    public void botoes(){
        linearGv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(relCkeckGv.isChecked()){
                relCkeckGv.setChecked(false);
                return;
            }
            relCkeckGv.setChecked(true);
            }
        });

        relCkeckGv.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                carregarCard();
            }
        });

        linearRat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(relCkeckRat.isChecked()){
                relCkeckRat.setChecked(false);
                return;
            }
            relCkeckRat.setChecked(true);
            }
        });

        relCkeckRat.setOnCheckedChangeListener(new AnimateCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                carregarCard();
            }
        });

        relSpnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                atualizarSpinnerCatGv();

                if(relSpnTipo.getSelectedItem().equals("Despesa")) {
                    relLinearReceita.setVisibility(View.GONE);
                    return;
                }

                relLinearReceita.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        relSpnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carregarCard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        relSpnCatGv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                carregarCard();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        edtDtInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtInicial);
            }
        });

        edtDtInicial.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                atualizarSpinnerCatGv();
            }
        });

        edtDtFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obterDataCalendar(edtDtFinal);
            }
        });

        edtDtFinal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                atualizarSpinnerCatGv();
            }
        });

        imgOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgOrder.getRotation() == 0){
                    imgOrder.setRotation(180);
                }else{
                    imgOrder.setRotation(0);
                }
                carregarCard();
            }
        });
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



    public void carregarCard(){

        if(!compareDate(edtDtInicial.getText().toString(), edtDtFinal.getText().toString())){
            Alert a = new Alert(getContext(), getString(R.string.atencao),"A data inicial não pode ser superior a data final!");
            return;
        }

        ArrayList<Relatorio> list = new ArrayList<Relatorio>();
        switch(relSpnTipo.getSelectedItem().toString()){
            case "Receita":

                if(relCkeckGv.isChecked()) {
                    String[] tabela = getSqlCarregarCard("GV", "TABLE");
                    String[] atributos = getSqlCarregarCard("GV", "COLUMN");
                    String[] agrupamento = getSqlCarregarCard("GV", "GROUPBY");
                    String[] clausulas = getSqlCarregarCard("GV", "WHERE");
                    String[] valorClausulas = getSqlCarregarCard("GV", "VALORWHERE");
                    String[] ordem = getSqlCarregarCard("GV", "ORDERBY");

                    Cursor cursor = dml.getAll(tabela[0], atributos, clausulas[0], valorClausulas, ordem[0], agrupamento[0]);
                    list = montaList(cursor, list);
                }

                if(relCkeckRat.isChecked()) {
                    String[] tabela = getSqlCarregarCard("RAT", "TABLE");
                    String[] atributos = getSqlCarregarCard("RAT", "COLUMN");
                    String[] agrupamento = getSqlCarregarCard("RAT", "GROUPBY");
                    String[] clausulas = getSqlCarregarCard("RAT", "WHERE");
                    String[] valorClausulas = getSqlCarregarCard("RAT", "VALORWHERE");
                    String[] ordem = getSqlCarregarCard("RAT", "ORDERBY");

                    Cursor cursor = dml.getAll(tabela[0], atributos, clausulas[0], valorClausulas, ordem[0], agrupamento[0]);
                    list = montaList(cursor, list);
                }
                break;

            case "Despesa":
                String[] tabela = getSqlCarregarCard("DESPESA", "TABLE");

                String[] atributos = getSqlCarregarCard("DESPESA", "COLUMN");
                String[] agrupamento = getSqlCarregarCard("DESPESA", "GROUPBY");
                String[] clausulas = getSqlCarregarCard("DESPESA", "WHERE");
                String[] valorClausulas = getSqlCarregarCard("DESPESA", "VALORWHERE");
                String[] ordem = getSqlCarregarCard("DESPESA", "ORDERBY");

                Cursor cursor = dml.getAll(tabela[0], atributos, clausulas[0], valorClausulas, ordem[0], agrupamento[0]);
                list = montaList(cursor, list);
                break;

        }

        atualizarMediaList(list);
    }


    public ArrayList<Relatorio> montaList(Cursor cursor, ArrayList<Relatorio> list){
        if(cursor != null){
            if (cursor.moveToFirst()){
                if (cursor.moveToFirst()) {
                    while (!cursor.isAfterLast()) {

                        list.add(new Relatorio(
                                cursor.getString(cursor.getColumnIndexOrThrow("ds")),
                                cursor.getString(cursor.getColumnIndexOrThrow("valor")))
                        );
                        cursor.moveToNext();
                    }
                }
            }
        }
        return list;
    }



    public void atualizarMediaList(ArrayList<Relatorio> listP){

        ArrayList<Relatorio> list = new ArrayList<>();
        Float total=0f;

        for(Relatorio r : listP){
            if(Float.parseFloat(r.getValor())<=0)
                return;

            total+= Float.parseFloat(r.getValor());
        }

        for(Relatorio r : listP){
            list.add(new Relatorio(r.getDescricao(), r.getValor(), FormatNumber.numToString(Float.parseFloat(r.getValor()) / (total / 100),"###,###.##")));
        }

        relTxtTotal.setText("R$ " + FormatNumber.numToString(total,"###,###,##0.00"));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CardAdapter(getContext(), list));

    }

    public String[] getSqlCarregarCard(String tipoItemP, String tipoRetornoP){
        String[] sql;

        switch(tipoItemP){
            case "GV":

                if(tipoRetornoP.equals("COLUMN"))
                    return new String[]{"sum("+GestaoDeViagensItens.VALOR+")as valor, " +
                            "avg("+GestaoDeViagensItens.VALOR+")as media, "+
                            Categorias.DESCRICAO+" as ds"
                    };

                if(tipoRetornoP.equals("TABLE"))
                    return new String[]{GestaoDeViagens.TABELA+" g "+
                            " join "+GestaoDeViagensItens.TABELA+" gv on(g."+GestaoDeViagens.ID+" = gv."+GestaoDeViagensItens.IDGV+")"+
                            " left join "+Categorias.TABELA+" c on(gv."+GestaoDeViagensItens.CATEGORIA+" = c."+Categorias.ID+")"};

                if(tipoRetornoP.equals("GROUPBY"))
                    return  new String[]{Categorias.DESCRICAO};

                if(tipoRetornoP.equals("WHERE"))
                    return new String[]{"("+GestaoDeViagens.DTINICIAL+" between DATE(?) and DATE(?) or "+
                            GestaoDeViagens.DTFINAL+" between DATE(?) and DATE(?))"+
                            " and ("+GestaoDeViagens.STATUS+" = ? or ? = '0')"+
                            " and (g."+GestaoDeViagens.ID+" = ? or ? ='0')"};

                if(tipoRetornoP.equals("VALORWHERE"))
                    return new String[]{
                    ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                    ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                    ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                    ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                    String.valueOf(relSpnStatus.getSelectedItemPosition()),
                    String.valueOf(relSpnStatus.getSelectedItemPosition()),
                    nvl(new GestaoDeViagem((GestaoDeViagem) relSpnCatGv.getSelectedItem()).getId(),"0"),
                    nvl(new GestaoDeViagem((GestaoDeViagem) relSpnCatGv.getSelectedItem()).getId(),"0")};

                if(tipoRetornoP.equals("ORDERBY"))
                    if(imgOrder.getRotation() == 0){
                        return new String[]{"sum(" + GestaoDeViagensItens.VALOR + ")desc"};
                    }else{
                        return new String[]{"sum(" + GestaoDeViagensItens.VALOR + ")asc"};
                    }

                break;

            case "RAT":
                if(tipoRetornoP.equals("COLUMN"))
                    return new String[]{"sum("+ RatsItens.VALOR+")as valor, " +
                            "avg("+RatsItens.VALOR+")as media, "+
                            "'Rat ' ||"+Rats.NRRAT +" as ds"
                    };

                if(tipoRetornoP.equals("TABLE"))
                    return new String[]{Rats.TABELA+" a "+
                            " join "+RatsItens.TABELA+" b on(a."+Rats.ID+" = b."+RatsItens.IDRAT+")"};

                if(tipoRetornoP.equals("GROUPBY"))
                    return  new String[]{Rats.NRRAT};

                if(tipoRetornoP.equals("WHERE"))
                    return new String[]{"("+Rats.DTINICIAL+" between DATE(?) and DATE(?) or "+
                            Rats.DTFINAL+" between DATE(?) and DATE(?))"+
                            " and ("+Rats.STATUS+" = ? or ? = '0')"+
                            " and (a."+Rats.IDGV+" = ? or ? ='0')"};

                if(tipoRetornoP.equals("VALORWHERE"))
                    return new String[]{
                            ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                            ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                            ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                            ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                            String.valueOf(relSpnStatus.getSelectedItemPosition()),
                            String.valueOf(relSpnStatus.getSelectedItemPosition()),
                            nvl(new GestaoDeViagem((GestaoDeViagem) relSpnCatGv.getSelectedItem()).getId(),"0"),
                            nvl(new GestaoDeViagem((GestaoDeViagem) relSpnCatGv.getSelectedItem()).getId(),"0")};

                if(tipoRetornoP.equals("ORDERBY"))
                    if(imgOrder.getRotation() == 0){
                        return new String[]{"sum(" + RatsItens.VALOR + ")desc"};
                    }else{
                        return new String[]{"sum(" + RatsItens.VALOR + ")asc"};
                    }

            break;

            case "DESPESA":
                if(tipoRetornoP.equals("TABLE"))
                    return new String[]{Despesas.TABELA+" a "+
                            " join "+Categorias.TABELA+" b on(a."+Despesas.CATEGORIA+" = b."+Categorias.ID+")"};

                if(tipoRetornoP.equals("COLUMN"))
                    return new String[]{"sum("+ Despesas.VALOR+")as valor, " +
                            "avg("+Despesas.VALOR+")as media, "+
                            Categorias.DESCRICAO +" as ds"
                    };

                if(tipoRetornoP.equals("GROUPBY"))
                    return  new String[]{Categorias.DESCRICAO};

                if(tipoRetornoP.equals("WHERE"))
                    return new String[]{Despesas.DATA+" between DATE(?) and DATE(?) "+
                            " and ("+Despesas.CATEGORIA+" = ? or ? = '0')"};

                if(tipoRetornoP.equals("VALORWHERE"))
                    return new String[]{
                            ConverterDate.StrToStr(edtDtInicial.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                            ConverterDate.StrToStr(edtDtFinal.getText().toString(),"dd/MM/yyyy","yyyy-MM-dd"),
                            String.valueOf(relSpnStatus.getSelectedItemPosition()),
                            String.valueOf(relSpnStatus.getSelectedItemPosition())};

                if(tipoRetornoP.equals("ORDERBY"))
                    if(imgOrder.getRotation() == 0){
                        return new String[]{"sum(" + Despesas.VALOR + ")desc"};
                    }else{
                        return new String[]{"sum(" + Despesas.VALOR + ")asc"};
                    }

                break;
        }

        return null;
    }

    public Boolean compareDate(String dtInicialP, String dtFinalP){
        if(dtInicialP.equals(dtFinalP))
            return true;

        return ConverterDate.StrToDate(dtInicialP).before(ConverterDate.StrToDate(dtFinalP));
    }


    public static String nvl(String value, String alternateValue) {
        if (value == null)
            return alternateValue;

        return value;
    }

    public static String nvl(Integer value, String alternateValue) {
        if (value == null)
            return alternateValue;

        return value.toString();
    }

    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
        private List<Relatorio> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<Relatorio> notes) {
            mList = notes;
            mContext = context;
        }

        @Override
        public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View notesView = inflater.inflate(R.layout.card_relatorio, parent, false);
            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }


        private Context getContext() {
            return mContext;
        }
        @Override
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {
            final Relatorio obj = mList.get(position);

            TextView descricao = viewHolder.descricao;
            descricao.setText(obj.getDescricao());

            TextView valor = viewHolder.valor;
            valor.setText("R$ "+ FormatNumber.numToString(Float.parseFloat(obj.getValor()),"###,##0.00"));

            TextView media = viewHolder.media;
            media.setText(obj.getMedia()+"%");

            ProgressBar progressBar = viewHolder.progressBar;
            progressBar.setProgress(Math.round(FormatNumber.stringtoNum(obj.getMedia())));

        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView descricao, valor, media;
            ProgressBar progressBar;
            CardView card;

            public ViewHolder(View itemView) {
                super(itemView);
                descricao = (TextView)itemView.findViewById(R.id.cardRelTxtDescricao);
                valor = (TextView)itemView.findViewById(R.id.cardRelValor);
                media = (TextView)itemView.findViewById(R.id.cardRelTxtMedia);
                progressBar = (ProgressBar)itemView.findViewById(R.id.cardRelProgress);
                card = (CardView)itemView.findViewById(R.id.cardRel);
            }
        }
    }

    public interface AtualizaIconeFiltro{
        public void esconder();
    }

}
