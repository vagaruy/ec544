/*
 * SunSpotApplication.java
 *
 * Created on Sep 12, 2013 2:26:23 PM;
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITemperatureInput;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.service.Task;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import java.util.Date;



import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.LedPlayer;
import org.RadioCommunication;
import org.RadioData;
import org.TimeKeeping;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class SunSpotApplication extends MIDlet {
    //to access and control the array of 3 color LEDs
    long read_temp_time;
    long server_listen_time;
    long send_temp_time;
    RadioCommunication radio;
    TimeKeeping time;
    LedPlayer led;
    RadioData data;
    double temp;
    void SunSpotApplication()
    {
        radio=new RadioCommunication();
        time =new TimeKeeping();
        led=new LedPlayer();
        data=new RadioData();
    }
    
    
    Task send_temperature = new Task( read_temp_time){
        
        public void doTask() throws IOException{
            data.temp=(float) find_temp();
            data.command="READING";
            data.timestamp=time.getTimeStamp();
            radio.sendData(data);
            led.temp_bit((int) data.temp);
            led.flash_sending();
            
        }
        
    };
    
    Task listen_server=new Task(server_listen_time)
    {
       //String task;
            public void doTask() throws IOException{
            data=radio.recieveData();
            if(data.command.compareTo("TIME")==0)
            {
                time.setTimeStamp(data.timestamp);
            }
            else if (data.command.compareTo("SEND_READING")==0)
            {
                data.command="READING";
                data.temp=(float) find_temp();
                data.timestamp=time.getTimeStamp();
                radio.sendData(data);
            }
           
       }
    };
    
    double find_temp() throws IOException
    {
        ITemperatureInput therm = (ITemperatureInput) Resources.lookup(ITemperatureInput.class);
        return therm.getCelsius();
    }
    

    protected void startApp() throws MIDletStateChangeException {
           
                       
            //Monitoring commands from the server
            BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
            
            //Get the MAC address from the radio....save it in a long
            long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
            send_temperature.start();
            listen_server.start();
    
    
    }
        //notifyDestroyed();                      // cause the MIDlet to exit
    protected void pauseApp() {
        // This is not currently called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
}
