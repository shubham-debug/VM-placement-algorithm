package DAandRDA;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

// this class will generate priority list of VMs and hosts and store them in a txt file
public class GeneratePriorityList {
	
	public void generatePriorityList(int numberOfVMs, int numberOfHosts) throws IOException{
		// s will contain all the priority of all the VMs and Hosts
		String s = "";
		FileWriter fw = new FileWriter("Prioritylist.txt");
		for(int i=0; i<numberOfVMs; i++) {
			HashSet<Integer> h = new HashSet<>();
			int counter = 0;
			while(h.size()<numberOfHosts && counter<(numberOfHosts + 50)) {
				counter += 1;
				int indexOfHost = (int)(numberOfHosts * Math.random());
				if(!h.contains(indexOfHost)) {
					h.add(indexOfHost);
					s += Integer.toString(indexOfHost);
					s += " ";
				}
			}
			s += "\n";
		}
		
		for(int i=0; i<numberOfHosts; i++) {
			HashSet<Integer> h = new HashSet<>();
			int counter = 0;
			while(h.size()<numberOfVMs && counter<(numberOfVMs + 50)) {
				counter += 1;
				int indexOfVM = (int)(numberOfVMs * Math.random());
				if(!h.contains(indexOfVM)) {
					h.add(indexOfVM);
					s += Integer.toString(indexOfVM);
					s += " ";
				}
			}
			s += "\n";
		}
		
		for(int i =0; i<s.length(); i++) {
			fw.write(s.charAt(i));
			
		}
		//System.out.println("written successfully");
		fw.close();
	}
	
	

}
