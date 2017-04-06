import java.util.*;
import java.io.*;
import java.text.*;
public class codeChallenge{
    /*
     hostMap                
     count how many times one host/IP address accessed any part of the site
     Key is host name and Value is times of one host
     
     resourceMap            
     count how much bandwidth consumption one resource used
     Key is resource name and Value is bandwidth consumption of one resource
     
     hourStack              
     count the number of times the site was accessed during this 60-minute window
     
     attempMap              
     record and update failed login attempt in 20-second time window and block following login attempt
     Key is host name and Value is HostAttempt object
     
     writerBlocked          
     create an output file and output information to that file
     
     startingTimeofLogFile  
     the very beginning date and time in the input file, which means the time in the first line
     */
    private static HashMap<String, Integer> hostMap;
    private static HashMap<String, Long> resourceMap;
    private static Stack<Hour> hoursStack;
    private static HashMap<String, HostAttempt> attemptMap;
    private static PrintWriter writerBlocked;
    private static Date startingTimeofLogFile;
    
    private final static int NUMSHOWN = 10;  // the number of lines shown in hours.txt, hosts.txt and resources.txt at most
    private final static String TIMEFORMAT = "dd/MMM/yyyy:HH:mm:ss Z";
    private final static String TIMEZONE   = "America/New_York";
    private final static String INPUTNAME = "../log_input/log.txt";
    private final static String HOSTSTXT = "../log_output/hosts.txt";
    private final static String RESOURCESTXT = "../log_output/resources.txt";
    private final static String HOURSTXT = "../log_output/hours.txt";
    private final static String BLOCKSTXT = "../log_output/blocked.txt";
    private static int sumLine;
    private static long sumBW;
    public static void main(String[] args){
        try{
            hostMap = new HashMap<String, Integer>();
            resourceMap = new HashMap<String, Long>();
            hoursStack = new Stack<Hour>();
            attemptMap = new HashMap<String, HostAttempt>();
            
            
            writerBlocked = new PrintWriter(BLOCKSTXT);
            String line = "";
            int i=1;
            
            //Begin read the first time in the first line of input file
            Scanner in = new Scanner(new File(INPUTNAME));
            in.next();
            in.next();
            in.next();
            String time = in.next().substring(1);
            String timeZone = in.next();
            timeZone = timeZone.substring(0,timeZone.length()-1);
            time = time + " " + timeZone;
            SimpleDateFormat format = new SimpleDateFormat(TIMEFORMAT);
            startingTimeofLogFile = format.parse(time);
            in.close();
            //finished reading first time
            
            FileReader fileReader = new FileReader(INPUTNAME);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null){
                parseLine(line);
                i++;
            }
            genHostsFile();
            genResourcesFile();
            genHoursFile();
            writerBlocked.close();
        }
        catch(FileNotFoundException exc){
            System.out.println("input file does not exist or cannot write to output file");
        }
        catch(NoSuchElementException exc) {
            System.out.println("error in scanning each line in input file");
        }
        catch(IOException exc) {
            System.out.println("error in reading lines in input file");
        }
        catch(ParseException exc) {
            System.out.println("error in parsing date and time");
        }
    }
    
    /*
     Parse each line in input file.
     Extract infomation like host name, access date and time, resource name, reply code and bandwidth consumption.
     And put relevant information into each relevant data structure.
     @param line  a String indicating each line of input file
     */
    private static void parseLine(String line) throws NoSuchElementException, ParseException, FileNotFoundException {
        
        Scanner in = new Scanner(line);
        String host = in.next();
        in.next();
        in.next();
        String time = in.next().substring(1);
        String timeZone = in.next();
        timeZone = timeZone.substring(0,timeZone.length()-1);
        time = time + " " + timeZone;
        putInHoursStack(time);
        String requestAction = in.next().substring(1);
        if(requestAction.charAt(requestAction.length()-1) == '\"'){ //check if request action is empty
            putInHostMap(host);
        }
        else{
            String resource = in.next();
            if(resource.charAt(resource.length()-1) == '\"'){
                resource = resource.substring(0,resource.length()-1);
            }
            while(!in.hasNextInt()){ //skill any irrelevant word
                in.next();
            }
            int replyCode = in.nextInt();
            int bandWidth;
            if(in.hasNextInt()){  //if there is '-', set bandWidth to 0
                bandWidth = in.nextInt();
            }
            else{
                bandWidth = 0;
            }
            sumBW = sumBW + bandWidth;
            putInHostMap(host);
            putInResourceMap(resource, (long) bandWidth);
            checkReqBlocked(host, time, replyCode, line);
        }
    }
    
    /*
     put host name into hostMap.
     If one host is put at first time, its Value will be assigned to be 1.
     If one host is already in hostMap, its Value will be increased by 1.
     
     @param hostName   name of one host/IP address
     */
    private static void putInHostMap(String hostName){
        if(hostMap.containsKey(hostName)){
            hostMap.put(hostName,hostMap.get(hostName)+1);
        }
        else{
            hostMap.put(hostName,1);
        }
    }
    
    /*
     Extract entry set of hostMap and put all entries converted to Host into an ArrayList<Host>.
     Sort this ArrayList and write the top-10 hosts into hosts.txt.
     */
    private static void genHostsFile() throws FileNotFoundException{
        ArrayList<Host> listHosts = new ArrayList<Host>();
        for(Map.Entry<String, Integer> entry : hostMap.entrySet()){
            listHosts.add(new Host(entry.getKey(),entry.getValue()));
        }
        Collections.sort(listHosts);
        PrintWriter writerHosts = new PrintWriter(HOSTSTXT);
        for(int i=0; i<NUMSHOWN && i<listHosts.size();i++){
            writerHosts.println(listHosts.get(i));
        }
        writerHosts.close();
    }
    
    /*
     put host name into hostMap.
     If one resource is put at first time, its Value will be assigned to be initial bandwidth consumption, bW.
     If one resource is already in hostMap, its Value will be increased by bW.
     
     @param resourceName   name of one resource
     @param bW             bandwidth consumption of that resource for each access
     */
    private static void putInResourceMap(String resourceName, long bW){
        if(resourceMap.containsKey(resourceName)){
            resourceMap.put(resourceName,resourceMap.get(resourceName) + bW);
        }
        else{
            resourceMap.put(resourceName,bW);
        }
    }
    
    /*
     Extract entry set of resourceMap and put all entries converted to Resource into an ArrayList<Resource>.
     Sort this ArrayList and write the top-10 resources into resources.txt
     */
    private static void genResourcesFile() throws FileNotFoundException{
        ArrayList<Resource> listResources = new ArrayList<Resource>();
        for(Map.Entry<String, Long> entry : resourceMap.entrySet()){
            listResources.add(new Resource(entry.getKey(),entry.getValue()));
        }
        Collections.sort(listResources);
        PrintWriter writerResources = new PrintWriter(RESOURCESTXT);
        for(int i=0; i< NUMSHOWN && i<listResources.size();i++){
            writerResources.println(listResources.get(i));
        }
        writerResources.close();
    }
    
    
    /*
     The beginning time of each time 1-hour time window is the time appears in log.txt.
     Since all times listed in log.txt are already in order, push new time into stack. It means that the newest time is on the top of stack.
     Before we push a new time into stack, we check the top time in stack whether the difference between new time and the top time is no more than 1 hour. If so, use addOneTime to increase the number of times the site was accessed during this 60-minute window by 1. And then pop that top time to another temporary stack. If more than 1 hour, stop comparing and popping and push back all times in temporary stack to original hoursStack.
     
     @param stringTime      date and time in the format of String
     */
    private static void putInHoursStack(String stringTime) throws ParseException{
        Stack<Hour> tmpStack = new Stack<Hour>();
        SimpleDateFormat format = new SimpleDateFormat(TIMEFORMAT);
        Date curTime = format.parse(stringTime);
        if(hoursStack.isEmpty()){
            hoursStack.push(new Hour(stringTime));
            return;
        }
        boolean ending = false;
        while(!hoursStack.isEmpty() && !ending){
            if(hoursStack.peek().checkInHour(curTime)){
                hoursStack.peek().addOneTime();
                tmpStack.push(hoursStack.pop());
            }
            else{
                ending = true;
            }
        }
        while(!tmpStack.isEmpty()){
            hoursStack.push(tmpStack.pop());
        }
        if(!hoursStack.peek().checkEqual(curTime)){
            hoursStack.push(new Hour(stringTime));
        }
    }
    
    /*
     First add all elements in hoursStack to an ArrayList<Hour> and only the beginning time of each time window to hoursSet<Date>.
     Second pick up the top-10 1-hour time window with largest number of times the site was accessed.
     Third pick up the first one in top-10 and shift the beginning time of the time window by decreasing 1 second. If the new time window didn't omit any old login attempts or add new ones, the new time window will have the same number of time as the old time window. And output the new time window to outputList.
     Keep doing the third step until we have ten time windows.
     */
    private static void genHoursFile() throws FileNotFoundException, ParseException{
        ArrayList<Hour> listHours = new ArrayList<Hour>();
        HashSet<Date> hoursSet = new HashSet<Date>();
        while(!hoursStack.isEmpty()){
            listHours.add(hoursStack.peek());
            hoursSet.add(hoursStack.pop().getBeginTime());
        }
        Collections.sort(listHours);
        ArrayList<Hour> tenHighest = new ArrayList<Hour>();
        PrintWriter writerHours = new PrintWriter(HOURSTXT);
        //i indicates how many elements we put in ArrayList tenHighest. Usually, 10 element. But sometimes there will be less than 10 elements in listHours
        int i=0;
        for(i=0;i < NUMSHOWN && i<listHours.size();i++){
            tenHighest.add(listHours.get(i));
        }
        int numOutput = 0;
        ArrayList<Hour> outputList = new ArrayList<Hour>();
        for(int j=0; j<i; j++){
            Hour tmpHour = tenHighest.get(j);
            if(startingTimeofLogFile.getTime() - tmpHour.getBeginTime().getTime() != 0){
                Calendar cal = Calendar.getInstance(); // creates calendar
                cal.setTime(tmpHour.getBeginTime());   // sets calendar time/date
                cal.add(Calendar.SECOND, -1);          // minus one second
                Date d1 = cal.getTime();
                cal.setTime(d1);
                cal.add(Calendar.SECOND, 3601);
                Date d2 = cal.getTime();
                outputList.add(tmpHour);
                while(!hoursSet.contains(d1) && !hoursSet.contains(d2)){
                    
                    SimpleDateFormat format = new SimpleDateFormat(TIMEFORMAT);
                    TimeZone tz = TimeZone.getTimeZone(TIMEZONE);
                    format.setTimeZone(tz);
                    outputList.add(new Hour(format.format(d1), tmpHour.getTimes()));
                    numOutput++;
                    
                    cal.setTime(d1);
                    cal.add(Calendar.SECOND, -1);
                    d1 = cal.getTime();
                    cal.setTime(d2);
                    cal.add(Calendar.SECOND, -1);
                    d2 = cal.getTime();
                }
            }
            else{
                numOutput++;
                outputList.add(tmpHour);
            }
            if(numOutput >= NUMSHOWN){
                break;
            }
        }
        Collections.sort(outputList);
        for(int j=0;j<NUMSHOWN && j<outputList.size();j++){
            writerHours.println(outputList.get(j));
        }
        writerHours.close();
    }
    
    /*
     Check whether one login attemp will be blocked or update the 20-second time window
     
     @param host        name of host/IP address
     @param time        date and time in the format of String
     @param replyCode   HTTP reply code
     @param line        one line of request in the format of String
     */
    private static void checkReqBlocked(String host, String time, int replyCode, String line) throws FileNotFoundException, ParseException{
        
        SimpleDateFormat format = new SimpleDateFormat(TIMEFORMAT);
        Date date = format.parse(time);
        HostAttempt attempt = new HostAttempt();
        if(!attemptMap.containsKey(host)){
            attemptMap.put(host, attempt);
            attemptMap.get(host).addOneTime(date, replyCode);
        }
        else{
            attempt = attemptMap.get(host);
            if(attempt.beginBlocking()){
                if(attempt.blocked(date)){
                    writerBlocked.println(line);
                }
                else{
                    attempt.reset();
                    attempt.addOneTime(date, replyCode);
                }
            }
            else{
                attempt.addOneTime(date, replyCode);
            }
        }
    }
    
}
