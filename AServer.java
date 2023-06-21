import java.io.*;
import java.net.*;

public class AServer {
    static int PORT = 8080; // ポート番号をプログラムの引数で与える

    public static void main(String[] args) throws IOException {

        ServerSocket s = new ServerSocket(PORT); // ソケットを作成する
        // Scanner sc = new Scanner(System.in);

        try {
            while(true){
                Socket socket = s.accept();// コネクション設定要求を待つ
                AServerThread thread = new AServerThread(socket);//スレッド生成
                thread.start();//スレッド実行
            }

        } finally {
            s.close();
        }
    }
}
