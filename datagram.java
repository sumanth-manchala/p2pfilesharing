import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.*;
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
    public void findFile(String name,File file)
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
                receive.path = fil.getPath();
            }
        }
    }
}
class receive extends Thread
{
    DatagramSocket dss;
    static String requested,path;

    receive() throws SocketException
    {
        dss = new DatagramSocket(3333);
        requested = "";
        path= null;
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
                    if(msg.equalsIgnoreCase("connect"))
                    {
                        //StringTokenizer str = new StringTokenizer(msg," ");
                        //int port = Integer.parseInt(str.nextToken());
                        Thread.sleep(1000);
                        Socket s = new Socket(dp.getAddress().getHostAddress(),2222);
                        File transferFile = new File (path); 
                        byte [] bytearray = new byte [(int)transferFile.length()]; 
                        FileInputStream fin = new FileInputStream(transferFile); 
                        BufferedInputStream bin = new BufferedInputStream(fin); 
                        bin.read(bytearray,0,bytearray.length); 
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        System.out.println("Sending Files..."); 
                        dout.write(bytearray,0,bytearray.length); 
                        dout.flush(); 
                        s.close();
                        System.out.println("File transfer complete");
                    }
                    else if((msg.startsWith("File found")||msg.equalsIgnoreCase("File not found")))
                    {  
                       System.out.println(msg);
                       if(msg.startsWith("File found"))
                       {
                           send.ips.add(dp.getAddress());
                       }
                    }
                    else
                    {
                        path = null;
                        System.out.println("Requested file name is : "+dp.getAddress().getHostAddress()+" "+msg);
                        FileSearch fs = new FileSearch();
                        String respone;
                        requested = msg;
                        fs.findFile(msg,new File("/home/sumanth/Documents"));
                        if(path!=null)
                        {
                            respone = "File found at " + path;
                        }
                        else
                        {
                            respone = "File not found";
                        } 
                        dss.send(new DatagramPacket(respone.getBytes(),respone.length(), dp.getAddress(), 3333));

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
    static Vector<InetAddress> ips = new Vector<InetAddress>();
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
                ips.clear();
                ds.send(dp);
                Thread.sleep(4000);
                System.out.println("InetAddresses" +ips);
                System.out.println("Select one of the IP's");
                System.out.println("*************0 1 2 3........*****************");
                for( InetAddress i : ips)
                {
                    System.out.println(i.getHostAddress());
                }
                int choice = sc.nextInt();
                ServerSocket ss = new ServerSocket(2222);
                //String port = Integer.toString(ss.getLocalPort());
                String port = "connect";
                dp = new DatagramPacket(port.getBytes(),port.length(),InetAddress.getByName(ips.get(choice).getHostAddress()),3333);
                ds.send(dp);
                Socket s = ss.accept();
                int filesize=1022386; 
                int bytesRead; 
                int currentTot = 0; 
                Socket socket = new Socket("127.0.0.1",15123); 
                byte [] bytearray = new byte [filesize]; 
                InputStream is = socket.getInputStream(); 
                FileOutputStream fos = new FileOutputStream(msg); 
                BufferedOutputStream bos = new BufferedOutputStream(fos); 
                bytesRead = is.read(bytearray,0,bytearray.length); 
                currentTot = bytesRead; 
                do 
                { 
                    bytesRead = is.read(bytearray, currentTot, (bytearray.length-currentTot)); 
                    if(bytesRead >= 0) 
                    currentTot += bytesRead; 
                } while(bytesRead > -1); 
                bos.write(bytearray, 0 , currentTot); 
                bos.flush(); bos.close(); s.close();
                s.close();
                ss.close();

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