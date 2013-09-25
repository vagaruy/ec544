/*
 * SunSpotApplication.java
 *
 * Created on Sep 12, 2013 2:26:23 PM;
 */

package org.sunspotworld;
import com.sun.spot.peripheral.IBattery;
import com.sun.spot.peripheral.Spot;
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
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;



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
    long read_temp_time=10000;
    long server_listen_time=1000;
    long send_temp_time=10000;
    double temp;
    DatagramConnection dgConnection ;
    Datagram dg ;
    String ADDR="broadcast";
    String type="radiogram";
    String port="37";
    ITriColorLEDArray leds;
    Date date;
    public void flash_sending(ITriColorLEDArray leds)
    {
        if(leds!=null)
        {
        ITriColorLED led=leds.getLED(1);
        led.setColor(LEDColor.RED);
        led.setOn(true);
        Utils.sleep(500);
        led.setOn(false);
        }
        else
        {
            System.out.println("LEDS cannot be found");
        }
        
    }
    public void temp_bit(int temp,ITriColorLEDArray leds)//supports temp 0 to 64C good enough I guess..
    {
        if(leds!=null)
        {
            LEDColor color;
            if(temp>=0 && temp<64)
            {
                if(temp>40)
                  color=LEDColor.RED;
                else if(temp>20)
                    color=LEDColor.YELLOW;
                else
                    color=LEDColor.BLUE;
                int counter=7;
                leds.setOff();
                while(temp>0)
                {
                    ITriColorLED led=leds.getLED(counter);
                    if(temp%2==0)
                    {
                        led.setOff();
                    }
                    else
                    {
                        led.setColor(color);
                        led.setOn();
                    }
                    temp=temp/2;
                    counter--;
                }
            }
        
            }       
        }
    
    public void battery_indicator(ITriColorLEDArray leds)//normalise the values from 0-100
    {
        IBattery battery = Spot.getInstance().getPowerController().getBattery();
        int bat = battery.getBatteryLevel();
        System.out.println("Battery level is"+bat);
        System.out.println("MAx Capacity is "+battery.getMaximumCapacity() );
        System.out.println("Available  Capacity is "+battery.getAvailableCapacity() );
        if(leds!=null)
        {
        ITriColorLED led=leds.getLED(0);
        if(bat>66)
        {
            led.setColor(LEDColor.GREEN);
        }
        else if(bat>33)
        {
            led.setColor(LEDColor.ORANGE);
        }
        else
        {
            led.setColor(LEDColor.RED);
        }
        led.setOn(true);
        }
    }
    
    public void sendData(RadioData data,DatagramConnection dgConnection,Datagram dg ) throws IOException
    {
        //System.out.println("data from isnide sendData is "+data.command);
        dg.reset();
        dg.writeUTF(data.command);
        dg.writeFloat(data.temp);
        dg.writeLong(data.timestamp);
        dgConnection.send(dg);
        
    }
    
    public RadioData recieveData(DatagramConnection dgConnection,Datagram dg)throws IOException
    {
        RadioData packet=new RadioData();
        dgConnection.receive(dg);
        //if(dg!=NULL)
        packet.command=dg.readUTF();
        packet.temp=dg.readFloat();
        packet.timestamp=dg.readLong();
        dg.reset();
        return packet;        
    }
    
    public long getTimeStamp(Date date)
    {
        return date.getTime();
    }
    
    public void setTimeStamp(long time,Date date)
    {
       date.setTime(time);
    }
    
   public class RadioCommunication {
    DatagramConnection dgConnection ;
    Datagram dg ;
    String ADDR="broadcast";
    String type="radiogram";
    String port="37";    
    
    
    
    void RadioCommunication()
    {
        try {
                    // The Connection is a broadcast so we specify it in the creation string
                    dgConnection = (DatagramConnection) Connector.open("radiogram://broadcast:37");
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                    //System.out.println(dg);
        } catch (IOException ex) {
                    System.out.println("Could not open radiogram broadcast connection");
                    return;
                }
    }
    public void sendData(RadioData data) throws IOException
    {
        //System.out.println("data from isnide sendData is "+data.command);
        dg.reset();
        dg.writeUTF(data.command);
        dg.writeFloat(data.temp);
        dg.writeLong(data.timestamp);
        dgConnection.send(dg);
        
    }
    
    public RadioData recieveData()throws IOException
    {
        RadioData packet=new RadioData();;
        packet.command=dg.readUTF();
        packet.temp=dg.readFloat();
        packet.timestamp=dg.readLong();
        dg.reset();
        return packet;        
    }
}

    
    
    void SunSpotApplication()
    {
        
        
    }
    
    
    Task send_temperature = new Task( send_temp_time){
        
        public void doTask() throws IOException{
            RadioData data=new RadioData();
            //RadioCommunication radio=new RadioCommunication();
            //TimeKeeping time=new TimeKeeping();
            
            data.temp=(float) find_temp();
            data.command="READING";
            data.timestamp=8878876;//time.getTimeStamp();
            System.out.println("Temperature is"+data.temp+"/n Command is"+data.command+"/n Timestamp is"+data.timestamp);
            
            
            flash_sending(leds);
            temp_bit((int)data.temp,leds);
            battery_indicator(leds);
            sendData(data,dgConnection,dg);
            
        }
        
    };
    
     Task listen_server=new Task(server_listen_time)
    {
       //String task;
            public void doTask() throws IOException{
            RadioData data=new RadioData();
            data=recieveData(dgConnection,dg);
            System.out.println("recieved packet is "+data.command+data.temp+data.timestamp);
            if(data.command.compareTo("TIME")==0)
            {
                setTimeStamp(data.timestamp,date);
            }
            else if (data.command.compareTo("SEND_READING")==0)
            {
                data.command="READING";
                data.temp=(float) find_temp();
                data.timestamp=getTimeStamp(date);
                sendData(data,dgConnection,dg);
            }
           
       }
    };
    
    double find_temp() throws IOException
    {
        ITemperatureInput therm = (ITemperatureInput) Resources.lookup(ITemperatureInput.class);
        System.out.println("Temperature is"+therm.getCelsius());
        return therm.getCelsius();
    }
    

    protected void startApp() throws MIDletStateChangeException {
        try {           
                   
            //Monitoring commands from the server
            BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
            
            //Get the MAC address from the radio....save it in a long
            long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
           
                    // The Connection is a broadcast so we specify it in the creation string
                    dgConnection = (DatagramConnection) Connector.open("radiogram://broadcast:37");
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
                    //System.out.println(dg);
        
             leds=(ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
             date=new Date();
            send_temperature.start();
            listen_server.start();
           
           // listen_server.start();
        } catch (IOException ex) {
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
