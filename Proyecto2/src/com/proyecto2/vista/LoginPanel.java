    package com.proyecto2.vista;

    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.KeyEvent;
    import java.awt.event.KeyAdapter;
    import com.proyecto2.controlador.MainController;
    import com.proyecto2.modelo.User;

    public class LoginPanel extends JPanel {
        private MainController controller;
        private MainWindow mainWindow;
        private JTextField tfCodigo;
        private JPasswordField pfPassword;
        private JButton btnLogin;
        private JLabel lblMsg;

        public LoginPanel(MainController controller, MainWindow mainWindow) {
            this.controller = controller;
            this.mainWindow = mainWindow;
            init();
        }

        private void init(){
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            JLabel l1 = new JLabel("Código:");
            JLabel l2 = new JLabel("Contraseña:");
            tfCodigo = new JTextField(14);
            pfPassword = new JPasswordField(14);
            btnLogin = new JButton("Ingresar");
            lblMsg = new JLabel(" ");

            c.insets = new Insets(4,4,4,4);
            c.gridx=0; c.gridy=0; add(l1,c);
            c.gridx=1; add(tfCodigo,c);
            c.gridx=0; c.gridy=1; add(l2,c);
            c.gridx=1; add(pfPassword,c);
            c.gridx=0; c.gridy=2; c.gridwidth=2; add(btnLogin,c);
            c.gridy=3; add(lblMsg,c);

            btnLogin.addActionListener(e -> performLogin());

            tfCodigo.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        pfPassword.requestFocus();
                    }
                }
            });

            pfPassword.addKeyListener(new KeyAdapter() {
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        performLogin();
                    }
                }
            });

            btnLogin.setMnemonic(KeyEvent.VK_ENTER);
        }

        private void performLogin() {
            String code = tfCodigo.getText().trim();
            String pass = new String(pfPassword.getPassword());
            User u = controller.authenticate(code, pass);
            if (u != null) {
                lblMsg.setText("Bienvenido " + u.getNombre());
                mainWindow.showAdmin(u);
            } else {
                lblMsg.setText("Credenciales inválidas");
            }
        }
    }