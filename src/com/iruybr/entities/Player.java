package com.iruybr.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.iruybr.main.Game;
import com.iruybr.main.Sound;
import com.iruybr.world.Camera;
import com.iruybr.world.World;

public class Player extends Entity {

	public boolean right,left,down,up;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;
	public double speed = 1.2;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerRightDamage;
	private BufferedImage playerLeftDamage;
	
	private boolean hasGun = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public double life = 100, maxLife = 100;
	public int mx, my;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerRightDamage = Game.spritesheet.getSprite(2*16, 3*16, 16, 16);
		playerLeftDamage = Game.spritesheet.getSprite(3*16, 3*16, 16, 16);
		for(int i = 0; i < 4; i++) {
			rightPlayer[i]= Game.spritesheet.getSprite(32 + (i * 16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i]= Game.spritesheet.getSprite(32 + (i * 16), 16, 16, 16);
		}
	}

	public void tick() {
		depth = 1;
		moved = false;
		if(right && World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x+=speed;
		}else if(left && World.isFree((int)(x-speed),this.getY())) {
			moved = true;
			dir = left_dir;
			x-=speed;
		}
		if(up && World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y-=speed;
		}else if(down && World.isFree(this.getX(),(int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		this.checkCollisionLifePack();
		this.checkCollisionAmmo();
		this.checkCollisionGun();
		
		if(isDamaged){
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot) {
			shoot = false;
			if(hasGun && ammo > 0) {
			
				ammo--;
				//System.out.println("Atirando");
				
				int dx = 0;
				int px = 0;
				int py = 7;
				if(dir == right_dir) {
					px = 18; 
					dx = 1;
				}else {
					px = -8;
					dx = -1;
				}
				 
			AmmoShoot bullet = new AmmoShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, 0);
			Game.bullets.add(bullet);	
			}
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			if(hasGun && ammo > 0) {		
				ammo--;
				
				int px = 0, py = 8;
				double angle = 0;
				if(dir == right_dir) {
					px = 18;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x)); 
				}else {
					px = -8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				 
			AmmoShoot bullet = new AmmoShoot(this.getX() + px, this.getY() + py, 3, 3, null, dx, dy);
			Game.bullets.add(bullet);	
			}
		}
		
		if(life <= 0) {
			Sound.deathEffect.play();
			life = 0;
			//Game Over!
			Game.gameState = "GAME_OVER";
		}
		
		updateCamera();
		
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkCollisionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColliding(this, atual)) {
				hasGun = true;
				//System.out.println("Pegou a arma");
				Game.entities.remove(atual);
				return;
			}
			}	
		}
	}
	
	public void checkCollisionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColliding(this, atual)) {
				ammo+=20;
				//System.out.println("Munição atual: " + ammo);
				Game.entities.remove(atual);
				return;
			}
			}	
		}
	}
	
	public void checkCollisionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isColliding(this, atual)) {
				life+=8;
				if(life >= 100)
					life = 100;
				Game.entities.remove(atual);
				return;
			}
			}	
		}
	}
	
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == right_dir) {
			g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				//Desenhar arma pra direita
				g.drawImage(Entity.WEAPON_RIGHT, this.getX() + 8 - Camera.x, this.getY() + 1 - Camera.y, null);
			}
			}else if(dir == left_dir) {
			g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			if(hasGun) {
				//Desenhar arma pra esquerda
				g.drawImage(Entity.WEAPON_LEFT, this.getX() - 8 - Camera.x, this.getY() + 1 - Camera.y, null);
			}
			}
		}else {
			if(dir == right_dir) {
				g.drawImage(playerRightDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					//Desenhar arma HIGHLIGHT pra direita
					g.drawImage(Entity.WEAPON_DAMAGE_RIGHT, this.getX() + 8 - Camera.x, this.getY() + 1 - Camera.y, null);
				}
				}else if(dir == left_dir) {
					g.drawImage(playerLeftDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
				if(hasGun) {
					//Desenhar arma HIGHLIGHT pra esquerda
					g.drawImage(Entity.WEAPON_DAMAGE_LEFT, this.getX() - 8 - Camera.x, this.getY() + 1 - Camera.y, null);
				}
				}
		}
	}
}
