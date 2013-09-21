package org;

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
    void TimeKeeping(long time)
    {
        date=new Date(time);
    }
    
    long getTimeStamp()
    {
        return date.getTime();
    }
    
}
