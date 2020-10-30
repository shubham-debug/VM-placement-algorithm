package DAandRDA;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import java.io.*;


public class RDA {
	
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
	 
	//this method returns the index of VM in arrayOfVMs that is rejected by the host
	//this is the utility method for engage method
	public int[] match(int vm, int[] matching, VM[] arrayOfVMs, Host host) {
		
		VM currVM = arrayOfVMs[vm];
		RDA ob3 = new RDA();
		int priorityOfCurrVM = ob3.index(host.priorityListOfVMs, vm);
		// breject is used to update the Host.bestRejected attribute
		int breject = 100;
		//temp will store all the VM that has priority less than currVM
		int[] temp = new int[100];
		Arrays.fill(temp, -1);
		int count = 0;
		//This loop will add all the VMs that has less priority the currVM in temp[] array
		for(int i = 0; i<100; i++) {
			if(i<host.pointer) {
				int prorityOfVM = ob3.index(host.priorityListOfVMs, host.currentlyMatchedVMs[i]);
				// Here I took greater than because we take index of VM as their priority and the VM whose index is low has high priority
				if(prorityOfVM > priorityOfCurrVM) {
					temp[count]=host.currentlyMatchedVMs[i];
					count += 1;
				}
			}
			// else part specifies that we have gone through all the VMs that are currently matched to the host
			else {
				break;
			}
		} 
		
		int cpu = 0;
		int memory = 0;
		count = 0;
		// here cpu is the need of currVM and same for memory and I take count<100 as this is the maximum count value possible
		// This loop will check that adding the cpu and memory requirement of all the VMs that has less priority than the current VM
		// and count is used to mark that upto what index of temp[] array the requirement is fulfilled
		while(cpu<currVM.cpuRequirement && memory<currVM.memoryRequirement && count<100) {
			if(temp[count]==-1) {
				//this condition means that we just go through all the vm that has less priority than currVM
				break;
			}
			int priorityOfVm = ob3.index(host.priorityListOfVMs, temp[count]);
			//This condition is to check that the Host.bestRejected is greater than the rejected VMs due to currentVM
			if(priorityOfVm < breject) {
				breject = priorityOfVm;
			}
			cpu += arrayOfVMs[temp[count]].cpuRequirement;
			memory += arrayOfVMs[temp[count]].memoryRequirement;
			count += 1;
		}
		
		// flag1 is to check that whether or not currentVM can be placed or not in the Host
		boolean flag1 = false;
		// if after rejecting some VMs we can acquire the currentVM then we enter this condition
		if(cpu>=currVM.cpuRequirement && memory>=currVM.memoryRequirement) {
			// here we set attributes of host and currentVM
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
		// This condition means that some VM is rejected and we have to add those VMs in the ans[] array and return this array.
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
	
	// This method will take the object of Host
	// and then reject all the VM that has priority less the Host.bestRejected
	// this method will return the array of VMs that is rejected by the host.
	private int[] reject(Host host) {
		int[] ans = new int[100];
		int[] newCurrentlyMatched = new int[100];
		Arrays.fill(ans, -1);
		Arrays.fill(newCurrentlyMatched, -1);
		int counter1 = 0;
		int counter = 0;
		RDA ob4 = new RDA();
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
	
	  
	 
	// This is the utility method for the rDA 
	// Its function is that it will place the VM in the appropriate host and return the array of VM that are rejected by the Host after placing the current VM.
	public int[] engage(int vm, int[] matching, VM[] arrayOfVMs, Host[] arrayOfHosts) {
		RDA ob1 = new RDA();
		// vmThatIsRejectedByTheCurrentHost array will contain all the VMs that is rejected by the host on placing the currentVM and also due to best rejected attribute of host
		int vmThatIsRejectedByTheCurrentHost[] = new int[100];
		// vmThatIsRejectedBecauseOfBestRejected array contain all the VMs that is rejected because of bestRejected attribute of host
		// This array is in the end will be merged with vmThatIsRejectedByTheCurrentHost and returned
		int[] vmThatIsRejectedBecauseOfBestRejected = new int[100];
		// tempArray is used to store rejectedVMs temporary and then merged with vmThatIsRejectedBecauseOfBestRejected
		int[] tempArray = new int[100];
		int counter = 0;
		//initially all the array is filled with -1 to marks these arrays empty
		Arrays.fill(vmThatIsRejectedByTheCurrentHost, -1);
		Arrays.fill(vmThatIsRejectedBecauseOfBestRejected, -1);
		Arrays.fill(tempArray, -1);
		VM currVM = arrayOfVMs[vm];
		//This loop will iterate over the priorityListOfHosts of currentVM and find the appropriate host for placement
		for(int j = currVM.pointer; j<currVM.priorityListOfHosts.length; j++) {
			// i is the index of host in arrayOfHosts[]
			int i = currVM.priorityListOfHosts[j];
			// This condition checks that whether or not we came to the end of priorityListOfHosts as it is possible that a VM priority list may or may not contain all the host
			// but as the priorityListOfHosts length is fixed for all the VMs so -1 specifies no host or empty position
			if(i == -1) {
				break;
			}
			// This condition means that the currentHost has required cpu capacity and memory capacity to acquire currentVM
			// if this condition satisfies the currentVM is placed in the host and no other VMs are rejected so we break the loop set all the appropriate attributes
			// and then returns an empty vmThatIsRejectedByTheCurrentHost
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
			// This condition means that the host bestRejected is higher than the priorityOfVM so we just continue the iteration 
			// and propose the next host in the priority list with next iteration
			else if(arrayOfHosts[i].bestRejected < ob1.index(arrayOfHosts[i].priorityListOfVMs, vm)){
				continue;
			}
			// This condition means that the host does not have capacity to acquire the currentVM
			// so  in this condition we call the match(currentVMIndex, matching, arrayOfVMs, hostToPropose)
			// which the check that whether or not currentVM can be placed or not in the host by removing some VMs with less priority
			else {
				vmThatIsRejectedByTheCurrentHost = ob1.match(vm, matching, arrayOfVMs, arrayOfHosts[i]);
				// In this condition we check the array vmThatIsRejectedByTheCurrentHost index 0 
				// if that index does not equal to -1 it means that some VMs are rejected and the match method will return array with rejected VMs
				// only when the currentVM is placed otherwise not.
				if(vmThatIsRejectedByTheCurrentHost[0]!=-1) {
					// freeVM method will free the VMs that is rejected by setting the attributes VM.currentlyMatched = false and VM.host = -1
					ob1.freeVM(vmThatIsRejectedByTheCurrentHost, arrayOfVMs, matching);
					currVM.pointer = j+1;
					currVM.currentlyMatched = true;
					matching[vm]=i;
					// we call the reject method to get all the VMs that has priority less than Host.bestRejected
					// we use break statement because the current VM is placed
					tempArray = ob1.reject(arrayOfHosts[i]);
					break;
				}
				// In this condition the currentVM is not placed in the proposed host so we update the Host.bestRejected and then call the reject method 
				// which then reject all the VMs that has less priority than Host.bestRejected
				else{
					int tempBestRejected = ob1.index(arrayOfHosts[i].priorityListOfVMs, vm);
					if(arrayOfHosts[i].bestRejected > tempBestRejected) {
						arrayOfHosts[i].bestRejected = tempBestRejected;
						tempArray = ob1.reject(arrayOfHosts[i]);
					}
				}
				// freeVM method will free the VMs that is rejected and stored in tempArray
				ob1.freeVM(tempArray, arrayOfVMs, matching);
				// Here we just add all the rejected VMs in the tempArray and store those in vmThatIsRejectedBecauseOfBestRejected
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
		// Here we count that upto which index the vmThatIsRejectedByTheCurrentHost which is filled by the VMs that is removed to place the currentVM in the Host
		for(int l = 0; l < vmThatIsRejectedByTheCurrentHost.length; l++) {
			if(vmThatIsRejectedByTheCurrentHost[l] == -1) {
				break;
			}
			else {
				count += 1;
			}
		}
		// Here we add all the VMs that is rejected because of bestRejected  in the vmThatIsRejectedByTheCurrentHost
		for(int l = 0; l<vmThatIsRejectedBecauseOfBestRejected.length; l++) {
			if(vmThatIsRejectedBecauseOfBestRejected[l] == -1) {
				break;
			}
			else {
				vmThatIsRejectedByTheCurrentHost[count] = vmThatIsRejectedBecauseOfBestRejected[l];
				count++;
			}
		}
		// All the VMs in the vmThatIsRejectedByTheCurrentHost is then return to the rDA method which will then added to the waitingQueue.
		return vmThatIsRejectedByTheCurrentHost;
	}
	 
	
	
	// this method will pop the first element of the array and return that element 
	// and also shift all the element one index toward left
	public int popArray(int[] arr,int capacity) {
		int item = arr[0];
		
		for(int i = 1; i<arr.length; i++) {
			arr[i-1] = arr[i]; 
		}
		arr[arr.length-1]=-1;
		return item;
		
	} 
	
	// this method will calculate the satisfaction factor of the VMs based on the formula for satisfaction factor
	public int[] satisfactionFactor(VM[] arrayOfVMs, int[] matching, int numHost) {
		int[] factor = new int[matching.length];
//		Arrays.fill(factor, -1);
		RDA ob = new RDA();
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
	
	// this method will calculate the satisfaction factor of the Hosts based on the formula for the satisfaction factor.
	public int[] satisfactionFactorHost(Host[] arrayOfHosts, int[] matching, int numVM, int numHost) {
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
	
	// These function of this method is the call the engage method until there is a VM in the waiting queue
	// In this method we finally find the matching which is stored in matching array
	// the matching array store the result as follows
	// if matching[0] = 2 (This means that in arrayOfVMs the VM at index 0 is matched with the Host at index 2 in arrayOfHosts)
	// if matching[0] = -1 (This means that in arrayOfVMs the VM at index 0 is not matched with any host)
	public void rDA(int numHost, int numVM, VM[] arrayOfVMs, Host[] arrayOfHosts ) {
		RDA ob = new RDA();
		// the waitingQueue initially contains all the VMs and it sends the VM for allocation in FIFO manner
		// waitingQueue store the result as follows:
		// if waitingQueue[2] = 4 (This means that VM at index 4 in arrayOfVMs is waiting for allocation
		// if waitingQueue[2] = -1 (This means that no VM is waiting at that index and after that index)
		int[] waitingQueue = new int[numVM];
		int[] matching = new int[numVM];
		for(int i = 0; i<numVM; i++) {
			waitingQueue[i] = i;
			matching[i] = -1;
		}
		
		// capacity tells us the number of VMs in waitingQueue
		int capacity = numVM;
		while(capacity != 0) { 
			//System.out.println("This is waitingQueue "+Arrays.toString(waitingQueue));
			// The popArray method will remove the element at index 0 and shift all the other elements one position left and fill the last index of array as -1 
			// to show that, this position is empty. and return the element at index 0 that is removed
			int vm = ob.popArray(waitingQueue, capacity);
			capacity -= 1;
			//this if condition is only to check that the popArray method does not pop the empty waitingQueue 
			if(vm == -1) {
				break;
			}
			//The engage method will find the appropriate matching for the vm and returns the array of VMs that are rejected by placing the current VM in the host
			int[] tempVM = ob.engage(vm, matching, arrayOfVMs, arrayOfHosts);
			// if the tempVM[0]!=-1, it means that by placing the current VM in the host some VMs are rejected
			// we then just add those VMs in the waitingQueue and also increase the capacity of the waitingQueue
			// if the tempVM[0] = -1 then no VM is deallocated by allocating the current VM
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
		//This section print the satisfaction factor of all the VM
		// if the VM is not placed then the satisfaction factor of that VM is -1
		System.out.println("This is satisfaction factor for VMs");
		int[] factor = ob.satisfactionFactor(arrayOfVMs, matching, numHost); 
		System.out.println(Arrays.toString(factor));
		System.out.println("This is satisfaction factor for Hosts");
		System.out.println(Arrays.toString(ob.satisfactionFactorHost(arrayOfHosts, matching, numVM, numHost)));
		
		
	}
	
	
	//This is the main method. Its task is the initialize all the VMs and Hosts Store them and then call the DA method to find the matching
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		
		//In this section we take the number of VM and Host 
		// and then we create that much VM and host and store them in arrayOfVMs and arrayOfHosts respectively
		// arrayOfHosts and arrayOfVMs contains the objects of Host and VM class.
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
		RDA obj = new RDA();
		obj.rDA(numberOfHosts, numberOfVMs, arrayOfVMs, arrayOfHosts); 

		
	} 


} 


