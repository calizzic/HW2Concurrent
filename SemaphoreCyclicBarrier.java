//nab2992
// EID 2


import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/* Use only semaphores to accomplish the required synchronization */
public class SemaphoreCyclicBarrier implements CyclicBarrier {

    private int parties;
    // TODO Add other useful variables
    private Semaphore barrier;
    private Semaphore newRound;
    private Semaphore accessPeople;
    private boolean active;
    private int arrivedPpl = 0;

    public SemaphoreCyclicBarrier(int parties) {
        this.parties = parties;
        // TODO Add any other initialization statements
        this.barrier = new Semaphore(0);
        this.newRound = new Semaphore(parties);
        this.active = true;
        this.arrivedPpl = 0;
        this.accessPeople = new Semaphore(1);
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
    public int await() throws InterruptedException {
        if(!active) {
        	return -1;
        }
        //Second semaphore that waits for all from previous round to be released before starting next round
        this.newRound.acquire();
        
        this.accessPeople.acquire();
        arrivedPpl += 1;
    	int position = arrivedPpl;
    	this.accessPeople.release();
    	
    	
        if(position >= parties){
            for(int i=0; i<parties; i++){
                barrier.release();
            }
        }
        this.barrier.acquire();
        
        //Before leaving, decrement arrivedPpl, last one to leave releases next round
        this.accessPeople.acquire();
        arrivedPpl -= 1;
    	int leaving = arrivedPpl;
    	this.accessPeople.release();
    	
        if(leaving <= 0){
            for(int i=0; i<parties; i++){
                newRound.release();
            }
        }
        
        return (position-1);
    }

    /*
     * This method activates the cyclic barrier. If it is already in
     * the active state, no change is made.
     * If the barrier is in the inactive state, it is activated and
     * the state of the barrier is reset to its initial value.
     */
    public void activate() throws InterruptedException {
        // TODO Implement this function
        if(!active){
            this.active = true;
            arrivedPpl = 0;
            barrier.drainPermits();
            newRound.drainPermits();
            for(int i=0; i<parties; i++){
                newRound.release();
            }
            this.accessPeople.drainPermits();
            this.accessPeople.release();
        }
    }

    /*
     * This method deactivates the cyclic barrier.
     * It also releases any waiting threads
     */
    public void deactivate() throws InterruptedException {
        // TODO Implement this function
        this.active = false;
        for(int i=0; i<parties; i++){
            newRound.release();
        }
        for(int i=0; i<parties * 2; i++){
            barrier.release();
        }
    }
}
