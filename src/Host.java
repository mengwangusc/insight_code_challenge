public class Host implements Comparable<Host> {
    
    /* Representation invariant:
     times > 0
     */
    
    /*
     hostName  the name of the host or IP address
     times     the times of one host accessing any part of the site
     */
    private String hostName;
    private int times;
    /**
     Initiate one host with its name and its times accessing the site
     @param hostName name of the host
     @param times    how many times the host accessed the site
     */
    public Host(String hostName, int times){
        this.hostName = hostName;
        this.times = times;
    }
    
    /*
     Collectoins.sort will use compareTo method to compare and sort Host objects.
     Host with more times will rank ahead. With the same amount of times, used lexicographical order to compare them.
     @param otherHost another Host object used to comapre with this Host
     @return int This returns negative, positive or 0 based on the comparison of this and otherHost
     */
    public int compareTo(Host otherHost){
        if(this.times < otherHost.times){
            return 1;
        }
        else if(this.times > otherHost.times){
            return -1;
        }
        else{
            return this.hostName.compareTo(otherHost.hostName);
        }
    }
    /*
     Used to print host information including name and times
     @return String hostName, times
     */
    public String toString(){
        return "" + this.hostName + "," + this.times;
    }
}
