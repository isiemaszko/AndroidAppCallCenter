package com.example.interceptphonecall;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CodeQRViewModel extends AndroidViewModel {

    private CodeQRRepository codeQRRepository;
    private LiveData<List<CodeQR>> codes;

    public CodeQRViewModel(@NonNull Application application) {
        super(application);
        codeQRRepository=new CodeQRRepository(application);
        codes=codeQRRepository.findAllCode();
    }

    public LiveData<List<CodeQR>> findAll(){
        return codes;
    }

    public void insert(CodeQR code){
        codeQRRepository.insert(code);
    }

    public void update(CodeQR code){
        codeQRRepository.update(code);
    }

    public void delete(CodeQR code){
        codeQRRepository.delete(code);
    }

}
