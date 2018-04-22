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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rafael.gestaodeviagem.db.Dml;
import com.app.rafael.gestaodeviagem.db.tabelas.Categorias;
import com.app.rafael.gestaodeviagem.entidades.Categoria;
import com.app.rafael.gestaodeviagem.entidades.TipoCategoria;
import com.app.rafael.gestaodeviagem.utilidades.RegraCampo;
import com.app.rafael.gestaodeviagem.utilidades.SnackBar;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CategoriasActivity extends AppCompatActivity {

    private FloatingActionButton btnAdicionar;
    private AlertDialog alertCategoria;
    private RecyclerView recyclerView;
    private Dml lancamentoDao;
    private Toolbar toolbar;
    private TextView toolbarTxtTitle;
    private LinearLayout categoriaLinear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorias);

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
        lancamentoDao = new Dml(CategoriasActivity.this);
        btnAdicionar = (FloatingActionButton)findViewById(R.id.bttAddCategoria);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerViewCategorias);
        toolbar = (Toolbar) findViewById(R.id.categoriaEdtToolbar);
        toolbarTxtTitle = (TextView)findViewById(R.id.categoriaTxtTitle);
        categoriaLinear = (LinearLayout) findViewById(R.id.categoriaLinear);
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
                adicionarCategoria("","","");
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


    @Override
    public void onBackPressed(){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        finish();
    }


    public void adicionarCategoria(final String idP, final String descricaoP, final String TipoP){

        final EditText edtDescricao;
        final Spinner spnTipo;
        final View viewInserirCategoria;
        final AlertDialog.Builder builder;
        final AlertDialog alert;

        viewInserirCategoria = getLayoutInflater().inflate(R.layout.inserir_categoria, null);
        builder = new AlertDialog.Builder(this);
        builder.setView(viewInserirCategoria);
        alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.getWindow().getAttributes().windowAnimations = R.style.AnimationBottomRightDiagonal;
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edtDescricao = (EditText) viewInserirCategoria.findViewById(R.id.edtDescricaoCategoria);
        edtDescricao.setText(descricaoP);

        spnTipo = (Spinner)viewInserirCategoria.findViewById(R.id.spnTipoCategoria);
        ArrayAdapter<TipoCategoria> spinnerArrayAdapter = new ArrayAdapter<TipoCategoria>(
                this,R.layout.spinner_item,TipoCategoria.values());
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spnTipo.setAdapter(spinnerArrayAdapter);

        if(!TipoP.trim().isEmpty()){
            spnTipo.setSelection(Integer.parseInt(TipoCategoria.getId(TipoP)));
        }

        viewInserirCategoria.findViewById(R.id.bttSalvarCategoria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RegraCampo.obrigatorio(edtDescricao, getApplicationContext())){

                    //Inclui no banco
                    ContentValues valores;
                    valores = new ContentValues();
                    valores.put(Categorias.DESCRICAO, edtDescricao.getText().toString());
                    valores.put(Categorias.TIPO, TipoCategoria.getId(
                            String.valueOf(spnTipo.getAdapter().getItem(spnTipo.getSelectedItemPosition()))));

                    if(idP.trim().isEmpty()){
                        lancamentoDao.insert(Categorias.TABELA, valores);
                        new SnackBar(getString(R.string.registroInserido), categoriaLinear, Snackbar.LENGTH_LONG);
                    }else{
                        lancamentoDao.update(Categorias.TABELA, valores, Categorias.ID+"="+idP);
                        new SnackBar(getString(R.string.registroAlterado), categoriaLinear, Snackbar.LENGTH_LONG);
                    }
                    alert.dismiss();
                    carregarCard();

                }
            }
        });

        viewInserirCategoria.findViewById(R.id.bttCancelarCategoria).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.show();
    }


    private void carregarCard() {
        //Faz o select de todos os dados passando por parametros a tabela, os campos e a ordem
        String[] campos =  {Categorias.ID, Categorias.DESCRICAO, Categorias.TIPO};
        Cursor cursor = lancamentoDao.getAll(Categorias.TABELA, campos, null, null,null);

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

            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.nenhumRegistro),
                        Toast.LENGTH_SHORT).show();
            }
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CardAdapter(CategoriasActivity.this,list));
    }


    public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

        private List<Categoria> mList;
        private Context mContext;
        AlertDialog alerta;

        public CardAdapter(Context context, List<Categoria> notes) {
            mList = notes;
            mContext = context;
        }


        @Override
        public CardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View notesView = inflater.inflate(R.layout.card_categorias, parent, false);

            CardAdapter.ViewHolder viewHolder = new CardAdapter.ViewHolder(notesView);
            return viewHolder;
        }


        private void alert_opcoes_card(final String idP, final String descricaoP, final String tipoP) {

            new FancyAlertDialog.Builder(CategoriasActivity.this)
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
                            lancamentoDao.delete(Categorias.TABELA, Categorias.ID +"="+idP);
                            carregarCard();
                            new SnackBar(getString(R.string.registroExcluido), categoriaLinear, Snackbar.LENGTH_LONG);
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            adicionarCategoria(idP, descricaoP, tipoP);
                        }
                    })
                    .build();

        }



        private Context getContext() {
            return mContext;
        }

        @Override
        public void onBindViewHolder(CardAdapter.ViewHolder viewHolder, final int position) {

            final Categoria obj = mList.get(position);

            TextView id = viewHolder.id;
            id.setText(obj.getId().toString());

            TextView descricao = viewHolder.descricao;
            descricao.setText(obj.getDescricao().toString());

            TextView tipo = viewHolder.tipo;
            tipo.setText(obj.getTipo().toString());


            viewHolder.card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Vibrar();
                    alert_opcoes_card(obj.getId().toString(),
                            obj.getDescricao().toString(),
                            obj.getTipo().toString());
                    return false;
                }
            });
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView id, descricao, tipo;
            CardView card;
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

            public ViewHolder(View itemView) {
                super(itemView);

                id = (TextView)itemView.findViewById(R.id.cardCategoriaId);
                descricao = (TextView)itemView.findViewById(R.id.cardCategoriaDescricao);
                tipo = (TextView)itemView.findViewById(R.id.cardCategoriaTipo);
                card = (CardView)itemView.findViewById(R.id.cardCategoria);
            }
        }
    }

}
