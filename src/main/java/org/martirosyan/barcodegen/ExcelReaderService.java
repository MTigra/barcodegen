package org.martirosyan.barcodegen;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReaderService implements AutoCloseable {
    private String excelPath;
    private boolean skipfirst;
    private int nameCol;
    private int codeCol;

    public ExcelReaderService(String excelPath, boolean skipfirst, int nameCol, int codeCol) throws IOException {
        this.excelPath = excelPath;
        this.codeCol = codeCol;
        this.nameCol = nameCol;
        this.skipfirst = skipfirst;


    }

    public List<ItemModel> getModel() throws IOException {
        FileInputStream file = new FileInputStream(excelPath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        ArrayList<ItemModel> items = new ArrayList<>();
        int i = 0;
        for (Row row : sheet) {
            if (i++ == 0) {
                continue;
            }
            String name = row.getCell(nameCol).getStringCellValue();
            String codeString = anyCellToString(row.getCell(codeCol));
            ItemModel itemModel = new ItemModel();
            itemModel.name = name;
            itemModel.code = codeString;
            items.add(itemModel);
        }
        workbook.close();
        file.close();
        return items;
    }


    @Override
    public void close() throws Exception {

    }

    private String anyCellToString(Cell cell) {
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                long round = Math.round(cell.getNumericCellValue());
                return String.valueOf(round);
            case STRING:
                String cellStr = cell.getStringCellValue();
                if (cellStr.contains(",")) {
                    return cellStr.substring(0,cellStr.indexOf(","));
                }
                return cell.getStringCellValue();
            default:
                return "00000000000000";

        }

    }
}
