package com.iruybr.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.iruybr.main.Game;

public class UI {
	
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(7, 3, 52, 10);
		g.setColor(Color.red);
		g.fillRect(8, 4, 50, 8);
		g.setColor(Color.green);
		g.fillRect(8, 4, (int)((Game.player.life/Game.player.maxLife)*50), 8);
		g.setColor(Color.white);
		g.setFont(new Font("arial", Font.BOLD, 8));
		g.drawString( (int)Game.player.life+"/"+(int)Game.player.maxLife, 18, 11);
	}

}
