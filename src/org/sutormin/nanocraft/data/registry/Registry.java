package org.sutormin.nanocraft.data.registry;

import java.util.*;
import java.util.function.Function;

public class Registry<T extends RegistryType> {
    private final Function<Integer, T> factory;
    private final Map<String, T> byName = new HashMap<>();
    private final List<T> byId = new ArrayList<>();
    public Registry(Function<Integer, T> factory){
        this.factory = factory;
    }
    public T addNew(String name){
        if (byName.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate registry name: " + name);
        }
        T thing = factory.apply(this.byId.size());
        this.byId.add(thing);
        this.byName.put(name,thing);
        thing.setName(name);
        return thing;
    }
    public T extend(T old, String newName){
        if (byName.containsKey(newName)) {
            throw new IllegalArgumentException("Duplicate registry name: " + newName);
        }
        @SuppressWarnings("unchecked") // copy() returns an instance of the same class as old
        T thing = (T) old.copy(byId.size());
        this.byId.add(thing);
        this.byName.put(newName,thing);
        thing.setName(newName);
        return thing;
    }
    public T extend(int oldId, String newName){
        return extend(byId.get(oldId),newName);
    }
    public T extend(String oldName, String newName){
        return extend(byName.get(oldName),newName);
    }
    public T get(int id){
        return byId.get(id);
    }
    public T get(String name){
        return byName.get(name);
    }
    public int size() { return byId.size(); }

    public List<T> getAll(){
        return Collections.unmodifiableList(byId);
    }
}