package com.arindo.waserbanura;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arindo.waserbanura.Constanta.Constanta;
import com.arindo.waserbanura.Model.ItemLaporanRinci;
import com.github.barteksc.pdfviewer.PDFView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.arindo.waserbanura.ActivityLaporan.bulaan;
import static com.arindo.waserbanura.ActivityLaporan.harri;
import static com.arindo.waserbanura.ActivityLaporan.tahunn;

public class ActivityKirimWa extends AppCompatActivity {

    private SharedPreferences preferences;
    private PDFView pdfView;
    private EditText edtNoTelefon;
    private Button btnkirimwa;
    private TextView tanggalkirimwa;
    String nomorHp, adminpos, gettotal, filename, Item;
    JSONArray jsonArray;
    JSONObject jsonObject;
    private NumberFormat formatRupiah;
    File file;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_wa);

        Locale localeID = new Locale("in", "ID");
        formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        pdfView = findViewById(R.id.pdfView);
        edtNoTelefon = findViewById(R.id.edtNoTelefon);
        btnkirimwa = findViewById(R.id.btnkirimwa);
        tanggalkirimwa = findViewById(R.id.tanggalkirimwa);
        tanggalkirimwa.setText(getNamaHari(harri, bulaan, tahunn) + "," + harri + " " + getNamaBulan(bulaan) + " "  + tahunn);
        preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
       //trxcodetxt.setText(preferences.getString("trxcodepref", null));
        adminpos = preferences.getString("trxadmpref", null);
        gettotal = getIntent().getStringExtra("totalkirimwa");
        Item = preferences.getString("preflaporanrinci", null);

        //Log.e("gettotal",gettotal );

        try {
            // ActivityCompat.requestPermissions(ActivityPembayaranBerhasil.this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

            // end of SD card checking
            File docsFolder = new File(Environment.getExternalStorageDirectory() + "/Waserba Nura");
            if (!docsFolder.exists()) {
                docsFolder.mkdir();
            }

            //path untuk menyimpan file dokument PDF
            filename = "Laporan" + getTanggal() + getWaktu() + ".pdf";

            file = new File(docsFolder.getAbsolutePath(), filename);
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
            Paragraph Laporanbulan = new Paragraph(new Paragraph(tanggalkirimwa.getText().toString(), fontheader3));
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
            dokumenadminpostransaksi.add(preferences.getString("trxadmpref", null));
            document.add(dokumenadminpostransaksi);

            //status kode transaksi
            Chunk kodetransaksi = new Chunk(new VerticalPositionMark());
            //judul
            Paragraph dokumenkodetransaksi = new Paragraph("Kode Transaksi", fontnormal);
            dokumenkodetransaksi.add(new Chunk(kodetransaksi));
            //isi
            dokumenkodetransaksi.add(preferences.getString("trxcodepref", null));
            document.add(dokumenkodetransaksi);

            //status tanggal
            Chunk tanggaltransaksi = new Chunk(new VerticalPositionMark());
            //judul
            Paragraph dokumentanggaltransaksi = new Paragraph("Tanggal Transaksi", fontnormal);
            dokumentanggaltransaksi.add(new Chunk(tanggaltransaksi));
            //isi
            dokumentanggaltransaksi.add(getTanggal2());
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

            PdfPCell celljuduljumlah2 = new PdfPCell(new Paragraph(formatRupiah.format(Integer.parseInt(gettotal)) , fontbody));
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

            jsonObject = new JSONObject(Item);
            jsonArray = jsonObject.getJSONArray("values");
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

            pdfView.fromFile(file)
                    .enableSwipe(true)
                    .enableDoubletap(false)
                    .load();

        } catch (IOException | DocumentException | ActivityNotFoundException | JSONException ie) {
            Log.e("createPdf",ie.getLocalizedMessage());
        }
        btnkirimwa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edtNoTelefon.getText().toString().equals("")) {
                    Toast.makeText(ActivityKirimWa.this, getString(R.string.error_message_form_empty),
                            Toast.LENGTH_SHORT).show();
                } else {
                    nomorHp = edtNoTelefon.getText().toString().substring(1);
                    KirimWhatsApp("62" + nomorHp);
                }
            }
        });
    }

    private void KirimWhatsApp(String number) {
        Intent kirimWA = new Intent(Intent.ACTION_SEND);
        kirimWA.setType("application/pdf");
        kirimWA.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+file));
        kirimWA.putExtra("jid", number+ "@s.whatsapp.net");
        kirimWA.setPackage("com.whatsapp");
        startActivity(kirimWA);
    }

    private String getTanggal2() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
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

    private String getNamaHari(String tanggal, String bulan, String tahun) {
        String dayOfWeek = "";
        try {
            String dateString2 = tanggal + "-" +  bulan + "-" + tahun;
            Date date = new SimpleDateFormat("dd-MM-yyyy").parse(dateString2);
            dayOfWeek = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date);
            if (dayOfWeek.toUpperCase().equals("SUNDAY") ) {
                dayOfWeek = "Minggu";
            } else if (dayOfWeek.toUpperCase().equals("MONDAY")) {
                dayOfWeek = "Senin";
            } else if (dayOfWeek.toUpperCase().equals("TUESDAY")) {
                dayOfWeek = "Selasa";
            } else if (dayOfWeek.toUpperCase().equals("WEDNESDAY")) {
                dayOfWeek = "Rabu";
            } else if (dayOfWeek.toUpperCase().equals("THURSDAY")) {
                dayOfWeek = "Kamis";
            }else if (dayOfWeek.toUpperCase().equals("FRIDAY")) {
                dayOfWeek = "Jumat";
            }else if (dayOfWeek.toUpperCase().equals("SATURDAY")) {
                dayOfWeek = "Sabtu";
            }
            Log.e("tanggal", dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayOfWeek;
    }

    private String getNamaBulan(String bulan){
        if (bulan.equals("01")) return "Januari";
        else if (bulan.equals("02")) return "Februari";
        else if (bulan.equals("03")) return "Maret";
        else if (bulan.equals("04")) return "April";
        else if (bulan.equals("05")) return "Mei";
        else if (bulan.equals("06")) return "Juni";
        else if (bulan.equals("07")) return "Juli";
        else if (bulan.equals("08")) return "Agustus";
        else if (bulan.equals("09")) return "September";
        else if (bulan.equals("10")) return "Oktober";
        else if (bulan.equals("11")) return "Novemver";
        else if (bulan.equals("12")) return "Desember";
        else return "";
    }

}
