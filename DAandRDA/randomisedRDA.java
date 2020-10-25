package DAandRDA;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import java.io.*;


public class randomisedRDA {
	
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
	 
	//this method returns the index of VM in arrayOfVMs that is rejected by the host
	//this is the utility method for engage method
	public int[] match(int vm, int[] matching, VM[] arrayOfVMs, Host host) {
		
		VM currVM = arrayOfVMs[vm];
		randomisedRDA ob3 = new randomisedRDA();
		int priorityOfCurrVM = ob3.index(host.priorityListOfVMs, vm);
		int breject = 100;
		//temp will store all the VM that has priority less than currVM
		int[] temp = new int[100];
		Arrays.fill(temp, -1);
		int count = 0;
		for(int i = 0; i<100; i++) {
			
			if(i<host.pointer) {
				int prorityOfVM = ob3.index(host.priorityListOfVMs, host.currentlyMatchedVMs[i]);
				// Here I took greater than because we take index of vm as their priority and the vm whose index is low has high priority
				if(prorityOfVM > priorityOfCurrVM) {
					temp[count]=host.currentlyMatchedVMs[i];
					count += 1;
				}
			}
			else {
				break;
			}
		} 
		
		int cpu = 0;
		int memory = 0;
		count = 0;
		// here cpu is the need of currVM and same for memory and I take count<100 as this is the maximum count value possible
		while(cpu<currVM.cpuRequirement && memory<currVM.memoryRequirement && count<100) {
			if(temp[count]==-1) {
				//this condition means that we just go through all the vm that has less priority than currVM
				break;
			}
			int priorityOfVm = ob3.index(host.priorityListOfVMs, temp[count]);
			if(priorityOfVm < breject) {
				breject = priorityOfVm;
			}
			cpu += arrayOfVMs[temp[count]].cpuRequirement;
			memory += arrayOfVMs[temp[count]].memoryRequirement;
			count += 1;
		}
		boolean flag1 = false;
		if(cpu>=currVM.cpuRequirement && memory>=currVM.memoryRequirement) {
			host.bestRejected = breject;
			flag1 = true;
			host.cpuCapacity += cpu - currVM.cpuRequirement;
			host.memoryCapacity += memory - currVM.memoryRequirement;
			int[] newCurrentlyMatchedList = new int[100];
			Arrays.fill(newCurrentlyMatchedList, -1);
			int counter = 0;
			for(int i = 0; i<100; i++) {
				boolean flag = true;
				if(host.currentlyMatchedVMs[i]==-1) {
					break;
				}
				for(int j = 0; j<count; j++) {
					if(temp[j]==host.currentlyMatchedVMs[i]) {
						flag = false;
						break;
					} 
				}
				if(flag) {
					newCurrentlyMatchedList[counter] = host.currentlyMatchedVMs[i];
					counter += 1;
				}
			} 
			newCurrentlyMatchedList[counter] = vm;
			counter += 1;
			host.currentlyMatchedVMs = newCurrentlyMatchedList;
			host.pointer = counter;
			 
		}
		int[] ans = new int[100]; 
		Arrays.fill(ans, -1);
		if(flag1) {
		for(int i =0; i<100; i++) {
			if(i<count) {
				ans[i]=temp[i];
			}
			else {
				ans[i]=-1;
			}
		}
			
	  }
	  return ans;
	}
	
	private int[] reject(Host host) {
		int[] ans = new int[100];
		int[] newCurrentlyMatched = new int[100];
		Arrays.fill(ans, -1);
		Arrays.fill(newCurrentlyMatched, -1);
		int counter1 = 0;
		int counter = 0;
		randomisedRDA ob4 = new randomisedRDA();
		for(int i = 0; i < 100; i++) {
			if(host.currentlyMatchedVMs[i] == -1) {
				break;
			}
			int temp = ob4.index(host.priorityListOfVMs, host.currentlyMatchedVMs[i]);
			if(temp > host.bestRejected) {
				ans[counter] = host.currentlyMatchedVMs[i];
				counter++;
			}
			else {
				newCurrentlyMatched[counter1] = host.currentlyMatchedVMs[i];
				counter1++;
			}
		}
		host.currentlyMatchedVMs = newCurrentlyMatched;
		host.pointer = counter1;
		return ans;
	}
    
	//This method is to free the State of VM that currently Says that it is matched
	private void freeVM(int[] vmThatIsRejectedByTheCurrentHost, VM[] arrayOfVMs, int[] matching) {
		for(int k=0; k<vmThatIsRejectedByTheCurrentHost.length; k++) {
			if(vmThatIsRejectedByTheCurrentHost[k]==-1) {
				break;
			}
			else {
				arrayOfVMs[vmThatIsRejectedByTheCurrentHost[k]].host=-1;
				arrayOfVMs[vmThatIsRejectedByTheCurrentHost[k]].currentlyMatched = false;
				matching[vmThatIsRejectedByTheCurrentHost[k]]=-1;
			}
	  }
	}
	
	  
	
	// This is the utility method for the DA 
	// Its function is that it will place the VM in the appropriate host and return the array of VM that are rejected by the Host after placing the current VM.
	public int[] engage(int vm, int[] matching, VM[] arrayOfVMs, Host[] arrayOfHosts, int numHost) {
		randomisedRDA ob1 = new randomisedRDA();
		int vmThatIsRejectedByTheCurrentHost[] = new int[100];
		int[] vmThatIsRejectedBecauseOfBestRejected = new int[100];
		int[] tempArray = new int[100];
		int counter = 0;
		Arrays.fill(vmThatIsRejectedByTheCurrentHost, -1);
		Arrays.fill(vmThatIsRejectedBecauseOfBestRejected, -1); 
		Arrays.fill(tempArray, -1);
		VM currVM = arrayOfVMs[vm];
		while(true){
			if(arrayOfVMs[vm].h.size() >= numHost) {
				break;
			}
			int j;
			while(true) {
				j = (int)(numHost*Math.random());
				if(arrayOfVMs[vm].h.contains(j)) {
					continue;
				}
				else {
					arrayOfVMs[vm].h.add(j);
					break;
				}
				
			}
			int i = currVM.priorityListOfHosts[j];
			if(i == -1) {
				break;
			}
			if(arrayOfHosts[i].cpuCapacity>=currVM.cpuRequirement && arrayOfHosts[i].memoryCapacity>=currVM.memoryRequirement) {
				arrayOfHosts[i].cpuCapacity -= currVM.cpuRequirement;
				arrayOfHosts[i].memoryCapacity -= currVM.memoryRequirement;
				arrayOfHosts[i].currentlyMatchedVMs[arrayOfHosts[i].pointer] = vm; 
				arrayOfHosts[i].pointer += 1;
				currVM.currentlyMatched = true;
				currVM.host = i;
				currVM.pointer = j+1;
				matching[vm] = i;
				break;
			} 
			else if(arrayOfHosts[i].bestRejected < ob1.index(arrayOfHosts[i].priorityListOfVMs, vm)){
				continue;
			}
			else {
				vmThatIsRejectedByTheCurrentHost = ob1.match(vm, matching, arrayOfVMs, arrayOfHosts[i]);
				if(vmThatIsRejectedByTheCurrentHost[0]!=-1) {
					ob1.freeVM(vmThatIsRejectedByTheCurrentHost, arrayOfVMs, matching);
					currVM.pointer = j+1;
					currVM.currentlyMatched = true;
					matching[vm]=i;
					tempArray = ob1.reject(arrayOfHosts[i]);
					break;
				}
				else{
					int tempBestRejected = ob1.index(arrayOfHosts[i].priorityListOfVMs, vm);
					if(arrayOfHosts[i].bestRejected > tempBestRejected) {
						arrayOfHosts[i].bestRejected = tempBestRejected;
						tempArray = ob1.reject(arrayOfHosts[i]);
					}
				}
				ob1.freeVM(tempArray, arrayOfVMs, matching);
				for(int l = 0; l < tempArray.length; l++) { 
					if(tempArray[l] == -1) {
						break;
					}
					else {
						vmThatIsRejectedBecauseOfBestRejected[counter] = tempArray[l];
						counter++;
					}
					
				}
			}
		}
		
		int count = 0;
		for(int l = 0; l < vmThatIsRejectedByTheCurrentHost.length; l++) {
			if(vmThatIsRejectedByTheCurrentHost[l] == -1) {
				break;
			}
			else {
				count += 1;
			}
		}
		for(int l = 0; l<vmThatIsRejectedBecauseOfBestRejected.length; l++) {
			if(vmThatIsRejectedBecauseOfBestRejected[l] == -1) {
				break;
			}
			else {
				vmThatIsRejectedByTheCurrentHost[count] = vmThatIsRejectedBecauseOfBestRejected[l];
				count++;
			}
		}
		return vmThatIsRejectedByTheCurrentHost;
	}
	
	
	public int popArray(int[] arr,int capacity) {
		int item = arr[0];
		
		for(int i = 1; i<arr.length; i++) {
			arr[i-1] = arr[i]; 
		}
		arr[arr.length-1]=-1;
		return item;
		
	} 
	
	
	public int[] satisfactionFactor(VM[] arrayOfVMs, int[] matching, int numHost) {
		int[] factor = new int[matching.length];
		Arrays.fill(factor, -1);
		randomisedRDA ob = new randomisedRDA(); 
		for(int i = 0; i<matching.length; i++) {
			if(matching[i]==-1) {
				continue;
			}
			int j = ob.index(arrayOfVMs[i].priorityListOfHosts, matching[i]);
			float sfactor = ((numHost - j)*100)/numHost; 
			int sfact = (int)sfactor;
			factor[i] = sfact;
		}
		
		return factor;
	}
	
	// These function of this method is the call the engage method until there is a VM in the waiting queue
	public void DA(int numHost, int numVM, VM[] arrayOfVMs, Host[] arrayOfHosts ) {
		randomisedRDA ob = new randomisedRDA();
		int[] waitingQueue = new int[numVM];
		int[] matching = new int[numVM];
		for(int i = 0; i<numVM; i++) {
			waitingQueue[i] = i;
			matching[i] = -1;
		}
		
		int capacity = numVM;
		while(capacity != 0) { 
			//System.out.println("This is waitingQueue "+Arrays.toString(waitingQueue));
			int vm = ob.popArray(waitingQueue, capacity);
			capacity -= 1;
			if(vm == -1) {
				break;
			}
			//The engage method will find the appropriate matching for the vm and returns the array of VMs that are rejected by placing the current VM in the host
			int[] tempVM = ob.engage(vm, matching, arrayOfVMs, arrayOfHosts, numHost);
			if(tempVM[0]!=-1) {
				int counter = 0;
				for(int i = capacity; i<numVM; i++) {
				   if(tempVM[counter]==-1) {
					  break;
				   }
				   waitingQueue[i] = tempVM[counter];
				   counter += 1;
				}
				capacity += counter;
			}		
		}    
		System.out.println("This is matching");
		for(int i = 0; i<numVM; i++) {
			System.out.print(matching[i]+" ");
		}
		System.out.println();
		System.out.println("This is satisfaction factor");
		int[] factor = ob.satisfactionFactor(arrayOfVMs, matching, numHost); 
		System.out.println(Arrays.toString(factor));
		
		
	}
	
	
	//This is the main method. Its task is the initialize all the VMs and Hosts Store them and then call the DA method to find the matching
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		
		Scanner sc = new Scanner(System.in); 
		System.out.println("Enter the number of VMs");
		int numberOfVMs = sc.nextInt();
		System.out.println("Enter the number of Hosts");
		int numberOfHosts = sc.nextInt();
		VM[] arrayOfVMs = new VM[numberOfVMs];
		for(int i = 0; i<numberOfVMs; i++) {
			arrayOfVMs[i] = new VM();
		}
		Host[] arrayOfHosts = new Host[numberOfHosts];
		for(int i = 0; i<numberOfHosts; i++) {
			arrayOfHosts[i] = new Host();
		}
		
		// this section will set priority to the VMs and hosts 
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
		randomisedRDA obj = new randomisedRDA();
		obj.DA(numberOfHosts, numberOfVMs, arrayOfVMs, arrayOfHosts);

		
	}


} 

  
