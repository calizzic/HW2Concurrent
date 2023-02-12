// nab2992
// EID 2

//todo: testing lol

public class PriorityQueue {
	private LinkedList queue;
	private int maxSize;

	public PriorityQueue(int maxSize) {
        // Creates a Priority queue with maximum allowed size as capacity
		this.queue = new LinkedList(maxSize);
		this.maxSize = maxSize;
	}

	public int add(String name, int priority) {
		// Adds the name with its priority to this queue.
		// Returns the current position in the list where the name was inserted;
		// otherwise, returns -1 if the name is already present in the list.
		// This method blocks when the list is full.
		LinkedListNode newNode = new LinkedListNode(name, priority);
		
		//Search should be somewhere else in case add in middle but putting here for now
		if (search(name) != -1 || queue.inAdding(name)) {
			return (-1);
		}
		queue.add(name);
		
		LinkedListNode n;
		LinkedListNode next;
		int idx = 0;
		
		queue.isFullIncrement();
		// case where HEAD is null
		if(queue.setHead(newNode)){
			queue.doneAdding(name);
			return(0);
		}
		
		//Gets the head and locks it
		//Double checks if not full and claims spot in the queue.
		
		n = queue.getHead();
		
		// case where priority[NEW] > priority[HEAD]
		if (priority > n.getPriority()) {
			queue.replaceHead(newNode);
			queue.doneAdding(name);
			queue.monitorLock.lock();
			queue.headFree.signal();
			queue.monitorLock.unlock();
			return(0);
		}

		while (n != null) {
			next = n.next;
			// case where priority[i] > priority[NEW] and next is null
			if (next == null) {
				// final check that queue is not full before adding new value
				n.next = newNode;
				n.lock.unlock();
				queue.doneAdding(name);
				queue.monitorLock.lock();
				queue.headFree.signal();
				queue.monitorLock.unlock();
				return (idx+1);
			}
			//Otherwise waits for lock
			next.lock.lock();
			
			// case where priority[i] > priority[NEW] > priority[j]
			if (priority > next.getPriority()) {
				// final check that queue is not full before adding new value
				n.lock.unlock();
				n.lock.lock();
				n.next = newNode;
				newNode.next = next;
				n.lock.unlock();
				queue.doneAdding(name);
				queue.monitorLock.lock();
				queue.headFree.signal();
				queue.monitorLock.unlock();
				next.lock.unlock();
				return (idx+1);
			}
			
			n.lock.unlock();
			queue.monitorLock.lock();
			queue.headFree.signal();
			queue.monitorLock.unlock();
			n = next;
			idx++;
		}
		return(idx);
	}

	public int search(String name) {
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
		LinkedListNode n = queue.getHead();
		int idx = 0;
		while(n != null){
			if(n.getName().equals(name)){
				n.lock.unlock();
				return(idx +1);
			}
			
			LinkedListNode next = n.next;
			if(next != null) {
				next.lock.lock();
			}
			n.lock.unlock();
			n = next;
			if(idx==0){
				queue.monitorLock.lock();
				queue.headFree.signal();
				queue.monitorLock.unlock();
			}
			idx++;
		}
		return(-1);
	}

	public String getFirst() {
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
		LinkedListNode head;
		while(queue.isEmpty()) {
		}
		head = queue.head;
		String headname = queue.head.getName();
		
		LinkedListNode next = head.next;
		if(next != null) {
			next.lock.lock();
		}
		queue.head = next;
		
		head.lock.unlock();
		if(next != null) {
			next.lock.unlock();
		}
		queue.monitorLock.lock();
		queue.headFree.signal();
		queue.notFull.signal();
		queue.monitorLock.unlock();
		
		if(queue.length.intValue()>0) {
			queue.length.decrementAndGet();
		}
		return(headname);
	}

	public int getSize(){return(this.maxSize);}
}