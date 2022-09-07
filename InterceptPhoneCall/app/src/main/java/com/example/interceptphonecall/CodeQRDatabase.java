package com.example.interceptphonecall;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {CodeQR.class}, version = 2,exportSchema = false)

public abstract class CodeQRDatabase extends RoomDatabase {
    public abstract CodeQRDao codeQRDao();

    private static volatile CodeQRDatabase INSTANCE;
    public static final int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseWriteExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    static Migration migration=new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'codeqr' ADD COLUMN 'name' TEXT");
        }
    };
    static CodeQRDatabase getDatabase(final Context context){
        if(INSTANCE==null){
            synchronized (CodeQRDatabase.class){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                            CodeQRDatabase.class, "codeqr_db")
                            .addMigrations(migration)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@Nullable SupportSQLiteDatabase db){
            super.onCreate(db);
            databaseWriteExecutor.execute(()->{
                CodeQRDao dao=INSTANCE.codeQRDao();
                  dao.deleteALl();//usuwanie wszytskiego
              /*   CodeQR code=new CodeQR(1, "123456789");
                dao.insert(code);*/
            });
        }
    };

}
