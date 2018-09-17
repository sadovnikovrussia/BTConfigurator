package tech.sadovnikov.bt_configurator;

import android.arch.persistence.room.RoomDatabase;

@android.arch.persistence.room.Database(entities = {Parameter.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract ParameterDao parameterDao();

}
