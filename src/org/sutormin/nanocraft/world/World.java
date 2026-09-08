package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.NanoCraft;
import org.sutormin.nanocraft.block.BlockTypes;

import java.util.*;
import java.util.function.Function;

public class World {
    private final Map<ChunkPos, Chunk> chunks = new HashMap<>();
    private final Set<ChunkPos> dirty = new LinkedHashSet<>();
    private final Map<ChunkPos, List<ChunkLoader.BlockChange>> pendingBlockChanges = new HashMap<>();

    public World() {
    }

    public void removeChunk(ChunkPos pos) {
        if (!chunks.containsKey(pos)) return;
        Chunk chunk = chunks.get(pos);
        chunk.cleanup();
        chunks.remove(pos);
    }

    public void addChunk(ChunkPos pos, Chunk chunk) {
        Chunk old = chunks.put(pos, chunk);
        if (old != null) old.cleanup();

        // Replay queued updates for this chunk
        List<ChunkLoader.BlockChange> pending = pendingBlockChanges.remove(pos);
        if (pending != null) {
            for (ChunkLoader.BlockChange change : pending) {
                int localX = Math.floorMod(change.x(), Chunk.SIZE_X);
                int localZ = Math.floorMod(change.z(), Chunk.SIZE_Z);
                chunk.setBlock(localX, change.y(), localZ, change.block());
            }
        }

        dirty.add(pos);
        dirty.add(pos.offset(-1, 0));
        dirty.add(pos.offset(1, 0));
        dirty.add(pos.offset(0, -1));
        dirty.add(pos.offset(0, 1));
    }

    private void remesh(ChunkPos pos) {
        Chunk c = chunks.get(pos);
        if (c != null) c.buildMesh();
    }

    public void drainNetworkChunks(int budget) {
        ChunkLoader.Pending p;
        while (budget-- > 0 && (p = ChunkLoader.poll()) != null) {
            Chunk c = new Chunk(p.pos());
            c.setBlocks(p.blocks());
            addChunk(p.pos(), c);
        }
    }

    public void flushDirty(int budget) {
        Iterator<ChunkPos> it = dirty.iterator();
        while (it.hasNext() && budget-- > 0) {
            ChunkPos pos = it.next();
            it.remove();
            Chunk c = chunks.get(pos);
            if (c != null) c.buildMesh();
        }
    }

    public void drainUnloads(int budget) {
        ChunkPos pos;
        while (budget-- > 0 && (pos = ChunkLoader.pollUnload()) != null) {
            removeChunk(pos);
            dirty.add(pos.offset(-1, 0));
            dirty.add(pos.offset(1, 0));
            dirty.add(pos.offset(0, -1));
            dirty.add(pos.offset(0, 1));
        }
    }

    //public void drainRemeshes(int budget) {
    //    ChunkPos change;
    //    while (budget-- > 0 && (change = ChunkLoader.pollRemesh()) != null) {
    //        dirty.add(change);
    //        //setBlockAt(change.x(), change.y(), change.z(), change.block());
    //    }
    //}

    public void drainBlockChanges(int budget) {
        ChunkLoader.BlockChange change;
        while (budget-- > 0 && (change = ChunkLoader.pollBlockChange()) != null) {
            setBlockAt(change.x(), change.y(), change.z(), change.block());
        }
    }

    public int chunkCount() {
        return chunks.size();
    }

    public char getBlockAt(int x, int y, int z) {
        ChunkPos chunkPos = getChunkPosFromBlock(x, z);
        Chunk chunk = chunks.get(chunkPos);
        if (chunk == null) return BlockTypes.AIR;

        int localX = Math.floorMod(x, Chunk.SIZE_X);
        int localZ = Math.floorMod(z, Chunk.SIZE_Z);

        return chunk.getBlock(localX, y, localZ);
    }

    public void setBlockAt(int x, int y, int z, char block) {
        ChunkPos chunkPos = getChunkPosFromBlock(x, z);
        Chunk chunk = chunks.get(chunkPos);

        if (chunk == null) {
            // Queue the block change until the chunk is added to the world
            pendingBlockChanges.computeIfAbsent(chunkPos, k -> new ArrayList<>())
                .add(new ChunkLoader.BlockChange(x, y, z, block));
            return;
        }

        int localX = Math.floorMod(x, Chunk.SIZE_X);
        int localZ = Math.floorMod(z, Chunk.SIZE_Z);

        chunk.setBlock(localX, y, localZ, block);
        dirty.add(chunkPos);

        if (localX == 0) dirty.add(chunkPos.offset(-1, 0));
        if (localX == Chunk.SIZE_X - 1) dirty.add(chunkPos.offset(1, 0));
        if (localZ == 0) dirty.add(chunkPos.offset(0, -1));
        if (localZ == Chunk.SIZE_Z - 1) dirty.add(chunkPos.offset(0, 1));
    }


    public Chunk getChunk(ChunkPos pos){
        return chunks.get(pos);
    }

    public ChunkPos getChunkPosFromBlock(int x, int z) {
        int chunkX = Math.floorDiv(x, Chunk.SIZE_X);
        int chunkZ = Math.floorDiv(z, Chunk.SIZE_Z);
        return new ChunkPos(chunkX, chunkZ);
    }

    public void renderChunks() {
        for (Chunk chunk : chunks.values()) {
            chunk.render();
        }
    }

    public void tick(){
        drainNetworkChunks(100);
        drainUnloads(50);
        drainBlockChanges(200);
        flushDirty(10);
    }

    public void cleanup() {
        for (Chunk chunk : chunks.values()) {
            chunk.cleanup();
        }
        chunks.clear();
    }
}