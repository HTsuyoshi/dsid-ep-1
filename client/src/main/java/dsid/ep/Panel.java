package dsid.ep;

import dsid.ep.parts.Part;
import dsid.ep.parts.PartRepository;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.rmi.Naming;
import java.util.AbstractMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

/**
 * Fields: uma classe contendo todos os campos que serao usados para pegar alguma informacao do usuario
 *  \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ AARUMA
 * @attr currentName: o nome do servidor
 * @attr currentStatus: guarda o status do servidor (UP ou DOWN)
 * @attr ipField: recebe o ip do servidor que vai ser conectar
 * @attr nameField: recebe o nome do servidor que vai ser conectar
 * @attr portField: recebe a porta do servidor que vai ser conectar
 */
class Fields extends JPanel {
    static protected JTextField currentPartCode;
    static protected JTextField currentPartName;
    static protected JTextField currentPartQuantity;
    static protected JTextField currentPartDescription;
    static protected JList subComponentList;
    static protected JLabel currentName;
    static protected JLabel currentStatus;
    static protected JTextField ipField;
    static protected JTextField nameField;
    static protected JTextField portField;
    static protected JList partList;
    static protected JPanel menu;
    static protected JTextField searchField;
}

/**
 * Panel: Classe geral com os metodos de comunicacao com o servidor
 *
 * @attr currentPartRepository: Uma instancia de PartRepositoryInerface para se comunicar com o servidor
 * @attr currentPart: A parte que esta sendo editada atualmente
 * @attr currentSubPartList: A atual lista de subcomponentes
 */
public class Panel extends Fields {
    protected static PartRepository currentPartRepository;
    protected static Part currentPart;
    protected static Vector<Map.Entry<Part, Integer>> currentSubPartList;

    public Panel() {
        this.currentSubPartList = new Vector<>();
        this.currentPart = null; //new PartImpl(UUID.randomUUID(), "None", "None");
    }

    /**
     * connect: Cria uma conexao com o servidor. Se conseguir se conectar vai retornar true, se nao conseguir vai retornar false
     *
     * @param repository: Uma string que contem o endereco do repositorio para se conectar
     */
    boolean connect(String repository) {
        try {
            this.currentPartRepository = (PartRepository) Naming.lookup(repository);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * addp: Adiciona uma Part no repositorio atualmente conectado
     *
     * @param code: Usado para gerar um UUID e ser colocado no repositorio
     * @param name: Nome da parte que vai ser adicionada no repositorio
     * @param description: Descricao da parte que vai ser adicionada no repositorio
     * @param quantity: Quantidade de Parts que vao ser adicionadas no repositorio
     */
    boolean addp(String code, String name, String description, int quantity) {
        try {
            currentPartRepository.addPart(UUID.fromString(code), quantity, name, description);
        } catch (Exception ex) {
            ex.printStackTrace(); return false; }
        return true;
    }

    /**
     * addToGridBagConstraing: Vai colocar um determinado JComponent no GridBagConstraint
     */
    void addToGridBagConstraint(JComponent object, int x, int y, GridBagConstraints c) {
        c.gridx = x;
        c.gridy = y;
        add(object, c);
    }
}

/**
 * ConnectPanel: Vai mostrar as informacoes necessarias para o usuario fazer uma conexao com o server
 */
class ConnectPanel extends Panel {

    public ConnectPanel() {
        this.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        setText(c);
        setButtons(c);
    }

    /**
     * setButtons: Vai adicionar os botoes para o usuario fazer a conexao
     *
     * @param c: Usado para organizar os JComponents nos seus devidos lugares
     */
    public void setButtons(GridBagConstraints c) {
        c.weightx = 0;
        c.gridx = 1;
        c.gridy = 6;

        /**
         * O evendo do botao connectt vai:
         * 1. Verificar se os campos currentIp e currentPort estao preenchidos
         * 2. Tentar criar uma conexao com o server
         *  2.1 Se conseguir se conectar vai mostrar as informacoes do server e mostrar UP para o usuario
         *  2.2 Se nao conseguir se conectar, vai mostrar DOWN para o usuario
         */
        JButton connect = new JButton("Bind");
        connect.addActionListener(e -> {
            String ip = ipField.getText().trim();
            String name = nameField.getText().trim();

            if (ip.isEmpty() || name.isEmpty()) {
                System.err.println("connect: Algum campo nao foi preenchido corretamente");
                return;
            }

            int port;

            try {
                port = Integer.parseInt(this.portField.getText()
                        .trim());
            } catch(NumberFormatException ex) {
                System.err.println("connect: Isso nao e um numero");
                return;
            }

            String repository = String.format("rmi://%s:%d/%s", ip, port, name);
            currentName.setText(repository);

            if (connect(repository)) {
                currentStatus.setForeground(Color.GREEN);
                currentStatus.setText("UP");
            } else {
                currentStatus.setForeground(Color.RED);
                currentStatus.setText("DOWN");
            }
        });

        this.add(connect, c);
    }

    /**
     * setText: Vai adicionar os textos e fields para o usuario preencher ou receber informacoes
     *
     * @param c: Usado para organizar os JComponents nos seus devidos lugares
     */
    public void setText(GridBagConstraints c) {

        currentName = new JLabel("None");
        currentStatus = new JLabel("DOWN");
        currentStatus.setForeground(Color.RED);
        ipField = new JTextField(16);
        ipField.setText("127.0.0.1");
        nameField = new JTextField("teste");
        portField = new JTextField("4444");

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 10;
        addToGridBagConstraint(new JLabel("Name:"), 0, 1, c);
        addToGridBagConstraint(currentName, 1, 1, c);
        addToGridBagConstraint(new JLabel("Status:"), 0, 2, c);
        addToGridBagConstraint(currentStatus, 1, 2, c);
        c.ipady = 0;
        addToGridBagConstraint(new JLabel("Enter Ip: "), 0, 3, c);
        addToGridBagConstraint(ipField, 1, 3, c);
        addToGridBagConstraint(new JLabel("Enter Name: "), 0, 4, c);
        addToGridBagConstraint(nameField, 1, 4, c);
        addToGridBagConstraint(new JLabel("Enter Port: "), 0, 5, c);
        addToGridBagConstraint(portField, 1, 5, c);
    }
}

/**
 * ShowPanel: classe que herda os atributos da classe Panel e mostra as informacoes necessarias para pesquisar as pecas dentro do servidor
 *
 * partList: mostra as pecas que foram retornadas do servidor
 * menu: janela que define o layout que vai ser mostrado para o usuario
 * searchField: usado para o usuario procurar por uma peca baseada no seu UUID
 */
class ShowPanel extends Panel {

    public ShowPanel() {
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setLayout(new BorderLayout());
        menu = new JPanel();
        setText(menu);
        setButtons(menu);
    }

    public void setButtons(JPanel menu) {

        JButton list = new JButton("listp");
        list.addActionListener(e -> {
            try {
                partList.setListData(currentPartRepository.getPartList());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        menu.add(list);

        JButton get = new JButton("getp");
        get.addActionListener(e -> {
            String partCode;

            if (partList.isSelectionEmpty()) return;

            partCode = ((Part) partList.getSelectedValue()).getCode().toString();

            if (partCode.trim().isEmpty()) {
                partCode = searchField.getText();
                if (partCode.trim().isEmpty()) {
                    System.err.println("getp: Nenhum codigo foi inserido para fazer a busca");
                    return;
                }
            }

            Part repoPart;

            try {
                repoPart = currentPartRepository.getPart(UUID.fromString(partCode));
            } catch(Exception ex) {
                ex.printStackTrace();
                return;
            }

            currentPartCode.setText(repoPart.getCode().toString());
            currentPartName.setText(repoPart.getName());
            currentPartQuantity.setText("0");
            currentPartDescription.setText(repoPart.getDescription());
            subComponentList.setListData(currentSubPartList);
            currentPart = repoPart;
        });
        menu.add(get);
    }

    public void setText(JPanel menu) {
        searchField = new JTextField(10);
        searchField.setSize(200,100);
        menu.add(searchField);
        menu.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));

        add(menu, BorderLayout.PAGE_START);

        partList = new JList();
        partList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        partList.setLayoutOrientation(JList.VERTICAL);
        partList.setVisibleRowCount(-1);
        partList.setCellRenderer(new PartCellRenderer());

        JScrollPane subComponents = new JScrollPane(partList);
        add(subComponents, BorderLayout.CENTER);
    }
}

/*
 * PartCellRenderer: usado para representar as informacoes de uma instancia Part dentro de uma JList
 */

class PartCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Part label = (Part) value;
        String code = String.valueOf(label.getCode());
        String name = label.getName();
        String desc = label.getDescription();
        String labelText = String.format("<html>Code: %s<br/>Name: %s<br/>Description: %s<br/>", code, name, desc);
        setText(labelText);
        return this;
    }
}

/*
 * InfoPanel: mostra as informacoes sobre uma determinada peca, como: nome, UUID, descricao, lista de subcomponentes
 *
 * currentPartCode: receber/mostrar o codigo de uma peca
 * currentPartName: receber/mostrar o nome de uma peca
 * currentPartQuantity: receber/mostrar a quantidade de pecas
 * currentPartDescription: receber/mostrar a descricao de uma peca
 * subComponentList: receber/mostrar uma lista de subcomponentes
 */

class InfoPanel extends Panel {

    public InfoPanel() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        setText(c);
        setButtons(c);
    }

    public void setButtons(GridBagConstraints c) {
        c.gridheight = 1;

        JButton genCode = new JButton("gen code");
        genCode.addActionListener(e -> {
            UUID newCode = UUID.randomUUID();
            currentPartCode.setText(newCode.toString());
            currentPart.setCode(newCode);
        });

        JButton clear = new JButton("clearlist");
        clear.addActionListener(e -> {
            this.currentSubPartList = new Vector<>();
            subComponentList.setListData(new Vector<>());
        });

        c.gridy = 5;
        JButton addSubPart = new JButton("addsubpart");
        addSubPart.addActionListener(e -> {
            int quantityInt;

            try {
                quantityInt = Integer.parseInt(currentPartQuantity.getText());
            } catch (NumberFormatException ex) {
                System.err.println("addsubpart: Isso nao e um numero");
                return;
            }

            currentSubPartList.add(new AbstractMap.SimpleEntry<>(currentPart, quantityInt));
            subComponentList.setListData(currentSubPartList);
        });

        JButton addPart = new JButton("addp");
        addPart.addActionListener(e -> {

            int quantityInt;
            try {
                quantityInt = Integer.parseInt(currentPartQuantity.getText());
            } catch (NumberFormatException ex) {
                System.err.println("addp: Isso nao e um numero");
                return;
            }

            if (currentPartCode.getText().trim().isEmpty() ||
                    currentPartName.getText().trim().isEmpty() ||
                    currentPartDescription.getText().trim().isEmpty()) {
                System.err.println("addp: Algum campo nao foi preenchido corretamente");
                return;
            }

            currentPart.setCode(UUID.fromString(currentPartCode.getText()));
            currentPart.setName(currentPartName.getText().trim());
            currentPart.setDescription(currentPartDescription.getText().trim());
            addp(currentPartCode.getText(),
                     currentPartName.getText().trim(),
                    currentPartDescription.getText().trim(),
                    quantityInt);
        });

        // TODO: show part attributes
        // TODO: Test shot part attributes
        JButton show = new JButton("showp");
        show.addActionListener(e -> {
            currentPartCode.setText(currentPart.getCode().toString());
            currentPartName.setText(currentPart.getName());
            currentPartQuantity.setText("0");
            currentPartDescription.setText(currentPart.getDescription());
            subComponentList.setListData(currentSubPartList);
        });

        addToGridBagConstraint(genCode, 0, 0, c);
        addToGridBagConstraint(clear, 0, 5, c);
        addToGridBagConstraint(addSubPart, 0, 6, c);
        addToGridBagConstraint(addPart, 0, 7, c);
        addToGridBagConstraint(show, 0, 8, c);
    }

    public void setText(GridBagConstraints c) {
        c.fill = GridBagConstraints.HORIZONTAL;

        currentPartCode = new JTextField(UUID.randomUUID().toString());
        currentPartName = new JTextField("None");
        currentPartQuantity = new JTextField("0");
        currentPartDescription = new JTextField("Description");
        subComponentList = new JList();
        subComponentList.setVisibleRowCount(3);
        subComponentList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        subComponentList.setLayoutOrientation(JList.VERTICAL);
        subComponentList.setCellRenderer(new SubComponentCellRenderer());
        JScrollPane subComponents = new JScrollPane(subComponentList);

        addToGridBagConstraint(new JLabel("Code: "), 1, 0, c);
        addToGridBagConstraint(currentPartCode, 2, 0, c);
        addToGridBagConstraint(new JLabel("Nome: "), 1, 1, c);
        addToGridBagConstraint(currentPartName, 2, 1, c);
        addToGridBagConstraint(new JLabel("Quantity: "), 1, 2, c);
        addToGridBagConstraint(currentPartQuantity, 2, 2, c);
        addToGridBagConstraint(new JLabel("Description: "), 1, 3, c);
        addToGridBagConstraint(currentPartDescription, 2, 3, c);
        addToGridBagConstraint(new JLabel("Sub components: "), 1, 4, c);
        c.gridheight = 5;
        addToGridBagConstraint(subComponents, 2, 4, c);
    }
}

/*
 * SubComponentCellRenderer: representar as informacoes de uma subpart dentro de uma peca em uma JList
 */

class SubComponentCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Map.Entry<Part, Integer> label = (Map.Entry<Part, Integer>) value;
        String code = label.getKey().getCode().toString();
        String name = label.getKey().getName();
        int quantity = label.getValue();
        String labelText = String.format("<html>Code: %s<br/>Quantity: %d<br/>Name: %s<br/>", code, quantity, name);
        setText(labelText);
        return this;
    }
}
