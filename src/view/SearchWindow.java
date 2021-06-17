package view;

import contexts.ActualNodeData;
import objects.NodeInformation;
import threads.ConnectNoBlocking;
import utils.CustomOutputStreamPane;
import utils.Print;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintStream;

public class SearchWindow extends JFrame implements ActionListener {
    private ActualNodeData nodeData;
    private static SearchWindow instance;
    //States
    private int port;
    //Auxiliar global components
    private JPanel mainPanel;
    private JTextField JTport , JTSearch;
    private JButton JBport, JBsetSource, JBDestination, JBsearch, JBconnection;
    private JPanel dataPanel, foldersPanel, adyacentsNodes, myNode;
    private JTable table;
    private JLabel actualNode, predecessorNode, succesorNode;
    private DefaultTableModel tableModel;
    //Auxiliar global Colors
    private final Color bgColor=new Color(240, 251, 255);
    private final Color darkBlue=new Color(35,103,205);
    private final Color textBlue=new Color(0,121,173);

    public static SearchWindow getWindow(){
        if(instance==null){
            instance= new SearchWindow();
        }
        return instance;
    }

    private SearchWindow(){
        configureFrame();
        initComponents();
        mainPanel.revalidate();
        mainPanel.repaint();
        nodeData=ActualNodeData.getInstance();
        Print.info("Write a port and select the folders to continue...");
    }

    private void configureFrame() {
        setLocationRelativeTo(null);
        setSize(600,670);
        setTitle("SearchFilesInRing | Port: No Port");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setIconImage(new ImageIcon(getClass().getResource("/imgs/fileSearch.png")).getImage());
        mainPanel=new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(bgColor);
        this.setVisible(true);
        this.add(mainPanel);
    }

    private void initComponents() {
        //Logo
        JPanel logo = new JPanel();
        logo.setBounds(0, 0, 376, 92);
        logo.setBackground(darkBlue);
        logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("SearchFilesInRing");
        title.setFont(new Font("Segoe UI", Font.BOLD, 41));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(bgColor);
        logo.add(Box.createRigidArea(new Dimension(1, 15)));
        logo.add(title);
        mainPanel.add(logo);

        //Port
        JTport = new JTextField();
        JTport.setBounds(385, 27, 88, 40);
        JTport.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        JTport.setForeground(textBlue);
        JTport.setHorizontalAlignment(SwingConstants.CENTER);
        JTport.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char caracter = e.getKeyChar();
                if ((((caracter < '0') || (caracter > '9')) && (caracter != '\b'))
                        || (JTport.getText().length() >= 4)) {
                    e.consume();
                }
            }
        });
        mainPanel.add(JTport);

        JBport = new JButton("Port");
        JBport.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JBport.setForeground(textBlue);
        JBport.setBounds(478, 27, 87, 40);
        JBport.addActionListener(this);
        mainPanel.add(JBport);

        //DataPanel
        dataPanel = new JPanel();
        dataPanel.setBounds(35, 105, 509, 70);
        dataPanel.setLayout(new BoxLayout(dataPanel,BoxLayout.Y_AXIS));
        dataPanel.setBackground(darkBlue);

        foldersPanel= new JPanel();
        foldersPanel.setBackground(darkBlue);

        JBsetSource= new JButton("Source Folder");
        JBsetSource.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JBsetSource.setForeground(textBlue);
        JBsetSource.setSize(216,40);
        JBsetSource.addActionListener(this);
        foldersPanel.add(JBsetSource);

        foldersPanel.add(Box.createRigidArea(new Dimension(30, 1)));

        JBDestination= new JButton("Destination Folder");
        JBDestination.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JBDestination.setForeground(textBlue);
        JBDestination.setSize(216,40);
        JBDestination.addActionListener(this);
        foldersPanel.add(JBDestination);

        JBconnection= new JButton("Connect...");
        JBconnection.setFont(new Font("Segoe UI", Font.BOLD, 20));
        JBconnection.setForeground(textBlue);
        JBconnection.setSize(500,40);
        JBconnection.addActionListener(this);

        dataPanel.add(Box.createRigidArea(new Dimension(1, 13)));
        dataPanel.add(foldersPanel);

        //Nodes
        adyacentsNodes = new JPanel(new GridLayout(2,1));

        JLabel JLpredtext= new JLabel("Predeccesor Node: ");
        JLpredtext.setFont(new Font("Segoe UI", Font.BOLD, 19));
        JLpredtext.setForeground(bgColor);

        predecessorNode=new JLabel("waiting...");
        predecessorNode.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        predecessorNode.setForeground(bgColor);

        JLabel JLsucctext= new JLabel("Successor Node: ");
        JLsucctext.setFont(new Font("Segoe UI", Font.BOLD, 19));
        JLsucctext.setForeground(bgColor);

        succesorNode=new JLabel("waiting...");
        succesorNode.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        succesorNode.setForeground(bgColor);

        JPanel nodeText= new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel nodeText2= new JPanel(new FlowLayout(FlowLayout.CENTER));
        nodeText.add(JLpredtext);
        nodeText.add(predecessorNode);
        nodeText.setBackground(darkBlue);
        adyacentsNodes.add(nodeText);
        nodeText2.add(JLsucctext);
        nodeText2.add(succesorNode);
        nodeText2.setBackground(darkBlue);
        adyacentsNodes.add(nodeText2);

        myNode= new JPanel();
        myNode.setLayout(new BoxLayout(myNode,BoxLayout.Y_AXIS));
        JLabel JLmynodetext= new JLabel("This Node: ");
        JLmynodetext.setFont(new Font("Segoe UI", Font.BOLD, 19));
        JLmynodetext.setForeground(bgColor);
        JLmynodetext.setAlignmentX(Component.CENTER_ALIGNMENT);
        actualNode=new JLabel("waiting...");
        actualNode.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        actualNode.setForeground(bgColor);
        actualNode.setAlignmentX(Component.CENTER_ALIGNMENT);
        myNode.add(JLmynodetext);
        myNode.add(actualNode);
        myNode.setBackground(darkBlue);

        mainPanel.add(dataPanel);

        //Table
        String headers[]={"Id","IPAddress", "RMIPort"};
        tableModel = new DefaultTableModel(headers, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table= new JTable(tableModel);
        table.getTableHeader().setBackground(darkBlue);
        table.getTableHeader().setForeground(bgColor);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 20));
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setBackground(Color.WHITE);
        table.setForeground(textBlue);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.setFillsViewportHeight(true);
        table.setFocusable(false);
        setCellsAlignment();
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBounds(35,190,509,206);
        scrollTable.setBackground(bgColor);

        mainPanel.add(scrollTable);

        //Search
        JTSearch = new JTextField();
        JTSearch.setFont(new Font("Segoe UI", Font.PLAIN, 28));
        JTSearch.setForeground(textBlue);
        JTSearch.setBounds(35, 411, 400, 40);
        mainPanel.add(JTSearch);

        JBsearch = new JButton("Search");
        JBsearch.setFont(new Font("Segoe UI", Font.BOLD, 17));
        JBsearch.setForeground(textBlue);
        JBsearch.setBounds(456, 410, 87, 40);
        JBsearch.setEnabled(false);
        JBsearch.addActionListener(this);
        mainPanel.add(JBsearch);

        JTextPane textArea = new JTextPane();

        JScrollPane scrollTextArea = new JScrollPane(textArea);
        scrollTextArea.setBounds(35,465,506,140);
        scrollTextArea.setAutoscrolls(true);
        PrintStream printStream = new PrintStream(new CustomOutputStreamPane(textArea));
        System.setOut(printStream);
        mainPanel.add(scrollTextArea);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton) e.getSource();
        if(btn.equals(JBport)){
            if(JTport.getText().length()<4){
                Print.error("Write a valid port...");
            }else{
                port=Integer.parseInt(JTport.getText());
                btn.setEnabled(false);
                JTport.setEditable(false);
                nodeData.setMyPort(port);
                this.setTitle("SearchFilesInRing | Port: "+port);
                Print.info("Port of datagram server: "+port);
            }
        }else{
            if(btn.equals(JBsetSource) || btn.equals(JBDestination)){
                File temp =selectDirectory();
                if(temp!=null){
                    boolean isSource=true;
                    if(btn.equals(JBsetSource)){
                        nodeData.setSource(temp);
                        Print.info("Source Folder: "+temp);
                    }else{
                        nodeData.setDestination(temp);
                        Print.info("Destination Folder: "+temp);
                        isSource=false;
                    }
                    foldersPanel.removeAll();
                    if(nodeData.getSource()!=null && nodeData.getDestination()!=null){
                        foldersPanel.add(JBconnection);
                    }else{
                        if(isSource)
                            foldersPanel.add(JBDestination);
                        else
                            foldersPanel.add(JBsetSource);
                    }
                    foldersPanel.revalidate();
                    foldersPanel.repaint();
                }
            }else{
                if(btn.equals(JBconnection)){
                    if(port!=0 && nodeData.getSource()!=null && nodeData.getDestination()!=null){
                        Print.strong("Getting members, wait 10 seconds...");
                        ConnectNoBlocking.getInstance().start();
                        dataPanel.removeAll();
                        dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));
                        dataPanel.add(adyacentsNodes);
                        dataPanel.add(myNode);
                        dataPanel.revalidate();
                        dataPanel.repaint();
                    }else{
                        Print.error("Write a port to continue...");
                    }
                }else{
                    if(btn.equals(JBsearch)){
                        if(JTSearch.getText().length()==0){
                            Print.error("Write a filename in the textbox...");
                        }else{
                            String fileName=JTSearch.getText();
                            nodeData.searchFileOnNet(fileName);
                        }
                    }
                }
            }
        }
    }

    private void setCellsAlignment()
    {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        TableModel tableModel = table.getModel();
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++)
        {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }

    private File selectDirectory(){
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Select a folder...");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        else {
            Print.error("Select a folder...");
            return null;
        }
    }

    public void updateTableAndLables(){
        try{
            actualNode.setText(nodeData.getMyInfo().toString());
            predecessorNode.setText(nodeData.getPredecessor().toString());
            succesorNode.setText(nodeData.getSuccessor().toString());
            if (tableModel.getRowCount() > 0) {
                for (int i=tableModel.getRowCount()-1; i>-1; i--) {
                    tableModel.removeRow(i);
                }
            }
            for(int i=0;i<nodeData.getMembersOfRing().size();i++){
                NodeInformation node=nodeData.getMembersOfRing().get(i);
                String temp[]={(i+1)+"",node.toString(), node.getRMIport()+""};
                tableModel.addRow(temp);
                if(nodeData.isMyIndex(i))
                    table.setRowSelectionInterval(i,i);
            }
        }catch(Exception e){
            //Don't do anything, it can happen
        }
    }

    public void enableSearch(){
        JBsearch.setEnabled(true);
    }
}
