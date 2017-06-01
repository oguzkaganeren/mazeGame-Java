
public class Stack {
	private int top;
	private Object[] element;
	public Stack(int capacity){
		top=-1;
		element=new Object[capacity];
	}
	public void push(Object obj){
		if (isFull()) {
			System.out.println("Stack overflow");
		}else {
			top++;
			element[top]=obj;
		}
	}
	public Object pop(){
		if (isEmpty()) {
			System.out.println("Stack is empty");
			return null;
		}else {
			Object refObj=element[top];
			top--;
			return refObj;
		}
	}
	public boolean isFull(){
		return top+1==element.length;
	}
	public boolean isEmpty(){
		return top==-1;
	}
	public int size(){
		return top+1;
	}
	public Object peek(){
		return element[top];
	}
}
