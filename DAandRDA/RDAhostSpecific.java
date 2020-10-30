package DAandRDA;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class RDAhostSpecific {
	
	// This method will return the index of element in array arr[]
	public int index(int[] arr, int element) {
		int ans = 100;
		for(int i=0; i<arr.length; i++) {
			if(arr[i]==element) {
				ans = i;
				break;
			}
		}
		return ans;
	}
	
	
	// this method will choose that whether currHost is more preferred or not than the current matching
	public int choose(int vm, VM currVM, int host, Host currHost) {
		int priorityOfcurrHost = this.index(currVM.priorityListOfHosts, host);
		int priorityOfMatchedHost = this.index(currVM.priorityListOfHosts, currVM.host);
		if(priorityOfcurrHost>priorityOfMatchedHost) {
			return -1;
		}
		int hostThatIsRejected = currVM.host;
		currVM.host = host;
		return hostThatIsRejected;	
	}
	
	// this method will remove vm from curentlyMatched array of host
	public void updateCurrentlyMatchedArrayOfHost(Host host, int vm) {
		int[] temp = new int[host.currentlyMatchedVMs.length];
		int counter = 0;
		for(int i = 0; i<host.currentlyMatchedVMs.length; i++) {
			if(host.currentlyMatchedVMs[i] == vm) {
				continue;
			}
			temp[counter] = host.currentlyMatchedVMs[i];
			counter++;
		}
		temp[counter] = -1;
		host.currentlyMatchedVMs = temp;
		host.pointer -= 1;
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void removeAllVMThatHasLessPriorityThanBestRejected(Host host, int vm, ArrayList<ArrayList<Integer>> matching, VM[] arrayOfVMs, int ht) {
		int priorityOfvm = this.index(host.priorityListOfVMs, vm);
		if(priorityOfvm<host.bestRejected) {
			host.bestRejected = priorityOfvm;
			for(int i = 0; i<host.pointer; i++) {
				if(this.index(host.priorityListOfVMs, host.currentlyMatchedVMs[i])>host.bestRejected) {
					arrayOfVMs[host.currentlyMatchedVMs[i]].currentlyMatched = false;
					arrayOfVMs[host.currentlyMatchedVMs[i]].host = -1;
					host.cpuCapacity += arrayOfVMs[host.currentlyMatchedVMs[i]].cpuRequirement;
					host.memoryCapacity += arrayOfVMs[host.currentlyMatchedVMs[i]].memoryRequirement;
					matching.get(ht).remove(new Integer(host.currentlyMatchedVMs[i]));
					this.updateCurrentlyMatchedArrayOfHost(host, host.currentlyMatchedVMs[i]);
					i--;
				}
			}
		}
	}
	
	// This method will send proposal to the VMs one by one and check that VM can be matched or not
	// If the VM is currently matched then we check that whether currHost is better choice for that VM or not
	public int[] engage(int host, Host currHost, VM[] arrayOfVMs, ArrayList<ArrayList<Integer>> matching, int numHosts, int numVMs, Host[] arrayOfHosts) {
		int[] hostWhichLostPairedVMs = new int[numHosts];
		int count = 0;
		Arrays.fill(hostWhichLostPairedVMs, -1);
		// this loop will continue until the currHost has capacity and currHost does not propose all the VMs in its preference list
		while(currHost.cpuCapacity>0 && currHost.memoryCapacity>0 && currHost.pointerForVM<numVMs) {
			int vm = currHost.priorityListOfVMs[currHost.pointerForVM];
			// if the vm = -1, this means that currHost proposed all the VMs
			// if vm>currHost.bestRejected we break the loop because the intuition says that
			// as the host is proposing VMs most prefered VM first and then on and on.
			// so if vm>currHost.bestRejected simply means that from this vm to end of preference list all the other VMs has less priority that bestRejected
			// here vm>currHost.bestRejected is taken because we are considering index of vm in the preference list as priority and the vm which has smaller index
			// is most preferred
			if(vm == -1 || vm>currHost.bestRejected) {
				break; 
			}
			currHost.pointerForVM += 1;
			VM currVM = arrayOfVMs[vm];
			if(!currVM.currentlyMatched) {
				if(currVM.cpuRequirement<=currHost.cpuCapacity && currVM.memoryRequirement<=currHost.memoryCapacity) {
					currVM.currentlyMatched = true;
					currVM.host = host;
					currHost.cpuCapacity -= currVM.cpuRequirement;
					currHost.memoryCapacity -= currVM.memoryRequirement;
					currHost.currentlyMatchedVMs[currHost.pointer] = vm;
					currHost.pointer += 1;
					matching.get(host).add(vm);
				}
			}
			else if(currVM.cpuRequirement<=currHost.cpuCapacity && currVM.memoryRequirement<=currHost.memoryCapacity){
				// this choose method will return the index of host that is currently matched to the currVM 
				// if the priority of currHost is less then this method will return -1
				int hostThatLoseVM = this.choose(vm,currVM,host,currHost);
				if(hostThatLoseVM != -1) {
					arrayOfHosts[hostThatLoseVM].cpuCapacity += currVM.cpuRequirement;
					arrayOfHosts[hostThatLoseVM].memoryCapacity += currVM.memoryRequirement;
					//System.out.println(hostThatLoseVM);
					matching.get(hostThatLoseVM).remove(new Integer(vm));
					// the task is to update the currentlyMatchedList of hostThatLoseVM
					this.updateCurrentlyMatchedArrayOfHost(arrayOfHosts[hostThatLoseVM], vm);
					// now the task is to update the best rejected of hostThatLoseVM and remove all the VM that has less priority than that
					this.removeAllVMThatHasLessPriorityThanBestRejected(arrayOfHosts[hostThatLoseVM],vm,matching,arrayOfVMs,hostThatLoseVM);
					hostWhichLostPairedVMs[count] = hostThatLoseVM;
					count += 1;
					currHost.cpuCapacity -= currVM.cpuRequirement;
					currHost.memoryCapacity -= currVM.memoryRequirement;
					currHost.currentlyMatchedVMs[currHost.pointer] = vm;
					currHost.pointer += 1; 
					matching.get(host).add(vm);
				}
				
			}  
			else {
				currHost.bestRejected = currHost.pointer;
				this.removeAllVMThatHasLessPriorityThanBestRejected(currHost, numVMs, matching, arrayOfVMs, host);
			}
		}
		return hostWhichLostPairedVMs; 
	} 
	
	// this method will pop the first element of the array and return that element 
	// and also shift all the element one index toward left
	public int popArray(int[] arr) {
		int item = arr[0];
		for(int i = 1; i<arr.length; i++) {
			arr[i-1] = arr[i]; 
		}
		arr[arr.length-1]=-1;
		return item;
			
	}
	
	// this method will calculate the satisfaction factor of the VMs based on the formula for satisfaction factor
	public int[] satisfactionFactor(VM[] arrayOfVMs, int numVM, int numHost) {
		int[] factor = new int[numVM];
//		Arrays.fill(factor, -1);
		RDA ob = new RDA();
		for(int i = 0; i<numVM; i++) {
			if(arrayOfVMs[i].host == -1) {
				continue;
			}
			int j = ob.index(arrayOfVMs[i].priorityListOfHosts, arrayOfVMs[i].host);
			float sfactor = ((numHost - j)*100)/numHost; 
			int sfact = (int)sfactor;
			factor[i] = sfact;
		}
			
		return factor;
	}
		
	// this method will calculate the satisfaction factor of the Hosts based on the formula for the satisfaction factor.
	public int[] satisfactionFactorHost(Host[] arrayOfHosts, int numVM, int numHost) {
		int[] factor = new int[numHost];
		for(int i = 0; i<numHost; i++) {
			int sfactor = 0;
			for(int j = 0; j<arrayOfHosts[i].pointer; j++) {
				int k = this.index(arrayOfHosts[i].priorityListOfVMs, arrayOfHosts[i].currentlyMatchedVMs[j]);
				float temp = ((numVM-k)*100)/numVM;
				sfactor += (int)temp;
			}
			if(arrayOfHosts[i].pointer == 0) {
				continue;
			}
			sfactor = sfactor/arrayOfHosts[i].pointer;
			factor[i] = sfactor;
		}
		return factor;
	}
	
	 
	// This is the rDA method to perform matching (Host Specific) 
	public void rDA(int numHosts, int numVMs, VM[] arrayOfVMs, Host[] arrayOfHosts){
		// waitingQueue will schedule the host to go for matching
		// It represent the host as the index of that host in arrayOfHosts array
		int[] waitingQueue = new int[numHosts];
		Arrays.fill(waitingQueue, -1);
		// matching will contain the VM matched to a particular host.
		// if matching.get(0) = [1,2,5]
		//    this means that host at index 0 in arrayOfHosts in matched with VMs at index 1,2 & 5 in arrayOfVMs
		// if matching.get(0) = [] 
		//     this means that host at index 0 in arrayOfHosts does not matched with any VMs
		ArrayList<ArrayList<Integer>> matching = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i<numHosts; i++) {
			waitingQueue[i] = i;
			ArrayList<Integer> obj = new ArrayList<Integer>();
			matching.add(obj);
		}
		int capacity = numHosts; 
		while(capacity>0) {
			int host = this.popArray(waitingQueue);
			capacity -= 1;
			if(host == -1) {
				break;
			}
			// engage method will return an array of host which lost matched VMs
			int[] hostWhichLostPairedVMs = this.engage(host, arrayOfHosts[host], arrayOfVMs, matching, numHosts, numVMs, arrayOfHosts);
			if(hostWhichLostPairedVMs[0] != -1) { 
				int counter = 0;
				for(int l = capacity; l<waitingQueue.length; l++) {
					if(hostWhichLostPairedVMs[counter] == -1) { 
						break;
					}
					waitingQueue[l] = hostWhichLostPairedVMs[counter];
					counter++;
				}
				capacity += counter;
			}	
		}
		for(int i = 0; i<matching.size(); i++) {
			System.out.println("Host at index "+i+" contains: "+matching.get(i));
		}
		// here call method for satisfaction factor 
		//This section print the satisfaction factor of all the VM
		// if the VM is not placed then the satisfaction factor of that VM is -1
		System.out.println("This is satisfaction factor for VMs");
		int[] factor = this.satisfactionFactor(arrayOfVMs, numVMs, numHosts); 
		System.out.println(Arrays.toString(factor));
		System.out.println("This is satisfaction factor for Hosts");
		System.out.println(Arrays.toString(this.satisfactionFactorHost(arrayOfHosts, numVMs, numHosts)));
		 
	}
	
	
	
	
	
	@SuppressWarnings("resource")
	public static void main(String args[]) throws IOException {
		//In this section we take the number of VM and Host 
		// and then we create that much VM and host and store them in arrayOfVMs and arrayOfHosts respectively
		// arrayOfHosts and arrayOfVMs contains the objects of Host and VM class.
		Scanner sc = new Scanner(System.in); 
		System.out.println("Enter the number of VMs(less than 99)");
		int numberOfVMs = sc.nextInt();
		System.out.println("Enter the number of Hosts(less than 99)");
		int numberOfHosts = sc.nextInt();
		VM[] arrayOfVMs = new VM[numberOfVMs];
		for(int i = 0; i<numberOfVMs; i++) {
			arrayOfVMs[i] = new VM();
		}
		Host[] arrayOfHosts = new Host[numberOfHosts];
		for(int i = 0; i<numberOfHosts; i++) {
			arrayOfHosts[i] = new Host();
		}
				
		// In this section we create the object of GeneratePriorityList
		// and then generate the priority list and store that priority in Prioritylist.txt file 
		GeneratePriorityList gn = new GeneratePriorityList();
		gn.generatePriorityList(numberOfVMs, numberOfHosts);
		File file = new File("Prioritylist.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st;
		int count = 0;
		while((st = br.readLine())!= null) {
			if(count<numberOfVMs) {
					int i=0;
					for(String temp:st.split(" ")) {
						arrayOfVMs[count].priorityListOfHosts[i]=Integer.parseInt(temp);
						i+=1;
					}
			}
			else {
				int i =0;
				for(String temp:st.split(" ")) {
					arrayOfHosts[count-numberOfVMs].priorityListOfVMs[i]=Integer.parseInt(temp);
					i+=1;
					
				}
			}
			count+=1;
						
		}
		// In this section we create call the rDA(numberOfHosts, numberOfVMs, arrayOfVMs, arrayOfHosts) method 
		// this method will then perform the matching based on RDA algorithm and store the result in matching[] array
		RDAhostSpecific obj = new RDAhostSpecific();
		obj.rDA(numberOfHosts, numberOfVMs, arrayOfVMs, arrayOfHosts); 
	}
}
