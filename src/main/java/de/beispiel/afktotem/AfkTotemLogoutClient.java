package de.beispiel.afktotem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class AfkTotemLogoutClient implements ClientModInitializer {

	/** 10 Sekunden * 20 Ticks */
	public static final int AFK_TICKS = 10 * 20;

	private static int idleTicks = 0;
	private static float lastYaw = Float.NaN;
	private static float lastPitch = Float.NaN;
	private static int lastSlot = -1;
	private static Screen openScreen = null;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(AfkTotemLogoutClient::tick);

		// Merkt sich, ob gerade ein Menue (Chat, Inventar, Pause ...) offen ist
		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			openScreen = screen;
			ScreenEvents.remove(screen).register(closed -> {
				if (openScreen == closed) openScreen = null;
			});
		});
	}

	private static void tick(Minecraft mc) {
		LocalPlayer player = mc.player;
		if (player == null) {
			idleTicks = 0;
			lastYaw = Float.NaN;
			return;
		}

		boolean input = openScreen != null; // Chat, Inventar usw. zaehlt als Eingabe

		float yaw = player.getYRot();
		float pitch = player.getXRot();
		int slot = player.getInventory().getSelectedSlot();
		if (!Float.isNaN(lastYaw) && (yaw != lastYaw || pitch != lastPitch)) input = true;
		if (lastSlot != -1 && slot != lastSlot) input = true;
		lastYaw = yaw;
		lastPitch = pitch;
		lastSlot = slot;

		var o = mc.options;
		KeyMapping[] keys = {
			o.keyUp, o.keyDown, o.keyLeft, o.keyRight,
			o.keyJump, o.keyShift, o.keySprint,
			o.keyAttack, o.keyUse, o.keyDrop, o.keySwapOffhand
		};
		for (KeyMapping k : keys) {
			if (k.isDown()) {
				input = true;
				break;
			}
		}

		if (input) idleTicks = 0;
		else idleTicks++;
	}

	public static boolean isAfk() {
		return idleTicks >= AFK_TICKS;
	}

	public static void disconnect(Minecraft mc) {
		var connection = mc.getConnection();
		if (connection != null) {
			connection.getConnection().disconnect(
				Component.literal("AFK Totem Logout: Totem ist gepoppt, waehrend du AFK warst."));
		}
	}
}
