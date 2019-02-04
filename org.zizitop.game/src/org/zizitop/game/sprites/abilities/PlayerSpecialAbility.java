package org.zizitop.game.sprites.abilities;

import org.zizitop.game.sprites.Entity;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Window;

/**
 * Ability, that have special methods for working with player instance.
 * <br><br>
 * Created 04.02.19 16:49
 *
 * @author Zizitop
 */
public interface PlayerSpecialAbility extends Ability {
	default void drawInterface(Entity owner, DisplayMode dm, Bitmap canvas) {}
	default void proceedInput(Entity owner, Window window) {}
}
