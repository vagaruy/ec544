/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
/**
 *
 * @author Vipul
 */
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
                    dgConnection = (DatagramConnection) Connector.open(type+"://"+ADDR+":"+port);
                    // Then, we ask for a datagram with the maximum size allowed
                    dg = dgConnection.newDatagram(dgConnection.getMaximumLength());
        } catch (IOException ex) {
                    System.out.println("Could not open radiogram broadcast connection");
                    return;
                }
    }
    void sendData(RadioData data) throws IOException
    {
        dg.reset();
        dg.writeUTF(data.command);
        dg.writeFloat(data.temp);
        dg.writeLong(data.timestamp);
        dgConnection.send(dg);
        
    }
    
    RadioData recieveData()throws IOException
    {
        RadioData packet=null;
        packet.command=dg.readUTF();
        packet.temp=dg.readFloat();
        packet.timestamp=dg.readLong();
        dg.reset();
        return packet;
        
    }
}
