package net.piinut.voidophobia.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import net.piinut.voidophobia.Voidophobia;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class VoidophobiaMixin {
	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		Voidophobia.LOGGER.info("Voidophobia mixin injected.");
	}
}
