/**
* An ssh client, that can be controlled using jython through an API.
*
* Created by bmcculley, 4/22/2016
* Site: http://mkdir.info
* Email: zenfed.96 AT gmail com
*/
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import java.io.InputStream;
import java.util.Scanner;
import java.io.Console;
import java.util.Properties;
import org.python.util.PythonInterpreter;

public class cmdExec
{
    JSch jsch = new JSch();
    
    private String user = null;
    private String host = null;
    private String paswrd = null;
    private Integer port = 22;
    private Session session = null;
    private Channel channel = null;
    
    private String command = null;
    private String ret = null;
    
    /**
     * Constructor method, currently assigns values for login
     * credentials
     *
     * @param user String value of the ssh username to login to the server.
     * @param host String value of the server to connect to.
     * @param paswrd String value of the password to connect to the server.
     *
     */
    public cmdExec(String user, String host, String paswrd)
    {
        this.user = user;
        this.host = host;
        this.paswrd = paswrd;
    }
    
    /**
     * Open the connection to the channel.
     * <p>
     * Maybe this should return something, so it's known if the connection
     * was successful or not.
     */
    public void connectSession()
    {
        try
        {
            // setup the session and channel
            session = jsch.getSession(user, host, port);
            
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            
            session.setPassword(paswrd);
            
            session.setConfig(config);
            session.connect();
        }
        catch (Exception e)
        {
            System.out.println("Exception: "+e);
        }
    }
    
    /**
     * Close the connection to the channel.
     *
     */
    public void closeSession()
    {
        channel.disconnect();
        session.disconnect();
    }
    
    /**
     * Sets the port to something other than default if necessary.
     *
     * @param port Integer value of port to connect to on the server.
     *
     */
    public void setPort(Integer port)
    {
        this.port = port;
    }
    
    /**
     * Retrieve command through manual user input
     *
     */
    public void getCommand()
    {
        Scanner cmdIn = new Scanner(System.in);
        command = cmdIn.nextLine();
    }
    
    /**
     * Set the command to run pragmatically
     *
     * @param command String value of the command that should be sent to
     *                the server
     *
     */
    public void setCommand(String command)
    {
        this.command = command;
    }
    
    /**
     * Retrieve the return value of the last command.
     *
     * @return String access the return value of the last command ran.
     *
     */
    public String getReturnVal()
    {
        return ret;
    }
    
    /**
     * A method to run the commands
     *
     */
    public void runCommand()
    {
        try
        {
            // clear the previous return value if any
            ret = "";
            
            channel = session.openChannel("exec");
            
            ((ChannelExec)channel).setCommand(command);
            
            InputStream in = this.channel.getInputStream();
            
            channel.setInputStream(null);
            
            ((ChannelExec)channel).setErrStream(System.err);
            
            
            channel.connect();
            byte[] tmp = new byte[1024];
            // need a better way to get the return from the server
            while (true)
            {
                
                while (in.available() > 0)
                {
                    
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                    {
                        break;
                    }
                    // grab the return, hopefully a number
                    //String strRet = new String(tmp, 0, i);
                    // trim whitespace and convert to int
                    //ret = Integer.parseInt(strRet.trim());
                    ret = new String(tmp, 0, i);
                }
                if(channel.isClosed())
                {
                    if(in.available()>0) continue;
                    //System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception ee)
                {
                    // Do something with the exception
                }
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    /**
     * <p>
     * The main method of our class, so we can run the whole shebang.</p>
     * <strong>Usage:</strong>
     * <p>
     * Currently the only possibility is running jython scripts. Future support
     * of jruby scripts is planned. Although, it should be fairly easy to add 
     * other scripting languages.</p>
     * <p>
     * -s Pass a jython script for the program to execute.</p>
     * <p>
     * It's possible to start directly from the command line by passing various 
     * parameters. </p>
     * <ul>
     * <li>-h host: pass as an ip or host name.</li>
     * <li>-port port: pass this if using something different then the standard 
     * port (22).</li>
     * <li>-u user: pass the user name to connect to the host as.</li>
     * <li>-p password: pass the password used to log into the host.</li> </ul>
     * <p>
     * <strong>An example connection:</strong></p>
     * <pre>java -jar sshClient.jar -h example.com -u user</pre>
     * <p>
     * As you can see the password parameter was left off. This can be done for 
     * added security, as it will prompt you for the password later.</p>
     *
     * @param args accepts a string array of possible arguments.
     *
     */
    public static void main(String[] args)
    {
        try
        {
            Boolean runScript = false;
            String script = null;
            String host = null;
            String user = null;
            String paswrd = null;
            Integer port = null;
            
            // use command line args to determine if a script should be run or
            // if the program should run manually
            for (int ac = 0; ac < args.length; ac++)
            {
                if (args[ac].equals("-s"))
                {
                    script = args[ac+1];
                    runScript = true;
                }
                else if (args[ac].equals("-u"))
                {
                    user = args[ac+1];
                }
                else if (args[ac].equals("-h"))
                {
                    host = args[ac+1];
                }
                else if (args[ac].equals("-p"))
                {
                    paswrd = args[ac+1];
                }
                else if (args[ac].equals("-port"))
                {
                    port = Integer.parseInt(args[ac+1]);
                }
                else if (args[ac].equals("-help"))
                {
                    System.out.println("Usage:\n\nRun a Script:\n\tsshClient.jar -s <path/to/script.py>\n");
                    System.out.println("Arguments:\n\t-u username\n\t-h hostname\n\t-p password");
                    System.exit(0);
                }
            }
            if (runScript)
            {
                PythonInterpreter interp = new PythonInterpreter();
                interp.exec("import cmdExec");
                interp.execfile(script);
            }
            else
            {
                if (user == null || user.isEmpty() || host == null || host.isEmpty())
                {
                    System.out.println("Enter user@host:");
                    Scanner scnUserHost = new Scanner(System.in);
                    host = scnUserHost.nextLine();
                    user = host.substring(0, host.indexOf('@'));
                    host = host.substring(host.indexOf('@')+1);
                }
                
                if (paswrd == null || paswrd.isEmpty())
                {
                    Console console = System.console();
                    char passwordArray[] = console.readPassword("Enter password: ");
                    paswrd = new String(passwordArray);
                }
                
                cmdExec ce = new cmdExec(user, host, paswrd);
                
                if (port != null)
                {
                    ce.setPort(port);
                }
                
                ce.connectSession();
                ce.getCommand();
                
                while (!ce.command.equals("exit"))
                {
                    ce.runCommand();
                    System.out.println(ce.getReturnVal());
                    ce.getCommand();
                }
                if (ce.command.equals("exit"))
                {
                    ce.closeSession();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
