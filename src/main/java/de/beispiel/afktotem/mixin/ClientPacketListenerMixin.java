package de.beispiel.afktotem.mixin;

import de.beispiel.afktotem.AfkTotemLogoutClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Shadow
	private Minecraft minecraft;

	/** Entity-Event 35 = Totem der Unsterblichkeit wurde ausgeloest. */
	@Inject(method = "handleEntityEvent", at = @At("HEAD"))
	private void afktotem$onEntityEvent(ClientboundEntityEventPacket packet, CallbackInfo ci) {
		if (packet.getEventId() != 35) return;
		if (minecraft.player == null || minecraft.level == null) return;
		if (packet.getEntity(minecraft.level) != minecraft.player) return;

		if (AfkTotemLogoutClient.isAfk()) {
			AfkTotemLogoutClient.disconnect(minecraft);
		}
	}
}
