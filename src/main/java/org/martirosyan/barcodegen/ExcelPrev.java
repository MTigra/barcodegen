package org.martirosyan.barcodegen;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ExcelPrev extends JDialog {
    private  NameAndCodeProps nameAndCodeProps;
    private JPanel contentPane;
    private JButton chooseNamesButton;
    private JButton chooseCodesButton;
    private JTable table1;
    private JButton OKButton;
    private JLabel codeLabel;
    private JLabel nameLabel;

    private int choosenNamesColumnIndex;
    private int choosenCodesColumnIndex;

    private static final int MAX_NUMBER_ROWS = 10;

    public ExcelPrev() {
        setContentPane(contentPane);
//        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);
        getRootPane().setDefaultButton(OKButton);
        chooseNamesButton.addActionListener(l->onChooseNamesButtonClicked());
        chooseCodesButton.addActionListener(l->onChooseCodesButtonClicked());

        OKButton.addActionListener(e->onOK());

        table1.setRowSelectionAllowed(false);
        JTableHeader header = table1.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = header.columnAtPoint(e.getPoint());
                if(header.getCursor().getType() == Cursor.E_RESIZE_CURSOR)
                    e.consume();
                else {
                    table1.setColumnSelectionAllowed(true);
                    table1.setRowSelectionAllowed(false);
                    table1.clearSelection();
                    table1.setColumnSelectionInterval(col,col);
                }
            }
        });

    }

    private void onOK() {

        dispose();
    }

    public ExcelPrev(String excelFilePath, NameAndCodeProps nameAndCodeProps) throws IOException {
        this();
        this.nameAndCodeProps = nameAndCodeProps;
        FileInputStream file = new FileInputStream(excelFilePath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        DefaultTableModel tableModel = new DefaultTableModel();
        table1.setModel(tableModel);

        for (int i = 0; i < (Math.min(lastRowNum, MAX_NUMBER_ROWS)); i++) {
            int j = 0;
            Row row = sheet.getRow(i);
            ArrayList rowData = new ArrayList();
            for (j = 0; j < row.getLastCellNum(); j++) {
                rowData.add(row.getCell(j));
                if (i==0){
                    tableModel.addColumn(row.getCell(j));
                }
            }

            tableModel.addRow(rowData.toArray());
        }
        workbook.close();
        file.close();

    }

    public static void main(String[] args) {
        ExcelPrev dialog = new ExcelPrev();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    private void onChooseNamesButtonClicked() {
        choosenNamesColumnIndex = table1.getSelectedColumn();
        nameLabel.setText(String.valueOf(choosenNamesColumnIndex));
        nameAndCodeProps.setNameColumnIndex(choosenNamesColumnIndex);
    }

    private void onChooseCodesButtonClicked() {
        choosenCodesColumnIndex = table1.getSelectedColumn();
        codeLabel.setText(String.valueOf(choosenCodesColumnIndex));
        nameAndCodeProps.setCodeColumnIndex(choosenCodesColumnIndex);
    }


}
