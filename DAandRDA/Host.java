package DAandRDA;

import java.util.Arrays;

public class Host {
	//these are the instances of hosts which stores the capacity of hosts, VMs it contains and other properties
	
	int cpuCapacity;
	int memoryCapacity;
	int bestRejected;
	int[] currentlyMatchedVMs = new int[100];
	int[] priorityListOfVMs = new int[100];
	// pointer point to the currentlyMatchedVMs that how many VMs are matched
	// this point to the index in currentlyMatchedVMs numberOfVMs matched
	int pointer = 0;
	// pointerForVM point upto which index we have traversed the priorityListOfVMs
	int pointerForVM = 0;
	//constructor to initialize the cpuCapacity and memoryCapacity of hosts
	Host() {
		cpuCapacity = (int)(1000*Math.random());
		memoryCapacity = (int)(10000*Math.random());
		bestRejected = 100;
		Arrays.fill(currentlyMatchedVMs, -1);
		Arrays.fill(priorityListOfVMs, -1);
	}

}
