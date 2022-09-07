package com.example.interceptphonecall;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CodeQRDao {
    @Insert(onConflict= OnConflictStrategy.REPLACE)
    void insert(CodeQR code);

    @Update
     void update(CodeQR code);

    @Delete
     void delete(CodeQR code);

    @Query("DELETE FROM codeqr")
    public void deleteALl();

    @Query("SELECT * FROM codeqr ORDER BY code")
    public LiveData<List<CodeQR>> findAll();

    @Query("SELECT * FROM codeqr WHERE id LIKE :idd")
    public List<CodeQR> findCodeQRWithID(int idd);

}
