package org.zizitop.desktop;

import org.zizitop.pshell.resources.ResourceLoader;
import org.zizitop.pshell.utils.Bitmap;
import org.zizitop.pshell.utils.DDFont;
import org.zizitop.pshell.utils.Log;
import org.zizitop.pshell.utils.Utils;
import org.zizitop.pshell.utils.exceptions.FileSavingException;
import org.zizitop.pshell.window.*;
import org.zizitop.pshell.window.DisplayMode;
import org.zizitop.pshell.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

/**
 * "Main 3D" inc. Window implementation<br> 
 * Very cool, but without hardware acceleration supporting in content.
 * @author Zizitop
 *
 */
public class JDWindow implements org.zizitop.pshell.window.Window {
	public static final boolean SHOW_FPS_OPTION = true;

	private static final InputOption input_switchConsole = InputOption.getInputOption("menu.console.switch");
	private static final InputOption input_openNewWindow = InputOption.getInputOption("menu.openNewWindow");
	private static final InputOption input_switchFullscreen = InputOption.getInputOption("menu.switchFullscreen");
	private static final InputOption input_screenshot = InputOption.getInputOption("menu.screenshot");

	private static final Bitmap pointer1 = ResourceLoader.getInstance().getTexture("pointer1");
	
	private static Cursor transparentCursor;
	private static Robot robot;
		
	private final Bitmap screen;

	private final JFrame frame;
	private final MainCanvas canvas;
	private final BufferedImage screenImage;

	private final InputSystem inputSystem;

	private Scene currentScene;

	// Text in upper right corner of window.
	private final String upperRightText;

	private int width;
	private int height;
	
	private final org.zizitop.pshell.window.DisplayMode dm;
	
	private int contentWidth;
	private int contentHeight;
	private int contentX;
	private int contentY;
	
	private boolean running, fullscreen; 
	
	private boolean contentUpdate;

	private double mouseScreenX;
	private double mouseScreenY;

	private double mouseX;
	private double mouseY;

	private double mouseDx;
	private double mouseDy;

	private double realFPS;
	
	static {
		
		try {
			@SuppressWarnings("unused")
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			
			// Transparent 16 x 16 pixel cursor image.
			BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

			// Create a new blank cursor.
			transparentCursor = Toolkit.getDefaultToolkit().createCustomCursor(
			    cursorImg, new Point(0, 0), "blank cursor");
			
			//Default game cursor
			//Image image = Utils.loadBuf("data/textures/pointer1.png");
			//cursor1 = toolkit.createCustomCursor(image , new Point(image.getWidth(null) / 2, image.getHeight(null) / 2), "Cursor 1");

		} catch(Exception e) {
			Log.write("Problems with cursors creating.", 3);
			Log.writeException(e);
		}
		
		try {
			robot = new Robot();
		} catch (Exception e) {
			Log.write("Problems with java.awt.Robot.", 4);
			Log.writeException(e);
		}
	}
	
	public JDWindow(String name, String upperRightText, int windowWidth, int windowHeight,
	                org.zizitop.pshell.window.DisplayMode dm, boolean fullscreen) {

		this.upperRightText = upperRightText;

		this.dm = dm;
		
		screenImage = new BufferedImage(dm.getContentWidth(), dm.getContentHeight(), BufferedImage.TYPE_INT_RGB);
		
		screen = JavaDesktop.getLinkedBitmap(screenImage);
		
		this.width = windowWidth;
		this.height = windowHeight;
		
		frame = new JFrame(name);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		
		JDWindow w = this;
		
		frame.addComponentListener(new ComponentAdapter() {
			@Override
		    public void componentResized(ComponentEvent e) {
				if(!w.fullscreen) {
					width = frame.getWidth();
					height = frame.getHeight();
				}
			}
		});
		
		frame.addWindowListener(new WindowAdapter() {
			 @Override
			 public void windowClosing(WindowEvent we) {
				 exitMainLoop();
			 }
			 
			 @Override
			 public void windowActivated(WindowEvent we) {
				 contentUpdate = true;
			 }
			 
			 @Override
			 public void windowDeactivated(WindowEvent we) {
//				 contentUpdate = false;
			 }
		});
		
		canvas = new MainCanvas();
		frame.add(canvas);
		
		canvas.setFocusTraversalKeysEnabled(false);

		JDInputHandler ih = new JDInputHandler();

		inputSystem = new InputSystem(ih);
		ih.link(canvas);
		
		canvas.setFocusable(true);
		
		canvas.addComponentListener(new ComponentAdapter() {
			
			@Override
		    public void componentResized(ComponentEvent e) {
				reshape(e.getComponent().getWidth(), e.getComponent().getHeight());
			}
		});		
		
		changeScene(EmptyScene.emptyScene);
		
		if(fullscreen) {
			setFullscreen(fullscreen, false);
		} else {
			frame.setVisible(true);
		}
	}
	
	@Override
	public void startMainLoop() throws Exception {
		try {
			running = true;
			
			//Time in seconds
			double lastTime = System.nanoTime() / 1000_000_000.0;
			double deltaForTick = 0, deltaForDraw = 0;
			
			//For fps measuring
			double lastFPSCheck = 0;
			long framesPerSecond = 0;
			
			while(running) {
				//For safety scene changing
				Scene currentScene = this.currentScene;
				
				//Time between cycles
				double delta = (System.nanoTime() / 1000_000_000.0) - lastTime;
				
				lastTime = System.nanoTime() / 1000_000_000.0;
				
				deltaForTick += delta * Window.TICK_FPS;
				
				//For smooth physics
				while(deltaForTick >= 1) {
					StaticUpdater.proceedUpdate(this);

					if(contentUpdate) {
						checkMainInput();
						currentScene.tick(this);
					}
					
					--deltaForTick;

					updateInput();
				}
				
				deltaForDraw += delta * dm.getFPS();
				
				if(contentUpdate) {
					currentScene.draw(screen, dm);
					++framesPerSecond;
					
					if(SHOW_FPS_OPTION) {
						DDFont.CONSOLE_FONT.draw(screen, 0, 0, "FPS: " + String.format("%.2f", realFPS), -1);
					}
					
					{						
//						DDFont.CONSOLE_FONT.draw(screen, 1 - DDFont.CONSOLE_FONT.getWidth(Main.VERSION), 0, Main.VERSION, 0xff7f00);
						
						final double w = (double)pointer1.width / dm.getBaseWidth();
						final double h = (double)pointer1.height / dm.getBaseHeight();
						
						switch(currentScene.getCustomCursor()) {
						case 1: screen.draw_SC(pointer1, mouseX / screen.width - w / 2,
								mouseY / screen.height - h / 2, w, h);
						break;
						}						
					}
				}
				
				
				//Drawing on canvas is slow
				while(deltaForDraw >= 1) {
					canvas.draw(screenImage);
					
					--deltaForDraw;
				}
				
				double currentTime = (System.nanoTime() / 1000_000_000.0);
				
				//Math real fps
				if((currentTime - lastFPSCheck) >= 1) {
					realFPS = (double)(framesPerSecond) / (double)(currentTime - lastFPSCheck);
					
					framesPerSecond = 0;
					lastFPSCheck = currentTime;
				}

			}
		} catch(Exception e) {
			Log.write("Exception in window main loop: " + e.getClass().getSimpleName(), 4);
			
			throw e;
		}
	}

	private void updateInput() {
		inputSystem.clear();

		double mouseDistanceX, mouseDistanceY;


		double mouseScreenPreviousX = mouseScreenX;
		double mouseScreenPreviousY = mouseScreenY;

		Point mousePosition = canvas.getMousePosition();

		if(mousePosition == null) return;

		mouseScreenX = mousePosition.x;
		mouseScreenY = mousePosition.y;

		mouseDistanceX = mouseScreenPreviousX - mouseScreenX;
		mouseDistanceY = mouseScreenPreviousY - mouseScreenY;

		mouseX = (double)(mouseScreenX - contentX) * (double)(screen.width) / (double)(contentWidth);
		mouseY = (double)(mouseScreenY - contentY) * (double)(screen.height) / (double)(contentHeight);

		if(mouseX >= screen.width) {
			mouseX = screen.width;
		} else if(mouseX < 0) {
			mouseX = 0;
		}

		if(mouseY >= screen.height) {
			mouseY = screen.height;
		} else if(mouseY < 0) {
			mouseY = 0;
		}

		mouseDx = this.mouseX - mouseX;
		mouseDy = this.mouseY - mouseY;

		if(contentUpdate && currentScene.lockMouse() && robot != null) {
			Point contentLocation = canvas.getLocationOnScreen();

			int mouseScreenX = contentLocation.x + canvas.getWidth() / 2;
			int mouseScreenY = contentLocation.y + canvas.getHeight() / 2;

			robot.mouseMove(mouseScreenX, mouseScreenY);

			mouseDistanceX += mousePosition.x + contentLocation.x - mouseScreenX;
			mouseDistanceY += mousePosition.y + contentLocation.y - mouseScreenY;
		}

		mouseDx = mouseDistanceX;
		mouseDy = mouseDistanceY;
	}
	
	private void setFullscreen(boolean fullscreen) {
		setFullscreen(fullscreen, true);
	}
	
	private void setFullscreen(boolean fullscreen, boolean initialized) {
		if(this.fullscreen == fullscreen) {
			 return;
		} else {
			this.fullscreen = fullscreen;
			
			if(fullscreen) {
				if(initialized) {
					frame.dispose();
				}
				
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				frame.setUndecorated(true);
				
				frame.setVisible(true);
			} else {
				if(initialized) {
					frame.dispose();
				}
				
				frame.setExtendedState(0);
				frame.setSize(width, height);
				frame.setLocationRelativeTo(null);
				frame.setUndecorated(false);	
				
				frame.setVisible(true);
			}
		}
	}
	
	private void checkMainInput() {
		if(getActionsCount(input_switchConsole) > 0 && !(currentScene instanceof ConsoleScene)) {
			changeScene(ConsoleScene.getConsoleScene(currentScene, screen));
		}
		
		if(getActionsCount(input_openNewWindow) > 0) {
			
			Thread t = new Thread(() -> {
				Window w = new JDWindow("Deadly Dungeon 3D", null, 800, 600, dm, false);
				
				try {
					//Console.console.switchConsole(w);
					w.startMainLoop();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			t.start();
		}
		
		if(getActionsCount(input_switchFullscreen) > 0) {
			setFullscreen(!fullscreen);
		}
		
		if(getActionsCount(input_screenshot) > 0) {
			try {
				if(JavaDesktop.saveBuf(screenImage, Window.SCREENSHOT_DIRECTORY, Utils.getDateAndTime(), Window.SCREENSHOT_EXTENSION)) {
					Log.write("Screenshot created: " + Utils.getDateAndTime(), 0);
				} else {
					Log.write("Screenshot not created.", 3);
				}
			} catch (FileSavingException e) {
				Log.write("Can't save screenshot.", 3);
				Log.writeException(e);
			}
			
			
		}
	}
	
	@Override
	public Bitmap copyScreen() {
		return screen.copy();
	}
	
	private void reshape(int newWidth, int newHeight) {
		
		final double aspect = (double)newWidth / (double)newHeight;
		final double targetAspect = (double)screen.width / (double)screen.height;
		
		double scale = (double)newHeight / (double)screen.height;
		double wrongScale = (double)newWidth / (double)screen.width;
		
		if(aspect > targetAspect) {
			//If window is too wide
			
			contentWidth = (int)((double)screen.width * scale);
			contentHeight = (int)((double)screen.height * scale);
			
			contentX = ((int)((double)screen.width * wrongScale) - contentWidth) / 2;
			contentY = 0;
		} else {
			//if window is too high
			
			/*Swap value*/{
				final double tmp = scale;
				scale = wrongScale;
				wrongScale = tmp;
			};
			
			contentWidth = (int)((double)screen.width * scale);
			contentHeight = (int)((double)screen.height * scale);
			
			contentX = 0;
			contentY = ((int)((double)screen.height * wrongScale) - contentHeight) / 2;
		}
	}
	
	/**
	 * You can do this in any time without problems.
	 */
	@Override
	public void changeScene(Scene newScene) {
		currentScene = newScene;
		
		if(canvas != null) {			
			switch(currentScene.getCustomCursor()) {
			case 0: canvas.setCursor(Cursor.getDefaultCursor()); break;
			default: canvas.setCursor(transparentCursor); break;
			}
		}
	}
	
	@Override
	public int getContentWidth() {
		return screen.width;
	}
	
	@Override
	public int getContentHeight() {
		return screen.height;
	}
	
	@Override
	public int getWidth() {
		return frame.getWidth();
	}
	
	@Override
	public int getHeight() {
		return frame.getHeight();
	}

	@Override
	public void exitMainLoop() {
		running = false;
		frame.dispose();
	}
	
	@Override
	public Scene getCurrentScene() {
		return currentScene;
	}

	private final WindowContext context = new WindowContext(this);

	@Override
	public WindowContext getContext() {
		return context;
	}

	@Override
	public void getFirstPressedKey(GetKeyResultReceiver gkrr) {
		inputSystem.getFirstPressedKey(gkrr);
	}

	@Override
	public boolean mouseMoved() {
		return getMouseDx() != 0 || getMouseDy() != 0;
	}

	@Override
	public DisplayMode getDisplayMode() {
		return dm;
	}

	public double getMouseX() {
		return mouseX;
	}

	public double getMouseY() {
		return mouseY;
	}

	@Override
	public double getMouseDx() {
		return mouseDx;
	}

	@Override
	public double getMouseDy() {
		return mouseDy;
	}

	@Override
	public String getLastEnteredText() {
		return inputSystem.getLastEnteredText();
	}

	public double getAspectRatio() {
		return getContentWidth();
	}

	public boolean isPressed(InputOption io) {
		return inputSystem.isPressed(io);
	}

	@Override
	public boolean wasReleased(InputOption io) {
		return inputSystem.wasReleased(io);
	}

	public int getActionsCount(InputOption io) {
		return inputSystem.getActionsCount(io);
	}

	private class MainCanvas extends Canvas {
		private static final long serialVersionUID = 1L;

		private void draw(BufferedImage data) {			
			BufferStrategy bs = this.getBufferStrategy();
			
			if(bs == null) {
				//Double fuffering
				this.createBufferStrategy(2);	
				return;
			}
			
			try {
				Graphics g = bs.getDrawGraphics();
				
				Graphics2D gg = (Graphics2D) g;
				
				gg.setColor(Color.BLACK);
				
				gg.fillRect(0, 0, getWidth(), getHeight());				
				
				//Hearth of the game graphics
				gg.drawImage(data, contentX, contentY, contentWidth, contentHeight, null);
				
				gg.dispose();
				bs.show();
			} catch(IllegalStateException e) {}
		}
	}	
}
