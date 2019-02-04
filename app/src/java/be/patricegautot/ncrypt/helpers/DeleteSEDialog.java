package be.patricegautot.ncrypt.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import be.patricegautot.ncrypt.R;

public class DeleteSEDialog {

    public interface OnDeleteSEDialogClickListener{
        void onPositiveClick();
        void onNegativeClick();
    }

    OnDeleteSEDialogClickListener mListener;

    public void setOnDeleteSEDialogClickListener(OnDeleteSEDialogClickListener v){
        mListener = v;
    }

    public void showDeleteDialog(final Context context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("DELETING ELEMENTS");

        View checkBoxView = View.inflate(context, R.layout.checkbox_dont_ask_me_again, null);
        CheckBox checkBox = checkBoxView.findViewById(R.id.checkbox_dontaskmeagain);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesHelper.setQuickDeleteStatus(context, b);
            }
        });

        alertDialog.setView(checkBoxView);
        alertDialog.setMessage(R.string.dialog_delete_se_message);
        alertDialog.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onPositiveClick();
            }
        });
        alertDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onNegativeClick();
            }
        });
        alertDialog.show();
    }
}
