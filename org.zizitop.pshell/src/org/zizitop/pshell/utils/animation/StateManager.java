package org.zizitop.pshell.utils.animation;

import org.zizitop.pshell.utils.Bitmap;

import java.io.Serializable;

/**
 * 
 * @author Zizitop
 *
 */
public class StateManager implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int currentState;
	private int statePointer;
	public double animationPointer;
	
	private final StateManagerHead manager;
	
	public StateManager(StateManagerHead manager) {
		this.currentState = manager.getStateManagerType().defaultState;
		
		this.manager = manager;
	}

	public void update(double timeMultiperForAnimation) {
		StateManagerType type = manager.getStateManagerType();
		
		--statePointer;
		
		if(statePointer <= 0 && !type.states[currentState].dontChangeToDefault) {
			switchState(type.defaultState);
		}
		
		animationPointer = type.states[currentState].animation.update(animationPointer, timeMultiperForAnimation);
	}
	
	public void switchState(final int newState) {
		StateManagerType type = manager.getStateManagerType();
		
		if(newState < 0 && newState >= type.states.length) {
			throw new IllegalArgumentException("New state pointer is out of range.");
		}
		
		final int oldState = currentState;
		
		currentState = newState;
		
		animationPointer = type.states[currentState].animation.restartAnimation();
		
		statePointer = type.states[currentState].duration;
		
		manager.proceedStateChange(this, oldState, newState);
	}

	public Bitmap getImage() {
		return manager.getStateManagerType().states[currentState].animation.getCurrentFrame(animationPointer);
	}
	
	public int getCurrentStateId() {
		return currentState;
	}
	
	public State getCurrentState() {
		return manager.getStateManagerType().states[currentState];
	}
	
	public int getStateTime() {
		return statePointer;
	}
	
	public int getAnimationPointer() {
		return (int) animationPointer;
	}
	
	public static class State {
		
		public final String name;
		public final int duration;
		public final Animation animation;
		public final boolean dontChangeToDefault;		
		
		public State(String name, int duration, Animation animation) {
			this(name, duration, animation, false);
		}
		
		public State(String name, int duration, Animation animation, boolean dontChangeToDefault) {
			this.name = name;
			this.duration = duration;
			this.animation = animation;
			
			this.dontChangeToDefault = dontChangeToDefault;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static class StateManagerType {
		
		public final State[] states;
		public final int defaultState;
		
		public StateManagerType(State[] states, int defaultState) {
			if(defaultState < 0 && defaultState >= states.length) {
				throw new IllegalArgumentException("Default state pointer is out of range.");
			}
			
			this.states = states;
			
			this.defaultState = defaultState;
		}
	}
	
	public interface StateManagerHead {
		default void proceedStateChange(StateManager as, int oldState, int newState) {}
		StateManagerType getStateManagerType();
	}
}
