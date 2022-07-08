package com.iruybr.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.iruybr.main.Game;
import com.iruybr.main.Sound;
import com.iruybr.world.AStar;
import com.iruybr.world.Camera;
import com.iruybr.world.Vector2i;

public class Enemy extends Entity{

	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	
	private BufferedImage[] sprites;
	
	private int life = 5;
	
	private boolean isDamaged = false;
	private int damagedFrames = 10, damageCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(8*16, 0, 16, 16);
		sprites[1] = Game.spritesheet.getSprite(9*16, 0, 16, 16);
	}
	
	public void tick() {
		depth = 0;
		//Definir m�scara de colis�o
		maskx = 3;																				//In�cio da m�scara x = 3
		masky = 3;																				//In�cio da m�scara y = 3
		mwidth = 8;																				//Largura da m�scara x = 8
		mheight = 8;																			//Altura da m�scara x = 8
		
		if(!isCollidingWithPlayer()) {															//Se Colis�oComJogador retorna falso
			if(path == null || path.size() == 0) {												//Se caminho � 0 ou n�o existe
				Vector2i start = new Vector2i((int)(x/16), (int)(y/16));						//Cria 
				Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));	//Cria 
				path = AStar.findPath(Game.world, start, end);									//Roda o algoritmo AStar
			}
		}else {																					//Se Colis�oComJogador retorna verdadeiro
			if(new Random().nextInt(100) < 5) {													//Se 5%deChance ocorrer
				//Sound.hurtEffect.play();														//Tocar som hurtEffect
				Game.player.life -= Game.rand.nextInt(8);										//Diminuir n�mero aleat�rio entre 1 e 3 da vida do jogador
				Game.player.isDamaged = true;													//JogadorSerDanificado retorna verdadeiro
				//System.out.println("Vida: "+ Game.player.life);								//Printa no console o life do jogador
			}
		}
		
		if(new Random().nextInt(100) < 60) 
			followPath(path);																	//Roda o m�todo SeguirCaminho
		if(new Random().nextInt(100) < 5) {
			Vector2i start = new Vector2i((int)(x/16), (int)(y/16));						//Cria 
			Vector2i end = new Vector2i((int)(Game.player.x/16), (int)(Game.player.y/16));	//Cria 
			path = AStar.findPath(Game.world, start, end);
		}
			
		//Anima��o do inimigo
		frames++;																				//Adiciona frames
		if(frames == maxFrames) {																//Se frame atual = limite de frames
			frames = 0;																			//Frame atual volta pra 0
			index++;																			//Muda para pr�xima imagem
			if(index > maxIndex) {																//Se imagem atual > �ltima imagem
				index = 0;																		//Se imagem atual volta pra primeira imagem
			}
		}
		
		collidingBullet();																		//Roda o m�todo Colis�oComProj�til
				
		//Morte do inimigo
		if(life <= 0) {																			//Se vida <= 0
			destroySelf();																		//Roda o m�todo AutoDestrui��o
			return;
		}
				
		//Anima��o/Highlight de dano
		if(isDamaged) {																			//Se SerDanificado retorna verdadeiro 
			this.damageCurrent++;																//Adiciona DanoAtual
			if(this.damageCurrent == this.damagedFrames) {										//
				this.damageCurrent = 0;															//
				this.isDamaged = false;															//SerDanificado retorna falso
			}
		}
				
	}
	
	public void destroySelf() {																	//M�todo AutoDestrui��o
		Game.enemies.remove(this);																//Remove esse inimigo
		Game.entities.remove(this);																//Remove essa entidade
	}
	
	public void collidingBullet() {																//M�todo ColidindoComProj�til
		for(int i = 0; i < Game.bullets.size(); i++) {											//
			Entity e = Game.bullets.get(i);														//
			if(e instanceof AmmoShoot) {														//
				if(Entity.isColliding(this, e)) {												//Se colis�o retorna verdadeiro
					isDamaged = true;															//SerDanificado retorna verdadeiro
					life--;																		//Vida diminu�da em 1
					Game.bullets.remove(i);														//Proj�til � removido
					return;
				}
			}
		}
		
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskx, this.getY() + masky, mwidth, mheight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return enemyCurrent.intersects(player);
	}
	
	
	public void render(Graphics g) {
		super.render(g);
		if(!isDamaged) {
			g.drawImage(sprites[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
		} else
			g.drawImage(Entity.ENEMY_DAMAGE, this.getX() - Camera.x, this.getY() - Camera.y, null);
		
		//Teste visualizando hitbox
		//g.setColor(Color.blue);
		//g.fillRect(this.getX() + maskx - Camera.x, this.getY() + masky - Camera.y, mwidth, mheight);
	}
}
