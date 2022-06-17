package dsid.ep;

import dsid.ep.parts.PartRepositoryImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String args[]) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                ServerGui server = new ServerGui("Server Manager");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

class ServerGui {

    private final int MIN_WIDTH = 400;
    private final int MIN_HEIGHT = 400;
    private final JFrame gui;

    private String repositoryName;
    private int repositoryPort;

    ServerGui(String name) throws RemoteException {
        repositoryName = "nome repositorio";
        repositoryPort = 4444;

        this.gui = new JFrame(name);
        setupFrame();
        setupMenu();
        setupPanel();
        this.gui.setVisible(true);

    }

    private void setupFrame() {
        System.err.println("Setup Frame...");
        this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gui.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    }

    private void setupMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenu help = new JMenu("Help");

        JMenuItem fileItem1 = new JMenuItem("Open");
        file.add(fileItem1);

        JMenuItem helpItem1 = new JMenuItem("Save as");
        help.add(helpItem1);

        gui.getContentPane().add(BorderLayout.NORTH, menu);
        System.err.println("Setup Menu...");
    }

    private void setupPanel() throws RemoteException {
        System.err.println("Setup panel...");
        JPanel connPanel = new JPanel(new BorderLayout());
        connPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JTextArea log = new JTextArea();
        log.setEditable(false);
        log.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));;
        connPanel.add(log, BorderLayout.CENTER);

        connPanel.add(new ConnectPanel(log), BorderLayout.NORTH);

        JButton quit = new JButton("quit");
        quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        connPanel.add(quit, BorderLayout.SOUTH);

        gui.getContentPane().add(connPanel, BorderLayout.CENTER);
    }
}

class ConnectPanel extends JPanel {

    private Registry registry;

    ConnectPanel(JTextArea log) throws RemoteException {
        LocateRegistry.createRegistry(4444);
        registry = LocateRegistry.getRegistry(4444);

        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        this.add(new JLabel("Name:"), c);

        c.gridx = 1;
        c.gridy = 1;
        JTextField currentIp = new JTextField(20);
        currentIp.setText("teste");
        this.add(currentIp, c);

        c.gridx = 0;
        c.gridy = 2;
        this.add(new JLabel("Port:"), c);

        c.gridx = 1;
        c.gridy = 2;
        JTextField currentPort = new JTextField(5);
        currentPort.setText("4444");
        this.add(currentPort, c);

        c.gridx = 0;
        c.gridy = 4;
        JButton connect = new JButton("Start");
        connect.addActionListener(e -> {
            String serverName = currentIp.getText()
                    .trim();
            Integer port = Integer.parseInt(currentPort.getText()
                    .trim());

            if (serverName.isEmpty() ||
                    port.toString().isEmpty()) return;

            try {
                LocateRegistry.createRegistry(port);
                registry = LocateRegistry.getRegistry(port);
                PartRepositoryImpl server = new PartRepositoryImpl();
                registry.bind(serverName, server);
                log.setText("Server rodando\nNome: " + serverName + "\nPort: " + port);
            } catch(Exception ex) {
                log.setText("Something went wrong: " + ex);
                ex.printStackTrace();
                return;
            }

            currentIp.setEditable(false);
            currentPort.setEditable(false);
        });

        this.add(connect, c);

        c.gridx = 1;
        c.gridy = 4;
        JButton disconnect = new JButton("Stop");
        disconnect.addActionListener(e -> {
            try {
                String serverName = currentIp.getText()
                        .trim();
                Integer port = Integer.parseInt(currentPort.getText()
                        .trim());

                if (serverName.isEmpty() ||
                        port.toString().isEmpty()) return;

                LocateRegistry.getRegistry(port).unbind(serverName);
                log.setText("Desconectado.");
            } catch(Exception ex) {
                log.setText("Something went wrong: " + ex);
                ex.printStackTrace();
            }

            currentIp.setEditable(true);
            currentPort.setEditable(true);
        });
        this.add(disconnect, c);

        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
}