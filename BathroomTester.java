import java.util.ArrayList;

public class BathroomTester {
	public static void main(String[] args) {
		//test1();
		test2();
;	}
	
	
	public static void test2() {
		FairUnifanBathroom bathroom = new FairUnifanBathroom();
		ArrayList<Thread> threads1 = new ArrayList<Thread>();
		for(int i = 0;i<20;i++) {
			threads1.add(new Thread(new UTBathroom(bathroom, i)));
			if(i%4 == 0) {
				for(int j = 0; j<10; j++) {
					threads1.add(new Thread(new OUBathroom(bathroom, j)));
				}
			}
		}
		for(int i = 0;i<threads1.size();i++) {
			threads1.get(i).start();
		}
	}
	
	public static void test1() {
		FairUnifanBathroom bathroom = new FairUnifanBathroom();
		ArrayList<Thread> threads1 = new ArrayList<Thread>();
		for(int i = 0;i<10;i++) {
			threads1.add(new Thread(new UTBathroom(bathroom, i)));
		}
		for(int i = 0;i<10;i++) {
			threads1.add(new Thread(new OUBathroom(bathroom, i)));
		}
		for(int i = 0;i<threads1.size();i++) {
			threads1.get(i).start();
		}
	}
	
}

class UTBathroom implements Runnable{
	private FairUnifanBathroom bathroom;
	private int id;
	public UTBathroom(FairUnifanBathroom bathroom, int id) {
		this.bathroom = bathroom;
		this.id = id;
	}
	@Override
	public void run() {
		try {
		bathroom.enterBathroomUT();
		//System.out.println("UT Entering ID: " + id);
		//Thread.sleep(100);
		Thread.sleep((int)(Math.random()*90 + 10));
		}catch(InterruptedException e){
			
		}
		bathroom.leaveBathroomUT();
		//System.out.println("UT Leaving ID: " + id);
	}
	
}

class OUBathroom implements Runnable{
	private FairUnifanBathroom bathroom;
	private int id;
	public OUBathroom(FairUnifanBathroom bathroom, int id) {
		this.bathroom = bathroom;
		this.id = id;
	}
	@Override
	public void run() {
		try {
		bathroom.enterBathroomOU();
		//System.out.println("OU Entering ID: " + id);
		//Thread.sleep(100);
		Thread.sleep((int)(Math.random()*90 + 10));
		}catch(InterruptedException e){
			
		}
		bathroom.leaveBathroomOU();
		//System.out.println("OU Leaving ID: " + id);
	}
}