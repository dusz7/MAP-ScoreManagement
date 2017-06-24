package com.hitiot.dusz7.mtdex.ex1;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

import com.hitiot.dusz7.mtdex.R;


public class CalculatorActivity extends AppCompatActivity{

    EditText editTextFormula;
    EditText textTextResult;
    // 调用的Java某计算引擎库
    private Symbols mSymbols;

    static {
        System.loadLibrary("native-calculate");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        editTextFormula = (EditText) findViewById(R.id.edit_formula);
        textTextResult = (EditText) findViewById(R.id.text_result);

        editTextFormula.setKeyListener(null);
        textTextResult.setKeyListener(null);

        mSymbols = new Symbols();
    }

    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.eq:
                onEquals();
                break;
            case R.id.del:
                onDelete();
                break;
            case R.id.clr:
                onClear();
                break;
            default:
                editTextFormula.append(((Button) v).getText());
                break;
        }
    }

    /**
     * 按"="之后，根据输入的公式计算结果
     */
    private void onEquals() {
        String formula = editTextFormula.getText().toString();

        // 调用c++ native方法
        String result = calculateResultFromJNI(formula);
        textTextResult.setText(result+"");

        // 调用Java 计算引擎库
//        try {
//            Double result = mSymbols.eval(formula);
//            textTextResult.setText(result+"");
//        }catch (SyntaxException e) {
//            e.printStackTrace();
//        }


    }

    /**
     * 按"del"回退键以后
     */
    private void onDelete() {
        // Delete works like backspace; remove the last character from the expression.
        final Editable formulaText = editTextFormula.getEditableText();
        final int formulaLength = formulaText.length();
        if (formulaLength > 0) {
            formulaText.delete(formulaLength - 1, formulaLength);
        }
    }

    /**
     * 按"clr"清除键以后
     */
    private void onClear() {
        if (TextUtils.isEmpty(editTextFormula.getText())) {
            return;
        }

        editTextFormula.getEditableText().clear();
        textTextResult.getEditableText().clear();

    }

    /**
     * 声明计算的native方法
     * @param formula
     * @return
     */
    public native String calculateResultFromJNI(String formula);
}
