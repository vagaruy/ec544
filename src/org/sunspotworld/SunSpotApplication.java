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
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import java.util.Date;



import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 * 
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
public class SunSpotApplication extends MIDlet {
    //to access and control the array of 3 color LEDs
    

    protected void startApp() throws MIDletStateChangeException {
        try {        
            //Print to the screen
            //System.out.println("Hello, world");
            
            //Monitoring commands from the server
            BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
            
            //Get the MAC address from the radio....save it in a long
            long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
            ITemperatureInput therm = (ITemperatureInput) Resources.lookup(ITemperatureInput.class);
             System.out.println(therm.getDescription());
             
                double temp= therm.getCelsius();
                
               
				
                             // wait 1 second
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
