import java.io.*;
import java.net.*;

class FileServerThread extends Thread {
    Socket socket;

    public FileServerThread(Socket s) {
        super();
        socket = s;
    }

    public void run() {
        try {
            // クライアントからのリクエストを受け付け
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String fileName = in.readLine();
            //System.out.println("Requested file: " + fileName);
            if (fileName == null) {
                //System.out.println("File name is null.");
                return;
            }
            // ファイルが存在しない場合は終了
            File file = new File(fileName);
            if (!file.exists()) {
                //System.out.println("File does not exist: " + fileName);
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            fileInputStream.close();
            byteArrayOutputStream.close();
            byte[] fileContent = byteArrayOutputStream.toByteArray();

            // ファイルの内容をクライアントに送信
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();
        } catch ( NumberFormatException e ) { // clientが接続を切った場合
            System.out.println(Thread.currentThread().getName() + "が切断されました");
            System.out.println("closing...");
        } catch (SocketException e) { //java.net.SocketException: Connection reset
            System.out.println(Thread.currentThread().getName() + "が切断されました");
            System.out.println("closing...");
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}

public class FileServer {
    static int PORT = 8070;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        try{
            while (true) {
                Socket socket = s.accept();
                FileServerThread t = new FileServerThread(socket);
                t.start();
            }
        } finally {
            s.close();
        }
    }
}