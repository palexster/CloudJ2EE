/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import com.amazonaws.services.simpleemail.model.GetSendQuotaRequest;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.InstanceType;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;
import config.ConfigVM;
import config.ParseInstance;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author nguyenki
 */
public class Manager {
    
    private double fixedThreshold; // A fixed threshold for the charge of each virtual machine
    private double percentMax; // The percent that the charge of a virtual machine can not exceed
    private double sondeDelaiTime; // The argument for the delai time that we use when executing the sonde.c
    private long sleepTime; // Time that the manager have to wait for the sonde to get the CPU perfomance result
    private String logsFileName; // File name to save the result from sondePC
    
    private ParseInstance parse;
    
    public Manager(double fixedThreshold, double percentMax, double sondeDelaiTime, long sleepTime, String logsFileName) {
        this.fixedThreshold = fixedThreshold;
        this.percentMax = percentMax;
        this.sondeDelaiTime = sondeDelaiTime;
        this.sleepTime = sleepTime;
        this.logsFileName = logsFileName;
        this.parse = new ParseInstance();
    }

    public Manager() {
            
    }

 
      
    public double getFixedThreshold() {
        return fixedThreshold;
    }

    public void setFixedThreshold(double fixedThreshold) {
        this.fixedThreshold = fixedThreshold;
    }

    public String getLogsFileName() {
        return logsFileName;
    }

    public void setLogsFileName(String logsFileName) {
        this.logsFileName = logsFileName;
    }

    public double getPercentMax() {
        return percentMax;
    }

    public void setPercentMax(double percentMax) {
        this.percentMax = percentMax;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public double getSondeDelaiTime() {
        return sondeDelaiTime;
    }

    public void setSondeDelaiTime(double sondeDelaiTime) {
        this.sondeDelaiTime = sondeDelaiTime;
    }

    public boolean isMachineOverloaded() {
        if (getFixedThreshold()* getPercentMax()> Double.parseDouble(getChargeCPU())) {
            return false;
        }
        return true;
    }

    public ParseInstance getParse() {
        return parse;
    }

    public void setParse(ParseInstance parse) {
        this.parse = parse;
    }
    
    
    //TODO: A revoir
    public void executeCommand() {
        try {
            Runtime rt = Runtime.getRuntime();
            String logFileName = getLogsFileName();
            String sondeDetailTime = String.valueOf(getSondeDelaiTime());
            String commands = "./sondeCPU/sonde "+logFileName+" "+sondeDetailTime;
            Process proc = rt.exec(commands);
            double sl = getSleepTime()*100;
            
            Thread.sleep(getSleepTime());
            proc.destroy();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
    }
    
    public void addVMToMysqlProxy(String ipNewVM) {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "mysql-proxy --proxy-backend-addresses=localhost:3306 --proxy-backend-addresses="+ipNewVM+":3306 &";
            Process proc = rt.exec(command);
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("UNABLE TO LINK THE NEW VM ");
        }
    }
    
    public String getChargeCPU() {
        String chargeCPU = "UNDEFINED";
        try {
            String filePath = System.getProperty("user.dir")+"/"+getLogsFileName();
            FileInputStream fstream = new FileInputStream(filePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String lastLine = "", l = "";
            while ((l = br.readLine()) != null) {
                System.out.println(l);
                if (l!=null) {
                    lastLine = l;
                }
            }
            in.close();
            Scanner sc = new Scanner(lastLine);
            sc.useDelimiter(" ");
            List<String> info = new ArrayList<String>();
            while (sc.hasNext()) {
                info.add(sc.next());
            }
            if (!info.isEmpty()) {
                chargeCPU = info.get(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        debug(chargeCPU);
        return chargeCPU;
    }
    
    public void debug(String v) {
        System.out.println(v);
    }
    
    public void debug(double v) {
        System.out.println(v);
    }
    
    
    public void stopAnImage(MachineVirtuelle m, String instanceID) {
       try {
        Jec2 ec2 = new Jec2(m.getAccesskey(),m.getSecretAccesskey(), false, m.getEucalyptusHost(), Integer.parseInt(m.getEucalyptusPort())); 
        ec2.setResourcePrefix(m.getEucalyptusService());
        ec2.setSignatureVersion(1);
        String[] instances = new String[] {instanceID};
        ec2.terminateInstances(instances);
        this.parse.removeInstance(instanceID);
       } catch (Exception e) {
           e.printStackTrace();
       }  
    }
        
    public void startImage(MachineVirtuelle m) {
        try {
            Jec2 ec2 = new Jec2(m.getAccesskey(),m.getSecretAccesskey(), false, m.getEucalyptusHost(), Integer.parseInt(m.getEucalyptusPort())); 
	    ec2.setResourcePrefix(m.getEucalyptusService());
            ec2.setSignatureVersion(1);
            LaunchConfiguration launchConfig = new com.xerox.amazonws.ec2.LaunchConfiguration(m.getImageName());
            launchConfig.setKeyName(m.getKeyName());
            launchConfig.setMinCount(1);
            launchConfig.setMaxCount(1);
            launchConfig.setKernelId(m.getKernelName());
	    launchConfig.setInstanceType(InstanceType.getTypeFromString("c1.medium"));
            
            ReservationDescription reservationDescription = ec2.runInstances(launchConfig);
            Instance instance = reservationDescription.getInstances().get(0);
            
            String[] instances = new String[] {instance.getInstanceId()};
            
            do {
         	instance = ec2.describeInstances(instances).get(0).getInstances().get(0);				
		System.out.println("Run: Instance ID = " + instance.getInstanceId() + ", State = " + instance.getState()+", IP = "+instance.getIpAddress());				
		Thread.sleep(5000);
            } while (!instance.isRunning());			
		System.out.println("Run: Instance ID = " + instance.getInstanceId() + ", Public DNS Name = " + instance.getDnsName() + ", Private DNS Name = " + instance.getPrivateDnsName());			
                this.parse.addNewInstanceRunning(instances[0]);
                System.out.println("Adding link from mysql-proxy to the new VM................");
                addVMToMysqlProxy(instance.getIpAddress());
	} catch (EC2Exception ex) {
            ex.printStackTrace();
        } 
        catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
	}
    }
    
    public void scheduler(Manager manager) {
        ConfigVM config = new ConfigVM();
        MachineVirtuelle vm1 = config.getVM("vm2");
        manager.executeCommand();
        if (isMachineOverloaded()) {
            System.out.println("Machine is overloaded");
            System.out.println("Starting new instance................");
            manager.startImage(vm1);
        } else {
            System.out.println("All machines is working well");
            System.out.println("Checking if we can turn of a VM to save the resouces");
            List<String> runningM = manager.getParse().getInstancesRunning();
            
            int nbVmRunning = manager.getParse().getInstancesRunning().size();
            if (nbVmRunning>0) {
                manager.executeCommand();
                double chargeVm = Double.valueOf(manager.getChargeCPU());
                if (chargeVm*(nbVmRunning+1)/nbVmRunning < manager.getFixedThreshold()) {
                    String removeInstance = runningM.get(0);
                    stopAnImage(vm1, removeInstance);
                    manager.getParse().removeInstance(removeInstance);
                    System.out.println("Turning of an instance with ID="+removeInstance);
                }
            } else {
                System.out.println("Only the master VM is running.....");
                System.out.println("No need to start new VM");
            }
        }
    }
    
    public static void main(String[] args){
        if (args.length==3) {
           Double percentmax = Double.valueOf(args[0]);
           Double sondeTime = Double.valueOf(args[1]);
           String logFileName = args[2];
           Manager manager = new Manager(100, percentmax, sondeTime, 1500, logFileName);
           manager.scheduler(manager);
       } else {
           System.out.println("Usage manager percentMax sondeTime logfile");
       }
        
//        Manager instance = new Manager(100,0.1,10,1500,"logsVM");
//       // instance.scheduler(instance);  
//        
//        
//        ConfigVM config = new ConfigVM();
//        MachineVirtuelle vm1 = config.getVM("vm1");
//        instance.startImage(vm1);
//        
    }
}


