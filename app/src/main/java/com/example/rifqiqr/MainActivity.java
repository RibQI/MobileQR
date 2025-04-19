package com.example.rifqiqr;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    //View Objects
    private Button buttonScan;
    private TextView textViewNama, textViewKelas, textViewNim;

    //qr code scanner object
    private IntentIntegrator qrScan;
    private Object view;
    String googleMap = "com.google.android.apps.maps";
    Uri gmmIntentUri;
    Intent mapIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View Objects
        buttonScan = (Button) findViewById(R.id.buttonScan);
        textViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewKelas = (TextView) findViewById(R.id.textViewKelas);
        textViewNim = (TextView) findViewById(R.id.textViewNim);

        //initialisasi scan object
        qrScan = new IntentIntegrator(this);

        //mengimplementasikan OnClickListener
        buttonScan.setOnClickListener(this::onClick);
    }

    //untuk mendapatkan hasil scanning
    @Override
            protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //jika qrcode tidak ada sama sekali
            String contents = result.getContents();
            if (result.getContents() == null) {
                Toast.makeText (this, "Hasil SCAN tidak ada", Toast.LENGTH_LONG).show();
            } else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }
            else if (contents.startsWith("tel:")){
                String PhoneNum = contents.replace("tel:","");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+Uri.encode(PhoneNum.trim())));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
                //Intent callIntent = new Intent(Intent.ACTION_CALL);
                //callIntent.setData(Uri.parse("tel:"+telp));
                //if{ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)}
                    startActivity(callIntent);
                }
            else if(Patterns.EMAIL_ADDRESS.matcher(result.getContents()).matches()){
                String email = String.valueOf(result.getContents());
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                //i.putExtra(Intent.EXTRA_EMAIL, newString[]{EMAIL});
                i.putExtra(Intent.EXTRA_SUBJECT, "tugas uas");
                i.putExtra(Intent.EXTRA_TEXT,"telah berhasil");
                i.setData(Uri.parse("mailto:"+email));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try{
                    startActivity(Intent.createChooser(i,"Send mail..."));
                    finish();
                } catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(MainActivity.this,"There are no email clients installed.",Toast.LENGTH_SHORT).show();
                }
            }
                //jika qr ada/ditemukan data nya
                try {
                    //konversi datanya ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //di set nilai datanya ke textviews
                    textViewNama.setText(obj.getString("nama"));
                    textViewKelas.setText(obj.getString("kelas"));
                    textViewNim.setText(obj.getString("nim"));
                } catch (JSONException e){
                    e.printStackTrace();
                    //jika kontolling ada di sini
                    //itu berarti format encoded tidak cocok
                    //dalam hal ini kita dapat menampilkan data apapun yg tesedia pada qrcode
                    //untuk di toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
                String lokasiku = String.valueOf(result.getContents());
                // Buat Uri dari intent string. Gunakan hasilnya ugntuk membuat Intent
            gmmIntentUri = Uri.parse(lokasiku);
            // Buat Uri dari intent gmmIntentUri. Set action => ACTION_VIEW
            mapIntent = new Intent(Intent.ACTION_VIEW,gmmIntentUri);
            // Set package Google Maps untuk tujuan aplikasi yang di Intent yaitu google maps
            mapIntent.setPackage(googleMap);
            if (mapIntent.resolveActivity(getPackageManager())!=null){
                startActivity(mapIntent);
            }
        } else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        public void onClick(View view) {
            //inisialisasi scanning qr code
        qrScan.initiateScan();
        }
}