
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketTest
{

    public static void main(String[] args) throws IOException
    {
        Socket socket = new Socket("mail.gmx.de", 25);
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        byte[] buf = new byte[65536];
        int c = in.read(buf);
        System.out.println(new String(buf,0,c));
        out.write("HELO a\r\n".getBytes());
        out.flush();
        c = in.read(buf); 
	if(c < 0) throw new RuntimeException("Answer should be '250 OK'");
    }
}
