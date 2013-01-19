/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author nguyenki
 */
public class ParseInstance {
    
    private String pathFile;
    private String masterPath;
    
    public ParseInstance() {
        this.pathFile = System.getProperty("user.dir")+"/runningInstances.txt";
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    
    public List<String> getInstancesRunning() {
        List<String> list = new ArrayList<String>();
        try {
                Scanner scan = new Scanner(new File(getPathFile()));
                while (scan.hasNextLine()) {
                String instanceName = scan.nextLine();
                list.add(instanceName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FILE RUNNING INSTANCES NOT FOUND");
        }
        return list;
    }
    
     
    public void addNewInstanceRunning(String newInstance)  {
        try {
            List<String> instances = getInstancesRunning();
            instances.add(newInstance);
            writeInstanceToFile(instances);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("FILE RUNNING INSTANCES NOT FOUND");
        }
    }
            
    public void removeInstance(String instance) {
        List<String> list = getInstancesRunning();
        if (list.contains(instance)) {
            list.remove(instance);
        }
        deleteFile();
        writeInstanceToFile(list);
    }
    

    
    public void deleteFile() {
        try {
            File file = new File(getPathFile());
            if(file.delete()) {
             System.out.println("Running instance file is deleted");   
            } else {
                System.out.println("Can not delete instance file");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void writeInstanceToFile(List<String> instances) {
        try {
            PrintWriter print = new PrintWriter(new File(getPathFile()));
            Iterator<String> it = instances.iterator();
            while (it.hasNext()) {
                String inst = it.next();
                print.println(inst);
            }
            print.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can not write instance id to file");
        }
    }
    
   public static void main(String args[]) {
        ParseInstance parse = new ParseInstance();
        parse.removeInstance("i-8D7745541");
    }
}
