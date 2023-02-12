//import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class LinkedList {

    public LinkedListNode head;
    private int maxLength;
    public AtomicInteger length = new AtomicInteger();
    final ReentrantLock monitorLock = new ReentrantLock();
    final Condition notFull = monitorLock.newCondition();
    final Condition notEmpty = monitorLock.newCondition();
    final Condition headFree = monitorLock.newCondition();
    public ArrayList<String> adding;

    public LinkedList(int length){
        this.head = null;
        this.maxLength = length;
        this.length.set(0);
        this.adding = new ArrayList<String>();
    }
    
    public void add(String name) {
    	monitorLock.lock();
    	adding.add(name);
    	monitorLock.unlock();
    }
    
    public boolean inAdding(String name) {
    	monitorLock.lock();
    	for(String nm: adding) {
    		if(nm.equals(name)) {
    	    	monitorLock.unlock();
    			return true;
    		}
    	}
    	monitorLock.unlock();
    	return false;
    }
    
    public void doneAdding(String name) {
    	monitorLock.lock();
    	for(int i = 0; i< adding.size(); i++) {
    		if(adding.get(i).equals(name)) {
    			adding.remove(i);
    			i--;
    		}
    	}
    	monitorLock.unlock();
    }

    public LinkedListNode getHead(){
        // Locks the head of the node, need to unlock/handle in PQueue
    	
        monitorLock.lock();
        if(head == null) {
        	monitorLock.unlock();
        	return null;
        }
        
        try{
            while(head.lock.isLocked() && !head.lock.isHeldByCurrentThread()){
                headFree.await();
            }
        }
        catch(InterruptedException e){System.out.println("InterruptedException caught");}
        
        
        if(head != null){
            head.lock.lock();
        }
        monitorLock.unlock();
        return(head);
    }

    public boolean setHead(LinkedListNode newHead){
        boolean success = false;
        monitorLock.lock();
        
        if(this.head == null){
            this.head = newHead;
            success = true;
            this.notEmpty.signal();
        }
        
        monitorLock.unlock();
        return(success);
    }
    
    //Need head lock before executing this function (now)
    public boolean replaceHead(LinkedListNode newHead){
        boolean success = false;
        monitorLock.lock();
        
        //Only one gets signaled at a time so ok for lock to not be synch with while
//        try{
//            while(head.lock.isLocked() && !head.lock.isHeldByCurrentThread()){
//                headFree.await();
//            }
//            head.lock.lock();
//        }
//        catch(InterruptedException e){System.out.println("InterruptedException caught");}
        
        newHead.next = this.head;
        this.head = newHead;
        success = true;
        //Error 1 - built up locks on non head.
        newHead.next.lock.unlock();
        
        headFree.signal();
        monitorLock.unlock();
        
        return(success);
    }
    

    public boolean isFull() {
        // Block the isFull condition if queue is full
    	// If it is more than full (in process of adding node) await?
        monitorLock.lock();
        try {
            while (length.intValue() >= maxLength) {
                notFull.await();
            }
        }
        catch(InterruptedException e){
            System.out.println("Interrupted Exception Caught");
        }
        
        monitorLock.unlock();
        return(length.intValue() >= maxLength);
    }
    
    //Copy
    public boolean isFullIncrement() {
        // Block the isFull condition if queue is full
    	// If it is more than full (in process of adding node) await?
        monitorLock.lock();
        try {
            while (length.intValue() >= maxLength) {
                notFull.await();
            }
        }
        catch(InterruptedException e){
            System.out.println("Interrupted Exception Caught");
        }
        
		this.length.incrementAndGet();
        
        monitorLock.unlock();
        return(length.intValue() > maxLength);
    }

    public boolean isEmpty(){
        //Block the isEmpty condition if queue is empty
    	//Once not empty, obtains lock for head
        monitorLock.lock();
        try{
            while(head == null || (head.lock.isLocked() && !head.lock.isHeldByCurrentThread())){
            	if(head == null) {
                    notEmpty.await();
            	}else {
                    headFree.await();
            	}
            }
        }
        catch(InterruptedException e){
            System.out.println("Interrupted Exception Caught");
        }
        head.lock.lock();
        monitorLock.unlock();
        return(head == null);
    	
    }
}
