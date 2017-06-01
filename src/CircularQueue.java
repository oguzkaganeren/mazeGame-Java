
public class CircularQueue {
	private int rear;
	private int front;
	private Object[] element;
	public CircularQueue(int capacity){
		rear=-1;
		front=0;
		element=new Object[capacity];
	}
	public void enqueue(Object inObj){
		if (isFull()) {
			System.out.println("The queue overflow");
		} else {
			rear=(rear+1)%element.length;
			element[rear]=inObj;
		}
	}
	public Object dequeue(){
		if (isEmpty()) {
			System.out.println("The queue is empty");
			return null;
		}else {
			Object refObj=element[front];
			element[front]=null;
			front=(front+1)%element.length;
			return refObj;
		}
	}
	public boolean isEmpty(){
		return element[front]==null;
	}
	public boolean isFull(){
		return (front==(rear+1)%element.length&&element[front]!=null&&element[rear]!=null);
	}
	public Object peek(){
		return element[front];
	}
}
