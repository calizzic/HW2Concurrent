import java.util.concurrent.locks.ReentrantLock;

public class LinkedListNode {
    private int priority;
    private String name;
    public LinkedListNode next;
    final ReentrantLock lock = new ReentrantLock();

    public LinkedListNode(String name, int priority){
        this.priority = priority;
        this.name = name;
        this.next = null;
    }

    public String getName(){
        return(this.name);
    }

    public int getPriority(){
        return(this.priority);
    }

}
