package me.srrapero720.dimthread.mixin.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(GameRules.Type.class)
public interface GameRuleTypeInvoker {
    @Invoker("<init>")
    public static <T extends GameRules.Value<T>> GameRules.Type<T> invokeInit(Supplier<ArgumentType<?>> argumentType, Function<GameRules.Type<T>, T> ruleFactory, BiConsumer<MinecraftServer, T> changeCallback, GameRules.VisitorCaller<T> ruleAcceptor, FeatureFlagSet requiredFeatures) {
        throw new AssertionError();
    }
}
