package com.darkhex.hexalibre;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String name="Nishchay";
        ImageView qrCodeImageView = findViewById(R.id.qr);

        QRCodeGenerator qrCodeGenerator = new QRCodeGenerator();
        Bitmap qrCodeBitmap = qrCodeGenerator.generateQRCode(name, 500, 500);
        qrCodeImageView.setImageBitmap(qrCodeBitmap);
    }
}
