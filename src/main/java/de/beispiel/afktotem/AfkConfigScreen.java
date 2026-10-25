package de.beispiel.afktotem;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AfkConfigScreen extends Screen {

	private Button toggleButton;

	public AfkConfigScreen() {
		super(Component.literal("AFK Totem Logout"));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int y = this.height / 2 - 50;

		StringWidget title = new StringWidget(cx - 100, y - 25, 200, 9, this.title, this.font);
		title.alignCenter();
		this.addRenderableWidget(title);

		toggleButton = Button.builder(toggleText(), b -> {
			AfkConfig.enabled = !AfkConfig.enabled;
			b.setMessage(toggleText());
			AfkConfig.save();
		}).bounds(cx - 100, y, 200, 20).build();
		this.addRenderableWidget(toggleButton);

		this.addRenderableWidget(new TimeSlider(cx - 100, y + 28, 200, 20));

		this.addRenderableWidget(Button.builder(Component.literal("Done"), b -> this.onClose())
			.bounds(cx - 100, y + 66, 200, 20).build());
	}

	private static Component toggleText() {
		return Component.literal("Mod: " + (AfkConfig.enabled ? "ON" : "OFF"));
	}

	@Override
	public void onClose() {
		AfkConfig.save();
		if (this.minecraft != null) this.minecraft.setScreen(null);
	}

	private static class TimeSlider extends AbstractSliderButton {
		TimeSlider(int x, int y, int w, int h) {
			super(x, y, w, h, Component.empty(), toValue(AfkConfig.seconds));
			this.updateMessage();
		}

		private static double toValue(int seconds) {
			return (seconds - AfkConfig.MIN_SECONDS) / (double) (AfkConfig.MAX_SECONDS - AfkConfig.MIN_SECONDS);
		}

		private int seconds() {
			return AfkConfig.clamp((int) Math.round(AfkConfig.MIN_SECONDS
				+ this.value * (AfkConfig.MAX_SECONDS - AfkConfig.MIN_SECONDS)));
		}

		@Override
		protected void updateMessage() {
			this.setMessage(Component.literal("AFK time: " + seconds() + " s"));
		}

		@Override
		protected void applyValue() {
			AfkConfig.seconds = seconds();
			AfkConfig.save();
		}
	}
}
