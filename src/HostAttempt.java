import java.util.*;
import java.io.*;
import java.text.*;
public class HostAttempt{
    /* Representation invariant:
     numFailed is 0,1,2 or 3
     If numFailed == 2, timeFailed1.compareTo(timeFailed2) <=0
     If numFailed == 3, timeFailed1.compareTo(timeFailed2) <=0 && timeFailed2.compareTo(timeFailed3) <=0
     */
    
    /*
     timeFailed1  first time of failed login attempt
     timeFailed2  second time of failed login attempt
     timeFailed3  thrid time of failed login attempt
     numFailed    the number of failed login in one 20 second time window
     */
    private Date timeFailed1;
    private Date timeFailed2;
    private Date timeFailed3;
    private int numFailed;
    private static final int ATTEMPTTIME = 20000; // 20 second in millisecond
    private static final int BLOCKEDTIME = 300000; //5 minute in millisecond
    
    /*
     Initiate one 20-second window for one host or IP address
     */
    
    public HostAttempt(){
        this.timeFailed1 = new Date();
        this.timeFailed2 = new Date();
        this.timeFailed3 = new Date();
        numFailed = 0;
    }
    
    /*
     Add one more time to this 20-second time window. Do respective actions based on time and replyCode
     @param otherTime  one Date object will be added to this host's 20-second time window
     @param replyCode  indicates whether it is a failed login attempt or not
     */
    public void addOneTime(Date otherTime, int replyCode){
        if(replyCode / 100 == 4){
            if(numFailed == 1){
                if(otherTime.getTime() - this.timeFailed1.getTime() <= ATTEMPTTIME){
                    this.timeFailed2 = otherTime;
                    numFailed = 2;
                }
                else{
                    this.timeFailed1 = otherTime;
                }
            }
            else if(numFailed == 2){
                if(otherTime.getTime() - this.timeFailed1.getTime() <= ATTEMPTTIME){
                    this.timeFailed3 = otherTime;
                    numFailed = 3;
                }
                else if(otherTime.getTime() - this.timeFailed1.getTime() > ATTEMPTTIME){
                    if(otherTime.getTime() - this.timeFailed2.getTime() <= ATTEMPTTIME){
                        this.timeFailed1 = this.timeFailed2;
                        this.timeFailed2 = otherTime;
                    }
                    else if(otherTime.getTime() - this.timeFailed2.getTime() > ATTEMPTTIME){
                        this.timeFailed1 = otherTime;
                        this.timeFailed2 = new Date();
                    }
                }
            }
            else if(numFailed == 0){
                this.timeFailed1 = otherTime;
                numFailed = 1;
            }
        }
        else{
            reset();
        }
    }
    /*
     if numFailed ==3, it means that 3 failed login attemp in one 20-second time window and begin blocking attempts from the same host in next 20 minute
     @return boolean  true if begin blocking and false if not
     */
    public boolean beginBlocking(){
        return numFailed == 3;
    }
    
    /*
     Check if otherTime is in the blocked 20-minute time window
     @param otherTime  one Date to be checked if it is in the blocked 20-minute time window
     @return  boolean  true if otherTime is blocked and false if not
     */
    public boolean blocked(Date otherTime){
        if(otherTime.getTime() - this.timeFailed3.getTime() <= BLOCKEDTIME){
            return true;
        }
        else{
            return false;
        }
    }
    
    /*
     Reset when three failed login attemps are achieved and a success login occurs
     */
    public void reset(){
        this.timeFailed1 = new Date();
        this.timeFailed2 = new Date();
        this.timeFailed3 = new Date();
        numFailed = 0;
    }
    
}
