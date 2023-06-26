import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class FileEncryptorGUI extends JFrame {

    private JTextField inputFilePathField;
    private JTextField outputFilePathField;
    private JPasswordField passphraseField;
    private JRadioButton encryptRadioButton;
    private JRadioButton decryptRadioButton;

    public FileEncryptorGUI() {
        setTitle("File Encryptor");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));

        JLabel titleLabel = new JLabel("File Encryptor");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(titleLabel);

        JLabel inputLabel = new JLabel("Input File:");
        inputFilePathField = new JTextField(20);
        JButton inputFileButton = new JButton("Browse");
inputFileButton.setBorder(BorderFactory.createEmptyBorder());
inputFileButton.setOpaque(false);
inputFileButton.setContentAreaFilled(false);


        inputFileButton.addActionListener(new BrowseActionListener(inputFilePathField));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(inputLabel);
        inputPanel.add(inputFilePathField);
        inputPanel.add(inputFileButton);
        panel.add(inputPanel);

        JLabel outputLabel = new JLabel("Output File:");
        outputFilePathField = new JTextField(20);
        JButton outputFileButton = new JButton("Browse");
        outputFileButton.addActionListener(new BrowseActionListener(outputFilePathField));
        JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        outputPanel.add(outputLabel);
        outputPanel.add(outputFilePathField);
        outputPanel.add(outputFileButton);
        panel.add(outputPanel);

        JLabel passphraseLabel = new JLabel("Passphrase:");
        passphraseField = new JPasswordField(20);
        JPanel passphrasePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passphrasePanel.add(passphraseLabel);
        passphrasePanel.add(passphraseField);
        panel.add(passphrasePanel);

        encryptRadioButton = new JRadioButton("Encrypt");
        decryptRadioButton = new JRadioButton("Decrypt");
        ButtonGroup modeButtonGroup = new ButtonGroup();
        modeButtonGroup.add(encryptRadioButton);
        modeButtonGroup.add(decryptRadioButton);
        JPanel modePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        modePanel.add(new JLabel("Mode:"));
        modePanel.add(encryptRadioButton);
        modePanel.add(decryptRadioButton);
        panel.add(modePanel);

        JButton processButton = new JButton("Process");
        processButton.addActionListener(new ProcessActionListener());
        panel.add(processButton);

        add(panel);
    }

    private class BrowseActionListener implements ActionListener {
        private JTextField textField;

        public BrowseActionListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(FileEncryptorGUI.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                textField.setText(selectedFile.getAbsolutePath());
            }
        }
    }

    private class ProcessActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String inputFilePath = inputFilePathField.getText();
            String outputFilePath = outputFilePathField.getText();
            String passphrase = new String(passphraseField.getPassword());
            boolean isEncryptMode = encryptRadioButton.isSelected();

            // Perform encryption/decryption based on the input values
            // You can call the existing encryption/decryption logic here

            // Display a message to indicate completion
            String message = isEncryptMode ? "Encryption" : "Decryption";
            JOptionPane.showMessageDialog(FileEncryptorGUI.this, message + " complete!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                FileEncryptorGUI gui = new FileEncryptorGUI();
                gui.setVisible(true);
            }
        });
    }
}
