package org.sunspotworld;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vipul
 */
import java.util.Date;
public class TimeKeeping {
    Date date;
    void TimeKeeping()
    {
        date=new Date(19999939);
    }
    
    public long getTimeStamp()
    {
        return date.getTime();
    }
    
    public void setTimeStamp(long time)
    {
       date.setTime(time);
    }
    
}
