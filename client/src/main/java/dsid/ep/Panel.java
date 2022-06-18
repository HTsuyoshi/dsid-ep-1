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
 *
 * @attr currentPartCode: Recebe o ID da Part
 * @attr currentPartName: Recebe o nome da Part
 * @attr currentPartQuantity: Recebe a quantitade de Parts
 * @attr currentPartDescription: Recebe a descricao da Part
 * @attr subComponentList: Usado para Mostrar uma lista de subParts
 * @attr currentName: O nome do servidor
 * @attr currentStatus: Guarda o status do servidor (UP ou DOWN)
 * @attr ipField: Recebe o ip do servidor que vai ser conectar
 * @attr nameField: Recebe o nome do servidor que vai ser conectar
 * @attr portField: Recebe a porta do servidor que vai ser conectar
 * @attr partList; Usado para Mostrar as Parts disponiveis no servidor
 * @attr searchField; Recebe um ID para o usuario receber alguma parte do servidor
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
    boolean addp(String code, String name, String description, int quantity, Vector<Map.Entry<Part, Integer>> subComp) {
        try {
            currentPartRepository.addPart(UUID.fromString(code), quantity, name, description);
            for (Map.Entry<Part, Integer> e : subComp) {
                currentPartRepository.addSubCompPart(UUID.fromString(code), e.getKey().getCode(), e.getValue(), e.getKey().getName(), e.getKey().getDescription());
            }
        } catch (Exception ex) {
            ex.printStackTrace(); return false; }
        return true;
    }

    /**
     * listp: Vai pegar a lista disponivel de pecas no servidor
     */
    void listp() {
        try {
            partList.setListData(currentPartRepository.getPartList());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * getp: Vai pegar uma peca do repositorio e passar as informacoes dela para o usuario
     *
     * @param partCode: O codigo da peca que vai ser recuperada
     */
    void getp(String partCode) {
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
    }

    /**
     * showp: Mostra as informacoes da referencia da peca atual
     */
    void showp() {
        currentPartCode.setText(currentPart.getCode().toString());
        currentPartName.setText(currentPart.getName());
        currentPartQuantity.setText("0");
        currentPartDescription.setText(currentPart.getDescription());
        subComponentList.setListData(currentSubPartList);
    }

    /**
     * gencode: Gera um novo identificador unico
     */
    void gencode() {
        UUID newCode = UUID.randomUUID();
        currentPartCode.setText(newCode.toString());
        currentPart.setCode(newCode);
    }

    /**
     * addsubpart: Adicionar uma nova subpart na lista de subpartes atual
     *
     * @param quantity: a quantidade de pecas que vao ser adicionadas
     */
    void addsubpart(int quantity) {
        currentSubPartList.add(new AbstractMap.SimpleEntry<>(currentPart, quantity));
        subComponentList.setListData(currentSubPartList);
    }

    /**
     * clearlist: limpa a lista de subpecas atual
     */
    void clearlist() {
        this.currentSubPartList = new Vector<>();
        subComponentList.setListData(new Vector<>());
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
         * O evento do botao connectt vai:
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
 * menu: janela que define o layout que vai ser mostrado para o usuario
 */
class ShowPanel extends Panel {
    private JPanel menu;

    public ShowPanel() {
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setLayout(new BorderLayout());
        menu = new JPanel();
        setText(menu);
        setButtons(menu);
    }

    public void setButtons(JPanel menu) {

        /**
         * O evento do botao listp vai:
         * 1. Recuperar uma lista com as Parts do servidor
         * 2. Mostrar para o usuario
         */
        JButton list = new JButton("listp");
        list.addActionListener(e -> {
            listp();
        });
        menu.add(list);

        /**
         * O evento do botao getp vai:
         * 1. Verificar se os campos foram preenchidos corretamente
         * 2. Recuperar a peca especificada pelo UUID e mostrar para o usuario
         */
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

            getp(partCode);
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

/**
 * PartCellRenderer: usado para representar as informacoes de uma instancia de Part dentro de uma JList
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

/**
 * InfoPanel: mostra as informacoes sobre uma determinada peca, como: nome, UUID, descricao, lista de subcomponentes
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

        /**
         * O evento do botao gencode gerar um novo UUID
         */
        JButton genCode = new JButton("gencode");
        genCode.addActionListener(e -> {
            gencode();
        });

        /**
         * O evento do botao clearlist limpar a lista de subPart atual
         */
        JButton clear = new JButton("clearlist");
        clear.addActionListener(e -> {
            clearlist();
        });

        /**
         * O evento do botao addsubpart vai:
         * 1. Verificar se o campo Quantity foi preenchido
         * 1.1 Se tiver sido preenchido vai adicionar a subPart no repositorio
         */
        c.gridy = 5;
        JButton addSubPart = new JButton("addsubpart");
        addSubPart.addActionListener(e -> {
            int quantity;

            try {
                quantity = Integer.parseInt(currentPartQuantity.getText());
            } catch (NumberFormatException ex) {
                System.err.println("addsubpart: Isso nao e um numero");
                return;
            }

            addsubpart(quantity);
        });

        /**
         * O evento do botao addp vai:
         * 1. Verificar os campos Quantidade, Nome, Codigo, Descricao
         * 2. Caso exista uma parte selecionada vai colocar as informacoes na parte atual
         * 3. Vai enviar para o servidor as informacoes de uma Part para ela ser implementada
         */
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

            if (currentPart != null) {
                currentPart.setCode(UUID.fromString(currentPartCode.getText()));
                currentPart.setName(currentPartName.getText().trim());
                currentPart.setDescription(currentPartDescription.getText().trim());
            }

            addp(currentPartCode.getText(),
                     currentPartName.getText().trim(),
                    currentPartDescription.getText().trim(),
                    quantityInt,
                    currentSubPartList);
        });

        /**
         * O evento do botao show vai:
         * 1. Verificar se existe uma parte selecionada
         * 1.1 Se existir vai exibir as informacoes dessa Part
         */
        JButton show = new JButton("showp");
        show.addActionListener(e -> {
            if (currentPart == null) return;
            showp();
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

/**
 * SubComponentCellRenderer: representar as informacoes de uma subPart dentro de uma peca em uma JList
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
