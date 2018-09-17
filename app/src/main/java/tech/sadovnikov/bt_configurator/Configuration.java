package tech.sadovnikov.bt_configurator;

import android.arch.persistence.room.Entity;

@Entity
public class Configuration {

    private String id = "1";
    private String versionTerminal = "2";

    public void setId(String id) {
        this.id = id;
    }

    public void setVersionTerminal(String versionTerminal) {
        this.versionTerminal = versionTerminal;
    }

    public String getId() {
        return id;
    }

    public String getVersionTerminal() {
        return versionTerminal;
    }
}
