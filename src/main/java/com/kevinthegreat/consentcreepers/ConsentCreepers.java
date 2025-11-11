package com.kevinthegreat.consentcreepers;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistrySetupCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.*;
import net.minecraft.server.dialog.action.StaticAction;
import net.minecraft.server.dialog.body.PlainMessage;

import java.util.List;
import java.util.Optional;

public class ConsentCreepers implements ModInitializer {
	public static final String MOD_ID = "consentcreepers";
    public static final ResourceKey<Dialog> CREEPER_CONSENT_DIALOG_KEY = ResourceKey.create(Registries.DIALOG, ResourceLocation.fromNamespaceAndPath(MOD_ID, "creeper_consent"));
	public static final ResourceLocation YES = ResourceLocation.fromNamespaceAndPath(MOD_ID, "yes");
	public static final ResourceLocation NO = ResourceLocation.fromNamespaceAndPath(MOD_ID, "no");
	public static final Dialog CREEPER_CONSENT_DIALOG = new ConfirmationDialog(
			new CommonDialogData(Component.literal("Creeper Consent"), Optional.empty(), false, true, DialogAction.CLOSE, List.of(new PlainMessage(Component.literal("Do you consent to this creeper exploding?"), PlainMessage.DEFAULT_WIDTH)), List.of()),
			new ActionButton(new CommonButtonData(Component.literal("Yes"), CommonButtonData.DEFAULT_WIDTH), Optional.of(new StaticAction(new ClickEvent.Custom(YES, Optional.empty())))),
			new ActionButton(new CommonButtonData(Component.literal("No"), CommonButtonData.DEFAULT_WIDTH), Optional.of(new StaticAction(new ClickEvent.Custom(NO, Optional.empty()))))
	);

	@Override
	public void onInitialize() {
        DynamicRegistrySetupCallback.EVENT.register(registryView -> {
            registryView.getOptional(Registries.DIALOG).ifPresent(registry -> {
                Registry.register(registry, CREEPER_CONSENT_DIALOG_KEY, CREEPER_CONSENT_DIALOG);
            });
        });
	}
}
