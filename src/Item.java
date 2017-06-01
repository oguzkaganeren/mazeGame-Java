import java.util.Timer;
import java.util.TimerTask;

public class Item {
	private char itemName;
	private int xPos;
	private int yPos;
	private Timer timer;
	private int time;
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public char getItemName() {
		return itemName;
	}
	public void setItemName(char itemName) {
		this.itemName = itemName;
	}
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	public int getxPos() {
		return xPos;
	}
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	public Item(int xPos,int yPos,char itemName){
		this.xPos=xPos;
		this.yPos=yPos;
		this.itemName=itemName;
		time=0;
		timerTask();
	}
	public void timerTask(){
		timer=new Timer();
		timer.schedule(new TimerTask() {
			  @Override
			  public void run() {
				  if (time==100) {
					  itemName=' ';
					  time=0;
				  }else {
					time++;
				}
				  
			  }
			},1000,1000);
	}
	
	
	
}
