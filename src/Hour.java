import java.util.*;
import java.io.*;
import java.text.*;
public class Hour implements Comparable<Hour>{
    /* Representation invariant:
     times >= 1
     */
    
    /*
     stringTime  string of combination of date, time and timezone
     beginTime   one Date object indicating the beginning time of this 60-minute window
     times       the number of times the site was accessed during this 60-minute window
     */
    
    private String stringTime;
    private Date beginTime;
    private int times;
    
    private static final int ONEHOUR = 3600000; // one hour in millisecond
    /*
     Create an Hour object with stringTime and set times = 1;
     @param stringTime  date and time in the format of String
     */
    
    public Hour(String stringTime) throws ParseException{
        this.stringTime = stringTime;
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
        beginTime = format.parse(stringTime);
        times = 1;
    }
    
    /*
     Create an Hour object with stringTime and times;
     @param stringTime  date and time in the format of String
     @param times       the number of times the site was accessed during this 60-minute window
     */
    
    public Hour(String stringTime, int times) throws ParseException{
        this.stringTime = stringTime;
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z");
        beginTime = format.parse(stringTime);
        this.times = times;
    }
    
    /*
     @return string    stringTime, date and time in the format of String
     */
    public String getStringTime(){
        return stringTime;
    }
    
    /*
     @return int    times, the number of times the site was accessed during this 60-minute window
     */
    public int getTimes(){
        return times;
    }
    
    /*
     @return Date   beginTime, the beginning date and time of the 1-hour time window
     */
    public Date getBeginTime(){
        return beginTime;
    }
    
    /*
     times increase by 1. Increase the number of times the site was accessed during this 60-minute window by one
     */
    public void addOneTime(){
        this.times = this.times + 1;
    }
    
    /*
     @param otherTime  another Date to be determined whether it is in this 1-hour window
     @return boolean   true if in the 1-hour window and false if not.
     */
    public boolean checkInHour(Date otherTime){
        return (otherTime.getTime() - this.beginTime.getTime()) <= ONEHOUR;
    }
    
    /*
     @param otherTime  another Date to be determined whether it equals the beginTime
     @return boolean   true if equal and false if not.
     */
    public boolean checkEqual(Date otherTime){
        return (otherTime.getTime() - this.beginTime.getTime()) ==0;
    }
    
    /*
     Collectoins.sort will use compareTo method to compare and sort Hour objects.
     Hour with more times will rank ahead. With the same amount of times, used lexicographical order to comparea them.
     @param otherHour  another Hour object used to comapre with this Hour
     @return int This returns negative, positive or 0 based on the comparison of this and otherHour
     */
    public int compareTo(Hour otherHour){
        if(this.times > otherHour.times){
            return -1;
        }
        else if(this.times < otherHour.times){
            return 1;
        }
        else{
            return this.stringTime.compareTo(otherHour.stringTime);
        }
    }
    
    /*
     Used to print 1-hour time window information including beginning date and time and the number of times the site was accessed during this 60-minute window
     @return String    stringTime, times
     */
    public String toString(){
        return "" + stringTime + "," + times;
    }
    
}
