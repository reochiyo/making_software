import java.io.*;
import java.net.*;

public class FileClient {

    private final int PORT = 8070;
    private InetAddress addr;
    private Socket socket = null;

    public FileClient() {
        try {
            this.addr = InetAddress.getByName("localhost"); // IP アドレスへの変換
            this.socket = new Socket(addr, PORT); // ソケットの生成
        } catch (IOException e) {
            System.out.println(e);
        }
        // socketがnullなら待つ
        while ( this.socket == null ) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //System.out.println(e);
            }
        }
    }

    public void request(String fileName,String username) throws IOException {
        // サーバーにファイルの内容をリクエストして受信
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        out.println(fileName);

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        byte[] fileContent = byteArrayOutputStream.toByteArray();

        // 受信したファイルの内容をファイルに保存
        // client/user_name/file_nameに保存
        File client = new File("client");
        if (!client.exists()) {
            client.mkdirs();
        }
        File dir = new File("client/" + username);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        fileName = "client/" + username + "/" + fileName;
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        fileOutputStream.write(fileContent);
        fileOutputStream.close();
    }
}
