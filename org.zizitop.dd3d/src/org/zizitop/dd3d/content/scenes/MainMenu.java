package org.zizitop.dd3d.content.scenes;

import org.zizitop.dd3d.content.inventory.InventoryPlayerBag;
import org.zizitop.dd3d.content.mainActors.Steve;
import org.zizitop.game.sprites.abilities.HealthAbility;
import org.zizitop.game.world.Level;
import org.zizitop.game.world.Sector;
import org.zizitop.game.world.World;
import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.menu.Button;
import org.zizitop.pshell.menu.StandardMenuBuilder;
import org.zizitop.pshell.menu.TextLabel;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Scene;

/**
 * 
 * @author Zizitop
 *
 */
public final class MainMenu extends StandardMenuBuilder {
	private Button play = new Button(Lang.getText("menu.mainMenu.playButton"));
	private Button load = new Button(Lang.getText("menu.mainMenu.loadButton"));
	
	private Button options = new Button(Lang.getText("menu.mainMenu.optionsButton"));
	private Button exit = new Button(Lang.getText("menu.mainMenu.exitButton"));
	
	public MainMenu() {}

	@Override
	protected void createMenu(Scene previousScene) {		
		mainContainer.add(play, load, options, exit);
		
		play.setActionListener(this);
		load.setActionListener(this);
		exit.setActionListener(this);
		options.setActionListener(this);
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.mainMenu.gameName");
	}
	
	@Override
	protected TextLabel getMenuNameLabel() {
		return super.getMenuNameLabel().setColor(0x7f0000);
	}
	
	public static Scene getGameScene(DisplayMode dm) {
		double[] verts = {
				-24, 24, 24, 24, -24, -24, 24, -24,

				-4, 4, 4, 4, -4, -4, 4, -4,
				-3, 4, 3, 4, -3, -4, 3, -4,
				-3, 3, 3, 3, -3, -3, 3, -3,
				-4, 3, 4, 3, -4, -3, 4, -3,
				-5, -3, -5, 3,
				-6, -3, -6, 3,
				-6, -24, -6, 24,
		};

		int[][] walls = {
				{-5, 0, 0, -1, 0, 0, -3, 0},
				{-2, 0, 0, -1, 0, 0, -4, 0},
				{-3, 0, 0, -1, 0, 0, -5, 0},
				{-4, 0, 0, -1, 0, 0, -2, 0},
		};

		int[][] verticies = {
				{0, 4, 8, 12, 13, 9, 5, 1, 0},
				{1, 5, 17, 13, 15, 19, 7, 3, 1},
				{3, 7, 11, 15, 14, 10, 6, 2, 3},
				{2, 6, 18, 14, 12, 16, 4, 0, 2},
		};

		Sector[] sectors = {
				new Sector(new int[]{12, 14, 15, 13, 12}, new int[]{-3, -2, -2, -2}, 0.5, 5.5),
				new Sector(new int[]{2, 3, 1, 0, 4, 8, 12, 13, 9, 5, 17, 13, 15, 19, 7, 11, 15, 14, 10, 6, 2},
						new int[]{0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0, 0, -1, 0, 0, 0}, 0, 3),
				new Sector(new int[]{12, 16, 18, 14, 12}, new int[]{0, -4, 0, -1}, 1, 6),
				new Sector(new int[]{16, 21, 20, 18, 16}, new int[]{0, -5, 0, -3}, 1.5, 6.5),
				new Sector(new int[]{21, 23, 22, 20, 21}, new int[]{0, -6, 0, -4}, 2, 7),
				new Sector(new int[]{22, 23, 1, 0, 2, 3, 22}, new int[]{-5, 0, 0, 0, 0, 0}, 2.5, 7.5),

//				new Sector(verticies[0], walls[0], 0, 3),
//				new Sector(verticies[1], walls[1], 0, 3),
//				new Sector(verticies[2], walls[2], 0, 3),
//				new Sector(verticies[3], walls[3], 0, 3),


//				new Sector(verticies[5], walls[5], 0, 3),
//				new Sector(verticies[6], walls[6], 0, 3),
		};

		Steve hero = new Steve(0, 0);

		hero.getAbilityHolder().addAbility(new InventoryPlayerBag());
		hero.getAbilityHolder().addAbility(new HealthAbility(100, 100));

		Level l = new Level(verts, sectors);
		l.add(hero);
		World world = new World(l, hero);

		return new GameSceneImpl(world);
	}

	@Override
	public void onAction(Event e) {
		if(e.eventSource == play) {
			e.window.changeScene(getGameScene(e.window.getDisplayMode()));
		} else if(e.eventSource == load) {
			switchScene(e.window, new LoadMenu());
		} else if(e.eventSource == options) {
			switchScene(e.window, new OptionsMenu());
		} else if(e.eventSource == exit) {
			e.window.exitMainLoop();
		}
	}
}
