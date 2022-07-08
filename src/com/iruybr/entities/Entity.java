package com.iruybr.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.iruybr.main.Game;
import com.iruybr.world.Camera;
import com.iruybr.world.Node;
import com.iruybr.world.Vector2i;

public class Entity {
	//
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7*16, 0, 16, 16);
	public static BufferedImage WEAPON_LEFT = Game.spritesheet.getSprite(7*16, 2*16, 16, 16);
	public static BufferedImage WEAPON_RIGHT = Game.spritesheet.getSprite(7*16, 1*16, 16, 16);
	public static BufferedImage WEAPON_DAMAGE_LEFT = Game.spritesheet.getSprite(8*16, 2*16, 16, 16);
	public static BufferedImage WEAPON_DAMAGE_RIGHT = Game.spritesheet.getSprite(8*16, 1*16, 16, 16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(8*16, 0, 16, 16);
	public static BufferedImage ENEMY_EN2 = Game.spritesheet.getSprite(9*16, 0, 16, 16);
	public static BufferedImage ENEMY_DAMAGE = Game.spritesheet.getSprite(9*16, 16, 16, 16);
	
	//Declarar dimens�es privadas
	protected double x;
	protected double y;
	protected int width;
	protected int height;
	
	//Declarar profundidade
	public int depth;
	
	//Declarar Lista
	protected List<Node> path;
	
	//
	public boolean debug = false;
	
	//
	private BufferedImage sprite;
	
	//Declarar dimensões públicas da máscara
	public int maskx, masky, mwidth, mheight;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {							//M�todo Entidade
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskx = 0;
		this.masky = 0;
		this.mwidth = width;
		this.mheight = height;
	}
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {
		
		@Override
		public int compare (Entity n0, Entity n1) {
			if(n1.depth < n0.depth)
				return +1;
			if(n1.depth > n0.depth)
				return -1;
			return 0;
		}
	};
	
	public void setMask(int maskx, int masky, int mwidth, int mheight) {								//M�todo SetarM�scara
		this.maskx = maskx;
		this.masky = masky;
		this.mwidth = mwidth;
		this.mheight = mheight;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public int getX() {
		return (int)this.x;
	}

	public int getY() {
		return (int)this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void tick() {
		
	}
	
	public double calculatedDistance(int x1, int y1, int x2, int y2) {									//M�todo Dist�nciaCalculada
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));								//C�lculo da dist�ncia (Geometria Anal�tica)
	}
	
	public boolean isColliding(int xnext, int ynext) {													//M�todo Est�Colidindo
		Rectangle enemyCurrent = new Rectangle(xnext + maskx, ynext + masky, mwidth, mheight);			//Ret�ngulo
		for(int i = 0; i < Game.enemies.size(); i++) {													//Para 
			Enemy e = Game.enemies.get(i);																//
			if(e == this)																				//Se
				continue;																				//Continue
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx, e.getY() + masky, mwidth, mheight);	//
			if(enemyCurrent.intersects(targetEnemy)) {													//Se
				return true;																			//Retorna verdadeiro
			}
		}
		
		return false;																					//Sen�o retorna falso
	}
	
	public void followPath(List<Node> path) {															//M�todo SeguirCaminho
		if(path != null) {
			if(path.size() > 0) {
				Vector2i target = path.get(path.size() - 1).tile;
				//xprev = x;
				//yprev = y;
				if(x < target.x * 16 /*&& !isColliding(this.getX() + 1, this.getY())*/) {
					x++;
				}else if(x > target.x * 16 /*&& !isColliding(this.getX() - 1, this.getY())*/) {
					x--;
				}
				
				if(y < target.y * 16 /*&& !isColliding(this.getX(), this.getY() + 1)*/) {
					y++;
				}else if(y > target.y * 16 /*&& !isColliding(this.getX(), this.getY() - 1)*/) {
					y--;
				}
				
				if(x == target.x * 16 && y == target.y * 16) {
					path.remove(path.size() - 1);
				}
				
			}
		}
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.mwidth, e1.mheight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.mwidth, e2.mheight);
		
		return e1Mask.intersects(e2Mask);
	}
	
	
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y,null);
		
		//Teste visualizando hitbox
		//g.setColor(Color.red);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, mwidth, mheight);
	}
}
