package com.johnston.lmhapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Johnston on 29/12/2014.
 */
public class PermissionFailedDialog extends DialogFragment {
    private View view;


    public static PermissionFailedDialog newInstance(String fail) {
        PermissionFailedDialog pfd = new PermissionFailedDialog();
        Bundle args = new Bundle();
        args.putString("fail", fail);
        pfd.setArguments(args);
        return pfd;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        String fail = (String) getArguments().get("fail");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.permission_failed_dialog, null);
        builder.setView(view);
        builder.setTitle("Cannot Continue");
        if (fail.contains("Your version of the app is no longer supported")) {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().finish();
                }
            });
        } else {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ((MainActivity) getActivity()).goToSettings();
                }
            });
        }
        fail = fail.substring(fail.indexOf("¬") + 1, fail.length() - 1);
        fail.replace("¬", "<br> &#8226;");
        ((TextView) view).setText(Html.fromHtml("The app cannot continue as: <br> &#8226;" + fail));
        return builder.create();
    }
}
