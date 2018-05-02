package main;

import org.apache.commons.lang3.SystemUtils;
import JCodeArea.JCodeArea;
import JCodeArea.Theme;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Scanner;

public class RamCompiler extends JFrame
{
    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem openMenu;
    private JMenuItem saveMenu;
    private JMenuItem menuItem3;
    private JButton runButton;
    private JButton debugButton;
    private JButton execNextButton;
    private JButton button3;
    private JScrollPane scrollPane3;
    private JTable ramTable;
    private JPanel panel2;
    private JScrollPane scrollPane4;
    private JTextPane outputPane;
    private JLabel label2;
    private JLabel label3;
    private JPanel panel1;
    private JLabel label5;
    private JLabel accLabel;
    private JLabel label6;
    private JLabel pcLabel;
    private JPanel panel3;
    private JRadioButton radioButton1;
    private JRadioButton radioButton2;
    private JRadioButton radioButton3;
    private JLabel inputFileNameLabel;
    private JLabel label7;
    private JLabel lastInputLabel;
    private JLabel label8;
    private JLabel lastInstructionLabel;
    private JLabel label9;
    private JLabel jumpLabel;
    private JLabel label1;
    private JLabel label4;
    private JButton button2;
    private JCodeArea codeArea;

    private JFileChooser fc;
    private BufferedReader br;
    private BufferedWriter bw;

    private final int MANUAL_INPUT = 0;
    private final int AUTOMATIC_INPUT = 1;
    private final int FILE_INPUT= 2;

    private Thread prog;
    private boolean progRunning;

    private Compiler compiler;
    private RAM ram;

    private String output;
    private InputDialog inputDialog;

    private int inputMode;
    private int[] inputArray;
    private int inputCount;
    private File inputFile;
    private Scanner inputFileScanner;

    private boolean isLinux;

    private RamCompiler()
    {
        initComponents();
    }

    private void initComponents()
    {
        try
        {
            if(SystemUtils.IS_OS_LINUX)
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                isLinux = true;
            }
            else UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }

        codeArea = new JCodeArea(Theme.DARK_THEME);
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        openMenu = new JMenuItem();
        saveMenu = new JMenuItem();
        menuItem3 = new JMenuItem();
        runButton = new JButton();
        debugButton = new JButton();
        execNextButton = new JButton();
        button3 = new JButton();
        scrollPane3 = new JScrollPane();
        ramTable = new JTable();
        panel2 = new JPanel();
        scrollPane4 = new JScrollPane();
        outputPane = new JTextPane();
        label2 = new JLabel();
        label3 = new JLabel();
        panel1 = new JPanel();
        label5 = new JLabel();
        accLabel = new JLabel();
        label6 = new JLabel();
        pcLabel = new JLabel();
        panel3 = new JPanel();
        radioButton1 = new JRadioButton();
        radioButton2 = new JRadioButton();
        radioButton3 = new JRadioButton();
        inputFileNameLabel = new JLabel();
        label7 = new JLabel();
        lastInputLabel = new JLabel();
        label8 = new JLabel();
        lastInstructionLabel = new JLabel();
        label9 = new JLabel();
        jumpLabel = new JLabel();
        label1 = new JLabel();
        label4 = new JLabel();
        button2 = new JButton();

        //======== this ========

        setName("frame");
        setTitle("Simulatore RAM");
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- openMenu ----
                openMenu.setText("Apri");
                openMenu.addActionListener(this::openMenuActionPerformed);
                menu1.add(openMenu);

                //---- saveMenu ----
                saveMenu.setText("Salva");
                saveMenu.addActionListener(this::saveMenuActionPerformed);
                menu1.add(saveMenu);

                //---- menuItem3 ----
                menuItem3.setText("Esci");
                menuItem3.addActionListener(e -> menuItem3ActionPerformed());
                menu1.add(menuItem3);
            }
            menuBar1.add(menu1);

            //---- runButton ----
            runButton.setText("Esegui");
            runButton.setIcon(new ImageIcon(getClass().getResource("/res/run.png")));
            runButton.setMaximumSize(new Dimension(96, 27));
            runButton.addActionListener(this::runButtonActionPerformed);
            menuBar1.add(runButton);

            //---- debugButton ----
            debugButton.setText("Debug");
            debugButton.setIcon(new ImageIcon(getClass().getResource("/res/debug.png")));
            debugButton.setMinimumSize(new Dimension(72, 29));
            debugButton.setMaximumSize(new Dimension(96, 29));
            debugButton.addActionListener(this::debugButtonActionPerformed);
            menuBar1.add(debugButton);

            //---- execNextButton ----
            execNextButton.setIcon(new ImageIcon(getClass().getResource("/res/nextInstruction.png")));
            execNextButton.setMinimumSize(new Dimension(72, 29));
            execNextButton.setMaximumSize(new Dimension(96, 29));
            execNextButton.setEnabled(false);
            execNextButton.addActionListener(this::execNextButtonActionPerformed);
            menuBar1.add(execNextButton);

            //---- button3 ----
            button3.setText("HALT");
            button3.setIcon(new ImageIcon(getClass().getResource("/res/stop.png")));
            button3.setMinimumSize(new Dimension(72, 29));
            button3.setMaximumSize(new Dimension(96, 29));
            button3.addActionListener(this::button3ActionPerformed);
            menuBar1.add(button3);
        }
        setJMenuBar(menuBar1);

        codeArea.setSize(535, 655);
        codeArea.setLocation(0, 0);
        codeArea.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                codeAreaKeyTyped(e);
            }
        });

        contentPane.add(codeArea);

        //======== scrollPane3 ========
        {
            scrollPane3.setBorder(LineBorder.createBlackLineBorder());

            //---- ramTable ----
            ramTable.setBorder(null);
            ramTable.setRowSelectionAllowed(false);
            ramTable.setShowVerticalLines(false);
            ramTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            ramTable.setCellSelectionEnabled(true);
            ramTable.setModel(new DefaultTableModel(new Object[][] {}, new String[] {"Address", "Value"})
            {
                Class<?>[] columnTypes = new Class<?>[] {Integer.class, Integer.class};
                boolean[] columnEditable = new boolean[] {false, false};
                @Override
                public Class<?> getColumnClass(int columnIndex)
                {
                    return columnTypes[columnIndex];
                }
                @Override
                public boolean isCellEditable(int rowIndex, int columnIndex)
                {
                    return columnEditable[columnIndex];
                }
            });
            scrollPane3.setViewportView(ramTable);
        }
        contentPane.add(scrollPane3);
        scrollPane3.setBounds(550, 260, 180, 395);

        //======== panel2 ========
        {
            panel2.setBackground(new Color(233, 233, 233));
            panel2.setLayout(null);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel2.getComponentCount(); i++) {
                    Rectangle bounds = panel2.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel2.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel2.setMinimumSize(preferredSize);
                panel2.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(panel2);
        panel2.setBounds(0, 655, 1005, 20);

        //======== scrollPane4 ========
        {
            scrollPane4.setFont(new Font("Monospaced", Font.PLAIN, 14));
            scrollPane4.setForeground(Color.green);
            scrollPane4.setBorder(null);

            //---- outputPane ----
            outputPane.setBorder(null);
            if(!isLinux)outputPane.setForeground(Color.green);
            else outputPane.setForeground(Color.black);
            outputPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
            outputPane.setBackground(Color.darkGray);
            outputPane.setEditable(false);
            scrollPane4.setViewportView(outputPane);
        }
        contentPane.add(scrollPane4);
        scrollPane4.setBounds(550, 45, 435, 170);

        //---- label2 ----
        label2.setText("Output");
        label2.setFont(new Font("Monospaced", Font.BOLD, 16));
        contentPane.add(label2);
        label2.setBounds(550, 15, 75, label2.getPreferredSize().height);

        //---- label3 ----
        label3.setText("RAM");
        label3.setFont(new Font("Monospaced", Font.BOLD, 16));
        contentPane.add(label3);
        label3.setBounds(550, 230, 75, 19);

        //======== panel1 ========
        {
            panel1.setBackground(Color.white);
            panel1.setBorder(LineBorder.createBlackLineBorder());
            panel1.setLayout(null);

            //---- label5 ----
            label5.setText("Accumulatore");
            panel1.add(label5);
            label5.setBounds(5, 10, label5.getPreferredSize().width, 20);

            //---- accLabel ----
            accLabel.setBorder(LineBorder.createBlackLineBorder());
            accLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(accLabel);
            accLabel.setBounds(115, 10, 105, 20);

            //---- label6 ----
            label6.setText("Program counter");
            panel1.add(label6);
            label6.setBounds(5, 40, label6.getPreferredSize().width, 20);

            //---- pcLabel ----
            pcLabel.setBorder(LineBorder.createBlackLineBorder());
            pcLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(pcLabel);
            pcLabel.setBounds(115, 40, 105, 20);

            //======== panel3 ========
            {
                panel3.setBackground(Color.white);
                panel3.setBorder(new TitledBorder("Tipo input"));
                panel3.setLayout(null);

                //---- radioButton1 ----
                radioButton1.setText("Manuale");
                radioButton1.setSelected(true);
                radioButton1.addActionListener(this::radioButton1ActionPerformed);
                panel3.add(radioButton1);
                radioButton1.setBounds(new Rectangle(new Point(10, 25), radioButton1.getPreferredSize()));

                //---- radioButton2 ----
                radioButton2.setText("Automatico");
                radioButton2.addActionListener(this::radioButton2ActionPerformed);
                panel3.add(radioButton2);
                radioButton2.setBounds(new Rectangle(new Point(10, 50), radioButton2.getPreferredSize()));

                //---- radioButton3 ----
                radioButton3.setText("File");
                radioButton3.addActionListener(this::radioButton3ActionPerformed);
                panel3.add(radioButton3);
                radioButton3.setBounds(new Rectangle(new Point(10, 75), radioButton3.getPreferredSize()));
                panel3.add(inputFileNameLabel);
                inputFileNameLabel.setBounds(70, 75, 140, 25);

                { // compute preferred size
                    Dimension preferredSize = new Dimension();
                    for(int i = 0; i < panel3.getComponentCount(); i++) {
                        Rectangle bounds = panel3.getComponent(i).getBounds();
                        preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                        preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                    }
                    Insets insets = panel3.getInsets();
                    preferredSize.width += insets.right;
                    preferredSize.height += insets.bottom;
                    panel3.setMinimumSize(preferredSize);
                    panel3.setPreferredSize(preferredSize);
                }
            }
            panel1.add(panel3);
            panel3.setBounds(5, 165, 220, 115);

            //---- label7 ----
            label7.setText("Ultimo input");
            panel1.add(label7);
            label7.setBounds(5, 70, 104, 20);

            //---- lastInputLabel ----
            lastInputLabel.setBorder(LineBorder.createBlackLineBorder());
            lastInputLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(lastInputLabel);
            lastInputLabel.setBounds(115, 70, 105, 20);

            //---- label8 ----
            label8.setText("Ultima istruzione");
            panel1.add(label8);
            label8.setBounds(5, 100, 110, 20);

            //---- lastInstructionLabel ----
            lastInstructionLabel.setBorder(LineBorder.createBlackLineBorder());
            lastInstructionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            panel1.add(lastInstructionLabel);
            lastInstructionLabel.setBounds(115, 100, 105, 20);

            //---- label9 ----
            label9.setText("JUMP");
            panel1.add(label9);
            label9.setBounds(5, 130, 110, 20);

            //---- jumpLabel ----
            jumpLabel.setBorder(LineBorder.createBlackLineBorder());
            jumpLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            jumpLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
            panel1.add(jumpLabel);
            jumpLabel.setBounds(50, 130, 170, 20);

            //---- label1 ----
            label1.setIcon(new ImageIcon(getClass().getResource("/res/logo-polimi.jpg")));
            panel1.add(label1);
            label1.setBounds(60, 285, 105, 100);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for(int i = 0; i < panel1.getComponentCount(); i++) {
                    Rectangle bounds = panel1.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = panel1.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                panel1.setMinimumSize(preferredSize);
                panel1.setPreferredSize(preferredSize);
            }
        }
        contentPane.add(panel1);
        panel1.setBounds(750, 260, 230, 395);

        //---- label4 ----
        label4.setText("Info");
        label4.setFont(new Font("Monospaced", Font.BOLD, 16));
        contentPane.add(label4);
        label4.setBounds(750, 230, 75, 19);

        //---- button2 ----
        button2.setText("Clear");
        button2.addActionListener(this::button2ActionPerformed);
        contentPane.add(button2);
        button2.setBounds(new Rectangle(new Point(910, 10), button2.getPreferredSize()));

        { // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }
        pack();
        setLocationRelativeTo(null);

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(radioButton1);
        buttonGroup1.add(radioButton2);
        buttonGroup1.add(radioButton3);

        fc = new JFileChooser();

        inputMode = MANUAL_INPUT;
        output = "";
        ram = new RAM();
        compiler = new Compiler(ram, this);

        setVisible(true);
    }


    public int read(int line, int memCell)
    {
        int input = 0;
        switch(inputMode)
        {
            case MANUAL_INPUT:
                (inputDialog = new InputDialog(this, line, memCell, false)).setVisible(true);
                input = inputDialog.getInput();
                break;
            case AUTOMATIC_INPUT:
                if(inputCount < inputArray.length)
                {
                    input = inputArray[inputCount];
                    inputCount++;
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Stringa di input terminata!\nDefault input = 0", "Errore READ", JOptionPane.WARNING_MESSAGE);
                    input = 0;
                }
                break;
            case FILE_INPUT:
                if(inputFileScanner.hasNextInt())input = inputFileScanner.nextInt();
                else
                {
                    JOptionPane.showMessageDialog(this, "File di input terminato!\nDefault input = 0", "Errore READ", JOptionPane.WARNING_MESSAGE);
                    input = 0;
                }
                System.out.println(input);
                break;
        }

        lastInputLabel.setText(String.valueOf(input));

        return input;
    }

    public void write(int value)
    {
        output += value+"\n";
        outputPane.setText(output);
    }

    public void updateTable()
    {
        ((DefaultTableModel)ramTable.getModel()).setRowCount(0);
        for(int i = 0; i < ram.getMem().size(); i++)
        {
            ((DefaultTableModel)ramTable.getModel()).addRow(new Object[]{i, ram.getCell(i)});
        }
        accLabel.setText(String.valueOf(ram.getCell(0)));
    }

    public void setProgramCounterLabel(int pc)
    {
        this.pcLabel.setText(String.valueOf(pc));
    }

    private void menuItem3ActionPerformed()
    {
        System.exit(0);
    }

    //ESEGUI
    private void runButtonActionPerformed(ActionEvent e)
    {
        runButton.setEnabled(false);
        debugButton.setEnabled(false);
        runProgram(false);
        runControl();
    }

    //DEBUG
    private void debugButtonActionPerformed(ActionEvent e)
    {
        runButton.setEnabled(false);
        debugButton.setEnabled(false);
        execNextButton.setEnabled(true);
        runProgram(true);
        runControl();
    }

    private void runProgram(boolean debugMode)
    {
        prog = new Thread(() -> {
            lastInstructionLabel.setText("");
            lastInputLabel.setText("");
            jumpLabel.setText("");
            accLabel.setText("");
            pcLabel.setText("");

            progRunning = true;
            Status s;
            ram.reset();
            compiler.setDebugMode(debugMode);

            if(inputMode == AUTOMATIC_INPUT)
            {
                (inputDialog = new InputDialog(RamCompiler.this, 0, 0, true)).setVisible(true);
                inputArray = inputDialog.getInputArray();
                inputCount = 0;
            }

            if(inputMode == FILE_INPUT) try
            {
                inputFileScanner = new Scanner(inputFile);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

            if((s = compiler.setProgram(codeArea.getText())).getStatus() != 0)
            {
                if(s.getStatus() != -1)JOptionPane.showMessageDialog(RamCompiler.this, s.getMessage(), "Errore alla linea "+(s.getLine()+1), JOptionPane.ERROR_MESSAGE);
            }
            else if((s = compiler.run()).getStatus() != 0)
            {
                if(s.getStatus() != -1)JOptionPane.showMessageDialog(RamCompiler.this, s.getMessage(), "Errore alla linea "+(s.getLine()+1), JOptionPane.ERROR_MESSAGE);
            }
            progRunning = false;
        });

        prog.start();
    }

    private void runControl()
    {
        new Thread(() -> {
            while(progRunning) try
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e1)
            {
                e1.printStackTrace();
            }
            runButton.setEnabled(true);
            debugButton.setEnabled(true);
            execNextButton.setEnabled(false);
            if(inputMode == FILE_INPUT)inputFileScanner.close();
        }).start();
    }

    private void openMenuActionPerformed(ActionEvent e)
    {
        try
        {
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                String text = "";
                String line;
                br = new BufferedReader(new FileReader(fc.getSelectedFile()));
                while((line = br.readLine()) != null)
                {
                    text += line+"\n";
                }
                br.close();
                codeArea.setText(text);
            }
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    private void saveMenuActionPerformed(ActionEvent e)
    {
        try
        {
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                String[] lines = codeArea.getText().split("\n");
                bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
                for(String line : lines)
                {
                    bw.write(line + "\n");
                    bw.flush();
                }
                bw.close();
            }

        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }

    private void codeAreaKeyTyped(KeyEvent e)
    {
        e.setKeyChar(Character.toUpperCase(e.getKeyChar()));
    }

    private void button2ActionPerformed(ActionEvent e)
    {
        output = "";
        outputPane.setText(output);
    }

    //HALT
    private void button3ActionPerformed(ActionEvent e)
    {
        if(progRunning)
        {
            compiler.setDebugMode(false);
            compiler.stop();
            compiler.execNextInstruction();
        }
    }

    private void execNextButtonActionPerformed(ActionEvent e)
    {
        if(progRunning && compiler.isDebugMode())
        {
            compiler.execNextInstruction();
        }
    }

    private void radioButton1ActionPerformed(ActionEvent e)
    {
        inputMode = MANUAL_INPUT;
        inputFileNameLabel.setText("");
    }

    private void radioButton2ActionPerformed(ActionEvent e)
    {
        inputMode = AUTOMATIC_INPUT;
        inputFileNameLabel.setText("");
    }

    private void radioButton3ActionPerformed(ActionEvent e)
    {
        if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                inputFile = fc.getSelectedFile();
                inputFileScanner = new Scanner(inputFile);
                inputMode = FILE_INPUT;
                inputFileNameLabel.setText(inputFile.getName());
            }
            catch (FileNotFoundException e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            switch(inputMode)
            {
                case MANUAL_INPUT:
                    radioButton3.setSelected(false);
                    radioButton1.setSelected(true);
                    break;
                case AUTOMATIC_INPUT:
                    radioButton3.setSelected(false);
                    radioButton2.setSelected(true);
                    break;
            }
        }
    }

    public void setLastInstruction(String str)
    {
        this.lastInstructionLabel.setText(str);
    }

    public void lastJump(String str)
    {
        this.jumpLabel.setText(str);
    }

    public static void main(String[] args)
    {
        new RamCompiler();
    }
}
