package DAandRDA;

import java.util.Arrays;
import java.util.HashSet;

public class VM {
	// these are the instance variables of VM or we can simply call it as VM requirement from the hosts
	
	int cpuRequirement;
	int memoryRequirement;
	HashSet<Integer> h = new HashSet<Integer>();
	int priorityListOfHosts[] = new int[100];
	int host;
	int pointer = 0;
	boolean currentlyMatched;
	
	//constructor to initialize the cpu and memory of VMs
	VM(){
		cpuRequirement = (int)(100*Math.random());
		memoryRequirement = (int)(1000*Math.random());
		currentlyMatched = false;
		Arrays.fill(priorityListOfHosts, -1);
		host = -1;
	}

}
