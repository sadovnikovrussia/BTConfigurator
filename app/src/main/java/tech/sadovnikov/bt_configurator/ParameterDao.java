package tech.sadovnikov.bt_configurator;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ParameterDao {

    @Query("SELECT * FROM Parameter")
    List<Parameter> getAll();

    @Query("SELECT * FROM Parameter WHERE name = :name")
    Parameter getParameterByName(String name);

    @Insert
    void insert(Parameter parameter);

    @Update
    void update(Parameter parameter);

    @Delete
    void delete(Parameter parameter);
}
