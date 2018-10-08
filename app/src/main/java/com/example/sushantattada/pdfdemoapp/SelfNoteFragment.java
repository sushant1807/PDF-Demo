package com.example.sushantattada.pdfdemoapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfFormXObject;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SelfNoteFragment extends Fragment {

    File myFile;

    public static final String FONT = "assets/fonts/NotoNaskhArabic-Regular.ttf";
  //  public static final String ARABIC = "\u0627\u0644\u0633\u0639\u0631 \u0627\u0644\u0627\u062c\u0645\u0627\u0644\u064a";
    public static final String ARABIC = "السعر الاجمالي للمخالفة";

    float fntSize = 0.0f;
    float lineSpacing = 0.0f;


    private View mRootView;
    private EditText mSubjectEditText, mBodyEditText;
    private Button mSaveButton;
    private PdfPCell cell;
    PdfWriter writer ; //= PdfWriter.getInstance(document, output);
    protected String columns;
    protected String rows;

    //Refactoring
    Document document = new Document();

    public SelfNoteFragment() throws IOException, DocumentException {
        // Required empty public constructor
    }

    public static SelfNoteFragment newInstance() throws IOException, DocumentException {
        SelfNoteFragment fragment = new SelfNoteFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_self_note, container, false);
        mSubjectEditText = (EditText) mRootView.findViewById(R.id.edit_text_subject);
        mBodyEditText = (EditText) mRootView.findViewById(R.id.edit_text_body);
        mSaveButton = (Button) mRootView.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubjectEditText.getText().toString().isEmpty()){
                    mSubjectEditText.setError("Subject is empty");
                    mSubjectEditText.requestFocus();
                    return;
                }

                if (mBodyEditText.getText().toString().isEmpty()){
                    mBodyEditText.setError("Body is empty");
                    mBodyEditText.requestFocus();
                    return;
                }

                try {
                    createPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return mRootView;
    }

    private void createPdf() throws IOException, DocumentException {

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "pdfdemo");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("Message", "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        myFile = new File(pdfFolder + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);

        writer = PdfWriter.getInstance(document, output);
        document.open();
        //BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        //Font f = new Font(baseFont);
        //ColumnText canvas = new ColumnText(writer.getDirectContent());

        contentWriterCNNotice();
        lineDivider();
        contentWriterCNNumber();
        lineDivider();
        contentWriterDateTime();
        lineDivider();
        contentWriterCNLocation();
        contentWriterCNLocationServer();
        lineDivider();
        contentWriterPlateNumber();
        lineDivider();
        contentWriterPlateType();
        lineDivider();
        contentWriterVehicleColor();
        lineDivider();
        contentWriterCNCategory();
        contentWriterCNCategoryServer();
        lineDivider();
        contentWriterCNValue();
        lineDivider();
        contentWriterInspectorID();
        lineDivider();
        //contentWriterBarCode();
        getBarcode1();
        lineDivider();

        document.close();
        promptForNextAction();

    }

    public void contentWriterCNNotice() throws DocumentException, IOException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("CHARGE NOTICE", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("إشعار مخالفة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterCNNumber() throws  DocumentException,IOException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("CN NUMBER", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("رقم المخالفة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }


    public void contentWriterDateTime() throws  DocumentException,IOException {
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("DATE/TIME", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("الوقت/التاريخ", PdfPCell.ALIGN_LEFT));

        document.add(table);
        //document.add(new Phrase("\n"));
    }

    public void contentWriterCNLocation() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("CN LOCATION", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("مكان المخالفة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterCNLocationServer() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterPlateNumber() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("PLATE NUMBER", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("رقم اللوحة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterPlateType() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("PLATE TYPE", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("نوع اللوحة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterVehicleColor() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("VEHICLE COLOR", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("لون السيارة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterCNCategory() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("CN CATEGORY", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("نوع المخالفة", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterCNCategoryServer() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("In the above case you have to provide two strings mandatory",
                PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterCNValue() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("CN VALUE", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("10.0 SR", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("قيمة المخالفة )رس(", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterInspectorID() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(3);
        table.setWidthPercentage(100);
        table.addCell(getCell("INSPECTOR ID", PdfPCell.ALIGN_RIGHT));
        table.addCell(getCell("1234567890", PdfPCell.ALIGN_CENTER));
        table.addCell(getArabicCell("رقم المراقب", PdfPCell.ALIGN_LEFT));

        document.add(table);
    }

    public void contentWriterBarCode() throws DocumentException, IOException{
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        for (int i = 0; i < 1; i++) {
            table.addCell(createBarcode(writer, String.format("%08d", i)));
        }
        document.add(table);
    }

    public void lineDivider() throws DocumentException {
        CustomDashedLineSeparator separator = new CustomDashedLineSeparator();
        separator.setDash(2);
        separator.setGap(0);
        separator.setLineWidth(1);
        Chunk linebreak = new Chunk(separator);
        document.add(linebreak);
        //document.add(Chunk.NEWLINE);
        document.add(new Phrase("\n"));
    }

    public PdfPCell getCell(String text, int alignment) throws IOException, DocumentException {
        Phrase p = new Phrase(text);
        PdfPCell cell = new PdfPCell(p);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        return cell;

    }

    PdfPCell getArabicCell(String text, int alignment) throws IOException, DocumentException{

        BaseFont baseFont = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font f = new Font(baseFont);
        Phrase p = new Phrase(text, f);
        PdfPCell cell = new PdfPCell(p);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
        return cell;
    }

    public static PdfPCell createBarcode(PdfWriter writer, String code) throws DocumentException, IOException {
        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode("");
        barcode.setBarHeight(5.0f);
        barcode.setSize(6);
        barcode.setBaseline(-1);
        PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.GRAY), true);
        //cell.setPadding(1);
        cell.getFixedHeight();

        return cell;

    }

    public void getBarcode1(){
        Barcode128 barcode = new Barcode128();
        barcode.setCodeType(Barcode.CODE128);
        barcode.setCode("");
        barcode.setBarHeight(5.0f);
        barcode.setSize(6);
        barcode.setBaseline(-1);
    }

    public void getBarcode() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        PdfContentByte cb = writer.getDirectContent();

        table.addCell("Change baseline:");
        Barcode128 code128 = new Barcode128();
        code128.setBaseline(-1);
        code128.setSize(12);
        code128.setCode("");
        code128.setCodeType(Barcode128.CODE128);
        Image code128Image = code128.createImageWithBarcode(cb, null, null);
        PdfPCell cell = new PdfPCell(code128Image);
        table.addCell(cell);

        table.addCell("Add text and bar code separately:");
        code128 = new Barcode128();
        code128.setFont(null);
        code128.setCode("");
        code128.setCodeType(Barcode128.CODE128);
        code128Image = code128.createImageWithBarcode(cb, null, null);
        cell = new PdfPCell();
        cell.addElement(new Phrase("PO #: " + ""));
        cell.addElement(code128Image);
        table.addCell(cell);

        document.add(table);
    }

    public void cellLayout(PdfPCell cell, Rectangle position,
                           PdfContentByte[] canvases) {
        PdfContentByte canvas = canvases[PdfPTable.TEXTCANVAS];
        ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                new Phrase(columns), position.getRight(2), position.getTop(12), 0);
        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(rows), position.getLeft(2), position.getBottom(2), 0);
        canvas = canvases[PdfPTable.LINECANVAS];
        canvas.moveTo(position.getLeft(), position.getTop());
        canvas.lineTo(position.getRight(), position.getBottom());
        canvas.stroke();
    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void emailNote() {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,mSubjectEditText.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, mBodyEditText.getText().toString());
        Uri uri = Uri.parse(myFile.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

    private void promptForNextAction() {
        final String[] options = { getString(R.string.label_email), getString(R.string.label_preview),
                getString(R.string.label_cancel) };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Note Saved, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(getString(R.string.label_email))){
                    emailNote();
                }else if (options[which].equals(getString(R.string.label_preview))){
                    viewPdf();
                }else if (options[which].equals(getString(R.string.label_cancel))){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    class CustomDashedLineSeparator extends DottedLineSeparator {

        protected float dash = 5;
        protected float phase = 2.5f;

        public float getDash() {
            return dash;
        }

        public float getPhase() {
            return phase;
        }

        public void setDash(float dash) {
            this.dash = dash;
        }

        public void setPhase(float phase) {
            this.phase = phase;
        }

        public void draw(PdfContentByte canvas, float llx, float lly, float urx, float ury, float y) {
            canvas.saveState();
            canvas.setLineWidth(lineWidth);
            canvas.setLineDash(dash, gap, phase);
            drawLine(canvas, llx, urx, y);
            canvas.restoreState();
        }
    }
}
