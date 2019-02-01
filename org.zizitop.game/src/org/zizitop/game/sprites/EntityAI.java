package org.zizitop.game.sprites;

import org.zizitop.game.world.World;

import java.io.Serializable;

/**
 * 
 * @author Zizitop
 *
 */
public abstract class EntityAI implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final Entity actor;
	
	public EntityAI(Entity actor) {
		this.actor = actor;
	}
	
	public void updateAI(World w) {
		
	}
}
