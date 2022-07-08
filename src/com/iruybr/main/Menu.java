package com.iruybr.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.iruybr.world.World;

public class Menu {
	
	public String[] options = {"novo jogo", "carregar jogo", "sair"};								//Declarando Strings p�blica com o nome das op��es do menu
	
	public int currentOption = 0;																	//Declarando vari�vel p�blica inteira Op��o Atual por padr�o 0
	public int maxOption = options.length - 1; 														//Declarando vari�vel p�blica inteira Op��es Totais por padr�o N�mero de Op��es existentes - 1
	
	public boolean up, down, enter;																	//Declarando vari�vel p�blica bollean 
	
	public static boolean pause = false;															//Declarando vari�vel p�blica bollean pause por padr�o falso
	
	public static boolean saveExists = false;														//Declarando vari�vel p�blica bollean saveExists por padr�o falso
	public static boolean saveGame = false;															//Declarando vari�vel p�blica bollean saveGame por padr�o falso
	
	public void tick() {																			//L�gica do Menu
		File file = new File("save.txt");															//Cria arquivo save.txt
		if(file.exists()) {																			//Se o arquivo existe
			saveExists = true;																		//saveExists = true
		}else {																						//Se o arquivo n�o existe
			saveExists = false;																		//saveExists = false
		}
		if(up) {																					//Se Direcional Cima pressionado
			up = false;																				//Direcional Cima por padr�o falso (Evita reconhecer bot�o pressionado infinitamente)
			currentOption--;																		//Op��o atual - 1 (Subir)
			if(currentOption < 0) {																	//Se Op��o Atual < 0 
				currentOption = maxOption;															//Voltar para a �ltima Op��o
			}
		}
		if(down) {																					//Se Direcional Baixo pressionado
			down = false;																			//Direcional Baixo por padr�o falso (Evita reconhecer bot�o pressionado infinitamente)
			currentOption++;																		//Op��o atual + 1 (Descer)
			if(currentOption > maxOption) {															//Se Op��o Atual > N�mero M�ximo de Op��es -> Voltar para a Primeira Op��o
				currentOption = 0;
			}
		}
		if(enter) {																					//Se Enter||Return pressionado
			enter = false;																			//Enter||Return por padr�o falso (Evita reconhecer bot�o pressionado infinitamente)
			if(options[currentOption] == "novo jogo" || options[currentOption] == "continuar") {	//Se Op��o Atual for "novo jogo" ou "continuar"
				Game.gameState = "NORMAL";															//Status do jogo muda para "NORMAL"
				pause = false;					  													//Vari�vel pause retorna falso
				file = new File("save.txt");	  													//Cria novo arquivo save.txt
				file.delete();	  																	//Apaga save.txt antigo
			}else if(options[currentOption] == "carregar jogo"){	  								//Se Op��o Atual for "carregar jogo"
				file = new File("save.txt");														//Cria arquivo save.txt
				if(file.exists()) {																	//Se o arquivo existe
					String saver = loadGame(10);													//
					applySave(saver);																//Roda m�todo AplicarSalvar
				}
			}else if(options[currentOption] == "sair") {											//Se Op��o Atual for "sair"
				System.exit(1);																		//Fechar programa
			}
		}
	}
	
	public static void applySave(String str) {														//M�todo AplicarSalvar
		String[] spl = str.split("/");																//
		for(int i = 0; i < spl.length; i++) {														//
			String[] spl2 = spl[i].split(":");														//
			switch(spl2[0]){																		//
				case "level":																		//
					World.restartGame("level"+spl2[1]+".png");										//
					Game.gameState = "NORMAL";														//
					pause = false;																	//
					break;
				case "vida":																		//
					Game.player.life = Integer.parseInt(spl2[1]);									//
					break;
			}
		}
	}
	
	public static String loadGame(int encode) {														//M�todo CarregarJogo
		String line = "";																			//String inicia vazia
		File file = new File("save.txt");															//
		if(file.exists()) {																			//Se arquivo existir
			try {
				String singleLine = null;
				BufferedReader reader = new BufferedReader(new FileReader("save.txt"));				//Ler o arquivo save.txt
				try {
					while((singleLine = reader.readLine()) != null){								//Ler linha por linha
						String[] trans = singleLine.split(":");										//
						char[] val = trans[1].toCharArray();										//
						trans[1] = "";
						for(int i = 0; i < val.length; i++) {
							val[i] -= encode;														//Decodifica
							trans[1] += val[i];														//
						}
						line += trans[0];
						line += ":";
						line += trans[1];
						line += "/";
					}
				}catch(IOException e) {}
			}catch(FileNotFoundException e) {}
		}
		return line;
	}
	
	public static void saveGame(String[] val1, int[] val2, int encode) {							//M�todo SalvarJogo
		BufferedWriter write = null;
		try {
			write = new BufferedWriter(new FileWriter("save.txt"));									//Escrever no arquivo save.txt
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < val1.length; i++) {														//Para
			String current = val1[i];																//
			current += ":";																			//
			char[] value = Integer.toString(val2[i]).toCharArray();									//Tranforma o valor num�rico de val2 em Char para criptografia
			for(int n = 0; n < value.length; n++) {													//Para
				value[n] += encode;																	//Codifica
				current += value[n];
			}
			try {
				write.write(current);																//Escrever
				if(i < val1.length - 1)
					write.newLine();
			}catch(IOException e) {}
		}
		try {
			write.flush();
			write.close();
		}catch(IOException e) {}
	}
	
	public void render(Graphics g) {																//Renderizando o Menu
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(new Color(0,0,0,100));
		g2.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
		g.setColor(Color.white);																	//Cor Branca
		g.setFont(new Font("arial", Font.BOLD, 40));												//Fonte Arial, Negrito, Tamanho 40
		g.drawString("CoronaVac", (Game.WIDTH*Game.SCALE/2) - 100, (Game.HEIGHT*Game.SCALE/2) - 180);	//Escrever String "CoronaVac", Posi��o x setada, Posi��o y setada
		
		//Op��es do menu
		g.setFont(new Font("arial", Font.BOLD, 28));												//Fonte Arial, Negrito, Tamanho 28
		if(pause == false){																			//Se Vari�vel pause for falso
			g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE/2) - 90, 120);							//Escrever String "Novo Jogo", Posi��o x setada, Posi��o y setada
		}else																						//Se Vari�vel pause for verdadeiro
			g.drawString("Resumir", (Game.WIDTH*Game.SCALE/2) - 90, 120);							//Escrever String "Resumir", Posi��o x setada, Posi��o y setada
		g.drawString("Carregar jogo", (Game.WIDTH*Game.SCALE/2) - 90, 160);							//Escrever String "Carregar jogo", Posi��o x setada, Posi��o y setada
		g.drawString("Sair", (Game.WIDTH*Game.SCALE/2) - 90, 200);									//Escrever String "Sair", Posi��o x setada, Posi��o y setada
		
		if(options[currentOption] == "novo jogo") {													//Se Op��o Atual for "novo jogo"
			g.drawString(">", (Game.WIDTH*Game.SCALE/2) - 130, 120);								//Escrever String ">", Posi��o x setada, Posi��o y setada
		}else if(options[currentOption] == "carregar jogo") {										//Se Op��o Atual for "carregar jogo"
			g.drawString(">", (Game.WIDTH*Game.SCALE/2) - 130, 160);								//Escrever String ">", Posi��o x setada, Posi��o y setada
		}else if(options[currentOption] == "sair") {												//Se Op��o Atual for "sair"
			g.drawString(">", (Game.WIDTH*Game.SCALE/2) - 130, 200);								//Escrever String ">", Posi��o x setada, Posi��o y setada
		}
	}

}
