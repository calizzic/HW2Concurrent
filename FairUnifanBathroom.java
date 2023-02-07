// EID 1
// EID 2
/*
 * If OU tries to access, wait till all UT done. Otherwise good.
 * Need to be next in line, has to be only people from your school in bathroom
 */

public class FairUnifanBathroom {   
	private boolean UTInBathroom = false;
	private boolean OUInBathroom = false;
	
	private int numInBathroom = 0;
	private int ticketNum = 0;
	private int currentTicket = 0;
	
	
	public synchronized void enterBathroomUT() throws InterruptedException {
	    // Called when a UT fan wants to enter bathroom	
		int ticket = ticketNum;
		ticketNum++;
		while(OUInBathroom || numInBathroom>=7 || currentTicket != ticket) {
			wait();
		}
		
		numInBathroom +=1;
		currentTicket +=1;
		UTInBathroom = true;
		System.out.println("UT entering bathroom Ticket: " + ticket);
		notifyAll();
	}
	
	public synchronized void enterBathroomOU() throws InterruptedException{
		// Called when a OU fan wants to enter bathroom
		int ticket = ticketNum;
		ticketNum++;
		while(UTInBathroom || numInBathroom>=7 || currentTicket != ticket) {
			wait();
		}
		
		numInBathroom +=1;
		currentTicket +=1;
		OUInBathroom = true;
		System.out.println("OU entering bathroom Ticket: " + ticket);
		notifyAll();
	}
	
	public synchronized void leaveBathroomUT() {
		// Called when a UT fan wants to leave bathroom
		numInBathroom -=1;
		if(numInBathroom ==0) {
			UTInBathroom = false;
		}
		System.out.println("UT Leaving bathroom");
		notifyAll();
	}

	public synchronized void leaveBathroomOU() {
		// Called when a OU fan wants to leave bathroom
		numInBathroom -=1;
		if(numInBathroom ==0) {
			OUInBathroom = false;
		}
		System.out.println("OU Leaving bathroom");
		notifyAll();
	}
}
	
