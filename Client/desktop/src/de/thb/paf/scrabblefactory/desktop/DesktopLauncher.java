package de.thb.paf.scrabblefactory.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.thb.paf.scrabblefactory.ScrabbleFactory;

/**
 * Launcher responsible for starting the game on a conventional desktop system.
 *
 * @author Dominic Schiller, Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 *
 * @see LwjglApplication
 */

public class DesktopLauncher {

	private static final int WINDOW_WIDTH = 1920;
	private static final int WINDOW_HEIGHT = 1080;

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = WINDOW_HEIGHT;
		config.width = WINDOW_WIDTH;
		config.useHDPI = true;
		new LwjglApplication(new ScrabbleFactory(), config);
	}
}
