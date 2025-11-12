package com.kevinthegreat.consentcreepers.mixin;

import com.kevinthegreat.consentcreepers.ConsentCreepers;
import com.kevinthegreat.consentcreepers.CreeperAccessor;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Comparator;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin {
    @Inject(method = "handleCustomClickAction", at = @At("HEAD"), cancellable = true)
    private void handleCreeperConsent(ServerboundCustomClickActionPacket serverboundCustomClickActionPacket, CallbackInfo ci) {
        if (!serverboundCustomClickActionPacket.id().getNamespace().equals(ConsentCreepers.MOD_ID)) return;
        ServerPlayer player = ((ServerGamePacketListenerImpl) (Object) this).getPlayer();
        Creeper creeper = Collections.min(player.level().getEntities(EntityTypeTest.forClass(Creeper.class), c -> ((CreeperAccessor) c).consentCreepers$askedForConsent()), Comparator.<Creeper>comparingDouble(c -> c.distanceToSqr(player)));
        if (creeper == null) return;

        if (serverboundCustomClickActionPacket.id().equals(ConsentCreepers.YES)) {
            ((com.kevinthegreat.consentcreepers.CreeperAccessor) creeper).consentCreepers$setConsent(true);
        } else if (serverboundCustomClickActionPacket.id().equals(ConsentCreepers.NO)) {
            ((com.kevinthegreat.consentcreepers.CreeperAccessor) creeper).consentCreepers$setConsent(false);
        }
        ci.cancel();
    }
}
