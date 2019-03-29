import java.net.*;
import java.util.*;
class receive extends Thread
{
    DatagramSocket dss;
    receive() throws SocketException
    {
        dss = new DatagramSocket(3333);
    }

    @Override
    public void run() 
    {
        Vector <String> v = new Vector<>();
        try
        {
           
            //System.out.println("Your Host addr: " + InetAddress.getLocalHost().getHostAddress());  
            // often returns "127.0.0.1"
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements();)
            {
                NetworkInterface e = n.nextElement();
        
                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements();)
                {
                    InetAddress addr = a.nextElement();
                    v.add(addr.getHostAddress());
                    System.out.println("  " + addr.getHostAddress());
                }
            }
            
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        
        while(true)
        {
            try
            {
                byte[] bf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(bf,1024);
                dss.receive(dp);
                if(!v.contains(dp.getAddress().getHostAddress()))
                {
                    String msg = new String(dp.getData(),0,dp.getLength());
                    System.out.println("Message from "+dp.getAddress().getHostAddress()+" "+msg);
                }
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }
       
    }

}
class send extends Thread
{
    Scanner sc;
    DatagramSocket ds;
    send() throws Exception
    {
        ds = new DatagramSocket();
        sc = new Scanner(System.in);
    }
    @Override
    public void run() 
    {
        /*try
        {
            DatagramSocket ds = new DatagramSocket();
            Scanner sc = new Scanner(System.in);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }*/
       
        while(true)
        {
            String msg = sc.nextLine();
            try
            {
                DatagramPacket dp = new DatagramPacket(msg.getBytes(),msg.length(),InetAddress.getByName("172.30.105.255"),3333);
                ds.send(dp);
               // ds.close();
            }
            catch(Exception e)
            {
                System.out.println(e);
            }
        }    
    }
}
public class datagram
{
    public static void main(String[] args) throws Exception
    {
            
        send s = new send();
        receive r = new receive();

        s.start();
        r.start();
    }
}