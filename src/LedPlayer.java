


import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.util.Utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vipul
 */
public class LedPlayer {
    
    ITriColorLEDArray leds=null;
    ITriColorLED led=null;
    
      
    void LedPlayer()
    {
       leds=(ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
       
           
    }
    int check_led()
    {   
        if(leds!=null)
            return 1;
        else
            return 0;
    }
    public void flash_sending()
    {
        if(leds!=null)
        {
        led=leds.getLED(1);
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
    
    public void battery_indicator(int bat)//normalise the values from 0-100
    {
        if(leds!=null)
        {
        led=leds.getLED(0);
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
    
    public void temp_bit(int temp)//supports temp 0 to 64C good enough I guess..
    {
        if(leds!=null)
        {
            LEDColor color;
            if(temp>=0 && temp<=64)
            {
                if(temp>40)
                  color=LEDColor.RED;
                else if(temp>20)
                    color=LEDColor.YELLOW;
                else
                    color=LEDColor.BLUE;
                int counter=0;
                while(temp>0)
                {
                    led=leds.getLED(counter);
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
                    counter++;
                }
            }
        
            }       
        }
  
   
    
    
}
    
    

