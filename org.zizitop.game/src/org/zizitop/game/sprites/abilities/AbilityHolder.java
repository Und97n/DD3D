package org.zizitop.game.sprites.abilities;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.world.World;
import org.zizitop.pshell.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Holder for {@link Ability}.
 * <br><br>
 * Created 02.02.19 19:31
 *
 * @author Zizitop
 */
public class AbilityHolder {
	private final List<Ability> abilities = new ArrayList<>();

	/**
	 * Get an ability of needed type.
	 * @param type exact class of ability
	 * @return null if no ability, else - needed ability
	 */
	public Ability getAbility(Class<? extends Ability> type) {
		for(Ability a: abilities) {
			if(a.getClass() == type) {
				// Only one ability of one type is possible
				return a;
			}
		}

		return null;
	}

	/**
	 * Add some ability to the holder
	 * @param ability ability for adding
	 * @return true if success, false if there is already ability with the same type
	 */
	public boolean addAbility(Ability ability) {
		if(getAbility(ability.getClass()) != null) {
			// Only one ability of one type is possible
			return false;
		}

		abilities.add(ability);

		return true;
	}

	/**
	 * Get all abilities, that extends/implements needed type.
	 * @param supertype needed type
	 * @return all abilities, that extends/implements needed supertype
	 */
	public<T extends Ability> T[] getAbilities(Class<T> supertype) {
		List<T> list = new ArrayList<T>();

		for(Ability a: abilities) {
			if(supertype.isAssignableFrom(a.getClass())) {
				list.add((T)a);
			}
		}

		return Utils.toArray(supertype, list);
	}

	/**
	 * Update all abilities.
	 * @param owner owner of this holder
	 * @param world owner of owner of this holder
	 */
	public void update(Entity owner, World world, double dt) {
		var iterator = abilities.iterator();

		while(iterator.hasNext()) {
			Ability e = iterator.next();

			e.update(owner, world, dt);

			// Remove ability if it is needed
			if(e.needRemove()) {
				iterator.remove();
			}
		}
	}

	/**
	 * Proceed consumer action for each ability.
	 */
	public void proceedAbilities(Consumer<Ability> consumer) {
		for(Ability a: abilities) {
			consumer.accept(a);
		}
	}
}
