package be.patricegautot.ncrypt.customObjects;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.activities.GetInputActivity;
import be.patricegautot.ncrypt.activities.OutputActivity;
import be.patricegautot.ncrypt.activities.SavedActivity;
import be.patricegautot.ncrypt.helpers.DeleteSEDialog;
import be.patricegautot.ncrypt.helpers.Helpers;
import be.patricegautot.ncrypt.helpers.ImageSaver;
import be.patricegautot.ncrypt.helpers.Keys;
import be.patricegautot.ncrypt.helpers.SharedPreferencesHelper;

public class SavedEncryptionAdapter extends RecyclerView.Adapter<SavedEncryptionAdapter.ViewHolder>{

    private List<SavedEncryption> data;
    private Context context;
    private String type;
    private boolean pickup;

    public SavedEncryptionAdapter(Context context){
        this.context = context;
    }

    public SavedEncryptionAdapter(List<SavedEncryption> data, Context context, String type, boolean pickup){
        this.data = data;
        this.context = context;
        this.type = type;
        this.pickup = pickup;
    }

    public void setData(List<SavedEncryption> data){
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayout linearLayout= (LinearLayout) LayoutInflater.from(context)
                                        .inflate(R.layout.saved_encryption_item, viewGroup, false);
        return new ViewHolder(linearLayout, context, data.get(i));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final SavedEncryption savedEncryption = data.get(i);
        viewHolder.keyHintTV.setText("Hint for key : " + data.get(i).getHint());
        viewHolder.titleTV.setText(savedEncryption.getTitle());

        if(pickup){
            viewHolder.menuBtn.setVisibility(View.GONE);
            viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, GetInputActivity.class);
                    intent.putExtra(Keys.KEY_ACTION, Keys.ACTION_DECRYPT);
                    intent.putExtra(Keys.KEY_TYPE, type);
                    intent.putExtra(Keys.KEY_BASE_SE, savedEncryption);
                    context.startActivity(intent);
                }
            });
        }

        viewHolder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, viewHolder.menuBtn);

                popup.inflate(R.menu.menu_saved_option);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mo_edit:
                                if(type.equals(Keys.TYPE_TEXT)) Helpers.saveDialog(context, savedEncryption.getText(),
                                        savedEncryption.getTitle(), savedEncryption.getHint(), savedEncryption.getId());
                                else          Helpers.savePictureDialog(context, savedEncryption, savedEncryption.getTitle(),
                                        savedEncryption.getHint(), savedEncryption.getId(), "E");
                                return true;
                            case R.id.mo_delete:
                                if(SharedPreferencesHelper.getQuickDeleteStatus(context)){
                                    Helpers.deleteSavedEncryption(context, savedEncryption);
                                    data.remove(viewHolder.getAdapterPosition());
                                    notifyItemRemoved(viewHolder.getAdapterPosition());
                                    return true;
                                }
                                DeleteSEDialog deleteSEDialog = new DeleteSEDialog();
                                deleteSEDialog.setOnDeleteSEDialogClickListener(new DeleteSEDialog.OnDeleteSEDialogClickListener(){
                                    @Override
                                    public void onPositiveClick() {
                                        Helpers.deleteSavedEncryption(context, savedEncryption);
                                        data.remove(viewHolder.getAdapterPosition());
                                        notifyItemRemoved(viewHolder.getAdapterPosition());
                                    }

                                    @Override
                                    public void onNegativeClick() {

                                    }
                                });
                                deleteSEDialog.showDeleteDialog(context);

                                return true;
                            case R.id.mo_decrypt:
                                Intent intent = new Intent(context, GetInputActivity.class);
                                intent.putExtra(Keys.KEY_ACTION, Keys.ACTION_DECRYPT);
                                intent.putExtra(Keys.KEY_TYPE, type);
                                intent.putExtra(Keys.KEY_BASE_SE, savedEncryption);
                                context.startActivity(intent);
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout linearLayout;
        private TextView titleTV, keyHintTV;
        private ImageButton menuBtn;
        private Context context;
        private SavedEncryption savedEncryption;

        public ViewHolder(@NonNull View itemView, final Context context, SavedEncryption savedEncryption){
            super(itemView);

            linearLayout = (LinearLayout) itemView;
            this.context = context;
            this.savedEncryption = savedEncryption;
            titleTV = itemView.findViewById(R.id.SE_title);
            keyHintTV = itemView.findViewById(R.id.SE_hint);
            menuBtn = itemView.findViewById(R.id.SE_menu_btn);

            menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((Activity) context).registerForContextMenu(view);
                    view.showContextMenu();
                }
            });
        }
    }
}
