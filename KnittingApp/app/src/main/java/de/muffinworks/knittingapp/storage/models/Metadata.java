package de.muffinworks.knittingapp.storage.models;

import java.util.UUID;

public class Metadata implements Comparable<Metadata> {
    /**
     * Used to identify the file this pattern is stored in.
     */
    private UUID id;
    protected String name = "Default name";

    public Metadata() {
        id = UUID.randomUUID();
    }

    public String getFilename() {
        return id + ".json";
    }

    public String getId() {
        return id.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Metadata clone() {
        Metadata m2 = new Metadata();
        m2.id = id;
        m2.name = name;
        return m2;
    }

    @Override
    public int compareTo(Metadata that) {
        return this.name.compareTo(that.name);
    }
}
