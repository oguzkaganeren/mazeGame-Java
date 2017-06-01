import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import enigma.console.TextAttributes;
import enigma.core.Enigma;

public class Game {
	private enigma.console.Console cn;
	private Random rnd = new Random();
	private CircularQueue input=new CircularQueue(10);//input k�sm�n� tutacak
	private Stack backPack=new Stack(5);//stack'dir i�inde item ismi, bosluk ve item no tutar
	private Item[] items=new Item[20];//i�erisinde oyunda yer alan itemleri tutar
	private Timer timer;//bilgisayar i�in tan�mland�
	TextAttributes txtAtt;//yaz� renkleri i�in tan�mland�
	Player ourPlayer;
	Player computer;
	private char[][] fileChar=new char[26][85];//i�erisinde sadece template tutar
	private int keypr;   // key pressed?
	private int rkey;    // key   (for press/release)
	private KeyListener klis; 
	private int targetX;//bilgisayar i�in kullan�lacak
	private int targetY;//bilgisayar i�in kullan�lacak
	private String[] computerEnvironment=new String[4];//i�erisinde bilgisayar�n gidebilece�i alanlar tutulacak(�evresindeki)
	private Stack computerGone;//gidece�i yolu tutar
	private String[] roadTimes;//gitti�i yollar� eklerim 2 defa eklendiyse tekrar o yolu kullanmaz
	private int positionOfNear;
	private int computerSpeed;//oyunun zorluk derecesine g�re de�i�ecektir.
	//menu i�in
	private String[] menu=new String[4];
	private int selectedMenu=0;
	private boolean isFinish=true;
	public Game(){
		cn=Enigma.getConsole("Energy Maze",90,27,18);
		String tempFileName="maze.txt";
		getTemplate(tempFileName);
		fillInput();
		//oyuncuyu olu�turuyoruz
		int[] xy=new int[2];
		do {
			xy[0]=rnd.nextInt(19)+2;
			xy[1]=rnd.nextInt(54)+2;
		} while (fileChar[xy[0]][xy[1]]!=' '||controlOfItems(xy[0],xy[1])!=' ');
		ourPlayer=new Player('H', xy[0], xy[1]);
		//-------------------------------
		//computer olu�turuyoruz
		xy=new int[2];
		do {
			xy[0]=rnd.nextInt(19)+2;
			xy[1]=rnd.nextInt(54)+2;
		} while (fileChar[xy[0]][xy[1]]!=' '||controlOfItems(xy[0],xy[1])!=' '
				||(ourPlayer.getxPosition()==xy[0]&&ourPlayer.getyPosition()!=xy[1]));
		computer=new Player('C', xy[0], xy[1]);
		//---------MENU------------------
		createKeyListener();
		menu[0]="New Game";
		menu[1]="Change Template";
		menu[2]="About The Game";
		menu[3]="Exit";
		String[] subMenu=new String[3];
		subMenu[0]="Easy";
		subMenu[1]="Normal";
		subMenu[2]="Hard";
		boolean isSelect=true;
		boolean isSelectLevel=true;
		while(isSelect) {
			 if(keypr==1) {    // if keyboard button pressed
	        	  switch (rkey) {
	        	  case KeyEvent.VK_UP:
	        		  if (selectedMenu>0) {
						selectedMenu--;
					}
	        		  break;
	        	  case KeyEvent.VK_DOWN:
	        		  if (selectedMenu<3) {
							selectedMenu++;
						}
	        		  break;
	        	  case KeyEvent.VK_ENTER:
	        		   	 switch (selectedMenu) {
	        		   	 case 0://New Game
	        		   		 keypr=0;    // last action 
	        		   		 clearScreen();
	        		   		 selectedMenu=0;
	        		   		 while (isSelectLevel) {
	        		   			 if(keypr==1) {    // if keyboard button pressed
	        			        	  switch (rkey) {
	        			        	  case KeyEvent.VK_UP:
	        			        		  if (selectedMenu>0) {
	        								selectedMenu--;
	        							}
	        			        		  break;
	        			        	  case KeyEvent.VK_DOWN:
	        			        		  if (selectedMenu<2) {
	        									selectedMenu++;
	        								}
	        			        		  break;
	        			        	  case KeyEvent.VK_ENTER:
	        			        		  isSelectLevel=false;
	        			        		  isSelect=false;
	        			        		  switch (selectedMenu) {
										case 0:
											computerSpeed=700;
											computer.setSpeed(computerSpeed);
											break;
										case 1:
											computerSpeed=500;
											computer.setSpeed(computerSpeed);
											break;
										case 2:
											computerSpeed=200;
											computer.setSpeed(computerSpeed);
											break;

										default:
											break;
										}
	        			        		  break;
	        			        	  }
	        			        	  keypr=0;    // last action  	  
	        			        }
	        		   			 try {
	        		 				Thread.sleep(60);
	        		 			} catch (InterruptedException e) {
	        		 				// TODO Auto-generated catch block
	        		 				e.printStackTrace();
	        		 			}
	        		   			drawMenu(subMenu);
	        		   			
							}
	        		   		 break;
						case 1://Template
							clearScreen();
							cn.getTextWindow().setCursorPosition(28,10);
							System.out.print("Please enter a path and a file name");
							Scanner scan=new Scanner(System.in);
							cn.getTextWindow().setCursorPosition(35,11);
							tempFileName=scan.nextLine();
							getTemplate(tempFileName);
							break;
						case 2://help
							drawHelp();
							Scanner scan2=new Scanner(System.in);
							scan2.nextLine();
							break;
						case 3://exit
							System.exit(0);//oyunu kapat�r
							break;
						}
	        		  break;

	        	  }
		          keypr=0;    // last action  
			 }
			 try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 drawMenu(menu);
		}
		//-------------------------------
		randomPlaceItems();
		drawScreen();
		computerMovements();
		while(isFinish) {
			 if(keypr==1) {    // if keyboard button pressed
	        	  switch (rkey) {
	        	  case KeyEvent.VK_LEFT:
	        		  playerMove(ourPlayer.getxPosition(),ourPlayer.getyPosition()-1);
	        		  break;
	        	  case KeyEvent.VK_RIGHT:
	        		  playerMove(ourPlayer.getxPosition(),ourPlayer.getyPosition()+1);
	        		  break;
	        	  case KeyEvent.VK_UP:
	        		  playerMove(ourPlayer.getxPosition()-1,ourPlayer.getyPosition());
	        		  break;
	        	  case KeyEvent.VK_DOWN:
	        		  playerMove(ourPlayer.getxPosition()+1,ourPlayer.getyPosition());
	        		  break;
	        	  case KeyEvent.VK_W:
						backpackIn(ourPlayer.getxPosition()-1, ourPlayer.getyPosition());
						break;
	        	  case KeyEvent.VK_A:
						backpackIn(ourPlayer.getxPosition(), ourPlayer.getyPosition()-1);
						break;
	        	  case KeyEvent.VK_S:
						backpackIn(ourPlayer.getxPosition()+1, ourPlayer.getyPosition());
						break;
	        	  case KeyEvent.VK_D:
						backpackIn(ourPlayer.getxPosition(), ourPlayer.getyPosition()+1);
						break;
	        	  case KeyEvent.VK_I:
						backpackOut(ourPlayer.getxPosition()-1, ourPlayer.getyPosition());
						break;
	        	  case KeyEvent.VK_J:
						backpackOut(ourPlayer.getxPosition(), ourPlayer.getyPosition()-1);
						break;
	        	  case KeyEvent.VK_K:
						backpackOut(ourPlayer.getxPosition()+1, ourPlayer.getyPosition());
						break;
	        	  case KeyEvent.VK_L:
						backpackOut(ourPlayer.getxPosition(), ourPlayer.getyPosition()+1);
						break;

	        	  }
		          keypr=0;    // last action  
			 }
			 try {
				Thread.sleep(ourPlayer.getSpeed());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 randomPlaceItems();//e�er timer ile itemlar�n isimleri silindiyse bu fons. sayesinde doldururuz
			 drawScreen();
		}
		clearScreen();//buraya d��t�yse oyun kaybedilmi�tir
		cn.getTextWindow().setCursorPosition(25, 5);
		for (int i = 0; i < 37; i++) {
			System.out.print("*");
		}
		cn.getTextWindow().setCursorPosition(25, 6);
		for (int i = 0; i < 10; i++) {
			System.out.print("*");
			for (int j = 0; j < 35; j++) {
				System.out.print(" ");
			}
			System.out.print("*");
			cn.getTextWindow().setCursorPosition(25, 7+i);
		}
		for (int i = 0; i < 37; i++) {
			System.out.print("*");
		}
		cn.getTextWindow().setCursorPosition(39, 10);
		System.out.print("GAME OVER");
	}
	/*ekran� yazd�rma*/
	public void drawScreen(){
		 addInputItems();//input i�erisindekiler ekrana yazmak i�in fileChar i�ine atar
		cn.getTextWindow().setCursorPosition(0, 0);
		for (int i = 0; i<fileChar.length; i++) {
			for (int j = 0; j < fileChar[0].length; j++) {
				if (fileChar[i][j]=='#') {
					txtAtt=new TextAttributes(Color.WHITE,Color.BLACK);
					cn.setTextAttributes(txtAtt);
					System.out.print(fileChar[i][j]);
				}else if (fileChar[i][j]=='<') {
						txtAtt=new TextAttributes(Color.WHITE,Color.BLACK);
						cn.setTextAttributes(txtAtt);
						System.out.print(fileChar[i][j]);
				}else if (fileChar[i][j]=='H') {
					txtAtt=new TextAttributes(Color.WHITE,Color.BLUE);
					cn.setTextAttributes(txtAtt);
					System.out.print(fileChar[i][j]);
				}else if (fileChar[i][j]=='C') {
					txtAtt=new TextAttributes(Color.WHITE,Color.RED);
					cn.setTextAttributes(txtAtt);
					System.out.print(fileChar[i][j]);
				}
				else {
					txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
					cn.setTextAttributes(txtAtt);
					System.out.print(fileChar[i][j]);
				}
			}
			System.out.println();
		}
		if (path!=null) {
			if (!path.isEmpty()) {
				drawPath();
			}
			
		}
		drawOurPlayer();
		drawComp();
		drawEnergy();
		drawItems();//itemleri yazd�r�r
		writeBackPack();
		
		
	}
	public void drawPath(){
		Stack tempGone=new Stack(1000);
		while (!path.isEmpty()) {
			int cX=Integer.parseInt(path.peek().toString().split(" ")[0]);
			int cY=Integer.parseInt(path.peek().toString().split(" ")[1]);
			cn.getTextWindow().setCursorPosition(cY, cX);
			System.out.print(".");
			tempGone.push(path.pop());
		}
		while (!tempGone.isEmpty()) {
			path.push(tempGone.pop());
		}
		path=null;
	}
	public void drawEnergy(){
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		cn.getTextWindow().setCursorPosition(60, 19);
		System.out.print(ourPlayer.getEnergy());//oyuncumuzun enerjisini yazd�r�r
		cn.getTextWindow().setCursorPosition(60, 20);
		System.out.print(computer.getEnergy());//oyuncumuzun enerjisini yazd�r�r
	}
	public void drawOurPlayer(){
		txtAtt=new TextAttributes(Color.WHITE,Color.BLUE);
		cn.setTextAttributes(txtAtt);
		cn.getTextWindow().setCursorPosition(ourPlayer.getyPosition(), ourPlayer.getxPosition());
		System.out.print(ourPlayer.getPlayerName());
	}
	public void drawComp(){
		txtAtt=new TextAttributes(Color.WHITE,Color.RED);
		cn.setTextAttributes(txtAtt);
		cn.getTextWindow().setCursorPosition(computer.getyPosition(), computer.getxPosition());
		System.out.print(computer.getPlayerName());
	}
	public void drawItems(){
		//itemleri ekrana yazd�r�yoruz
				for (int i = 0; i < items.length; i++) {
					if (items[i].getxPos()!=-1&&items[i].getyPos()!=-1)//-1 olmas� s�rt �antas�nda olmas� anlam�na gelir 
					{
						int itemTime=items[i].getTime();
						txtAtt=new TextAttributes(Color.WHITE,new Color(150+itemTime,150+itemTime,itemTime*2));
						cn.setTextAttributes(txtAtt);
						cn.getTextWindow().setCursorPosition(items[i].getyPos(), items[i].getxPos());
						System.out.print(items[i].getItemName());
					}
				}
	}
	public void drawMenu(String[] inMenu){
		cn.getTextWindow().setCursorPosition(0, 0);
		for (int i = 0; i < 27; i++) {
			for (int j = 0; j < 89; j++) {
				cn.getTextWindow().setCursorPosition(j, i);
				txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
				cn.setTextAttributes(txtAtt);
				System.out.print(" ");
			}
		}
		for (int i = 0; i <inMenu.length ; i++) {
			cn.getTextWindow().setCursorPosition(35,i+10);
			if (i==selectedMenu) {
				txtAtt=new TextAttributes(Color.WHITE,Color.BLACK);
				cn.setTextAttributes(txtAtt);
				System.out.print(inMenu[i]);
			}
			else {
				txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
				cn.setTextAttributes(txtAtt);
				System.out.print(inMenu[i]);
			}
		}
	}
	public void drawHelp(){
		clearScreen();
		cn.getTextWindow().setCursorPosition(15,4);
		System.out.print("Keys:");
		cn.getTextWindow().setCursorPosition(17,5);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("Cursor Keys");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To move human player");
		cn.getTextWindow().setCursorPosition(17,6);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("W");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To put an item on your head into the backpack ");
		cn.getTextWindow().setCursorPosition(17,7);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("A");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To put an item on your left into the backpack ");
		cn.getTextWindow().setCursorPosition(17,8);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("D");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To put an item on your right into the backpack ");
		cn.getTextWindow().setCursorPosition(17,9);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("S");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To put an item on your bottom into the backpack ");
		cn.getTextWindow().setCursorPosition(17,10);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("I");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To remove an item on your head into the backpack ");
		cn.getTextWindow().setCursorPosition(17,11);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("J");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To remove an item on your left into the backpack ");
		cn.getTextWindow().setCursorPosition(17,12);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("L");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To remove an item on your right into the backpack ");
		cn.getTextWindow().setCursorPosition(17,13);
		txtAtt=new TextAttributes(Color.BLACK,Color.YELLOW);
		cn.setTextAttributes(txtAtt);
		System.out.print("K");
		txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
		cn.setTextAttributes(txtAtt);
		System.out.print(":To remove an item on your bottom into the backpack ");
		txtAtt=new TextAttributes(Color.WHITE,Color.BLACK);
		cn.setTextAttributes(txtAtt);
		cn.getTextWindow().setCursorPosition(17,25);
		System.out.print("Please enter any key on your board to pass menu");
	}
	//menudeki yaz�lar� siler(menuden sonra i�lem yapt�racaksam kullan�r�m)
	public void clearScreen(){
		cn.getTextWindow().setCursorPosition(0, 0);
		for (int i = 0; i < 27; i++) {
			for (int j = 0; j < 89; j++) {
				cn.getTextWindow().setCursorPosition(j, i);
				txtAtt=new TextAttributes(Color.BLACK,Color.WHITE);
				cn.setTextAttributes(txtAtt);
				System.out.print(" ");
			}
		}
	}

	public void createKeyListener(){
		klis=new KeyListener() {
	         public void keyTyped(KeyEvent e) {}
	         public void keyPressed(KeyEvent e) {
	            if(keypr==0) {
	               keypr=1;
	               rkey=e.getKeyCode();
	            }
	         }
	         public void keyReleased(KeyEvent e) {}
	      };
	      cn.getTextWindow().addKeyListener(klis);
	};
	public void playerMove(int inputX,int inputY){
		if(fileChar[inputX][inputY]==' '|| controlOfItems(inputX,inputY)=='*'){
					 if (controlOfItems(inputX,inputY)=='*') {
						 ourPlayer.setEnergy(ourPlayer.getEnergy()+25);
						 items[getItemNo(inputX, inputY)].setItemName(' ');
						 randomPlaceItems();
					 }
					//e�er 1,2,3,4 ise s�r�kleme i�lemi yapacak
					 else if ((int)controlOfItems(inputX,inputY)>48
							 &&(int)controlOfItems(inputX,inputY)<53) {
						 switch (rkey) {
			        	  case KeyEvent.VK_LEFT:
			        		  if (fileChar[inputX][inputY-1]==' '&&controlOfItems(inputX,inputY-1)==' ') {
									 //item'i ilereye s�r�yorum
									 items[getItemNo(inputX, inputY)].setyPos(inputY-1);
									 ourPlayer.setxPosition(inputX);
									 ourPlayer.setyPosition(inputY);
									 controlOfSameItem(items[getItemNo(inputX, inputY-1)].getxPos(),items[getItemNo(inputX, inputY-1)].getyPos());//item'�n etraf�n� kontrol eder
								 }
			        		  break;
			        	  case KeyEvent.VK_RIGHT:
			        		  if (fileChar[inputX][inputY+1]==' '&&controlOfItems(inputX,inputY+1)==' ') {
									 //item'i ilereye s�r�yorum
									 items[getItemNo(inputX, inputY)].setyPos(inputY+1);
									 ourPlayer.setxPosition(inputX);
									 ourPlayer.setyPosition(inputY);
									 controlOfSameItem(items[getItemNo(inputX, inputY+1)].getxPos(),items[getItemNo(inputX, inputY+1)].getyPos());//item'�n etraf�n� kontrol eder
								 }
			        		  break;
			        	  case KeyEvent.VK_UP:
			        		  if (fileChar[inputX-1][inputY]==' '&&controlOfItems(inputX-1,inputY)==' ') {
									 //item'i ilereye s�r�yorum
									 items[getItemNo(inputX, inputY)].setxPos(inputX-1);
									 ourPlayer.setxPosition(inputX);
									 ourPlayer.setyPosition(inputY);
									 controlOfSameItem(items[getItemNo(inputX-1, inputY)].getxPos(),items[getItemNo(inputX-1, inputY)].getyPos());//item'�n etraf�n� kontrol eder
								 }
			        		  break;
			        	  case KeyEvent.VK_DOWN:
			        		  if (fileChar[inputX+1][inputY]==' '&&controlOfItems(inputX+1,inputY)==' ') {
									 //item'i ilereye s�r�yorum
									 items[getItemNo(inputX, inputY)].setxPos(inputX+1);
									 ourPlayer.setxPosition(inputX);
									 ourPlayer.setyPosition(inputY);
									 controlOfSameItem(items[getItemNo(inputX+1, inputY)].getxPos(),items[getItemNo(inputX+1, inputY)].getyPos());//item'�n etraf�n� kontrol eder
								 }
			        		  break;
			        	  }
					}
		}
		if (fileChar[inputX][inputY]==' '&&(rkey== KeyEvent.VK_DOWN||rkey==KeyEvent.VK_UP||rkey==KeyEvent.VK_RIGHT||rkey==KeyEvent.VK_LEFT)&&controlOfItems(inputX,inputY)==' ') {
			ourPlayer.setxPosition(inputX);
			 ourPlayer.setyPosition(inputY);
			 ourPlayer.setEnergy(ourPlayer.getEnergy()-1);
			if (ourPlayer.getEnergy()==0) {
				ourPlayer.setSpeed(200);
			}else {
				ourPlayer.setSpeed(100);
			}
   	
		}
	}
	//backpack i�ine atma k�sm�
	public void backpackIn(int inputX,int inputY){
		if (getItemNo(inputX,inputY)!=-1&&!backPack.isFull()&&ourPlayer.getEnergy()-100>=0)//item varsa e�er 
			{
			//backpack'e item nosunu da at�yoruz ona g�re i�lem yapaca��z
			backPack.push(items[getItemNo(inputX,inputY)].getItemName()+" "+getItemNo(inputX,inputY));
			int itemNo=Integer.parseInt(backPack.peek().toString().substring(2));
			items[itemNo].getTimer().cancel();
			items[itemNo].setxPos(-1);//konumunu -1liyoruz ki cantada oldu�u belli olsun
			items[itemNo].setyPos(-1);
			ourPlayer.setEnergy(ourPlayer.getEnergy()-100);
			drawScreen();
		}
	}
	//backpack i�inden ��karma
	public void backpackOut(int inputX,int inputY){
		if (getItemNo(inputX,inputY)==-1&&!backPack.isEmpty()&&fileChar[inputX][inputY]==' '
				&&(computer.getxPosition()!=inputX||computer.getyPosition()!=inputY))//item varsa e�er 
			{
			//ikincil olarak item noyu tutuyoruz s�rt�antas�nda
				int itemNo=Integer.parseInt(backPack.peek().toString().substring(2));
				//item ismini silmemi�tik zaten sadece konum belirliyoruz
				items[itemNo].setxPos(inputX);
				items[itemNo].setyPos(inputY);
				items[itemNo].timerTask();
				if (!backPack.isEmpty()) {
					backPack.pop();
				}
				controlOfSameItem(inputX,inputY);
				drawScreen();
		}
	}
	/*drawTemplate ekrana labirenti �izer*/
	public void getTemplate(String pathTemplate){
		File myFile=new File(pathTemplate);//dosyam�z�n yeri
		Scanner fileIn;//dosyay� scanner ile cekece�iz
		try {
			fileIn=new Scanner(myFile);
			for (int i = 0; fileIn.hasNextLine(); i++) {
				String tempStr=fileIn.nextLine();
				for (int j = 0; j < tempStr.length(); j++) {
					fileChar[i][j]=tempStr.charAt(j);
				}
			}
					
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	//Input i�erisini random doldurma i�lemi yapar
	public void fillInput(){
		while (!input.isFull()) {
			int rand=rnd.nextInt(2);//bir veya s�f�ra g�re * veya say� koyaca��z
			if (rand==0) {
				rand=rnd.nextInt(4)+1;// bir ile d�rt aras� say�ya g�re input i�lemi yapaca��z
				if (!input.isFull()) {
					
					input.enqueue(rand);
				}
			}else {
				if (!input.isFull()) {
					input.enqueue('*');
				}
			}
		}
	}
	//koordinanta g�re item kontrol�
	public char controlOfItems(int x,int y){
		for (int i = 0; i < items.length; i++) {
			if (items!=null) {
				if (items[i]!=null) {
					if (items[i].getxPos()==x&&items[i].getyPos()==y) {
						return items[i].getItemName();
					}
				}
				
			}
		}
		return ' ';
	}
	//input queue i�indekileri fileChar i�ine atarki sonra onu yazd�ral�m
	public void addInputItems(){
		for (int i = 0; i < 10; i++) {
			fileChar[4][58+i]=input.peek().toString().charAt(0);
			input.enqueue(input.dequeue());
		}
	}

	//koordinanta g�re item arar ve item no d�nd�r�r
	public int getItemNo(int xP,int yP){
		for (int i = 0; i < items.length; i++) {
			if (items[i]!=null) {
				if (items[i].getxPos()==xP&&items[i].getyPos()==yP) {
					return i;
				}
			}
		}
		return -1;
	}
	//rasgele yerle�tirme i�lemi yapar(*,1,2,3,4)
	public void randomPlaceItems(){
			//rasgele say�y� olu�turduk bunu string arrayde de�i�tirmek i�in a�a��daki i�lemi yapt�k
			for (int i = 0; i < items.length; i++) {
				int x;
				int y;
				do {
					x=rnd.nextInt(19)+2;
					y=rnd.nextInt(54)+2;
				} while (fileChar[x][y]!=' '||getItemNo(x+1,y)!=-1||getItemNo(x,y+1)!=-1
						||getItemNo(x-1,y)!=-1||getItemNo(x,y-1)!=-1||getItemNo(x,y)!=-1
						||(ourPlayer.getxPosition()==x&&ourPlayer.getyPosition()==y)
						||(computer.getxPosition()==x&&computer.getyPosition()==y));
				if (items[i]!=null) {
					if (items[i].getItemName()==' '&&items[i].getxPos()!=-1&&items[i].getyPos()!=-1) {
						items[i].setItemName(input.dequeue().toString().charAt(0));
						items[i].setTime(0);
						items[i].setxPos(x);
						items[i].setyPos(y);
						fillInput();
					}
				}else {
					items[i]=new Item(x, y, input.dequeue().toString().charAt(0));
					fillInput();
				}
			}
			drawItems();
	}

	//backpack stack'inin i�ini filechar'a at�yoruz
	public void writeBackPack(){
			Stack refStack=new Stack(backPack.size());
			int size=backPack.size();
			//ilk �nce fileChar i�erisini bo�alt�yorum ki itemleri yere koyunca dolu gibi g�z�kmesin
			for (int i = 0; i < 5; i++) {
				fileChar[14-i][60]=' ';
			}
			//elemanlar� fileChar'a ekliyorum ve stack'deki elemanlar� gecici stack'e at�yorum
			for (int i = size; i > 0; i--) {
				fileChar[15-i][60]=backPack.peek().toString().charAt(0);
				refStack.push(backPack.pop());
			}
			//kalan elemanlar� yerine koyuyorum
			while (!refStack.isEmpty()) {
				backPack.push(refStack.pop());
				
			}
	}
	//itemlerin etraf�n� kontrol eder ayn� say�dan varsa ona g�re enerji verecek
		public void controlOfSameItem(int positionX,int positionY){
			if (getItemNo(positionX,positionY)!=-1)//item varsa e�er
				{
				int count=0;
				//say� olup olmad���n� kontrol ediyoruz(42 y�ld�z�n asc�� kodu)
				for (int i = 0; i < items.length; i++) {
					if ((items[getItemNo(positionX,positionY)].getxPos()==items[i].getxPos()+1
							&&items[getItemNo(positionX,positionY)].getyPos()==items[i].getyPos()
							||items[getItemNo(positionX,positionY)].getxPos()==items[i].getxPos()-1
							&&items[getItemNo(positionX,positionY)].getyPos()==items[i].getyPos()
							||items[getItemNo(positionX,positionY)].getyPos()==items[i].getyPos()+1
							&&items[getItemNo(positionX,positionY)].getxPos()==items[i].getxPos()
							||items[getItemNo(positionX,positionY)].getyPos()==items[i].getyPos()-1
							&&items[getItemNo(positionX,positionY)].getxPos()==items[i].getxPos())
							&&getItemNo(positionX,positionY)!=i
							&&items[getItemNo(positionX,positionY)].getItemName()==items[i].getItemName()) {
						count++;
						items[i].setItemName(' ');//itemi siliyoruz
					}
				}
				if (count>0) {
					items[getItemNo(positionX,positionY)].setItemName(' ');//itemi siliyoruz
					if (count==1) {
						ourPlayer.setEnergy(ourPlayer.getEnergy()+100);
					}else if (count==2) {
						ourPlayer.setEnergy(ourPlayer.getEnergy()+200);
					}else if (count==3) {
						ourPlayer.setEnergy(ourPlayer.getEnergy()+400);
					}
					randomPlaceItems();
					
				}
			}
			
		}
		Stack path;
	//computer k�sm�
		public void computerMovements(){
			timer=new Timer();
			if (path==null) {
				  path=new Stack(5000);
			  }
			
			timer.schedule(new TimerTask() {
				  @Override
				  public void run() {
					if (computer.getEnergy()==0) {
						computer.setSpeed(computerSpeed/2);
					}else {
						computer.setSpeed(computerSpeed);
					}
					  
					  int x=0;
					  int y=0;
					  findPath();
					  path =reverseStack(computerGone);
					  x=Integer.parseInt(path.peek().toString().split(" ")[0]);
					  y=Integer.parseInt(path.peek().toString().split(" ")[1]);
					  path.pop();
					  computer.setxPosition(x);
					  computer.setyPosition(y);
					  computer.setEnergy(computer.getEnergy()-1);//0'in alt�na du�memesini class'da kontrol ettim
					  //e�er targeta ula�m�ssa
					  if (controlOfItems(computer.getxPosition(), computer.getyPosition())=='*') {
						  int itemNo=getItemNo(computer.getxPosition(), computer.getyPosition());
						  items[itemNo].setItemName(' ');
						  computer.setEnergy(computer.getEnergy()+50);
					  }
					  else if (computer.getxPosition()==ourPlayer.getxPosition()&&computer.getyPosition()==ourPlayer.getyPosition()) {
						timer.cancel();
						for (int i = 0; i < items.length; i++) {
							items[i].getTimer().cancel();
						}
						isFinish=false;
					}
				  }
				},1000,computer.getSpeed());
		}
		public Stack reverseStack(Stack stc){
			Stack tempStc=new Stack(stc.size());
				while (!stc.isEmpty()) {
					tempStc.push(stc.pop());
				}
			return tempStc;
		}
		public void findPath(){
			findTarget();
			computerGone=new Stack(5000);
			roadTimes=new String[5000];
			int cX=computer.getxPosition();
			int cY=computer.getyPosition();
			int j=1;
			roadTimes[0]=cX+" "+cY;//gitti�i yollara bilgisayar�n konumunu koyuyorum oraya gitmesin
			do {
				//gidebilece�i yerler
				if (fileChar[cX+1][cY]==' '
						&&!((int)controlOfItems(cX+1,cY)>48&&(int)controlOfItems(cX+1,cY)<53)
						) {
					computerEnvironment[0]=((cX+1)+" "+cY);	
				}else {
					computerEnvironment[0]=null;
				}
				if (fileChar[cX][cY-1]==' '
						&&!((int)controlOfItems(cX,cY-1)>48&&(int)controlOfItems(cX,cY-1)<53)
						) {
					computerEnvironment[1]=(cX+" "+(cY-1));	
				}else {
					computerEnvironment[1]=null;
				}
				if (fileChar[cX-1][cY]==' '
						&&!((int)controlOfItems(cX-1,cY)>48&&(int)controlOfItems(cX-1,cY)<53)
						) {
					computerEnvironment[2]=((cX-1)+" "+cY);	
				}else {
					computerEnvironment[2]=null;
				}
				if (fileChar[cX][cY+1]==' '
						&&!((int)controlOfItems(cX,cY+1)>48&&(int)controlOfItems(cX,cY+1)<53)
						) {
					computerEnvironment[3]=(cX+" "+(cY+1));	
				}else {
					computerEnvironment[3]=null;
				}
				int distance =99999;
				positionOfNear=-1;
				
				//en yak�n noktay� bulup oraya gidece�iz
				for (int i = 0; i < computerEnvironment.length; i++) {
					if (computerEnvironment[i]!=null) {
						String[] compXY=computerEnvironment[i].split(" ");
						double x=targetX-Double.parseDouble(compXY[0]);
						double y=targetY-Double.parseDouble(compXY[1]);
						int tempDistance = (int)Math.sqrt((Math.pow(x,2)+Math.pow(y,2)));
						if (tempDistance<distance&&!controlRoadTimes(computerEnvironment[i])) {
								distance=tempDistance;//en yak�n mesafeyi buluyoruz
								positionOfNear=i;
						}
					}
				}
				if (positionOfNear==-1&&!computerGone.isEmpty()) {
					roadTimes[j]=computerGone.pop().toString();
					j++;
					if (!computerGone.isEmpty()) {
						cX=Integer.parseInt(computerGone.peek().toString().split(" ")[0]);
						cY=Integer.parseInt(computerGone.peek().toString().split(" ")[1]);
						
					}
					
					
				}else if(positionOfNear!=-1) {
					roadTimes[j]=computerEnvironment[positionOfNear];
					j++;
					computerGone.push(computerEnvironment[positionOfNear]);
					cX=Integer.parseInt(computerEnvironment[positionOfNear].split(" ")[0]);
					cY=Integer.parseInt(computerEnvironment[positionOfNear].split(" ")[1]);
				}
			} while (targetX!=cX||targetY!=cY);
			
			
		}
		//yolu kontrol eder, e�er bir defa gittiyse o yola bir daha gitmez
		public boolean controlRoadTimes(String computerEnv){
			int count=0;
			for (int i = 0; i < roadTimes.length; i++) {
				if (roadTimes[i]!=null) {
					if (computerEnv.equals(roadTimes[i])) {
						count++;
					}
				}
			}
			if (count>=2) {
				return true;
			} else {
				return false;
			}
		}

		public void findTarget(){
			int closestItem=999999;
			int tempDistancePx = Math.abs(ourPlayer.getxPosition() - computer.getxPosition());
			int tempDistancePy = Math.abs(ourPlayer.getyPosition()- computer.getyPosition());
			int tempDistanceP = (int)Math.sqrt((Math.pow(tempDistancePx,2)+Math.pow(tempDistancePy,2)));	
			if (tempDistanceP<closestItem) {
				closestItem = tempDistanceP;
		    	targetX=ourPlayer.getxPosition();
				targetY=ourPlayer.getyPosition();
			}
			if (computer.getEnergy()<100) {
				for (int i = 0; i < items.length; i++) {
					if (items[i].getItemName()=='*') {
						int tempDistanceX = Math.abs(items[i].getxPos() - computer.getxPosition());
						int tempDistanceY = Math.abs(items[i].getyPos() - computer.getyPosition());
						int tempDistance = (int)Math.sqrt((Math.pow(tempDistanceX,2)+Math.pow(tempDistanceY,2)));
					    if(tempDistance < closestItem){
					    	closestItem = tempDistance;
					    	targetX=items[i].getxPos();
							targetY=items[i].getyPos();
					    }
					}
				}
			}
			
				
		}
		

}
