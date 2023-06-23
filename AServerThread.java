import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class AServerThread extends func{
    private Socket socket;

    public AServerThread(Socket socket){
        this.socket = socket;
    }

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

            // usernameとpasswordを保存するテーブルを制作
            create_table(db);
            // チャットルームの名前を保存するテーブルを制作
            create_table_chatname(db);

            /*---------------------------------------------------------------------------------------- */
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // データ受信用バッファの設定
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true); // 送信バッファ設定

            out.println("Serverと接続しました。"); // 1

            try{
                while(true){
                    /*---------------------------------------------------------------------------------------- */
                    // login and signup
                    Integer option = Integer.parseInt(in.readLine()); // 2

                    /*---------------------------------------------------------------------------------------- */
                    String username = in.readLine(); // 3
                    String pass = in.readLine(); // 4

                    String hash_pass = make_hash(pass);

                    /*---------------------------------------------------------------------------------------- */
                    if (option == 1) {
                            List<String> list_data = new ArrayList<String>();
                            list_data = login_user(db, username, hash_pass);
                            Boolean LOGIN = false;
                            if (list_data.size() == 1) {
                                LOGIN = true;
                                out.println(LOGIN); // 5
                                ChatOption(in, out, db, username);
                            } else {
                                out.println(LOGIN); // 5
                            }

                        /*---------------------------------------------------------------------------------------- */
                    } else if (option == 2) {
                        if (checkname(username) == false) {
                            out.println("ユーザー名に \\ / : * ? \" < > | は使えません。");// 6
                        } else {
                            List<String> list_data2 = new ArrayList<String>();
                            list_data2 = check_user(db, username);
                                if (list_data2.size() == 1) {
                                    if (Integer.parseInt(list_data2.get(0)) == 1) {
                                        out.println("'" + username + "'というユーザーが存在します"); // 6
                                    } else {
                                        add_user(db, username, hash_pass);
                                        out.println("ユーザーを追加しました"); // 6
                                    }
                                } else {
                                    out.println("ユーザーを追加できませんでした"); // 6
                                }
                            }
                    } else {
                        Boolean LOGIN = false;
                        out.println(LOGIN); // 5
                    }
                }
            /*---------------------------------------------------------------------------------------- */
            } catch (IOException e) {//突然接続が切れた場合
                System.err.println(e);
                System.out.println("closing...");
                try {
                    socket.close();
                } catch (IOException e2) {
                    System.err.println(e2);
                }
                return;//スレッド消滅
            }

        } catch ( NumberFormatException e ) { // clientが接続を切った場合
            System.err.println(Thread.currentThread().getName() + "が切断されました");
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

    private void ChatOption(BufferedReader in, PrintWriter out, File db, String username) throws IOException {
        while (true) {
            Integer option2 = Integer.parseInt(in.readLine()); // 6
            File chat_log;

            if (option2 == 1) {// 1.新規作成
                // チャットルームの名前を受信
                String room_name = in.readLine();// 7
                // room_nameの制限 \ / : * ? " < > |
                if (checkname(room_name) == false) {
                    out.println("チャットルーム名に \\ / : * ? \" < > | は使えません。");// 8
                } else {

                    // チャットルームがあるかdbから探す
                    List<String> list_data2 = new ArrayList<String>();
                    list_data2 = check_chat_room(db, room_name);

                    if (list_data2.size() == 1) {
                        if (Integer.parseInt(list_data2.get(0)) == 1) {
                            out.println("'" + room_name + "'というチャットルームが存在します。別のチャットルーム名を入力してください。");// 8
                        } else {
                            // チャットルームの名前をテーブルに追加
                            add_chatname(db, room_name);

                            chat_log = new File(room_name + "_chat_log.txt");
                            chat_log.createNewFile();

                            out.println("チャットルームを作成しました!");// 8
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

                if (Integer.parseInt(list_data2.get(0)) == 1) {
                    out.println(room_name + "に参加できました!");// 10
                    out.println(true);// 10.5
                    out.println(room_name);// 11

                    chat_log = new File(room_name + "_chat_log.txt");

                    while (true) {
                        BufferedReader chat_log_reader = new BufferedReader(new FileReader(chat_log));
                        PrintWriter chat_log_writer = new PrintWriter(new BufferedWriter(new FileWriter(chat_log, true)));
                        /*
                         * String chat_log_line;
                         * while ((chat_log_line = chat_log_reader.readLine()) != null) {
                         * out.println(chat_log_line);// 13
                        * }
                        */
                        chat_log_reader.close();

                        String message = in.readLine();

                        String chat_log_data = username + " : " +message;// 12
                        chat_log_writer.println(chat_log_data);
                        chat_log_writer.close();

                        if (message.equals("END")) {
                            break;
                        }
                    }

                } else {
                    out.println(room_name + "に参加できませんでした。");// 10
                    out.println(false);// 10.5
                    // break;
                }

            /*---------------------------------------------------------------------------------------- */
            } else if (option2 == 3) {// 3.退出する
                break;
            }
        }
    }
}
