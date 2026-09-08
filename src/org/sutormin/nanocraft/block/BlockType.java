package org.sutormin.nanocraft.block;

import org.sutormin.nanocraft.resources.Textures;

public class BlockType {
    private final char id;
    private final String name;
    private final int[] textures = new int[6];

    public BlockType(int id, String name) {
        this.id = (char) id; this.name = name;
        //this.setTextures(this.name+".png");
    }

    public BlockType setTextures(int which, String path){
        int t = Textures.BLOCK.addTexture("assets/texture/block/" + path);
        if ((which & 1) != 0) textures[5] = t;
        if ((which & 2) != 0) textures[4] = t;
        if ((which & 4) != 0) textures[3] = t;
        if ((which & 8) != 0) textures[2] = t;
        if ((which & 16) != 0) textures[1] = t;
        if ((which & 32) != 0) textures[0] = t;
        return this;
    }

    public BlockType setTextures(String path) {
        int t = Textures.BLOCK.addTexture("assets/texture/block/" + path);
        for (var i = 0; i < 6; i++) {
            textures[i] = t;
        }
        return this;
    }

    public int getTexture(int which) {
        return textures[which];
    }

    public char getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
