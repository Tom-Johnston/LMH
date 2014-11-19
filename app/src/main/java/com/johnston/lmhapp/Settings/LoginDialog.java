package com.johnston.lmhapp.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.johnston.lmhapp.ImageGeneratorAsync;
import com.johnston.lmhapp.MainActivity;
import com.johnston.lmhapp.R;

/**
 * Created by Jake on 14/11/2014.
 */
public class LoginDialog extends DialogFragment {
    View view;

    static LoginDialog newInstance() {
        LoginDialog f = new LoginDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.login_dialog, null);
        builder.setView(view);
        builder.setTitle("Input Login Details");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText passwordView = (EditText) view.findViewById(R.id.Password);
                String password = passwordView.getText().toString();
                EditText usernameView = (EditText) view.findViewById(R.id.Username);
                String username = usernameView.getText().toString();
                SharedPreferences LogIn = getActivity().getSharedPreferences("LogIn", 0);
                SharedPreferences.Editor editor = LogIn.edit();
                editor.putString("Username", username);
                editor.putString("Password", password);
                Toast toast = Toast.makeText(getActivity(), "Details Saved.", Toast.LENGTH_SHORT);
                toast.show();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                editor.commit();
                final MainActivity main = (MainActivity)getActivity();
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bitmap bitmap = (Bitmap) message.obj;
                        ((ImageView) main.findViewById(R.id.graphic)).setImageBitmap(bitmap);
                    }
                };
                int sizex = (int) ((main.findViewById(R.id.graphic)).getWidth() * 1.1);
//        Make the sizex an even number.
                sizex = (sizex / 2) * 2;
                int sizey = sizex / 2;
                new ImageGeneratorAsync().execute(username, handler, sizex, sizey, getActivity().getApplicationContext(),false);
                Byte three = 3;
                editor.putString("Name","");
                ((TextView) main.findViewById(R.id.name)).setText("");
                editor.commit();
                final Handler nameHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        String name = (String) message.obj;
                        ((TextView) main.findViewById(R.id.name)).setText(name);
                    }
                };
                TextView usernameTextView = (TextView) main.findViewById(R.id.username);
                usernameTextView.setText(username);
                main.getInfo(null, nameHandler, three);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        SharedPreferences LogIn = getActivity().getSharedPreferences("LogIn", 0);
            EditText Username = (EditText) view.findViewById(R.id.Username);
            EditText Password = (EditText) view.findViewById(R.id.Password);
            Username.setText(LogIn.getString("Username", ""));
            Password.setText(LogIn.getString("Password", ""));
        ((CheckBox)view.findViewById(R.id.checkBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText Password = (EditText) view.findViewById(R.id.Password);
                if (isChecked) {
                    Password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    Password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        final AlertDialog d = builder.create();
        return d;
    }

}
