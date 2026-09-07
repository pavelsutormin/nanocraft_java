package org.sutormin.nanocraft.networking.packets;

import org.sutormin.nanocraft.networking.NetworkPhase;
import org.sutormin.nanocraft.networking.packets.config.s2c.FinishConfiguration;
import org.sutormin.nanocraft.networking.packets.misc.s2c.IgnorePacket;
import org.sutormin.nanocraft.networking.packets.misc.s2c.KeepAliveS2C;
import org.sutormin.nanocraft.networking.packets.config.s2c.KnownPacksChallenge;
import org.sutormin.nanocraft.networking.packets.misc.s2c.Ping;
import org.sutormin.nanocraft.networking.packets.login.s2c.EncryptionRequest;
import org.sutormin.nanocraft.networking.packets.login.s2c.LoginSuccess;
import org.sutormin.nanocraft.networking.packets.login.s2c.SetCompression;
import io.netty.channel.Channel;
import org.sutormin.nanocraft.networking.packets.play.s2c.ChunkData;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class PacketList {
  public static S2CPacket getS2CPacket(int id, NetworkPhase phase, Channel channel) {
    if (phase == NetworkPhase.LOGIN && id == 1) return new EncryptionRequest(channel);
    if (phase == NetworkPhase.LOGIN && id == 3) return new SetCompression();
    if (phase == NetworkPhase.LOGIN && id == 2) return new LoginSuccess(channel);

    if (phase == NetworkPhase.CONFIG && id == 1) return new IgnorePacket("PluginMessage");
    if (phase == NetworkPhase.CONFIG && id == 12) return new IgnorePacket("FeatureFlags");
    if (phase == NetworkPhase.CONFIG && id == 14) return new KnownPacksChallenge(channel);
    if (phase == NetworkPhase.CONFIG && id == 7) return new IgnorePacket("RegistryData");
    if (phase == NetworkPhase.CONFIG && id == 13) return new IgnorePacket("UpdateTags");
    if (phase == NetworkPhase.CONFIG && id == 3) return new FinishConfiguration(channel);

    if (phase == NetworkPhase.CONFIG && id == 4) return new KeepAliveS2C(channel);
    if (phase == NetworkPhase.CONFIG && id == 5) return new Ping(channel);
    if (phase == NetworkPhase.PLAY && id == 44) return new KeepAliveS2C(channel);
    if (phase == NetworkPhase.PLAY && id == 61) return new Ping(channel);

    if (phase == NetworkPhase.PLAY && id == 45) return new ChunkData();

    return null;
  }
}
