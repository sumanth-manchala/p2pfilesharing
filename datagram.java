import java.io.File;
import java.net.*;
import java.util.*;

class MyIpAddress 
{
    Vector <String> ip ;
    MyIpAddress()
    {
        ip = new Vector<>();
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
                    ip.add(addr.getHostAddress());
                    System.out.println("  " + addr.getHostAddress());
                }
            }
            
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

    }

}
class FileSearch
{
    //Vector <File> files = new Vector<>();
    /*String filename;
    FileSearch(String filename)
    {
        this.filename = filename;
    }
    public void search()
    {
        File dir = new File("/home/sumanth/Documents");
        FilenameFilter filter = new FilenameFilter() 
        {
           public boolean accept (File dir, String name) 
           { 
              return name.startsWith(this.filename);
           } 
        }; 
        String[] children = dir.list(filter);
        if (children == null) 
        {
           System.out.println("Either dir does not exist or is not a directory"); 
        } 
        else 
        { 
           for (int i = 0; i< children.length; i++) 
           {
              String filename = children[i];
              System.out.println(filename);
           } 
        } 
 
    }*/
    public Boolean findFile(String name,File file)
    {
        File[] list = file.listFiles();
        if(list!=null)
        for (File fil : list)
        {
            if (fil.isDirectory())
            {
                findFile(name,fil);
            }
            else if (name.equalsIgnoreCase(fil.getName()))
            {
                //System.out.println(fil.getParentFile());
                //this.files.add(fil.getParentFile());
                return true;
            }
        }
        return false;
    }
}
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
        MyIpAddress myIpAddress = new MyIpAddress();
        
        while(true)
        {
            try
            {
                byte[] bf = new byte[1024];
                DatagramPacket dp = new DatagramPacket(bf,1024);
                dss.receive(dp);
                if(!myIpAddress.ip.contains(dp.getAddress().getHostAddress()))
                {
                    String msg = new String(dp.getData(),0,dp.getLength());
                    if(!(msg.equalsIgnoreCase("file found")||msg.equalsIgnoreCase("file not found")))
                    {
                        System.out.println("Requested file name is : "+dp.getAddress().getHostAddress()+" "+msg);
                        FileSearch fs = new FileSearch();
                        String respone;
                        if(fs.findFile(msg,new File("/home/sumanth/Documents")))
                        {
                            respone = "File found";
                        }
                        else
                        {
                            respone = "File not found";
                        } 
                        dss.send(new DatagramPacket(respone.getBytes(),respone.length(), dp.getAddress(), 3333));

                    }
                    else
                    {
                        System.out.println(msg);
                    }
                   
                    
                    //dss.send(new Datagra);
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