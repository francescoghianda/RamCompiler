package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputDialog extends JDialog
{
    private JPanel panel1;
    private JLabel label1;
    private JFormattedTextField formattedTextField1;
    private JButton okButton;
    private JPanel hSpacer1;
    private JPanel hSpacer2;
    private JPanel vSpacer1;

    private int line;
    private int memCell;

    private boolean automaticInput;
    private char prec;

    public InputDialog(Frame owner, int line, int memCell, boolean automaticInput)
    {
        super(owner);
        this.line = line;
        this.memCell = memCell;
        this.automaticInput = automaticInput;
        initComponents();
    }

    private void okButtonActionPerformed(ActionEvent e)
    {
        this.close();
    }

    private void formattedTextField1KeyTyped(KeyEvent e)
    {
        char c = e.getKeyChar();
        if(automaticInput && prec != ' ')
        {
            prec = c;
            if(!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' || c == ' '))e.consume();
        }
        else
        {
            prec = c;
            if(!(c == '0' || c == '1' || c == '2' || c == '3' || c == '4' || c == '5' || c == '6' || c == '7' || c == '8' || c == '9'))e.consume();
        }
    }

    private void close()
    {
        if(formattedTextField1.getText().trim().equals(""))formattedTextField1.setText("0");
        this.setVisible(false);
    }

    private void initComponents()
    {
        setModal(true);

        panel1 = new JPanel();
        label1 = new JLabel();
        formattedTextField1 = new JFormattedTextField();
        okButton = new JButton();
        hSpacer1 = new JPanel(null);
        hSpacer2 = new JPanel(null);
        vSpacer1 = new JPanel(null);

        //======== this ========
        setResizable(false);
        setTitle("Input");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setLayout(null);

            //---- label1 ----
            label1.setText("READ:  (linea: "+line+" | RAM address -> "+memCell+")");
            panel1.add(label1);
            label1.setBounds(20, 10, 265, label1.getPreferredSize().height);

            //---- formattedTextField1 ----
            formattedTextField1.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
            formattedTextField1.setOpaque(true);

            formattedTextField1.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyTyped(KeyEvent e) {
                    formattedTextField1KeyTyped(e);
                }
            });

            panel1.add(formattedTextField1);
            formattedTextField1.setBounds(20, 30, 265, 31);

            //---- okButton ----
            okButton.setText("OK");
            okButton.addActionListener(e -> okButtonActionPerformed(e));
            panel1.add(okButton);
            okButton.setBounds(120, 75, 70, 25);
            panel1.add(hSpacer1);
            hSpacer1.setBounds(0, 35, 25, 20);
            panel1.add(hSpacer2);
            hSpacer2.setBounds(285, 35, 25, 20);
            panel1.add(vSpacer1);
            vSpacer1.setBounds(135, 100, 25, 25);

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
        contentPane.add(panel1, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
    }


    public int getInput()
    {
        if(!automaticInput)
        {
            return Integer.parseInt(this.formattedTextField1.getText());
        }
        return 0;
    }

    public int[] getInputArray()
    {
        String[] tokens = formattedTextField1.getText().trim().split(" ");
        int[] inputArray = new int[tokens.length];

        for(int i = 0; i < tokens.length; i++)
        {
            inputArray[i] = Integer.parseInt(tokens[i]);
        }

        return inputArray;
    }
}
