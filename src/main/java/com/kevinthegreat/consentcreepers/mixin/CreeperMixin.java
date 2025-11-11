package com.kevinthegreat.consentcreepers.mixin;

import com.kevinthegreat.consentcreepers.ConsentCreepers;
import com.kevinthegreat.consentcreepers.CreeperAccessor;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Creeper.class)
public abstract class CreeperMixin extends Monster implements CreeperAccessor {
    @Shadow
    protected abstract void explodeCreeper();

    @Unique
    private boolean asked;
    @Unique
    private boolean consent;

    protected CreeperMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public boolean consentCreepers$askedForConsent() {
        return asked;
    }

    @Override
    public void consentCreepers$setConsent(boolean consent) {
        this.consent = consent;
        explodeCreeper();
    }

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;explodeCreeper()V"))
    private void askForConsent(Creeper instance, Operation<Void> original) {
        if (!(level() instanceof ServerLevel)) return;

        if (asked) return;
        Player player = this.getTarget() instanceof Player p ? p : level().getNearestPlayer(this, 10);
        player.openDialog(level().registryAccess().getOrThrow(ConsentCreepers.CREEPER_CONSENT_DIALOG_KEY));
        asked = true;
    }

    @WrapWithCondition(method = "explodeCreeper", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;explode(Lnet/minecraft/world/entity/Entity;DDDFLnet/minecraft/world/level/Level$ExplosionInteraction;)V"))
    private boolean explodeIfConsent(ServerLevel instance, Entity entity, double x, double y, double z, float r, Level.ExplosionInteraction explosionInteraction) {
        return consent;
    }
}
