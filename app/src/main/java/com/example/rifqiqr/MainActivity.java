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
            if (result.getContents() == null) {
                Toast.makeText (this, "Hasil SCAN tidak ada", Toast.LENGTH_LONG).show();
            } else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            }
            else if (Patterns.PHONE.matcher(result.getContents()).matches()){
                String telp = String.valueOf(result.getContents());
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+telp));
                //if{ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)}
                    startActivity(callIntent);
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
            } else{
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        public void onClick(View view) {
            //inisialisasi scanning qr code
        qrScan.initiateScan();
        }
}