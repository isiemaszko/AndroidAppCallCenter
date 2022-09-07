package com.example.interceptphonecall;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CodeQRRepository {
    private CodeQRDao codeQRDao;
    private LiveData<List<CodeQR>> codes;

    CodeQRRepository(Application application){
        CodeQRDatabase database=CodeQRDatabase.getDatabase(application);
        codeQRDao=database.codeQRDao();
        codes=codeQRDao.findAll();
    }

    LiveData<List<CodeQR>> findAllCode(){return codes;}

    void findCodeQRWithID(int id){
        CodeQRDatabase.databaseWriteExecutor.execute(()->{
            codeQRDao.findCodeQRWithID(id);
        });
    }



    void insert(CodeQR code){
        CodeQRDatabase.databaseWriteExecutor.execute(()->{
            codeQRDao.insert(code);
        });
    }

    void update(CodeQR code){
        CodeQRDatabase.databaseWriteExecutor.execute(()->{
            codeQRDao.update(code);
        });
    }

    void delete(CodeQR code){
        CodeQRDatabase.databaseWriteExecutor.execute(()->{
            codeQRDao.delete(code);
        });
    }
}
