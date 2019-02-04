package org.zizitop.dd3d.content.scenes;

import org.zizitop.pshell.menu.ActionComponent.ActionListener;
import org.zizitop.pshell.menu.ActionComponent.Event;
import org.zizitop.pshell.menu.BackButton;
import org.zizitop.pshell.menu.Slider;
import org.zizitop.pshell.menu.StandardMenuBuilder;
import org.zizitop.pshell.resources.Sound;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.utils.Lang;
import org.zizitop.pshell.window.Scene;

import static org.zizitop.pshell.resources.Sound.SoundType;

public class SoundOptionsMenu extends StandardMenuBuilder {
	private Slider mainVolume = new Slider(Lang.getText("menu.soundOptionsMenu.mainVolumeSlider") + ": " + 
			Math.round(Sound.getMainVolume() * 100.0) + "%", 0.45, 0.1, 0, 1);
	private Slider menuVolume = new Slider(Lang.getText("menu.soundOptionsMenu.menuVolumeSlider") + ": " + 
			Math.round(SoundType.MenuSound.getTypeVolume() * 100.0) + "%", 0.45, 0.1, 0, 1);
	private Slider effectsVolume = new Slider(Lang.getText("menu.soundOptionsMenu.effectsVolumeSlider") + ": " + 
			Math.round(SoundType.MenuSound.getTypeVolume() * 100.0) + "%", 0.45, 0.1, 0, 1);
	private Slider musicVolume = new Slider(Lang.getText("menu.soundOptionsMenu.musicVolumeSlider") + ": " + 
			Math.round(SoundType.MenuSound.getTypeVolume() * 100.0) + "%", 0.45, 0.1, 0, 1);
	
	public SoundOptionsMenu() {}
	
	@Override
	protected void createMenu(Scene previousScene) {
		mainVolume.setFont(DDFont.CONSOLE_FONT);
		menuVolume.setFont(DDFont.CONSOLE_FONT);
		effectsVolume.setFont(DDFont.CONSOLE_FONT);
		musicVolume.setFont(DDFont.CONSOLE_FONT);
		
		mainVolume.setValue(Sound.getMainVolume());
		menuVolume.setValue(SoundType.MenuSound.getTypeVolume());
		effectsVolume.setValue(SoundType.Effects.getTypeVolume());
		musicVolume.setValue(SoundType.Music.getTypeVolume());
		
		mainVolume.setActionListener(new VolumeActionListener("mainVolume", null));
		menuVolume.setActionListener(new VolumeActionListener("menuVolume", SoundType.MenuSound));
		effectsVolume.setActionListener(new VolumeActionListener("effectsVolume", SoundType.Effects));
		musicVolume.setActionListener(new VolumeActionListener("musicVolume", SoundType.Music));
		
		mainContainer.add(mainVolume, menuVolume, effectsVolume, musicVolume, new BackButton(previousScene));
	}
	
	private static class VolumeActionListener implements ActionListener {
		private String namePart;
		private SoundType type;
		
		public VolumeActionListener(String namePart, SoundType type) {
			this.namePart = namePart;
			this.type = type;
		}
		
		@Override
		public void onAction(Event e) {
			Slider s = (Slider) e.eventSource;
			
			final double value = s.getValue();
			
			if(type != null) {
				type.setTypeVolume((float) value);
			} else {
				Sound.setMainVolume(value);
			}
			
			s.setText(Lang.getText("menu.soundOptionsMenu." + namePart + "Slider") + ": " + 
					Math.round(value * 100.0) + "%");
		}
		
	}

	@Override
	protected String getMenuName() {
		return Lang.getText("menu.soundOptionsMenu.nameOfMenu");
	}
}
