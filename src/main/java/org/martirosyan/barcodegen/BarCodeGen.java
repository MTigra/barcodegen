package org.martirosyan.barcodegen;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class BarCodeGen extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton browseButton;
    private JTextField filePathTextField;
    private JRadioButton пропуститьПервуюСтрокуRadioButton;
    private JTextField barCodeHeightTextField;
    List<ItemModel> items;

    public BarCodeGen() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onBrowseButtonClicked();
                } catch (IOException | URISyntaxException ioException) {
                    ioException.printStackTrace();
                }

            }
        });
    }

    private void onOK() throws IOException {
        PdfCreatorService pdfCreatorService = new PdfCreatorService();
        pdfCreatorService.writeTo("barcodes.pdf",items);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onBrowseButtonClicked() throws IOException, URISyntaxException {

        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.home"))); //Downloads Directory as default
        chooser.setDialogTitle("Выберите Файл Excel");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Excel file (*.xlsx,*.xls)","xlsx","xls"));
        chooser.setAcceptAllFileFilterUsed(false);

        String filePath = null;
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            filePath = chooser.getSelectedFile().getAbsolutePath();
            filePathTextField.setText(filePath);
        }
//        ExcelPreview excelPreview = new ExcelPreview(Paths.get(filePath));
        NameAndCodeProps nameAndCodeProps = new NameAndCodeProps();
        ExcelPrev excelPreview = new ExcelPrev(filePath, nameAndCodeProps);
        excelPreview.pack();
        excelPreview.setVisible(true);

        ExcelReaderService excelReaderService = new ExcelReaderService(filePath,true,nameAndCodeProps.getNameColumnIndex(),nameAndCodeProps.getCodeColumnIndex());
        items = excelReaderService.getModel();


    }

    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        BarCodeGen dialog = new BarCodeGen();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
