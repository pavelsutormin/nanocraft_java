package org.sutormin.nanocraft.world;

import org.sutormin.nanocraft.NanoCraft;
import org.sutormin.nanocraft.block.BlockTypes;

import java.util.*;
import java.util.function.Function;

public class World {
    private final Map<ChunkPos, Chunk> chunks = new HashMap<>();
    private final List<ChunkPos> forceRemeshChunks = new ArrayList<>();
    private final List<ChunkPos> cancelRemeshChunks = new ArrayList<>();
    private final List<BlockData> setBlockPromises = new ArrayList<>();

    public World() {
    }

    public void makeChunk(ChunkPos pos) {
        if (chunks.containsKey(pos)) return;
        Chunk chunk = new Chunk(pos);
        chunks.put(pos, chunk);
        forceRemeshChunks.add(pos.offset(-1,0));
        forceRemeshChunks.add(pos.offset(1,0));
        forceRemeshChunks.add(pos.offset(0,-1));
        forceRemeshChunks.add(pos.offset(0,1));
        cancelRemeshChunks.add(pos);
    }

    public void meshChunk(ChunkPos pos) {
        if (!chunks.containsKey(pos)) return;
        Chunk chunk = chunks.get(pos);
        if (chunk.mesh.generated && (!forceRemeshChunks.contains(pos) || cancelRemeshChunks.contains(pos))) return;
        chunk.buildMesh();
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
        chunk.buildMesh();
        remesh(pos.offset(-1, 0));
        remesh(pos.offset(1, 0));
        remesh(pos.offset(0, -1));
        remesh(pos.offset(0, 1));
    }

    private void remesh(ChunkPos pos) {
        Chunk c = chunks.get(pos);
        if (c != null) c.buildMesh();
    }

    private boolean snapped = false;

    public void drainNetworkChunks(int budget) {
        ChunkLoader.Pending p;
        while (budget-- > 0 && (p = ChunkLoader.poll()) != null) {
            addChunk(p.pos(), new Chunk(p.pos()));
            chunks.get(p.pos()).setBlocks(p.blocks());

            if (!snapped) {
                snapped = true;
                NanoCraft.CAMERA.updatePosition(
                        p.pos().x() * Chunk.SIZE_X + 8,
                        100 + 3,
                        p.pos().z() * Chunk.SIZE_Z + 8,
                        0,
                        0
                );
            }
        }
    }


    public int chunkCount() {
        return chunks.size();
    }

    public void loadChunksAndUnloadAllOtherChunks(Collection<ChunkPos> posList) {
        Set<ChunkPos> keepSet = new HashSet<>(posList);

        chunks.entrySet().removeIf(entry -> {
            if (!keepSet.contains(entry.getKey())) {
                entry.getValue().cleanup();
                return true;
            }
            return false;
        });

        forceRemeshChunks.clear();
        cancelRemeshChunks.clear();

        for (ChunkPos pos : posList) {
            makeChunk(pos);
        }

        for (ChunkPos pos : posList) {
            meshChunk(pos);
        }
    }

    public short getBlockAt(int x, int y, int z) {
        ChunkPos chunkPos = getChunkPosFromBlock(x, z);
        Chunk chunk = chunks.get(chunkPos);
        if (chunk == null) return BlockTypes.AIR;

        int localX = Math.floorMod(x, Chunk.SIZE_X);
        int localZ = Math.floorMod(z, Chunk.SIZE_Z);

        return chunk.getBlock(localX, y, localZ);
    }

    public void setBlockAt(int x, int y, int z, short block) {
        ChunkPos chunkPos = getChunkPosFromBlock(x, z);
        Chunk chunk = chunks.get(chunkPos);
        if (chunk == null) return;

        int localX = Math.floorMod(x, Chunk.SIZE_X);
        int localZ = Math.floorMod(z, Chunk.SIZE_Z);


        chunk.setBlock(localX, y, localZ, block);
        chunk.buildMesh();

        if (localX == 0) meshChunk(new ChunkPos(chunkPos.x() - 1, chunkPos.z()));
        if (localX == Chunk.SIZE_X - 1) meshChunk(new ChunkPos(chunkPos.x() + 1, chunkPos.z()));
        if (localZ == 0) meshChunk(new ChunkPos(chunkPos.x(), chunkPos.z() - 1));
        if (localZ == Chunk.SIZE_Z - 1) meshChunk(new ChunkPos(chunkPos.x(), chunkPos.z() + 1));
    }

    public void setBlockPromise(int x, int y, int z, Function<Short, Short> block){
        setBlockPromises.add(new BlockData(x,y,z,block));
    }

    private void checkBlockPromises() {
        Iterator<BlockData> iterator = setBlockPromises.iterator();
        while (iterator.hasNext()) {
            BlockData blockData = iterator.next();
            int x = blockData.x();
            int y = blockData.y();
            int z = blockData.z();
            ChunkPos chunkPos = getChunkPosFromBlock(x, z);
            Chunk chunk = chunks.get(chunkPos);

            if (chunk == null) continue;

            short block = blockData.block().apply(getBlockAt(x, y, z));
            if (block != BlockTypes.NULL) setBlockAt(x, y, z, block);

            iterator.remove();
        }
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

    public void cleanup() {
        for (Chunk chunk : chunks.values()) {
            chunk.cleanup();
        }
        chunks.clear();
    }
    private record BlockData(int x, int y, int z, Function<Short, Short> block){}
}