package dsid.ep;

import dsid.ep.parts.PartRepositoryImpl;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

/**
 * Server: Vai ser utilizada para inicializar a interface grafica
 */
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

/**
 * ServerGui: A interface grafica que vai ser permitir o usuario abrir um servidor em uma porta especifica e com um
 * nome especifico
 *
 * @attr MIN_WIDTH; A largura minimo que a janela precisa para poder mostrar suas infomacoes
 * @attr MIN_HEIGH; A altura minima que a janela precisa para poder mostrar suas infomacoes
 * @attr gui; A interface principal
 * @attr repositoryName; O nome do repositorio que vai ser utilizado na coexao
 * @attr reopsitoryPort; A porta do repositorio que vai ser utilizado na coexao
 */
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
        setupPanel();
        this.gui.setVisible(true);

    }

    /**
     * setupFrame: Vai inicializar algumas propriedades do JFrame
     */
    private void setupFrame() {
        System.err.println("Setup Frame...");
        this.gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gui.setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
    }

    /**
     * setupPanel: Vai inicializar os textos, fields e botoes que vao aparecer na aplicacao
     */
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
        quit.addActionListener(e -> System.exit(0));
        connPanel.add(quit, BorderLayout.SOUTH);

        gui.getContentPane().add(connPanel, BorderLayout.CENTER);
    }
}

/**
 * ConnectPanel: Vai inicializar as funcionalidades que serao utilizadas durante uma conexao
 */
class ConnectPanel extends JPanel {

    private Registry registry;

    /**
     * addToGridBagConstraing: Vai colocar um determinado JComponent no GridBagConstraint
     */
    void addToGridBagConstraint(JComponent object, int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        add(object, c);
    }

    ConnectPanel(JTextArea log) throws RemoteException {

        this.setLayout(new GridBagLayout());

        JTextField currentName = new JTextField(20);
        currentName.setText("teste");
        JTextField currentPort = new JTextField(5);
        currentPort.setText("4444");

        /**
         * O evendo do botao start vai:
         * 1. Verificar se os campos currentIp e currentPort estao preenchidos
         * 2. Tentar abrir o server
         *  2.1 Se abrir, vai mostrar as informacoes do server e impossibilitar de alterar as configuracoes
         *  2.2 Se nao abrir, vai mostrar o erro no log
         */
        JButton start = new JButton("Start");
        start.addActionListener(e -> {
            String serverName = currentName.getText()
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

            currentName.setEditable(false);
            currentPort.setEditable(false);
        });

        /**
         * O evendo do botao stop vai:
         * 1. Verificar se os campos currentIp e currentPort estao preenchidos
         * 2. Tentar fechar o server
         *  2.1 Se fechar, vai permitir que o usuario possa alterar as configuracoes
         *  2.2 Se nao fechar, vai mostrar o erro no log
         */
        JButton stop = new JButton("Stop");
        stop.addActionListener(e -> {
            try {
                String serverName = currentName.getText()
                        .trim();
                Integer port = Integer.parseInt(currentPort.getText()
                        .trim());

                if (serverName.isEmpty() ||
                        port.toString().isEmpty()) return;


                registry = LocateRegistry.getRegistry(port);
                registry.unbind(serverName);
                log.setText("Desconectado.");
            } catch(Exception ex) {
                log.setText("Something went wrong: " + ex);
                ex.printStackTrace();
            }

            currentName.setEditable(true);
            currentPort.setEditable(true);
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        addToGridBagConstraint(new JLabel("Name:"), 0, 1, c);
        addToGridBagConstraint(currentName, 1, 1, c);
        addToGridBagConstraint(new JLabel("Port:"), 0, 2, c);
        addToGridBagConstraint(currentPort, 1, 2, c);
        addToGridBagConstraint(start, 0, 4, c);
        addToGridBagConstraint(stop, 1, 4, c);

        this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }
}