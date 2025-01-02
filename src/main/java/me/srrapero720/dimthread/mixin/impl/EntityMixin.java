package me.srrapero720.dimthread.mixin.impl;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import me.srrapero720.dimthread.DimThread;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract Level level();

    @Shadow protected abstract Entity teleportCrossDimension(ServerLevel serverLevel, TeleportTransition teleportTransition);

    @Shadow protected abstract Entity teleportSameDimension(ServerLevel serverLevel, TeleportTransition teleportTransition);

    /**
     * Schedules moving entities between dimensions to the server thread. Once all the world finish ticking,
     * {@code moveToWorld()} is processed in a safe manner avoiding concurrent modification exceptions.
     * <p>
     * For example, the entity list is not thread-safe and modifying it from multiple threads will cause
     * a crash. Additionally, loading chunks from another thread will cause a deadlock in the server chunk manager.
     */
    @Inject(method = "teleport", at = @At("HEAD"), cancellable = true, remap = false)
    public void moveToWorld(TeleportTransition teleportTransition, CallbackInfoReturnable<Entity> cir) {
        if (!DimThread.MANAGER.isActive(teleportTransition.newLevel().getServer())) return;

        if (DimThread.owns(Thread.currentThread())) {
            if (this.level() instanceof ServerLevel serverLevel) {
                boolean bl = serverLevel.dimension() != teleportTransition.newLevel().dimension();
                if (bl) {
                    teleportTransition.newLevel().getServer().execute(() -> this.teleportCrossDimension(teleportTransition.newLevel(), teleportTransition));
                } else {
                    teleportTransition.newLevel().getServer().execute(() -> this.teleportSameDimension(serverLevel, teleportTransition));
                }
            }
            cir.setReturnValue(null);
        }
    }
}