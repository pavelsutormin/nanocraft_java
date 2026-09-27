package org.sutormin.nanocraft.world.chunk;

public record ChunkPos(int x, int z) {
    public ChunkPos offset(int ox, int oz) {
        return new ChunkPos(x + ox, z + oz);
    }
}