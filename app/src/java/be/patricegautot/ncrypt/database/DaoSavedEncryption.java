package be.patricegautot.ncrypt.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import be.patricegautot.ncrypt.customObjects.SavedEncryption;

@Dao
public interface DaoSavedEncryption {
    @Insert
    Long insertSE(SavedEncryption savedEncryption);

    @Query("SELECT * FROM SavedEncryption ORDER BY timeInMillis")
    LiveData<List<SavedEncryption>> querryAll();
    @Query("SELECT * FROM SavedEncryption WHERE type=:type ORDER BY timeInMillis")
    LiveData<List<SavedEncryption>> querryTypeOnly(int type);

    @Update
    void updateSavedEncryption(SavedEncryption savedEncryption);

    @Delete
    void deleteSavedEncryption(SavedEncryption savedEncryption);
}
