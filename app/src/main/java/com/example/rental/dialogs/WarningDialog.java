package com.example.rental.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.rental.R;
import com.google.firebase.database.FirebaseDatabase;


public class WarningDialog extends AppCompatDialogFragment {

    private String key;
    private String key2;
    private int type;

    public WarningDialog(String key, String key2, int type) {
        this.key = key;
        this.key2 = key2;
        this.type = type;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_popup, null);
        builder.setView(view);

        (view.findViewById(R.id.cancelPopUp)).setOnClickListener(v -> {
            dismiss();
        });

        (view.findViewById(R.id.buttonYes)).setOnClickListener(v -> {
            if (type == 1) {
                FirebaseDatabase.getInstance().getReference("post").child(key).removeValue();
            } else if (type == 2) {
                FirebaseDatabase.getInstance().getReference("post").child(key).child("comments").child(key2).removeValue();
            }
            dismiss();
        });

        (view.findViewById(R.id.buttonNo)).setOnClickListener(v -> {
            dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        return dialog;
    }


}
