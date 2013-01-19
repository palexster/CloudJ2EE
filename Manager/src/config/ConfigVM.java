/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.FileInputStream;
import java.util.Properties;
import manager.MachineVirtuelle;

/**
 *
 * @author nguyenki
 */
public class ConfigVM {
    Properties prop;
    Properties prInst;
    
    public ConfigVM() {
        this.prop = new Properties();
    }
    
	
    public MachineVirtuelle getVM(String vmName) {
        MachineVirtuelle m = null;
        try {
            this.prop.load(new FileInputStream(System.getProperty("user.dir")+"//VM//"+vmName+".properties"));
            m = new MachineVirtuelle(getImageName(), getKernelName(),getkeyName(),getAccesskey(), getSecretAccesskey(), getEcalyptusHost(),getEucalyptusPort(), getEucalyptusService());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("FILE NOT FOUND");
        }
        return m;
    }
    
    public ConfigVM(Properties pr) {
        this.prop = pr;
    }
    
    public String getImageName() {
        return this.prop.getProperty("imageName");
    }
    
    public String getKernelName() {
        return this.prop.getProperty("kernelName");
    }
    
    public String getkeyName() {
        return this.prop.getProperty("keyName");
    }
    
    public String getAccesskey() {
        return this.prop.getProperty("accesskey");
    }
    
    public String getSecretAccesskey() {
        return this.prop.getProperty("secretAccesskey");
    }
    
    public String getEcalyptusHost() {
        return this.prop.getProperty("eucalyptusHost");
    }
    
    public String getEucalyptusPort() {
        return this.prop.getProperty("eucalyptusPort");
    }
    
    public String getEucalyptusService() {
        return this.prop.getProperty("eucalyptusService");
    }
    
}
