package eshop.test;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TestEventListener {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Real-Time Calculation Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2));

        JLabel label1 = new JLabel("Number 1:");
        JLabel label2 = new JLabel("Number 2:");
        JLabel labelResult = new JLabel("Result:");

        JTextField textField1 = new JTextField();
        JTextField textField2 = new JTextField();
        JTextField textFieldResult = new JTextField();
        textFieldResult.setEditable(false); // Make the result field read-only

        frame.add(label1);
        frame.add(textField1);
        frame.add(label2);
        frame.add(textField2);
        frame.add(labelResult);
        frame.add(textFieldResult);

        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                calculate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                calculate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                calculate();
            }

            private void calculate() {
                try {
                    double number1 = Double.parseDouble(textField1.getText());
                    double number2 = Double.parseDouble(textField2.getText());
                    double result = number1 + number2; // Example calculation
                    textFieldResult.setText(String.valueOf(result));
                } catch (NumberFormatException ex) {
                    textFieldResult.setText("Invalid input");
                }
            }
        };

        textField1.getDocument().addDocumentListener(documentListener);
        textField2.getDocument().addDocumentListener(documentListener);

        frame.pack();
        frame.setVisible(true);
    }
}

