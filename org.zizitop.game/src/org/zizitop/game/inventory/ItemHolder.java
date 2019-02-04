package org.zizitop.game.inventory;

import org.zizitop.game.sprites.Entity;
import org.zizitop.game.sprites.Structure;
import org.zizitop.game.sprites.abilities.Ability;
import org.zizitop.game.world.World;
import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.window.Window;

/**
 * For item holding in the world
 * @author Zizitop
 *
 */
public class ItemHolder extends Structure {
	private static final long serialVersionUID = 1L;

	//If player drop this item here holds timer
	private int canNotPickup = 0;
	private Item item;	
	
	public ItemHolder(double x, double y, Item it) {
		this(x, y, it, false);
	}
	
	public ItemHolder(double x, double y, Item it, boolean droppedFromMainActor) {
		super(x, y, it == null ? 0 : it.getZPosition());
		
		this.item = it;
		
		if(droppedFromMainActor) {
			canNotPickup = 60;
		}
	}

	@Override
	public double getElasticity() {
		return 0;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public double getSizeX() {
		return 0.3;
	}

	@Override
	public double getSizeY() {
		return 0.3;
	}

	@Override
	public Bitmap getTexture() {
		return item == null ? ResourceLoader.getInstance().getTexture("null") : item.getItemTexture();
	}

	public Item getItem() {
		return item;
	}

	@Override
	public void onEntityCollision(Entity e, World world) {
		PickupAbility[] pickupAbilities = e.getAbilityHolder().getAbilities(PickupAbility.class);

		for(PickupAbility a: pickupAbilities) {
			if(a.addItem(item)) {
				world.remove(this);
				return;
			}
		}
	}
}
