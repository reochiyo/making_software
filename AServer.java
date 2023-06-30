import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

//１つのクライアントと通信を行うスレッド
class AServerThread extends Serverfunc {
    Socket socket; //このクライアントに対応するソケット

    //コンストラクタ（使用するソケットを指定）
    public AServerThread(Socket s){
        super();
        socket = s;
    }

    //スレッドで実行される内容、start();を実行すると呼び出される
    public void run() {
        try {
            // データベースの設定
            File db = new File("database.db");
            if (!db.exists()) {
                try {
                        db.createNewFile();
                } catch (IOException e) {
                    System.out.println(e);
                }
            }

            // usernameとpasswordを保存するテーブルを作成
            create_table(db);
            // チャットルームの名前を保存するテーブルを作成
            create_table_chatname(db);

            /*---------------------------------------------------------------------------------------- */
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // データ受信用バッファの設定
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true); // 送信バッファ設定

            out.println("Serverと接続しました。"); // 1

            try{
                while(true){
                    /*---------------------------------------------------------------------------------------- */
                    // ログインとサインインの画面
                    Integer option = Integer.parseInt(in.readLine()); // 2

                    /*---------------------------------------------------------------------------------------- */
                    //ユーザー名とパスワードの画面
                    Boolean ok = Boolean.parseBoolean(in.readLine());// 3
                    String username = in.readLine(); // 4
                    String pass = in.readLine(); // 5
                    String hash_pass = make_hash(pass);

                    /*---------------------------------------------------------------------------------------- */
                    if (ok) {
                        if (option == 1) {//ログインしたとき
                            List<String> list_data = new ArrayList<String>();
                            list_data = login_user(db, username, hash_pass);
                            Boolean LOGIN = false;
                            if (list_data.size() == 1) {//ログイン成功のとき
                                LOGIN = true;
                                out.println(LOGIN); // 5

                                while (true) {
                                    Integer option2 = Integer.parseInt(in.readLine()); // 6
                                    File chat_log;

                                    /*---------------------------------------------------------------------------------------- */
                                    if (option2 == 1) {// 1.新規作成
                                        // チャットルームの名前を受信
                                        String room_name = in.readLine();// 7
                                        // room_nameの制限 \ / : * ? " < > |
                                        if (room_name.contains("\\") || room_name.contains("/") || room_name.contains(":")
                                                || room_name.contains("*") || room_name.contains("?")
                                                || room_name.contains("\"") || room_name.contains("<")
                                                || room_name.contains(">") || room_name.contains("|")) {
                                            out.println("チャットルーム名に \\ / : * ? \" < > | は使えません。");// 8
                                        } else {
                                            // チャットルームがあるかdbから探す
                                            List<String> list_data2 = new ArrayList<String>();
                                            list_data2 = check_chat_room(db, room_name);

                                            if (list_data2.size() == 1) {
                                                if (Integer.parseInt(list_data2.get(0)) == 1) {
                                                    out.println("'" + room_name + "'というチャットルームが存在します");// 8
                                                } else {
                                                    // チャットルームの名前をテーブルに追加
                                                    add_chatname(db, room_name);

                                                    chat_log = new File(room_name + "_chat_log.txt");
                                                    chat_log.createNewFile();
                                                    out.println("チャットルームを作成しました");// 8
                                                }
                                            } else {
                                                out.println("チャットルームを作成できませんでした。");// 8
                                            }
                                        }

                                    /*---------------------------------------------------------------------------------------- */
                                    } else if (option2 == 2) {// 2.既存に参加
                                        String room_name = in.readLine();// 9

                                        // ルームをdbから探してチャットルームに参加する
                                        List<String> list_data2 = new ArrayList<String>();
                                        list_data2 = check_chat_room(db, room_name);
                                        Boolean JOIN_CHAT = false;
                                        if (Integer.parseInt(list_data2.get(0)) == 1) {
                                            JOIN_CHAT = true;
                                            out.println(room_name + "に参加できました!");// 10
                                            out.println(JOIN_CHAT);// 10.5

                                            chat_log = new File(room_name + "_chat_log.txt");

                                            while (true) {
                                                out.println(chat_log);// 11

                                                PrintWriter chat_log_writer = new PrintWriter(new BufferedWriter(new FileWriter(chat_log, true)));

                                                String message = in.readLine();// 12

                                                if (!(message.equals(""))){
                                                    String chat_log_data = username + " : " +message;
                                                    chat_log_writer.println(chat_log_data);
                                                }

                                                chat_log_writer.close();

                                                if (message.equals("END")) {
                                                    break;
                                                }
                                            }

                                        } else {
                                            JOIN_CHAT = false;
                                            out.println(room_name + "に参加できませんでした。");// 10
                                            out.println(JOIN_CHAT);// 10.5
                                        }

                                    /*---------------------------------------------------------------------------------------- */
                                    } else if (option2 == 3) {// 3.退出する
                                        break;
                                    }
                                }
                            /*---------------------------------------------------------------------------------------- */
                            } else {//ログイン失敗の時
                                out.println(LOGIN); // 5
                            }
                        /*---------------------------------------------------------------------------------------- */
                        } else if (option == 2) {
                            Boolean SignUp = false;
                            if (username.contains("\\") || username.contains("/") || username.contains(":")
                                    || username.contains("*") || username.contains("?")
                                    || username.contains("\"") || username.contains("<")
                                    || username.contains(">") || username.contains("|")) {
                                SignUp = false;
                                out.println(SignUp); // 6.0
                                out.println("ユーザー名に \\ / : * ? \" < > | は使えません。");// 6.1
                            } else {
                                List<String> list_data2 = new ArrayList<String>();
                                list_data2 = check_user(db, username);
                                if (list_data2.size() == 1) {
                                    if (Integer.parseInt(list_data2.get(0)) == 1) {
                                        SignUp = false;
                                        out.println(SignUp); // 6.0
                                        out.println("'" + username + "'というユーザーが存在します"); // 6.1
                                    } else {
                                        add_user(db, username, hash_pass);
                                        SignUp = true;
                                        out.println(SignUp); // 6.0
                                        // out.println("ユーザーを追加しました"); // 6.1
                                    }
                                } else {
                                    SignUp = false;
                                    out.println(SignUp); // 6.0
                                    out.println("ユーザーを追加できませんでした"); // 6.1
                                }
                            }
                        }
                    } else {
                        Boolean LOGIN = false; // This code probably never runs
                        out.println(LOGIN); // 5
                    }
                }
            /*---------------------------------------------------------------------------------------- */
            } catch (NumberFormatException | NullPointerException e) { // clientが接続を切った場合
                System.out.println(Thread.currentThread().getName() + "が切断されました");
            } catch (SocketException e) { //java.net.SocketException: Connection reset
                System.out.println(Thread.currentThread().getName() + "が切断されました");
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e2) {
                    System.err.println(e2);
                }
                /*　以下の"return;"をコメントアウトすると警告は消えるが、 */
                //return; //スレッド消滅
            }

        } catch ( NumberFormatException | NullPointerException e ) { // clientが接続を切った場合
            System.out.println(Thread.currentThread().getName() + "が切断されました");
        } catch (SocketException e) { //java.net.SocketException: Connection reset
            System.out.println(Thread.currentThread().getName() + "が切断されました");
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

public class AServer {
    static int PORT = 8080; // ポート番号をプログラムの引数で与える

    public static void main(String[] args) throws IOException {

        // FileServerをexecで実行する
        Runtime.getRuntime().exec("java FileServer");

        ServerSocket s = new ServerSocket(PORT); // ソケットを作成する

        try {
            while(true){
                Socket socket = s.accept();// コネクション設定要求を待つ
                AServerThread t = new AServerThread(socket);//スレッド生成
                t.start();//スレッド実行
            }
        } finally {
            s.close();
        }
    }
}
