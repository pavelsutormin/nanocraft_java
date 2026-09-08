package org.sutormin.nanocraft;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.sutormin.nanocraft.networking.Networking;
import org.sutormin.nanocraft.networking.packets.play.player.C2SSetPlayerPosition;
import org.sutormin.nanocraft.world.Chunk;
import org.sutormin.nanocraft.world.ChunkPos;

public class Camera {

    private final Vector3f position = new Vector3f(8.0f, 20.0f, 25.0f);
    private final Vector3f lastPosition = new Vector3f(position);

    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);

    private final Vector3f movement = new Vector3f();
    private final Vector3f velocity = new Vector3f(0f, 0f, 0f);

    private float yaw = -90.0f;
    private float pitch = 0.0f;

    private final float sensitivity = 0.1f;

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(
            position,
            movement.set(position).add(front),
            up
        );
    }

    public void updatePosition(
        float forwardBack,
        float rightLeft,
        float upDown,
        float speed,
        float dt
    ) {
        float distance = speed * dt;

        if (forwardBack != 0.0f) {
            velocity.add(
                movement
                    .set(front.x, 0.0f, front.z)
                    .normalize()
                    .mul(forwardBack * distance)
            );
        }

        if (rightLeft != 0.0f) {
            velocity.add(
                movement
                    .set(right)
                    .mul(rightLeft * distance)
            );
        }

        if (upDown != 0.0f) {
            velocity.add(
                movement
                    .set(up)
                    .mul(upDown * distance)
            );
        }
    }

    public void processMouseInput(float xOffset, float yOffset) {
        yaw += xOffset * sensitivity;
        pitch -= yOffset * sensitivity;

        pitch = Math.max(-89.0f, Math.min(89.0f, pitch));

        updateFront();
    }

    private void updateFront() {
        front.x = (float) (
            Math.cos(Math.toRadians(yaw))
                * Math.cos(Math.toRadians(pitch))
        );

        front.y = (float) Math.sin(Math.toRadians(pitch));

        front.z = (float) (
            Math.sin(Math.toRadians(yaw))
                * Math.cos(Math.toRadians(pitch))
        );

        front.normalize();

        front.cross(
            0.0f,
            1.0f,
            0.0f,
            right
        ).normalize();
    }

    public ChunkPos getChunkPos() {
        int chunkX = (int) Math.floor(position.x / Chunk.SIZE_X);
        int chunkZ = (int) Math.floor(position.z / Chunk.SIZE_Z);

        return new ChunkPos(chunkX, chunkZ);
    }

    public void setPosition(double x, double y, double z) {
        position.set(
            (float) x,
            (float) y,
            (float) z
        );
    }

    public void setRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;

        updateFront();
    }

    public void setVelocity(
        double x,
        double y,
        double z
    ) {
        velocity.set(
            (float) x,
            (float) y,
            (float) z
        );
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    public float getZ() {
        return position.z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getVelocityX() {
        return velocity.x;
    }

    public float getVelocityY() {
        return velocity.y;
    }

    public float getVelocityZ() {
        return velocity.z;
    }

    public void tick() {
        position.add(velocity);
        velocity.mul(0.98f);
        if (!position.equals(lastPosition)) {
            ByteBuf buf = Unpooled.buffer();
            C2SSetPlayerPosition.make(
                buf,
                position.x,
                position.y-64,
                position.z,
                (byte) 0
            );

            Networking.sendPacket(buf);

            lastPosition.set(position);
        }
    }
}