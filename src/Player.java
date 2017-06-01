public class Player {
	private Stack backpack;//sadece H oyuncusuna var
	private char playerName;
	private int energy;
	private int xPosition;//oyuncular�n x ve ye koordinantlar�n� tutaca��z
	private int yPosition;
	private int speed;
	public Player(char whoAre,int x,int y){
		energy=100;//ba�lang�� energy de�eri
		if (whoAre=='H') {
			backpack=new Stack(5);//e�er bizim oyuncumuzsa s�rt �antas� var
		}
		speed=200;
		playerName=whoAre;
		this.xPosition=x;
		this.yPosition=y;
	}
	public Stack getBackpack() {
		return backpack;
	}
	public void setBackpack(Stack backpack) {
		this.backpack = backpack;
	}
	public int getEnergy() {
		return energy;
	}
	public void setEnergy(int energy) {
		if (energy>=0) {
			this.energy = energy;
		}
	}
	public int getxPosition() {
		return xPosition;
	}
	public void setxPosition(int xPosition) {
		this.xPosition = xPosition;
	}
	public int getyPosition() {
		return yPosition;
	}
	public void setyPosition(int yPosition) {
		this.yPosition = yPosition;
	}
	public char getPlayerName() {
		return playerName;
	}
	public void setPlayerName(char playerName) {
		this.playerName = playerName;
	}
	public int getSpeed() {
		return speed;
	}
	public void setSpeed(int speed) {
		this.speed = speed;
	}
}
