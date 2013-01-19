/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

/**
 *
 * @author kimthuatnguyen
 */
public class MachineVirtuelle {
    private String imageName;
    private  String kernelName;
    private  String keyName;
    private  String accesskey;
    private  String secretAccesskey;
    private  String eucalyptusHost;
    private  String eucalyptusPort;
    private  String eucalyptusService;
   
    public MachineVirtuelle(String imageName, String kernelName, String keyName, String accesskey, String secretAccesskey, String eucalyptusHost, String eucalyptusPort, String eucalyptusService) {
        this.imageName = imageName;
        this.kernelName = kernelName;
        this.keyName = keyName;
        this.accesskey = accesskey;
        this.secretAccesskey = secretAccesskey;
        this.eucalyptusHost = eucalyptusHost;
        this.eucalyptusPort = eucalyptusPort;
        this.eucalyptusService = eucalyptusService;
    }

    
    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getEucalyptusHost() {
        return eucalyptusHost;
    }

    public void setEucalyptusHost(String eucalyptusHost) {
        this.eucalyptusHost = eucalyptusHost;
    }

    public String getEucalyptusPort() {
        return eucalyptusPort;
    }

    public void setEucalyptusPort(String eucalyptusPort) {
        this.eucalyptusPort = eucalyptusPort;
    }

    public String getEucalyptusService() {
        return eucalyptusService;
    }

    public void setEucalyptusService(String eucalyptusService) {
        this.eucalyptusService = eucalyptusService;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getKernelName() {
        return kernelName;
    }

    public void setKernelName(String kernelName) {
        this.kernelName = kernelName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getSecretAccesskey() {
        return secretAccesskey;
    }

    public void setSecretAccesskey(String secretAccesskey) {
        this.secretAccesskey = secretAccesskey;
    }

}