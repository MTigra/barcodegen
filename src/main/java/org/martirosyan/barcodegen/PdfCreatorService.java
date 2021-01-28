package org.martirosyan.barcodegen;

import com.itextpdf.barcodes.BarcodeEAN;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import sun.misc.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PdfCreatorService {

    public PdfCreatorService( ) {
    }

    public void writeTo(String path, List<ItemModel> items) throws IOException {
        PdfWriter writer = new PdfWriter(path);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("arial.ttf");
        byte[] bytes = IOUtils.readAllBytes(resourceAsStream);
        PdfFont myFont = PdfFontFactory.createFont(bytes, PdfEncodings.IDENTITY_H);
        document.setFont(myFont);
        Table table = new Table(2).useAllAvailableWidth();

        for (ItemModel itemModel : items) {

            Paragraph paragraph = new Paragraph(itemModel.name);
            table.addCell(paragraph);
            table.addCell(createBarcode(itemModel.code, pdfDocument));

        }
        document.add(table);

        document.close();
    }


    private Cell createBarcode(String code, PdfDocument pdfDoc) {
        BarcodeEAN barcode = new BarcodeEAN(pdfDoc);
        barcode.setCodeType(BarcodeEAN.EAN13);
        barcode.setCode(code);

        // Create barcode object to put it to the cell as image
        PdfFormXObject barcodeObject = barcode.createFormXObject(null, null, pdfDoc);
        Cell cell = new Cell().add(new Image(barcodeObject).scaleToFit(200, 25));
        cell.setPaddingTop(10);
        cell.setPaddingRight(10);
        cell.setPaddingBottom(10);
        cell.setPaddingLeft(10);

        return cell;
    }

}
