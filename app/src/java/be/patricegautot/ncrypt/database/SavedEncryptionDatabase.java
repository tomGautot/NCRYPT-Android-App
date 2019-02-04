package be.patricegautot.ncrypt.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.io.ObjectStreamException;
import java.util.Objects;

import be.patricegautot.ncrypt.customObjects.SavedEncryption;

@Database(entities = {SavedEncryption.class}, version = 1, exportSchema = false)
public abstract class SavedEncryptionDatabase extends RoomDatabase {
    private static final String LOG_TAG = "SavedEncryptionDatabase";
    private static final Object LOCK = new Object();
    private static final String BASE_NAME = "SavedEncryptionList";

    private static SavedEncryptionDatabase sInstance;

    public static SavedEncryptionDatabase getInstance(Context context){
        if(sInstance == null){
            synchronized (LOCK) {
                sInstance =
                        Room.databaseBuilder(context.getApplicationContext(),
                                SavedEncryptionDatabase.class,
                                SavedEncryptionDatabase.BASE_NAME)
                                .build();
            }
        }

        return sInstance;
    }

    public abstract DaoSavedEncryption savedEncryptionDao();
}
