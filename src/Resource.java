public class Resource implements Comparable<Resource> {
    /* Representation invariant:
     bandWidth >= 0
     */
    
    /*
     resourceName  the name of resource
     bandWidth     bandwidth consumption of this resource
     */
    private String resourceName;
    private long bandWidth;
    
    /**
     Create one resource with its name and its current bandwidth consumption
     @param resourceName    name of the resource
     @param bandWidth       bandwidth consumption of the resource
     */
    public Resource(String resourceName, long bandWidth){
        this.resourceName = resourceName;
        this.bandWidth = bandWidth;
    }
    /*
    Collectoins.sort will use compareTo method to compare and sort Resource objects.
    Resource with more bandwidth consumption will rank ahead. With the same amount of bandwidth consumption, used lexicographical order to comparea them.
    @param otherResource another Resource object used to comapre with this Resource
    @return int This returns negative, positive or 0 based on the comparison of this and otherResource
    */
    
    public int compareTo(Resource otherResource){
        if(this.bandWidth > otherResource.bandWidth){
            return -1;
        }
        else if(this.bandWidth < otherResource.bandWidth){
            return 1;
        }
        else{
            return this.resourceName.compareTo(otherResource.resourceName);
        }
    }
    
    /*
     Used to print resource information including name
     @return String resourceName
     */
    
    public String toString(){
        return "" + this.resourceName;
    }
}
