// CSC3322
// EID 2

/* Use only Java monitors to accomplish the required synchronization */
public class MonitorCyclicBarrier implements CyclicBarrier {

    private int parties;
    private int current;
    private boolean active;
    // TODO Add other useful variables

    public MonitorCyclicBarrier(int parties) {
        this.parties = parties;
        this.current = 0;
        this.active = true;
    }

    /*
     * An active CyclicBarrier waits until all parties have invoked
     * await on this CyclicBarrier. If the current thread is not
     * the last to arrive then it is disabled for thread scheduling
     * purposes and lies dormant until the last thread arrives.
     * An inactive CyclicBarrier does not block the calling thread. It
     * instead allows the thread to proceed by immediately returning.
     * Returns: the arrival index of the current thread, where index 0
     * indicates the first to arrive and (parties-1) indicates
     * the last to arrive.
     */
    public synchronized int await() throws InterruptedException {
        // TODO Implement this function
    	int place = this.current;
    	if(!this.active) {
    		return -1;
    	}
    	
    	this.current +=1;
    	if(this.current<this.parties && this.active) {
    		wait();
    	}else {
    		notifyAll();
    		this.current = 0;
    	}
        return place;
    }

    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     * 
     */
    public synchronized void activate() throws InterruptedException {
        if(!this.active) {
        	this.active = true;
            this.current = 0;
        }
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public synchronized void deactivate() throws InterruptedException {
        this.active = false;
        notifyAll();
    }
}
