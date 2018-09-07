package com.app.rafael.gestaodeviagem;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.db.tabelas.Despesas;
import com.app.rafael.gestaodeviagem.db.tabelas.GestaoDeViagensItens;
import com.app.rafael.gestaodeviagem.entidades.Relatorio;
import com.app.rafael.gestaodeviagem.utilidades.ConverterDate;
import com.app.rafael.gestaodeviagem.utilidades.FormatNumber;
import com.app.rafael.gestaodeviagem.utilidades.GetDate;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends Fragment {

    private View view;
    private Dml dml;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_home, container, false);
        inicialise();
        carregarCard();
        return view;
    }


    public void inicialise(){
        dml = new Dml(getContext());
        recyclerView = (RecyclerView)view.findViewById(R.id.homeRecyclerView);
    }


    public void carregarCard(){

        ArrayList<Relatorio> list = new ArrayList<Relatorio>();

        String[] tabelaReceita = getSqlCarregarCard("RECEITA", "TABLE");
        String[] atributosReceita = getSqlCarregarCard("RECEITA", "COLUMN");
        String[] agrupamentoReceita = getSqlCarregarCard("RECEITA", "GROUPBY");
        String[] clausulasReceita = getSqlCarregarCard("RECEITA", "WHERE");
        String[] valorClausulasReceita = getSqlCarregarCard("RECEITA", "VALORWHERE");
        String[] ordemReceita = getSqlCarregarCard("RECEITA", "ORDERBY");

        Cursor cursorReceita = dml.getAll(tabelaReceita[0], atributosReceita, clausulasReceita[0], valorClausulasReceita, ordemReceita[0], agrupamentoReceita[0]);
        list = montaList(cursorReceita, list, "RECEITA");


        String[] tabela = getSqlCarregarCard("DESPESA", "TABLE");
        String[] atributos = getSqlCarregarCard("DESPESA", "COLUMN");
        String[] agrupamento = getSqlCarregarCard("DESPESA", "GROUPBY");
        String[] clausulas = getSqlCarregarCard("DESPESA", "WHERE");
        String[] valorClausulas = getSqlCarregarCard("DESPESA", "VALORWHERE");
        String[] ordem = getSqlCarregarCard("DESPESA", "ORDERBY");

        Cursor cursor = dml.getAll(tabela[0], atributos, clausulas[0], valorClausulas, ordem[0], agrupamento[0]);
        list = montaList(cursor, list, "DESPESA");

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CardAdapter(getContext(), list));
    }


    public String[] getSqlCarregarCard(String tipoItemP, String tipoRetornoP){
        String[] sql;
        if(tipoItemP.equals("DESPESA")){
            if (tipoRetornoP.equals("TABLE"))
                return new String[]{Despesas.TABELA + " a " +
                        " join " + Categorias.TABELA + " b on(a." + Despesas.CATEGORIA + " = b." + Categorias.ID + ")"};

            if (tipoRetornoP.equals("COLUMN"))
                return new String[]{"sum(" + Despesas.VALOR + ")as valor, " +
                        "avg(" + Despesas.VALOR + ")as media, " +
                        "date(" + Despesas.DATA + ", 'start of month') as ds"};

            if (tipoRetornoP.equals("GROUPBY"))
                return new String[]{"date(" + Despesas.DATA + ", 'start of month')"};

            if (tipoRetornoP.equals("WHERE"))
                return new String[]{Despesas.DATA + " between DATE(?) and DATE(?) "};

            if (tipoRetornoP.equals("VALORWHERE"))
                return new String[]{
                        ConverterDate.StrToStr(GetDate.today(true, -3, 0).toString(), "dd/MM/yyyy", "yyyy-MM-dd"),
                        ConverterDate.StrToStr(GetDate.today(false, 0, 0).toString(), "dd/MM/yyyy", "yyyy-MM-dd")};

            if (tipoRetornoP.equals("ORDERBY"))
                return new String[]{"date(" + Despesas.DATA + ", 'start of month')desc"};

        }else if(tipoItemP.equals("RECEITA")){
            if (tipoRetornoP.equals("TABLE"))
                return new String[]{GestaoDeViagensItens.TABELA + " a "};

            if (tipoRetornoP.equals("COLUMN"))
                return new String[]{"sum(" + GestaoDeViagensItens.VALOR + ")as valor, " +
                        "avg(" + GestaoDeViagensItens.VALOR + ")as media, " +
                        "date(" + GestaoDeViagensItens.DATA + ", 'start of month') as ds"};

            if (tipoRetornoP.equals("GROUPBY"))
                return new String[]{"date(" + GestaoDeViagensItens.DATA + ", 'start of month')"};

            if (tipoRetornoP.equals("WHERE"))
                return new String[]{GestaoDeViagensItens.DATA + " between DATE(?) and DATE(?) and "+GestaoDeViagensItens.CATEGORIA+" = 2"};

            if (tipoRetornoP.equals("VALORWHERE"))
                return new String[]{
                        ConverterDate.StrToStr(GetDate.today(true, -3, 0).toString(), "dd/MM/yyyy", "yyyy-MM-dd"),
                        ConverterDate.StrToStr(GetDate.today(false, 0, 0).toString(), "dd/MM/yyyy", "yyyy-MM-dd")};

            if (tipoRetornoP.equals("ORDERBY"))
                return new String[]{"date(" + GestaoDeViagensItens.DATA + ", 'start of month')desc"};
        }

        return null;
    }


    public ArrayList<Relatorio> montaList(Cursor cursor, ArrayList<Relatorio> list, String tipoP){
        if(cursor != null){
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {

                    if(tipoP.equals("RECEITA")){
                        list.add(new Relatorio(
                                ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow("ds")),"yyyy-MM-dd","MM/yyyy"),
                                cursor.getString(cursor.getColumnIndexOrThrow("valor")))
                        );
                    }else if(tipoP.equals("DESPESA")){
                        for(int i=0; i < list.size(); i++){
                            if(list.get(i).getDescricao().equals(ConverterDate.StrToStr(cursor.getString(cursor.getColumnIndexOrThrow("ds")),"yyyy-MM-dd","MM/yyyy"))){
                                list.get(i).setValorDespesa(cursor.getString(cursor.getColumnIndexOrThrow("valor")));
                            }else if(list.get(i).getValorDespesa()==null ||list.get(i).getValorDespesa().isEmpty()){
                                list.get(i).setValorDespesa("0");
                            }
                        }
                    }
                    cursor.moveToNext();
                }
            }
        }
        return list;
    }


    public Float getMediaItem(Float total, Float valor){
        if(valor <=0)
            return 0f;

        return valor / (total / 100);
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
            View notesView = inflater.inflate(R.layout.card_home, parent, false);
            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }


        private Context getContext() {
            return mContext;
        }
        @Override
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {
            final Relatorio obj = mList.get(position);

            TextView descricao = viewHolder.mes;
            descricao.setText(obj.getDescricao());

            TextView valorReceita = viewHolder.valorReceita;
            valorReceita.setText("R$ "+ FormatNumber.numToString(Float.parseFloat(obj.getValor()),"###,##0.00"));

            TextView valorDespesa = viewHolder.valorDespesa;
            valorDespesa.setText("R$ "+ FormatNumber.numToString(Float.parseFloat(obj.getValorDespesa()),"###,##0.00"));

            TextView valorResultado = viewHolder.valorResultado;
            valorResultado.setText("R$ "+ FormatNumber.numToString(Float.parseFloat(obj.getValor()) - Float.parseFloat(obj.getValorDespesa()),"###,##0.00"));

            Float total = Float.parseFloat(obj.getValor()) + Float.parseFloat(obj.getValorDespesa());

            ProgressBar progressBar = viewHolder.progressBar;
            progressBar.setProgress(Math.round(getMediaItem(total, Float.parseFloat(obj.getValor()))));

            ProgressBar progressBarDespesa = viewHolder.progressBarDespesa;
            progressBarDespesa.setMax(100);

            if(Math.round(Float.parseFloat(obj.getValorDespesa())) > 0f){
                progressBarDespesa.setProgress(Math.round(getMediaItem(total, Float.parseFloat(obj.getValorDespesa()))));
            }else{
                progressBarDespesa.setProgress(1);
            }

            ProgressBar progressBarResultado = viewHolder.progressBarResultado;
            progressBarResultado.setProgress(Math.round(getMediaItem(total, Float.parseFloat(obj.getValor()) - Float.parseFloat(obj.getValorDespesa()))));
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mes, valorReceita, valorDespesa, valorResultado;
            ProgressBar progressBar, progressBarDespesa, progressBarResultado;
            CardView card;

            public ViewHolder(View itemView) {
                super(itemView);
                mes = (TextView)itemView.findViewById(R.id.cardHomeTxtDescricao);
                valorReceita = (TextView)itemView.findViewById(R.id.cardHomeTxtMedia);
                valorDespesa = (TextView)itemView.findViewById(R.id.cardHomeTxtMediaDespesa);
                valorResultado = (TextView)itemView.findViewById(R.id.cardHomeTxtMediaResultado);
                progressBar = (ProgressBar)itemView.findViewById(R.id.cardHomeProgress);
                progressBarDespesa = (ProgressBar)itemView.findViewById(R.id.cardHomeProgressDespesa);
                progressBarResultado = (ProgressBar)itemView.findViewById(R.id.cardHomeProgressResultado);
                card = (CardView) itemView.findViewById(R.id.cardHome);
            }
        }
    }

}
