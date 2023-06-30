import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.Rectangle;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Component;

public class Client extends Clientfunc {

    public static void main(String[] args) throws IOException {
        final int PORT = 8080;
        InetAddress addr = InetAddress.getByName("localhost"); // IP アドレスへの変換

        Socket socket = new Socket(addr, PORT); // ソケットの生成

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // データ受信用バッファの設定
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true); // 送信バッファ設定

            System.out.println(in.readLine());//1

            // frameを実装する
            JFrame frame = new JFrame("MyApplication");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Rectangle table = new Rectangle(1000, 500);
            frame.setBounds(table);// frameのサイズを指定→table
            frame.setLocationRelativeTo(null);// 画面の真ん中にframeを表示

            gui(frame, in, out);

        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }

    public static void gui(JFrame frame, BufferedReader in, PrintWriter out) throws IOException {
        while(true){
            frame.getContentPane().removeAll();// パネルを取り除く
            Integer[] option = { null };
            option = init_panel(frame, out, option); // ログインとサインインのボタンを表示 p1
            /*---------------------------------------------------------------------------------------- */
            frame.getContentPane().removeAll();// パネル取り除く
            String username = input_panel(frame, out); // ユーザー名とパスワードの入力欄を表示 p2
            /*---------------------------------------------------------------------------------------- */
            if (option[0] == 1) {//ログインしたとき
                Boolean LOGIN = false;
                LOGIN = Boolean.parseBoolean(in.readLine()); // 5

                if (LOGIN == true) {// ログイン成功のとき
                    while (true) {
                        frame.getContentPane().removeAll();// パネルを取り除く
                        Integer[] option2 = { null };
                        option2 = chat_action_panel(frame, out, option2); // チャットルームの新規作成、既存に参加、退出のボタンを表示 p4
                        /*---------------------------------------------------------------------------------------- */
                        if (option2[0] == 1) {// 1.新規作成
                            frame.getContentPane().removeAll();// パネルを取り除く
                            create_chat_panel(frame, out); // チャットルームの新規作成のパネルを表示 p5

                            String chat_f = in.readLine();// 8

                            frame.getContentPane().removeAll();// パネルを取り除く
                            result_chat_panel(frame, chat_f); // チャットルームの新規作成の結果を表示 p6

                        /*---------------------------------------------------------------------------------------- */
                        } else if (option2[0] == 2) {// 2. 既存に参加
                            frame.getContentPane().removeAll();// パネルを取り除く

                            join_chat_panel(frame, out); // チャットルームの参加のパネルを表示 p7

                            String join_message = in.readLine();// 10
                            Boolean JOIN = false;
                            JOIN = Boolean.parseBoolean(in.readLine());// 10.5

                            if (JOIN == true) {// チャットルームに参加できたとき
                                Boolean isMac = System.getProperty("os.name").toLowerCase().startsWith("mac");
                                Integer scroll_height = 0;
                                Integer max = 0;
                                Integer min = 0;
                                while (true) {
                                    frame.getContentPane().removeAll();// パネルを取り除く
                                    if(! isMac){
                                        // frameのlayoutを初期化
                                        frame.setLayout(null);
                                        // frameにBorderLayoutを設定
                                        frame.setLayout(new BorderLayout());
                                    }

                                    Boolean chat_option []= { null };
                                    Boolean quit_option []= { null };
                                    Boolean load_option []= { null }; // mac is used

                                    JPanel p8 = new JPanel();
                                    p8.setLayout(null);

                                    JLabel label_chat_success = new JLabel(join_message); // mac is used
                                    String room_name = in.readLine();// 11
                                    // chat_log.txtを表示
                                    if (isMac){
                                        log_panel(p8, username, room_name, scroll_height, max, min); // ログを表示
                                    } else {
                                        log_panel(frame, username, room_name, scroll_height, max, min); // ログを表示 p8_1
                                    }

                                    JLabel label_messagelabel = new JLabel("メッセージ：");
                                    JTextField tf_message = new JTextField();
                                    JButton send_btn = new JButton("SEND");
                                    send_btn.setMnemonic(KeyEvent.VK_S);
                                    JButton quit_btn = new JButton("QUIT");
                                    quit_btn.setMnemonic(KeyEvent.VK_Q);
                                    JButton load_btn = new JButton("LOAD"); // mac is used
                                    load_btn.setMnemonic(KeyEvent.VK_L); // mac is used


                                    send_btn.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                            String message = tf_message.getText();
                                            if (message.equals(null) || message.equals("") || message.equals("END")) {
                                                JOptionPane.showMessageDialog(null, "無効なメッセージです");
                                            } else {
                                                out.println(message);// 12
                                                chat_option[0] = true;
                                            }

                                        }
                                    });

                                    quit_btn.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                            out.println("END");// 12

                                            quit_option[0] = true;
                                        }
                                    });

                                    tf_message.setColumns(10);
                                    if(! isMac){
                                        p8.setLayout(new BorderLayout());
                                        p8.add(label_messagelabel, BorderLayout.WEST);
                                        p8.add(tf_message, BorderLayout.CENTER);
                                        p8.add(send_btn, BorderLayout.EAST);
                                        p8.add(quit_btn, BorderLayout.SOUTH);
                                        frame.getContentPane().add(p8, BorderLayout.SOUTH);
                                    } else {
                                        load_btn.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                out.println("");//12
                                                load_option[0] = true;
                                            }
                                        });
                                        label_chat_success.setBounds(400, 10, 800, 25);
                                        label_messagelabel.setBounds(300, 350, 200,25);
                                        label_messagelabel.setHorizontalAlignment(JLabel.RIGHT);
                                        tf_message.setBounds(500, 350, 200,25);
                                        send_btn.setBounds(700, 350, 60,25);
                                        quit_btn.setBounds(500, 400, 60,25);
                                        load_btn.setBounds(420, 400, 60, 25);
                                        p8.add(label_chat_success);
                                        p8.add(label_messagelabel);
                                        p8.add(tf_message);
                                        p8.add(send_btn);
                                        p8.add(quit_btn);
                                        p8.add(load_btn);
                                        frame.add(p8);
                                    }
                                    frame.setVisible(true);
                                    tf_message.requestFocus(); // メーッセージの入力欄にフォーカスを当てる
                                    frame.validate();
                                    frame.repaint();// 画面を書き直す
                                    // optionの値が決まるまで以下のコードを実行しない
                                    while (true) {
                                        if (chat_option [0] != null ) {
                                            break;
                                        }else if (quit_option [0] != null){
                                            break;
                                        }else if (isMac && load_option [0] != null){
                                            break;
                                        }else{
                                            try {
                                                if (isMac){
                                                    Thread.sleep(100);
                                                } else {
                                                    Thread.sleep(1000);
                                                }
                                                // ======================================
                                                // 現在のスクロールの状態を取得
                                                JScrollPane scroll = null;
                                                JPanel p8_tmp = null;
                                                if (isMac) {
                                                    // frameのp8パネルのJScrollPane scrollを取得する
                                                    p8_tmp = (JPanel) frame.getContentPane().getComponents()[0];
                                                } else {
                                                    // frameのcenterのp8_1パネルのセンターのJScrollPane scrollを取得する
                                                    p8_tmp = (JPanel) frame.getContentPane().getComponent(0);
                                                }
                                                Component[] components = p8_tmp.getComponents();
                                                for (Component component : components) {
                                                    if (component instanceof JScrollPane) {
                                                        scroll = (JScrollPane) component;
                                                    }
                                                }
                                                JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
                                                max = verticalScrollBar.getMaximum();
                                                min = verticalScrollBar.getMinimum();
                                                scroll_height = verticalScrollBar.getValue();
                                                // ======================================

                                                if (! isMac) {
                                                    frame.getContentPane().removeAll();// パネルを取り除く
                                                    log_panel(frame,username, room_name,scroll_height, max, min); // ログを表示 p8_1

                                                    frame.getContentPane().add(p8, BorderLayout.SOUTH);
                                                    frame.setVisible(true);
                                                    tf_message.requestFocus(); // メーッセージの入力欄にフォーカスを当てる
                                                    frame.validate();
                                                    frame.repaint();// 画面を書き直す
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }

                                    if(quit_option[0] != null){
                                        break;
                                    }
                                }
                            } else {// チャットルームに参加できなかったとき
                                frame.getContentPane().removeAll();// パネルを取り除く
                                fail_join_panel(frame, join_message); // チャットルームに参加できなかったパネルを表示 p9
                            }
                        /*---------------------------------------------------------------------------------------- */
                        } else if (option2[0] == 3) {// 3.退出する
                            frame.getContentPane().removeAll();// パネルを取り除く
                            room_exit_panel(frame, in, out); // ルームを退出したパネルを表示 p10 (gui再帰)
                        /*---------------------------------------------------------------------------------------- */
                        }
                    }
                } else {// ログイン失敗のとき
                    frame.getContentPane().removeAll();// パネルを取り除く
                    fail_login_panel(frame, in, out); // ログイン失敗のパネルを表示 p3 (gui再帰)
                }
            /*---------------------------------------------------------------------------------------- */
            } else if (option[0] == 2) {// サインインしたとき
                frame.getContentPane().removeAll();//パネルを取り除く
                Boolean SIGNUP = false;
                SIGNUP = Boolean.parseBoolean(in.readLine()); // 6.0
                if (SIGNUP == true) {// サインイン成功のとき
                    succuss_signup_panel(frame, in, out); // signup成功のパネルを表示 p11 (gui再帰)
                } else {// サインアップできなかった場合
                    fail_signup_panel(frame, in, out); // signup失敗のパネルを表示 p12 (gui再帰)
                }
            }
        }
    }

    private static Integer[] init_panel(JFrame frame, PrintWriter out, Integer[] option) throws IOException {
        // パネルp1をframeに追加する
        JPanel p1 = new JPanel();
        p1.setLayout(null);

        // ログインボタンの実装
        JButton login_btn = new JButton("Login");
        login_btn.setMnemonic(KeyEvent.VK_L);
        login_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option[0] = 1;
                out.println(option[0]); // 2
            }
        });

        // サインアップボタンの実装
        JButton signup_btn = new JButton("Signup");
        signup_btn.setMnemonic(KeyEvent.VK_S);
        signup_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option[0] = 2;
                out.println(option[0]);// 2
            }
        });

        login_btn.setBounds(375, 225, 100, 50);
        signup_btn.setBounds(525, 225, 100, 50);
        p1.add(login_btn);
        p1.add(signup_btn);
        frame.getContentPane().add(p1);
        frame.setVisible(true);

        // optionの値が決まるまで以下のコードを実行しない
        while (true) {
            if (option[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return option;
    }

    private static String input_panel(JFrame frame, PrintWriter out) throws IOException {
        // パネルp2の実装
        JPanel p2 = new JPanel();
        p2.setLayout(null);

        JLabel label_user = new JLabel("ユーザー名：");
        JLabel label_pass = new JLabel("パスワード：");
        JTextField tf_user = new JTextField();
        JPasswordField tf_pass = new JPasswordField();
        Boolean[] ok = { null };
        Boolean[] noerror = { null };
        JButton ok_btn = new JButton("OK");
        String[] obj_user = { null };
        ok_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok[0] = true;
                String username = tf_user.getText();
                obj_user[0] = username;
                char[] pass = tf_pass.getPassword();
                String pass_str = new String(pass);
                String hash_pass = make_hash(pass_str);

                // もしusernameまたはpassのどちらか一方が空白だった場合はエラーを表示させる
                if (username.equals(null) || username.equals("")) {
                    JOptionPane.showMessageDialog(null, "ユーザー名を入力してください");
                } else if (pass_str.equals(null) || pass_str.equals("")) {
                    JOptionPane.showMessageDialog(null, "パスワードを入力してください");
                } else {
                    out.println(ok[0]);// 3
                    out.println(username);// 4
                    out.println(hash_pass);// 5
                    noerror[0] = true;
                }
            }
        });

        tf_pass.setColumns(10);
        tf_user.setColumns(10);
        label_user.setBounds(390, 200, 150, 25);
        label_pass.setBounds(390, 275, 150, 25);
        tf_user.setBounds(545, 200, 150, 25);
        tf_pass.setBounds(545, 275, 150, 25);
        ok_btn.setBounds(800, 300, 50, 50);
        p2.add(tf_user);
        p2.add(tf_pass);
        p2.add(label_user);
        p2.add(label_pass);
        p2.add(ok_btn);
        frame.add(p2);
        frame.setVisible(true);
        tf_user.requestFocus(); // ユーザー名の入力欄にフォーカスを当てる
        frame.validate();
        frame.repaint();// 画面を書き直す

        // エラーなし（usernameもpasswordも正常に入力）になるまで以下のコードを実行しない
        while (true) {
            if (noerror[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
        return obj_user[0];
    }

    private static Integer[] chat_action_panel(JFrame frame, PrintWriter out, Integer[] option2) throws IOException {
        // パネルp4の実装
        JPanel p4 = new JPanel();
        p4.setLayout(null);

        JLabel label_choice = new JLabel("ログインに成功しました！");

        JButton create_chat = new JButton("Create");
        create_chat.setMnemonic(KeyEvent.VK_C);
        create_chat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option2[0] = 1;// 6
                out.println(option2[0]);
            }
        });

        JButton join_chat = new JButton("Join");
        join_chat.setMnemonic(KeyEvent.VK_J);
        join_chat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option2[0] = 2;// 6
                out.println(option2[0]);
            }
        });

        JButton logout = new JButton("Exit");
        logout.setMnemonic(KeyEvent.VK_E);
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option2[0] = 3;// 6
                out.println(option2[0]);
            }
        });

        label_choice.setBounds(425, 136, 200, 25);
        create_chat.setBounds(300, 236, 100, 25);
        join_chat.setBounds(450, 236, 100, 25);
        logout.setBounds(600, 236, 100, 25);
        p4.add(label_choice);
        p4.add(create_chat);
        p4.add(join_chat);
        p4.add(logout);
        frame.add(p4);
        frame.setVisible(true);
        frame.validate();
        frame.repaint();// 画面を書き直す

         // ３つのボタンのうちどれかが押されるまで以下のコードを実行しない
        while (true) {
            if (option2[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }

        return option2;
    }

    private static void create_chat_panel(JFrame frame, PrintWriter out) throws IOException {
        // パネルp5の実装
        JPanel p5 = new JPanel();
        p5.setLayout(null);

        JLabel label_newchatname = new JLabel("新規のチャットルーム名：");
        JTextField tf_newchatname = new JTextField();

        Boolean[] ok3 = { null };
        JButton ok3_btn = new JButton("OK");
        ok3_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok3_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok3[0] = true;
                String room_name = tf_newchatname.getText();
                out.println(room_name);// 7
            }
        });

        label_newchatname.setBounds(300, 236, 200, 25);
        tf_newchatname.setBounds(525, 236, 200, 25);
        ok3_btn.setBounds(675, 261, 50, 50);
        p5.add(label_newchatname);
        p5.add(tf_newchatname);
        p5.add(ok3_btn);
        frame.add(p5);
        frame.setVisible(true);
        tf_newchatname.requestFocus(); // チャットルーム名の入力欄にフォーカスを当てる
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok3[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private static void result_chat_panel(JFrame frame, String chat_f) throws IOException {
        // パネルp6の実装
        JPanel p6 = new JPanel();
        p6.setLayout(null);

        JLabel label_chatresult = new JLabel(chat_f);

        Boolean[] ok4 = { null };
        JButton ok4_btn = new JButton("OK");
        ok4_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok4_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok4[0] = true;
            }
        });

        // 今付いているframeにキーリスナーをはずす
        try {
            frame.removeKeyListener(frame.getKeyListeners()[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 何もしない
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok4[0] = true;
                }
            }
        });

        label_chatresult.setBounds(420, 236, 500, 25);
        ok4_btn.setBounds(570, 286, 50, 50);
        p6.add(label_chatresult);
        p6.add(ok4_btn);
        frame.add(p6);
        frame.setVisible(true);
        frame.requestFocus();
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok4[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private static void join_chat_panel(JFrame frame, PrintWriter out/* , String get_room_name[]*/) throws IOException{
        // パネルp7の実装
        JPanel p7 = new JPanel();
        p7.setLayout(null);

        JLabel label_chatname = new JLabel("既存のチャットルーム名：");
        JTextField tf_chatname = new JTextField();

        Boolean[] ok5 = { null };
        JButton ok5_btn = new JButton("OK");
        ok5_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok5_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok5[0] = true;
                String room_name = tf_chatname.getText();
                //get_room_name[0] = room_name;
                out.println(room_name);// 9
            }
        });

        label_chatname.setBounds(300, 236, 200, 25);
        tf_chatname.setBounds(525, 236, 200, 25);
        ok5_btn.setBounds(675, 261, 50, 50);
        p7.add(label_chatname);
        p7.add(tf_chatname);
        p7.add(ok5_btn);
        frame.add(p7);
        frame.setVisible(true);
        tf_chatname.requestFocus(); // チャットルーム名の入力欄にフォーカスを当てる
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok5[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private static void fail_join_panel(JFrame frame, String join_message) throws IOException {
        // パネルp9の実装
        JPanel p9 = new JPanel();
        p9.setLayout(null);

        JLabel label_chat_failure = new JLabel(join_message);
        Boolean[] ok6 = { null };
        JButton ok6_btn = new JButton("OK");
        ok6_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok6_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok6[0] = true;
            }
        });

        // 今付いているframeにキーリスナーをはずす
        try {
            frame.removeKeyListener(frame.getKeyListeners()[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 何もしない
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok6[0] = true;
                }
            }
        });

        label_chat_failure.setBounds(420, 236, 500, 25);
        ok6_btn.setBounds(570, 286, 50, 50);
        p9.add(label_chat_failure);
        p9.add(ok6_btn);
        frame.add(p9);
        frame.setVisible(true);
        frame.requestFocus();
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok6[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    private static void room_exit_panel(JFrame frame, BufferedReader in, PrintWriter out) throws IOException {
        // パネルp10の実装
        JPanel p10 = new JPanel();
        p10.setLayout(null);

        JLabel label_logout = new JLabel("退出できました！");
        Boolean[] ok7 = { null };
        JButton ok7_btn = new JButton("OK");
        ok7_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok7_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok7[0] = true;
            }
        });

        // 今付いているframeにキーリスナーをはずす
        try {
            frame.removeKeyListener(frame.getKeyListeners()[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 何もしない
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok7[0] = true;
                }
            }
        });

        label_logout.setBounds(420, 236, 500, 25);
        ok7_btn.setBounds(570, 286, 50, 50);
        p10.add(label_logout);
        p10.add(ok7_btn);
        frame.add(p10);
        frame.setVisible(true);
        frame.requestFocus();
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok7[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gui(frame, in, out);
                break;
            }
        }
    }

    private static void fail_login_panel(JFrame frame, BufferedReader in, PrintWriter out) throws IOException {
        // パネルp3の実装
        JPanel p3 = new JPanel();
        p3.setLayout(null);

        JLabel label_login_failure = new JLabel("ログインに失敗しました。");

        Boolean[] ok2 = { null };
        JButton ok2_btn = new JButton("OK");
        ok2_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok2_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok2[0] = true;
            }
        });

        // 今付いているframeにキーリスナーをはずす
        try {
            frame.removeKeyListener(frame.getKeyListeners()[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 何もしない
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok2[0] = true;
                }
            }
        });

        label_login_failure.setBounds(420, 236, 200, 25);
        ok2_btn.setBounds(570, 286, 50, 50);
        p3.add(label_login_failure);
        p3.add(ok2_btn);
        frame.add(p3);
        frame.setVisible(true);
        frame.requestFocus();
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok2[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gui(frame, in, out);
                break;
            }
        }
    }

    private static void succuss_signup_panel(JFrame frame, BufferedReader in, PrintWriter out) throws IOException {
        // パネルp11の実装
        JPanel p11 = new JPanel();
        p11.setLayout(null);

        JLabel label_signup = new JLabel("サインインが完了しました！");
        Boolean[] ok8 = { null };
        JButton ok8_btn = new JButton("OK");
        ok8_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok8_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok8[0] = true;
            }
        });

        // 今付いているframeにキーリスナーをはずす
        try {
            frame.removeKeyListener(frame.getKeyListeners()[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 何もしない
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok8[0] = true;
                }
            }
        });

        label_signup.setBounds(420, 236, 500, 25);
        ok8_btn.setBounds(570, 286, 50, 50);
        p11.add(label_signup);
        p11.add(ok8_btn);
        frame.add(p11);
        frame.setVisible(true);
        frame.requestFocus();
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok8[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gui(frame, in, out);
                break;
            }
        }
    }

    private static void fail_signup_panel(JFrame frame, BufferedReader in, PrintWriter out) throws IOException {
        // パネル12の実装
        JPanel p12 = new JPanel();
        p12.setLayout(null);

        JLabel label_signup_failure = new JLabel(in.readLine()); // 6.1

        Boolean[] ok9 = { null };
        JButton ok9_btn = new JButton("OK");
        ok9_btn.setMnemonic(KeyEvent.VK_ENTER);
        ok9_btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok9[0] = true;
            }
        });

        // 今付いているframeにキーリスナーをはずす
        try {
            frame.removeKeyListener(frame.getKeyListeners()[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 何もしない
        }
        frame.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ok9[0] = true;
                }
            }
        });

        label_signup_failure.setBounds(420, 236, 200, 25);
        ok9_btn.setBounds(570, 286, 50, 50);
        p12.add(label_signup_failure);
        p12.add(ok9_btn);
        frame.add(p12);
        frame.setVisible(true);
        frame.requestFocus();
        frame.validate();
        frame.repaint();// 画面を書き直す

        // okボタンが押されるまで以下のコードを実行しない
        while (true) {
            if (ok9[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                gui(frame, in, out);
                break;
            }
        }
    }

    private static void ReadFromTextFile(JTextArea t,String username, String filename) {
        try {
            FileClient file_client = new FileClient();
            file_client.request(filename, username);
            filename = "client/" + username + "/" + filename;
            File file = new File(filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            // ファイル末端まで、各行をstrに読み込んでからJTextAreaコンテナに追加していく
            String str = br.readLine();
            t.setText("");
            while (str != null) {
                t.append(str + "\n");
                str = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // this function is for mac
    private static void log_panel(JPanel p8 ,String username,String room_name, Integer scroll_height, Integer max, Integer min) throws IOException {
        // chat_log.txtを表示
        JTextArea text = new JTextArea();// テキスト表示領域を作成
        text.setEditable(false);//textの編集不可設定
        ReadFromTextFile(text,username,room_name); // 注意 usernameとroom_nameの順番
        JScrollPane scroll = new JScrollPane();//スクロールバーを追加
        scroll.getViewport().setView(text);

        JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
        if (max == 0){
            max = verticalScrollBar.getMaximum();
        }
        verticalScrollBar.setMaximum(max);
        verticalScrollBar.setMinimum(min);
        verticalScrollBar.setValue(scroll_height);
        scroll.setBounds(100, 35, 800, 310);
        p8.add(scroll);
    }

    // this function is for linux
    private static void log_panel(JFrame frame,String username, String room_name, Integer scroll_height, Integer max, Integer min) throws IOException {
        JPanel p8_1 = new JPanel();
        p8_1.setLayout(null);
        // chat_log.txtを表示
        JTextArea text = new JTextArea();// テキスト表示領域を作成
        text.setEditable(false);//textの編集不可設定
        ReadFromTextFile(text,username,room_name);
        JScrollPane scroll = new JScrollPane();//スクロールバーを追加
        scroll.getViewport().setView(text);

        JScrollBar verticalScrollBar = scroll.getVerticalScrollBar();
        if (max == 0){
            max = verticalScrollBar.getMaximum();
        }
        verticalScrollBar.setMaximum(max);
        verticalScrollBar.setMinimum(min);
        verticalScrollBar.setValue(scroll_height);

        p8_1.setLayout(new BorderLayout());
        p8_1.add(scroll, BorderLayout.CENTER);
        frame.getContentPane().add(p8_1, BorderLayout.CENTER);
    }

}