package de.beispiel.afktotem;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Simple settings stored in config/afktotemlogout.properties */
public class AfkConfig {

	public static final int MIN_SECONDS = 1;
	public static final int MAX_SECONDS = 60;

	public static boolean enabled = true;
	public static int seconds = 10;

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("afktotemlogout.properties");
	}

	public static void load() {
		Path f = file();
		if (!Files.exists(f)) return;
		try (InputStream in = Files.newInputStream(f)) {
			Properties p = new Properties();
			p.load(in);
			enabled = Boolean.parseBoolean(p.getProperty("enabled", "true"));
			seconds = clamp(Integer.parseInt(p.getProperty("seconds", "10").trim()));
		} catch (IOException | NumberFormatException e) {
			enabled = true;
			seconds = 10;
		}
	}

	public static void save() {
		Properties p = new Properties();
		p.setProperty("enabled", Boolean.toString(enabled));
		p.setProperty("seconds", Integer.toString(seconds));
		try (OutputStream out = Files.newOutputStream(file())) {
			p.store(out, "AFK Totem Logout settings");
		} catch (IOException ignored) {
		}
	}

	public static int clamp(int s) {
		return Math.max(MIN_SECONDS, Math.min(MAX_SECONDS, s));
	}
}
