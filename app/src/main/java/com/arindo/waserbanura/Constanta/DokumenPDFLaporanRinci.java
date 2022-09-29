package com.arindo.waserbanura.Constanta;

import android.content.ActivityNotFoundException;
import android.os.Environment;
import android.util.Log;

import com.arindo.waserbanura.ActivityLaporan;
import com.arindo.waserbanura.ActivityLaporanRinci;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DokumenPDFLaporanRinci {
    private Locale localeID;
    private NumberFormat formatRupiah;

    public DokumenPDFLaporanRinci() {
        localeID = new Locale("in","ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
    }

    private String getTanggal() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    private String getWaktu() {
        DateFormat dateFormat = new SimpleDateFormat("HHmm");
        Date date2 = new Date();
        return dateFormat.format(date2);
    }

    public void createdokumenPDFRinci (JSONArray jsonArray) {
        try {
            // end of SD card checking
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Waserba Nura");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }

            //path untuk menyimpan file dokument PDF
            String filename = "Laporan" + getTanggal() + getWaktu() + ".pdf";

            File file = new File(docsFolder.getAbsolutePath(), filename);
            if (file.exists()) {
                file.delete();
                file = new File(docsFolder.getAbsolutePath(), filename);
            }

            Log.e("Berhaasil", String.valueOf(file));
            //buat objeck dokument
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.setPageSize(PageSize.A4);
            document.addCreationDate();

            document.addAuthor("Arindo");
            document.addCreator("Waserba Nura");

            //warna color Font
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            //ukururan Font
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 12.0f;
            //objek BaseFont besechekbox, untuk membuat kotak checkbox yang diambil dari font type windings.ttf
            //objek BaseFont baseunchekbox, untuk membuat uncheckbox yang diambil dari font type wingdings1.ttf
            BaseFont montserratregular = BaseFont.createFont("res/font/montserratregular.ttf", "UTF-8", BaseFont.EMBEDDED);
            BaseFont timesnewroman = BaseFont.createFont("res/font/times_new_roman.ttf", "UTF-8", BaseFont.EMBEDDED);
            //garis pemisah (garis horisaontal)
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 60));

            Font fontheader = new Font(timesnewroman, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font fontheader2 = new Font(timesnewroman, 12.0f, Font.NORMAL, BaseColor.BLACK);
            Font fontheader3 = new Font(timesnewroman, 8.0f, Font.NORMAL, BaseColor.BLACK);
            Font fontbody = new Font(timesnewroman, 10.0f, Font.NORMAL, BaseColor.BLACK);
            Font fontnormal = new Font(montserratregular, mValueFontSize, Font.NORMAL, BaseColor.BLACK);

            //Chunk mempresentasikan potongan dari sebuah text, Chunk dapat berisi singel karater atau bebrapa kalimat

            //Nama paket
            // Chunk namapaketchunk = new Chunk("WASERBA NURA",fontheader);
            // Paragraph namapaketparagraf = new Paragraph(namapaketchunk);
            // namapaketparagraf.setAlignment(Element.ALIGN_CENTER);
            // document.add(namapaketparagraf);

            Paragraph namapaketchunk = new Paragraph(new Paragraph("WASERBA NURA", fontheader));
            namapaketchunk.setAlignment(Element.ALIGN_CENTER);
            document.add(namapaketchunk);

            //Laporan
            Paragraph laporan = new Paragraph(new Paragraph("Laporan Rincian Transaksi", fontheader2));
            laporan.setAlignment(Element.ALIGN_CENTER);
            document.add(laporan);

            //Chunk laporan = new Chunk("Laporan Transaksi ",fontheader2);
            //Paragraph laporanparagraf = new Paragraph(laporan);
            // laporanparagraf.setAlignment(Element.ALIGN_CENTER);
            //document.add(laporanparagraf);

            //Laporanbulan
            Paragraph Laporanbulan = new Paragraph(new Paragraph(ActivityLaporan.tanggallpr.getText().toString(), fontheader3));
            Laporanbulan.setAlignment(Element.ALIGN_CENTER);
            document.add(Laporanbulan);
            // Chunk laporanbulan = new Chunk(ActivityLaporan.totalhsl.getText().toString(),fontheader3);
            // Paragraph laporanbulanparagraf = new Paragraph(laporanbulan);
            // laporanbulanparagraf.setAlignment(Element.ALIGN_CENTER);
            // document.add(laporanbulanparagraf);

            //lineSeparator
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            //status adminpos
            Chunk adminpostransaksi = new Chunk(new VerticalPositionMark());
            //judul
            Paragraph dokumenadminpostransaksi = new Paragraph("Admin Pos", fontnormal);
            dokumenadminpostransaksi.add(new Chunk(adminpostransaksi));
            //isi
            dokumenadminpostransaksi.add(ActivityLaporan.adminpshsl.getText().toString());
            document.add(dokumenadminpostransaksi);

            //status kode transaksi
            Chunk kodetransaksi = new Chunk(new VerticalPositionMark());
            //judul
            Paragraph dokumenkodetransaksi = new Paragraph("Kode Transaksi", fontnormal);
            dokumenkodetransaksi.add(new Chunk(kodetransaksi));
            //isi
            dokumenkodetransaksi.add(ActivityLaporanRinci.codetrx.getText().toString());
            document.add(dokumenkodetransaksi);


            //status tanggal
            Chunk tanggaltransaksi = new Chunk(new VerticalPositionMark());
            //judul
            Paragraph dokumentanggaltransaksi = new Paragraph("Tanggal Transaksi", fontnormal);
            dokumentanggaltransaksi.add(new Chunk(tanggaltransaksi));
            //isi
            dokumentanggaltransaksi.add(ActivityLaporanRinci.tglhsl.getText().toString());
            document.add(dokumentanggaltransaksi);

            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));


            PdfPTable tablejumlah = new PdfPTable(1); // 4 columns.
            tablejumlah.setWidthPercentage(100);
            float[] columnWidthsjumlah = new float[]{10f};
            tablejumlah.setWidths(columnWidthsjumlah);

            tablejumlah.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celljuduljumlah1 = new PdfPCell(new Paragraph("Jumlah Total", fontbody));
            celljuduljumlah1.setHorizontalAlignment(Element.ALIGN_LEFT);
            celljuduljumlah1.setPaddingBottom(5);
            tablejumlah.addCell(celljuduljumlah1);

            PdfPCell celljumlah = new PdfPCell(tablejumlah);
            celljumlah.setColspan(6);

            PdfPTable tablejumlah2 = new PdfPTable(1); // 4 columns.
            tablejumlah2.setWidthPercentage(100);
            float[] columnWidthsjumlah2 = new float[]{10f};
            tablejumlah2.setWidths(columnWidthsjumlah2);

            tablejumlah2.setHorizontalAlignment(Element.ALIGN_CENTER);

            PdfPCell celljuduljumlah2 = new PdfPCell(new Paragraph(ActivityLaporanRinci.total.getText().toString(), fontbody));
            celljuduljumlah2.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljuduljumlah2.setPaddingBottom(5);
            tablejumlah2.addCell(celljuduljumlah2);

            PdfPCell celljumlah2 = new PdfPCell(tablejumlah2);
            celljumlah2.setColspan(1);

            //buat objeck table
            PdfPTable table = new PdfPTable(7); // 4 columns.
            table.setWidthPercentage(100);
            float[] columnWidths = new float[]{15f, 40f, 70f, 80f, 20f, 40f, 45f};
            table.setWidths(columnWidths);

            table.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell celljudul1 = new PdfPCell(new Paragraph("No", fontnormal));
            celljudul1.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul1.setPaddingBottom(5);
            table.addCell(celljudul1);
            PdfPCell celljudul2 = new PdfPCell(new Paragraph("Place ID", fontnormal));
            celljudul2.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul2.setPaddingBottom(5);
            table.addCell(celljudul2);
            PdfPCell celljudul3 = new PdfPCell(new Paragraph("Menu ID", fontnormal));
            celljudul3.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul3.setPaddingBottom(5);
            table.addCell(celljudul3);
            PdfPCell celljudul4 = new PdfPCell(new Paragraph("Nama Barang", fontnormal));
            celljudul4.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul4.setPaddingBottom(5);
            table.addCell(celljudul4);
            PdfPCell celljudul5 = new PdfPCell(new Paragraph("qty", fontnormal));
            celljudul5.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul5.setPaddingBottom(5);
            table.addCell(celljudul5);
            PdfPCell celljudul6 = new PdfPCell(new Paragraph("Harga", fontnormal));
            celljudul6.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul6.setPaddingBottom(5);
            table.addCell(celljudul6);
            PdfPCell celljudul7 = new PdfPCell(new Paragraph("Total", fontnormal));
            celljudul7.setHorizontalAlignment(Element.ALIGN_CENTER);
            celljudul7.setPaddingBottom(5);
            table.addCell(celljudul7);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);
                PdfPCell cell1 = new PdfPCell(new Paragraph(String.valueOf(i + 1), fontbody));
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell1.setPaddingBottom(5);
                table.addCell(cell1);

                PdfPCell cell2 = new PdfPCell(new Paragraph(json.getString("places_id"), fontbody));
                cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setPaddingBottom(5);
                table.addCell(cell2);

                PdfPCell cell3 = new PdfPCell(new Paragraph(json.getString("menu_id"), fontbody));
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell3.setPaddingBottom(5);
                table.addCell(cell3);

                PdfPCell cell4 = new PdfPCell(new Paragraph(json.getString("menu"), fontbody));
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell4.setPaddingBottom(5);
                table.addCell(cell4);

                PdfPCell cell5 = new PdfPCell(new Paragraph(json.getString("qty"), fontbody));
                cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell5.setPaddingBottom(5);
                table.addCell(cell5);

                PdfPCell cell6 = new PdfPCell(new Paragraph(formatRupiah.format(Integer.parseInt(json.getString("trx_price"))), fontbody));
                cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell6.setPaddingBottom(5);
                table.addCell(cell6);

                PdfPCell cell7 = new PdfPCell(new Paragraph(formatRupiah.format(Integer.parseInt(json.getString("trx_total"))), fontbody));
                cell7.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell7.setPaddingBottom(5);
                table.addCell(cell7);
            }
            table.addCell(celljumlah);
            table.addCell(celljumlah2);
            document.add(table);
            //close dokument
            document.close();

        } catch (IOException | DocumentException ie) {
            Log.e("createPdf",ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Log.e("createPdf",ae.getLocalizedMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}