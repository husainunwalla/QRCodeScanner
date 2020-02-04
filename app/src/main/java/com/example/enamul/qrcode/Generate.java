package com.example.enamul.qrcode;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static com.example.enamul.qrcode.R.drawable.ic_launcher_foreground;

public class Generate extends AppCompatActivity {
    ImageView imageView;
    Button button;
    Button btnScan;
    EditText idEditText, priceEditText, nameEditText;
    String idString;
    Thread thread ;
    public final static int QRcodeWidth = 350 ;
    Bitmap bitmap ;
//comment
    int ID = 0;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("inventory");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        imageView = (ImageView)findViewById(R.id.imageView);
        idEditText = (EditText)findViewById(R.id.idEditText);
        button = (Button)findViewById(R.id.button);
        btnScan = (Button)findViewById(R.id.btnScan);
        imageView.setImageResource(ic_launcher_foreground);
        idEditText.setFocusable(false);
        nameEditText = findViewById(R.id.nameEditText);
        priceEditText = findViewById(R.id.priceEditText);




        databaseReference.child("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idEditText.setText(dataSnapshot.getValue().toString());
                databaseReference.child("id").setValue(Integer.valueOf(Integer.parseInt(dataSnapshot.getValue().toString()) + 1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //databaseReference.child("id").setValue(Integer.parseInt(idEditText.toString()) + 1);

                if(!idEditText.getText().toString().isEmpty()){
                    idString = idEditText.getText().toString();

                    try {
                        bitmap = TextToImageEncode(idString);
                        //Setting image to bitmap
                        imageView.setImageBitmap(bitmap);
                        databaseReference.child(idString).child("name").setValue(nameEditText.getText().toString());
                        databaseReference.child(idString).child("price").setValue(priceEditText.getText().toString());


                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    idEditText.requestFocus();
                    Toast.makeText(Generate.this, "Please Enter Your Scanned Test" , Toast.LENGTH_LONG).show();
                }

            }
        });



    }

    //Generate QR Code Bitmap
    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }





}
