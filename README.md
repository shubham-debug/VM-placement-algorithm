# VM-placement-algorithm
Here DA and RDA many to one VM placement algorithm is coded in java
There are basically five classes: VM, Host, GenerationOfPriorityList, DAALgo and RDA.
VM class: 
       This class is a representation of VM, It has certain attributes like CPU requirement, Memory requirement etc.
       
Host class:
        This class is a representation of Host, It has certain attributes like CPU capacity , Memory capacity etc.
        
GenerationOfPriorityList Class: 
        This class generates the priority list for VMs and Hosts and store them in a text file
        
DAALgo class:
         This class create array of VMs and Hosts, Initialize them and then find the matching in many to one fashion.
         
RDA class: 
         This class perform matching by intoducting some improvement so that Type 2 Blocking pair cannot be formed.
