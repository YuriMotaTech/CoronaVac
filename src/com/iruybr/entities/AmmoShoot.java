package com.iruybr.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.iruybr.main.Game;
import com.iruybr.world.Camera;

public class AmmoShoot extends Entity{
	
	private double dx;
	private double dy;
	private double ammoSpeed = 4;
	
	private int life = 35, curLife =0;
	
	public AmmoShoot(int x, int y, int width, int height, BufferedImage sprite, double dx, double dy) {
		super(x, y, width, height, sprite);
		this.dx = dx;
		this.dy = dy;
	}

	public void tick() {
		x+=dx*ammoSpeed;
		y+=dy*ammoSpeed;
		curLife++;
		if(curLife == life) {
			Game.bullets.remove(this);
			return;
		}
		
	}
	
	public void render(Graphics g) {
		g.setColor(Color.cyan);
		g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, width, height);
	}
	
}
