package org.sutormin.nanocraft.data.types;

import org.sutormin.nanocraft.data.registry.RegistryType;
import org.sutormin.nanocraft.world.Direction;
import org.sutormin.nanocraft.world.FaceCullCache;

import java.util.ArrayList;
import java.util.List;

public class BlockShape extends RegistryType {
    public record Vertex(short x, short y, short z){};
    public record Face(int[] vertices, Direction dir, boolean shouldCull, float[][] uv){};
    private List<Vertex> vertices = new ArrayList<>();
    private List<Face> faces = new ArrayList<>();
    // filled lazily by FaceCullCache.infoOf
    private FaceCullCache.ShapeInfo cullInfo;

    public BlockShape(int id) {super(id);}
    @Override
    public RegistryType copy(int newId) {return new BlockShape(newId);}

    public int addVertex(float x, float y, float z){
        vertices.add(new Vertex((short) Math.round(x*128), (short) Math.round(y*128), (short) Math.round(z*128)));
        return vertices.size()-1;
    }

    @Deprecated
    private int addVertex(short x, short y, short z){
        vertices.add(new Vertex(x,y,z));
        return vertices.size()-1;
    }

    public int addFace(int[] verticies, Direction dir, boolean shouldCull, float[][] uv){
        faces.add(new Face(verticies,dir,shouldCull,uv));
        return faces.size()-1;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public FaceCullCache.ShapeInfo getCullInfo() {
        return cullInfo;
    }

    public void setCullInfo(FaceCullCache.ShapeInfo cullInfo) {
        this.cullInfo = cullInfo;
    }

}
