package org.sutormin.nanocraft.networking.packets.play.player;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.joml.Vector3f;
import org.sutormin.nanocraft.NanoCraft;
import org.sutormin.nanocraft.networking.coders.VarCoder;
import org.sutormin.nanocraft.networking.packets.types.S2CPacket;

public class S2CSyncPlayerPosition implements S2CPacket {

  /*
   * Position
   */
  public static final int RELATIVE_X = 0x0001;
  public static final int RELATIVE_Y = 0x0002;
  public static final int RELATIVE_Z = 0x0004;

  /*
   * Rotation
   */
  public static final int RELATIVE_YAW = 0x0008;
  public static final int RELATIVE_PITCH = 0x0010;

  /*
   * Velocity
   */
  public static final int RELATIVE_VELOCITY_X = 0x0020;
  public static final int RELATIVE_VELOCITY_Y = 0x0040;
  public static final int RELATIVE_VELOCITY_Z = 0x0080;

  /*
   * Rotate the existing velocity according to
   * the rotation delta before applying the
   * velocity contained in this packet.
   */
  public static final int ROTATE_VELOCITY = 0x0100;

  private final Channel channel;

  public S2CSyncPlayerPosition(Channel channel) {
    this.channel = channel;
  }

  @Override
  public void read(ByteBuf buf) {

    /*
     * Teleport ID
     */
    int teleportId = VarCoder.readVarInt(buf);

    /*
     * Position
     */
    double x = buf.readDouble();
    double y = buf.readDouble();
    double z = buf.readDouble();

    /*
     * Velocity
     */
    double velocityX = buf.readDouble();
    double velocityY = buf.readDouble();
    double velocityZ = buf.readDouble();

    /*
     * Rotation
     */
    float yaw = buf.readFloat();
    float pitch = -buf.readFloat();

    /*
     * Teleport flags
     */
    int flags = buf.readInt();

    var camera = NanoCraft.CAMERA;

    /*
     * Save the old rotation because ROTATE_VELOCITY
     * uses the rotation delta.
     */
    float oldYaw = camera.getYaw();
    float oldPitch = camera.getPitch();

    /*
     * Position
     */

    if ((flags & RELATIVE_X) != 0) {
      x += camera.getX();
    }

    if ((flags & RELATIVE_Y) != 0) {
      y += camera.getY()-64;
    }

    if ((flags & RELATIVE_Z) != 0) {
      z += camera.getZ();
    }

    /*
     * Rotation
     */

    if ((flags & RELATIVE_YAW) != 0) {
      yaw += oldYaw;
    }

    if ((flags & RELATIVE_PITCH) != 0) {
      pitch += oldPitch;
    }

    /*
     * Velocity
     *
     * First rotate the EXISTING velocity if requested.
     *
     * Minecraft specifies that this happens before
     * applying the velocity contained in this packet.
     */
    if ((flags & ROTATE_VELOCITY) != 0) {

      Vector3f rotatedVelocity = new Vector3f(
          camera.getVelocityX(),
          camera.getVelocityY(),
          camera.getVelocityZ()
      );

      float deltaYaw = yaw - oldYaw;
      float deltaPitch = pitch - oldPitch;

      /*
       * Yaw rotates around the Y axis.
       */
      rotatedVelocity.rotateY(
          (float) Math.toRadians(-deltaYaw)
      );

      /*
       * Pitch rotates around the X axis.
       */
      rotatedVelocity.rotateX(
          (float) Math.toRadians(deltaPitch)
      );

      camera.setVelocity(
          rotatedVelocity.x,
          rotatedVelocity.y,
          rotatedVelocity.z
      );
    }

    /*
     * Now apply the velocity from the packet.
     */
    double finalVelocityX =
        (flags & RELATIVE_VELOCITY_X) != 0
            ? camera.getVelocityX() + velocityX
            : velocityX;

    double finalVelocityY =
        (flags & RELATIVE_VELOCITY_Y) != 0
            ? camera.getVelocityY() + velocityY
            : velocityY;

    double finalVelocityZ =
        (flags & RELATIVE_VELOCITY_Z) != 0
            ? camera.getVelocityZ() + velocityZ
            : velocityZ;

    /*
     * Apply everything to the actual game state.
     */
    camera.setPosition(x, y+64, z);

    camera.setRotation(
        90+yaw,
        -pitch
    );

    camera.setVelocity(
        finalVelocityX,
        finalVelocityY,
        finalVelocityZ
    );

    /*
     * The client must acknowledge the teleport.
     */
    sendConfirmTeleportation(teleportId);
  }

  private void sendConfirmTeleportation(int teleportId) {
    ByteBuf buf = channel.alloc().buffer();
    C2SConfirmTeleport.make(buf,teleportId);
    channel.writeAndFlush(buf);
  }

}