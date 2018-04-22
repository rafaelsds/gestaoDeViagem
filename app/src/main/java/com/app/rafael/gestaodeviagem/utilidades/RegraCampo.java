package com.app.rafael.gestaodeviagem.utilidades;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.rafael.gestaodeviagem.R;

public class RegraCampo {

    public static Boolean obrigatorio(TextView item, Context c){
        if(item.getText()==null || item.getText().toString().isEmpty()) {
            item.setError(c.getString(R.string.campoObrigatorio));
            return false;
        }
        return true;
    }

    public static Boolean obrigatorio(EditText item, Context c){
        if(item.getText().toString().isEmpty()) {
            item.requestFocus();
            item.setError(c.getString(R.string.campoObrigatorio));
            return false;
        }
        item.setError(null);
        return true;
    }

    public static Boolean obrigatorio(Spinner item, Context c){
        if(item.getSelectedItem().toString().trim().isEmpty()){
            item.requestFocusFromTouch();
            return obrigatorio((TextView)item.getSelectedView(), c);
        }
        return true;
    }

}