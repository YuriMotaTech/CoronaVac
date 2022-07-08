package com.iruybr.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.iruybr.entities.AmmoShoot;
import com.iruybr.entities.Enemy;
import com.iruybr.entities.Entity;
import com.iruybr.entities.Player;
import com.iruybr.graficos.Spritesheet;
import com.iruybr.graficos.UI;
import com.iruybr.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener{					//	


	private static final long serialVersionUID = 1L;												//
	public static JFrame frame;																		//
	private Thread thread;																			//
	private boolean isRunning = true;																//
	public static final int WIDTH = 240;															//Declarando variï¿½vel pï¿½blica, estï¿½tica e final inteira Opï¿½ï¿½o Atual por padrï¿½o 240
	public static final int HEIGHT = 160;															//
	public static final int SCALE = 3;																//
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 3;														//
	
	private BufferedImage image;																	//

	public static List<Entity> entities;															//
	public static List<Enemy> enemies;																//
	public static List<AmmoShoot> bullets;															//
	
	public static Spritesheet spritesheet;															//
	
	public static World world;																		//
	
	public static Player player;																	//
	
	public static Random rand;																		//
	
	public UI ui;																					//
	
	//public int xx, yy;
	
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	//public Font newfont;
	
	public static String gameState = "MENU";														//
	private boolean showMessageGameOver = false;													//
	private int framesGameOver = 0;																	//
	private boolean restartGame = false;															//
	
	public Menu menu;																				//
	public static int[] pixels;
	public BufferedImage lightmap;
	public static int[] lightMapPixels;
	public static int[] minimapPixels;
	
	public boolean saveGame = false;
	
	public static BufferedImage minimap;
	
	public int mx, my;
	
	public Game() {																					//Mï¿½todo Construtor Game()
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize()));				//Fullscreen
		setPreferredSize(new Dimension(WIDTH*SCALE,HEIGHT*SCALE));								//Modo Janela
		initFrame();

		//Inicializando Objetos
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightMapPixels = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightMapPixels, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<AmmoShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0,0,16,16,spritesheet.getSprite(32,0,16,16));
		entities.add(player);
		world = new World("/level1.png");
		
		minimap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		minimapPixels = ((DataBufferInt)minimap.getRaster().getDataBuffer()).getData();
		
		/*
		try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		menu = new Menu();
	}
	
	public void initFrame(){
		frame = new JFrame("CoronaVac");
		frame.add(this);
		//frame.setUndecorated(true);															//Ativar pra fullscreen(retira barra da janela)
		frame.setResizable(false);
		frame.pack();
		//Icone da Janela
		Image gameIcon = null;																	//Declarar objeto do tipo Image
		try {																					//Tentar
			gameIcon = ImageIO.read(getClass().getResource("/gameIcon.png")); 					//Ler caminho do ï¿½cone
		}catch(IOException e) {																	//Caso dï¿½ exeï¿½ï¿½o
			e.printStackTrace();																//Joga na tela
		}
		Toolkit toolkit = Toolkit.getDefaultToolkit();											//
		Image image = toolkit.getImage(getClass().getResource("/cursorIcon.png"));				//
		Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "img");					//Criar cursor personalizado
		
		frame.setCursor(c);																		//
		frame.setIconImage(gameIcon);															//
		frame.setAlwaysOnTop(true);																//
		frame.setLocationRelativeTo(null);														//
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);									//
		frame.setVisible(true);																	//
	}
	
	public synchronized void start(){
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop(){
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		Game game = new Game();
		game.start();
	}
	
	public void tick(){
		if(gameState == "NORMAL") {
			//xx++;
			//yy++;
			if(this.saveGame) {
				this.saveGame = false;
				String[] opt1 = {"level"};
				int[] opt2 = {this.CUR_LEVEL};
				Menu.saveGame(opt1, opt2, 10);
				System.out.println("Jogo Salvo!");
			}
			this.restartGame = false;
			for(int i = 0; i < entities.size(); i++) {
				Entity e = entities.get(i);
				e.tick();
			}
			
			for(int i = 0; i < bullets.size(); i++) {
				bullets.get(i).tick();
			}
			
			if(enemies.size() == 0) {
				//System.out.println("PrÃ³ximo Level!");
				//AvanÃ§ar para o prÃ³ximo level!
				CUR_LEVEL++;
				if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
				}
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restartGame(newWorld);
			}
		}else if (gameState == "GAME_OVER") {
			//System.out.println("Game Over!");
			this.framesGameOver++;
			if(this.framesGameOver == 30) {
				this.framesGameOver = 0;
				if(this.showMessageGameOver) {
					this.showMessageGameOver = false;
				}else {
						this.showMessageGameOver = true;
				}
				
				if(restartGame) {
					this.restartGame = false;
					Game.gameState = "NORMAL";
					CUR_LEVEL = 1;
					String newWorld = "level"+CUR_LEVEL+".png";
					//System.out.println("newWorld");
					World.restartGame(newWorld);
				}
			}
		}else if(gameState == "MENU"){
			menu.tick();
		} 
	}
	//RotaÃ§Ã£o
	/*
	public void drawRectangleExample(int xoff, int yoff) {
		for(int xx = 0; xx < 32 ; xx++) {
			for(int yy = 0; yy < 32 ; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT)
					continue;
				pixels[xOff + (yOff*WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	//Vinheta
	public void applyLight() {
		
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				if(lightMapPixels[xx + (yy * WIDTH)] == 0xffffffff) {
					int pixel = Pixel.getLightBlend(pixels[xx+yy*WIDTH], 0x808080, 0);
					//pixels[xx + (yy * WIDTH)] = pixel;
				}
			}
		}
		
	}
	
	public void render( ){
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0,0,WIDTH,HEIGHT);
		
		/*Renderizaï¿½ï¿½o do Jogo*/
		world.render(g);
		Collections.sort(entities, Entity.nodeSorter);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		
		applyLight();
		ui.render(g);
		/**/
		g.dispose();
		g = bs.getDrawGraphics();
		//drawRectangleExample(xx, yy);
		g.drawImage(image,0,0,WIDTH*SCALE,HEIGHT*SCALE,null);
		g.setFont(new Font("arial", Font.BOLD, 19));
		g.setColor(Color.white);
		g.drawString("Munição: " + player.ammo, 20, 470);
	
		if(gameState == "MENU"){
			player.updateCamera();
			menu.render(g);
		}else if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g.setFont(new Font("arial", Font.BOLD, 30));
			g.setColor(Color.white);
			g.drawString("Game Over!", ((WIDTH/2)*SCALE) - 70, ((HEIGHT/2)*SCALE) - 10);
			g.setFont(new Font("arial", Font.BOLD, 25));
			if(showMessageGameOver) 
			g.drawString(">Pressione Enter para recomeçar<", ((WIDTH/2)*SCALE) - 200, ((HEIGHT/2)*SCALE) + 30);
		}
		
		World.renderMinimap();
		g.drawImage(minimap, 615, 10, World.WIDTH*5, World.HEIGHT*5 , null);																	//Renderizar Minimapa
		
		//Rotacionar objeto na direção do mouse
		/*
		Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(200 + 25 - my, 200 + 25 - mx);
		g2.rotate(angleMouse, 200+25 ,200+25);
		g.setColor(Color.red);
		g.fillRect(200, 200, 50, 50);
		*/
		
		//Adicionando fonte personalizada
		/*
		g.setFont(newfont);
		g.drawString("Teste com nova fonte", 20, 20);
		*/
		bs.show();
	}
	
	public void run(){
		//Sound.musicBackground.loop();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double timer = System.currentTimeMillis();
		requestFocus();
		while(isRunning){
			long now = System.nanoTime();
			delta+= (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1){
				tick();
				render();
				frames++;
				delta--;
			}
			
			if(System.currentTimeMillis() - timer >= 1000){
				System.out.println("FPS: "+frames);
				frames = 0;
				timer+=1000;
			}
		}
		stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = true;
			
			if(gameState == "MENU")
				menu.down = true;
		}else if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = true;
			
			if(gameState == "MENU")
				menu.up = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.restartGame = true;
			if(gameState == "MENU") {
			menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState = "MENU";
			menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL") {
				this.saveGame = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT ||
				e.getKeyCode() == KeyEvent.VK_D) {
			player.right = false;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT ||
				e.getKeyCode() == KeyEvent.VK_A) {
			player.left = false;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_DOWN ||
				e.getKeyCode() == KeyEvent.VK_S) {
			player.down = false;
		}else if(e.getKeyCode() == KeyEvent.VK_UP ||
				e.getKeyCode() == KeyEvent.VK_W) {
			player.up = false;
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() / 3);
		player.my = (e.getY() / 3);
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.mx = e.getX();
		this.my = e.getY();
	}

}

