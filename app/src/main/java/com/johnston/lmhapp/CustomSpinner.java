package com.johnston.lmhapp;

/**
 * Created by Jake on 29/10/2014.
 */


import java.lang.reflect.Method;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Spinner;

//com.mitosoft.ui.widgets.NoDefaultSpinner
public class CustomSpinner extends Spinner {

    private int lastSelected = 0;
    private static Method s_pSelectionChangedMethod = null;


    static {
        try {
            Class noparams[] = {};
            Class targetClass = AdapterView.class;

            s_pSelectionChangedMethod = targetClass.getDeclaredMethod("selectionChanged", noparams);
            if (s_pSelectionChangedMethod != null) {
                s_pSelectionChangedMethod.setAccessible(true);
            }

        } catch( Exception e ) {
            Log.e("Custom spinner, reflection bug:", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public CustomSpinner(Context context) {
        super(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void testReflectionForSelectionChanged() {
        try {
            Class noparams[] = {};
            s_pSelectionChangedMethod.invoke(this, noparams);
        } catch (Exception e) {
            Log.e("Custom spinner, reflection bug: ", e.getMessage());
            e.printStackTrace();
        }
    }




    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(this.lastSelected == this.getSelectedItemPosition())
            testReflectionForSelectionChanged();
        if(!changed)
            lastSelected = this.getSelectedItemPosition();

        super.onLayout(changed, l, t, r, b);
    }
}