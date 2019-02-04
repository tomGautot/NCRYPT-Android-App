package be.patricegautot.ncrypt.helpers;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Random;

import be.patricegautot.ncrypt.R;
import be.patricegautot.ncrypt.activities.OutputActivity;
import be.patricegautot.ncrypt.activities.SavedActivity;
import be.patricegautot.ncrypt.customObjects.SavedEncryption;
import be.patricegautot.ncrypt.database.SavedEncryptionDatabase;

public class Helpers {

    public static void saveDialog(final Context context, final String encryptedString, String baseTextDescription, String keyHint, final int id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("TEXT SAVING");

        final EditText inputTitle = new EditText(context);
        inputTitle.setHint("Initial Message Description");
        inputTitle.setText(baseTextDescription);

        final EditText inputKey = new EditText(context);
        inputKey.setHint("Key reminder");
        inputKey.setText(keyHint);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(40, 2, 40, 2);

        layout.addView(inputTitle);
        layout.addView(inputKey);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton(R.string.save_text_alertdialog,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String title = inputTitle.getText().toString();
                        String keyReminder = inputKey.getText().toString();
                        final SavedEncryption savedEncryption = (id == -1) ?
                                new SavedEncryption(encryptedString, title, keyReminder, SavedEncryption.TYPE_TEXT):
                                new SavedEncryption(id, encryptedString, title, keyReminder, SavedEncryption.TYPE_TEXT, -1);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if(id == -1){
                                    SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().insertSE(savedEncryption);
                                    //Log.e("SaveDialog", "New SavedEncryption created");
                                }
                                else{
                                    SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().updateSavedEncryption(savedEncryption);
                                    //Log.e("SaveDialog", "Updated SavedEncryption");
                                  }
                            }
                        }).start();

                    }
                });

        alertDialog.setNegativeButton(R.string.cancel_text_alertdialog,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static void deleteSavedEncryption(final Context context, final SavedEncryption savedEncryption){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().deleteSavedEncryption(savedEncryption);
            }
        }).start();
    }

    public static void savePictureDialog(final Context context, final SavedEncryption baseSE, String baseBitmapDescription, String keyHint, final int id, final String action){
        final Bitmap encryptedBitmap = Helpers.loadSEBitmap(context, baseSE);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("PICTURE SAVING");

        final EditText inputTitle = new EditText(context);
        inputTitle.setHint("Initial Picture Description");
        inputTitle.setText(baseBitmapDescription);

        final EditText inputKey = new EditText(context);
        inputKey.setHint("Key reminder");
        inputKey.setText(keyHint);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(40, 2, 40, 2);

        layout.addView(inputTitle);
        layout.addView(inputKey);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("SAVE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                long timeInMillis = Calendar.getInstance().getTimeInMillis();
                                String fileName = getFileName(timeInMillis);

                                ImageSaver imageSaver = new ImageSaver(context);

                                //Store To Public
                                if(action.equals("D")) ImageSaver.SaveImage(context, encryptedBitmap, getFileName(timeInMillis), getPublicDirectoryName());

                                String title = inputTitle.getText().toString();
                                String keyReminder = inputKey.getText().toString();

                                if(action.equals("E")){
                                    final SavedEncryption savedEncryption = new SavedEncryption(id, baseSE.getText(), title, keyReminder, SavedEncryption.TYPE_IMAGE, baseSE.getTimeInMillis());
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().updateSavedEncryption(savedEncryption);
                                        }
                                    }).start();
                                    return;
                                }

                                //Store To Private
                                imageSaver.setExternal(false).setDirectoryName(getPrivateDirectoryName()).setFileName(getFileName(timeInMillis)).save(encryptedBitmap);

                                String privateFilePath = imageSaver.getPath();


                                final SavedEncryption savedEncryption = (id == -1) ?
                                        new SavedEncryption(privateFilePath, title, keyReminder, SavedEncryption.TYPE_IMAGE, timeInMillis):
                                        new SavedEncryption(id, privateFilePath, title, keyReminder, SavedEncryption.TYPE_IMAGE, timeInMillis);

                                if(id == -1){
                                    SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().insertSE(savedEncryption);
                                    //Log.e("SaveDialog", "New SavedEncryption created");
                                }
                                else{
                                    SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().updateSavedEncryption(savedEncryption);
                                    //Log.e("SaveDialog", "Updated SavedEncryption");
                                }
                            }
                        }).start();

                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static void savePictureDialog(final Context context, final Bitmap encryptedBitmap, String baseBitmapDescription, String keyHint, final int id, final String action){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("PICTURE SAVING");

        final EditText inputTitle = new EditText(context);
        inputTitle.setHint("Initial Picture Description");
        inputTitle.setText(baseBitmapDescription);

        final EditText inputKey = new EditText(context);
        inputKey.setHint("Key reminder");
        inputKey.setText(keyHint);

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        layout.setPadding(40, 2, 40, 2);

        layout.addView(inputTitle);
        layout.addView(inputKey);

        alertDialog.setView(layout);

        alertDialog.setPositiveButton("SAVE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                long timeInMillis = Calendar.getInstance().getTimeInMillis();
                                String fileName = getFileName(timeInMillis);

                                ImageSaver imageSaver = new ImageSaver(context);

                                //Store To Public
                                if(action.equals("D")) ImageSaver.SaveImage(context, encryptedBitmap, getFileName(timeInMillis), getPublicDirectoryName());

                                String title = inputTitle.getText().toString();
                                String keyReminder = inputKey.getText().toString();

                                //Store To Private
                                imageSaver.setExternal(false).setDirectoryName(getPrivateDirectoryName()).setFileName(getFileName(timeInMillis)).save(encryptedBitmap);

                                String privateFilePath = imageSaver.getPath();


                                final SavedEncryption savedEncryption = (id == -1) ?
                                        new SavedEncryption(privateFilePath, title, keyReminder, SavedEncryption.TYPE_IMAGE, timeInMillis):
                                        new SavedEncryption(id, privateFilePath, title, keyReminder, SavedEncryption.TYPE_IMAGE, timeInMillis);

                                if(id == -1){
                                    SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().insertSE(savedEncryption);
                                    //Log.e("SaveDialog", "New SavedEncryption created");
                                }
                                else{
                                    SavedEncryptionDatabase.getInstance(context).savedEncryptionDao().updateSavedEncryption(savedEncryption);
                                    //Log.e("SaveDialog", "Updated SavedEncryption");
                                }
                            }
                        }).start();

                    }
                });

        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static void savePictureToGalleryConfirmationDialog(final Context context, final Bitmap bitmap){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("TEXT SAVING");

        TextView tv = new TextView(context);
        tv.setText(R.string.image_gallery_save_text);
        tv.setPadding(40, 2, 40, 2);
        alertDialog.setView(tv);

        ;

        alertDialog.setPositiveButton(R.string.save_text_alertdialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ImageSaver.SaveImage(context, bitmap, getFileName(Calendar.getInstance().getTimeInMillis()), getPublicDirectoryName());
                    }
                }).start();
            }
        });

        alertDialog.setNegativeButton(R.string.cancel_text_alertdialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        alertDialog.show();

    }

    public static Bitmap loadSEBitmap(Context context, SavedEncryption savedEncryption){
        if(savedEncryption.getType() != SavedEncryption.TYPE_IMAGE) return null;
        return new ImageSaver(context).setFileName(getFileName(savedEncryption.getTimeInMillis()))
                .setDirectoryName(getPrivateDirectoryName()).load();
    }

    public static String getFilepathFromSE(Context context, SavedEncryption savedEncryption){
        if(savedEncryption.getType() != SavedEncryption.TYPE_IMAGE) return null;
        return new ImageSaver(context).setFileName(getFileName(savedEncryption.getTimeInMillis()))
                .setDirectoryName(getPrivateDirectoryName()).getPath();

    }

    private static String getFileName(long time) {
        return "PNG_" + time + "_.png";
    }

    private static String getPrivateDirectoryName(){
        return "NCRYPT_PRIVATE";
    }

    private static String getPublicDirectoryName(){
        return "NCRYPT";
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight && width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            inSampleSize = 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    } // FOR BITMAP RESIZING

    public static int randomNumberInRange(int min, int max){
        final int random = new Random().nextInt((max-min)+1) + min;
        return random;
    }


}
