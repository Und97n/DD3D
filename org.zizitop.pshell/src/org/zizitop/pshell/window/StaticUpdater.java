package org.zizitop.pshell.window;

import java.util.ArrayList;
import java.util.List;

public abstract class StaticUpdater {
	private static final List<StaticUpdater> updaters = new ArrayList<>(); 
	
	private static final ArrayList<Window> sources = new ArrayList<>();
	
	public StaticUpdater() {
		updaters.add(this);
	}
	
	public static void proceedUpdate(Window actionSource) {
		if(sources.contains(actionSource)) {
			sources.clear();
			sources.add(actionSource);
			
			for(int i = updaters.size() - 1; i >= 0; --i) {
				updaters.get(i).update();
			}
		} else {
			sources.add(actionSource);
		}
	}
	
	public abstract void update();
}
