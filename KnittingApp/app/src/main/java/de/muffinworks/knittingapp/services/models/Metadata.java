package de.muffinworks.knittingapp.services.models;

import java.util.UUID;

/**
 * Created by Bianca on 22.07.2016.
 */
public class Metadata {
    /**
     * Used to identify the file this pattern is stored in.
     */
    private UUID id;

    protected String name;

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

}
