package org.sutormin.nanocraft.networking.packets;

import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.packets.config.S2CFinishConfiguration;
import org.sutormin.nanocraft.networking.packets.config.S2CRegistryData;
import org.sutormin.nanocraft.networking.packets.play.S2CJoinGame;
import org.sutormin.nanocraft.networking.packets.misc.S2CDisconnect;
import org.sutormin.nanocraft.networking.packets.misc.S2CIgnorePacket;
import org.sutormin.nanocraft.networking.packets.misc.S2CKeepAlive;
import org.sutormin.nanocraft.networking.packets.config.S2CKnownPacksChallenge;
import org.sutormin.nanocraft.networking.packets.misc.S2CPing;
import org.sutormin.nanocraft.networking.packets.login.S2CEncryptionRequest;
import org.sutormin.nanocraft.networking.packets.login.S2CLoginSuccess;
import org.sutormin.nanocraft.networking.packets.login.S2CSetCompression;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.packets.play.player.S2CSyncPlayerPosition;
import org.sutormin.nanocraft.networking.packets.play.world.block.S2CBlockUpdate;
import org.sutormin.nanocraft.networking.packets.play.world.block.S2CSectionBlocksUpdate;
import org.sutormin.nanocraft.networking.packets.play.world.chunk.S2CChunkBatchFinished;
import org.sutormin.nanocraft.networking.packets.play.world.chunk.S2CChunkData;
import org.sutormin.nanocraft.networking.packets.play.world.chunk.S2CUnloadChunk;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class PacketList {
  public static S2CPacket getS2CPacket(int id, NetworkPhase phase, Channel channel) {
    if (phase == NetworkPhase.LOGIN && id == 0) return new S2CDisconnect(true);
    if (phase == NetworkPhase.LOGIN && id == 1) return new S2CEncryptionRequest(channel);
    if (phase == NetworkPhase.LOGIN && id == 3) return new S2CSetCompression();
    if (phase == NetworkPhase.LOGIN && id == 2) return new S2CLoginSuccess(channel);

    if (phase == NetworkPhase.CONFIG && id == 1) return new S2CIgnorePacket("PluginMessage");
    if (phase == NetworkPhase.CONFIG && id == 2) return new S2CDisconnect(false);
    if (phase == NetworkPhase.CONFIG && id == 13) return new S2CIgnorePacket("FeatureFlags");
    if (phase == NetworkPhase.CONFIG && id == 15) return new S2CKnownPacksChallenge(channel);
    if (phase == NetworkPhase.CONFIG && id == 7) return new S2CRegistryData();
    if (phase == NetworkPhase.CONFIG && id == 14) return new S2CIgnorePacket("UpdateTags");
    if (phase == NetworkPhase.CONFIG && id == 3) return new S2CFinishConfiguration(channel);

    if (phase == NetworkPhase.CONFIG && id == 4) return new S2CKeepAlive(channel);
    if (phase == NetworkPhase.CONFIG && id == 5) return new S2CPing(channel);
    if (phase == NetworkPhase.PLAY && id == 45) return new S2CKeepAlive(channel);
    if (phase == NetworkPhase.PLAY && id == 62) return new S2CPing(channel);

    if (phase == NetworkPhase.PLAY && id == 46) return new S2CChunkData();
    if (phase == NetworkPhase.PLAY && id == 11) return new S2CChunkBatchFinished(channel);
    if (phase == NetworkPhase.PLAY && id == 38) return new S2CUnloadChunk();
    if (phase == NetworkPhase.PLAY && id == 8) return new S2CBlockUpdate();
    if (phase == NetworkPhase.PLAY && id == 86) return new S2CSectionBlocksUpdate();
    if (phase == NetworkPhase.PLAY && id == 32) return new S2CDisconnect(false);


    if (phase == NetworkPhase.PLAY && id == 73) return new S2CSyncPlayerPosition(channel);
    if (phase == NetworkPhase.PLAY && id == 50) return new S2CJoinGame(false);
    if (phase == NetworkPhase.PLAY && id == 84) return new S2CJoinGame(true);

    return null;
  }
}
