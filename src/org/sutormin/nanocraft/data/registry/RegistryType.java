package org.sutormin.nanocraft.data.registry;

public abstract class RegistryType {
    private String name;
    private final int id;

    protected RegistryType(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public void setName(String n){
        name = n;
    }
    public String getName() {
        return name;
    }
    public abstract RegistryType copy(int newId);
}
