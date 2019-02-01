package org.zizitop.pshell.utils;

import java.io.Serializable;
import java.util.Collection;

/**
 * 
 * @author Zizitop
 * 
 */
public class LinkedStack<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private StackElement<T> last;
	
	@SafeVarargs
	public LinkedStack(T... collection) {
		for(T t: collection) {
			last = new StackElement<T>(t, last);
		}
	}
	
	public LinkedStack(Collection<T> collection) {
		for(T t: collection) {
			last = new StackElement<T>(t, last);
		}
	}
	
	public void push(T value) {
		last = new StackElement<T>(value, last);
	}
	
	public T pop() {
		if(last == null) {
			throw new StackExhaustionException("Stack is empty, butt need return value!");
		}
		
		StackElement<T> ret = last;
		last = ret.previous;
		
		return ret.value;
	}
	
	public T peek() {
		if(last == null) {
			throw new StackExhaustionException("Stack is empty, butt need return value!");
		}
		
		return last.value;
	}
	
	public boolean isEmpty() {
		return last == null;
	}
	
	private static class StackElement<T> {
		public final T value;
		public final StackElement<T> previous;
		
		public StackElement(T value, StackElement<T> previous) {
			this.value = value;
			this.previous = previous;
		}
	}
	
	protected static class StackExhaustionException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		
		public StackExhaustionException(String message) {
			super(message);
		}
	}
}
