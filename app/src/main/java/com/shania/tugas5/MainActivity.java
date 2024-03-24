package com.shania.tugas5;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private EditText customerNameEditText, itemCodeEditText, quantityEditText;
    private RadioGroup membershipRadioGroup;
    private View chooseItemLayout, showResultLayout;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        customerNameEditText = findViewById(R.id.customerNameEditText);
        itemCodeEditText = findViewById(R.id.itemCodeEditText);
        quantityEditText = findViewById(R.id.quantityEditText);
        membershipRadioGroup = findViewById(R.id.membershipRadioGroup);
        Button nextButton = findViewById(R.id.nextButton);
        chooseItemLayout = findViewById(R.id.chooseItemLayout);
        showResultLayout = findViewById(R.id.showResultLayout);
        resultTextView = findViewById(R.id.resultTextView);
        final TextView itemPriceTextView = findViewById(R.id.itemPriceTextView);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customerName = customerNameEditText.getText().toString().trim();
                String itemCode = itemCodeEditText.getText().toString().trim();
                int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());

                // Periksa apakah RadioButton dipilih
                if (membershipRadioGroup.getCheckedRadioButtonId() == -1) {
                    // Tidak ada RadioButton yang dipilih, atur diskon keanggotaan sebagai 0
                    Toast.makeText(MainActivity.this, "Pilih jenis keanggotaan", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedMembershipId = membershipRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedMembershipRadioButton = findViewById(selectedMembershipId);
                String membership = selectedMembershipRadioButton.getText().toString();
                Toast.makeText(MainActivity.this, "Membership: " + membership, Toast.LENGTH_SHORT).show();

                double totalPrice = calculateTotalPrice(itemCode, quantity);
                double discount = calculateDiscount(totalPrice);
                double membershipDiscount = calculateMembershipDiscount(totalPrice, membership);
                double totalPriceAfterDiscount = totalPrice - discount - membershipDiscount;
                double unitPrice = getItemPrice(itemCode);

                DecimalFormat decimalFormat = new DecimalFormat("Rp #,###");
                String formattedUnitPrice = decimalFormat.format(unitPrice);
                String formattedDiscount = decimalFormat.format(discount);
                String formattedMembershipDiscount = decimalFormat.format(membershipDiscount);
                String formattedTotalPriceAfterDiscount = decimalFormat.format(totalPriceAfterDiscount);
                String formattedTotalPrice = decimalFormat.format(totalPrice * quantity);

                String welcomeMessage = getText(R.string.text5) + customerName + "\n";
                String memberTypeMessage = getText(R.string.text6) + membership + "\n\n";
                String transactionDetails = getText(R.string.text7) + "\n" +
                        getText(R.string.text8) + itemCode + "\n" +
                        getText(R.string.text9) + getProductName(itemCode) + "\n" +
                        getText(R.string.text10) + formattedUnitPrice + "\n" +
                        getText(R.string.text11) + formattedTotalPrice + "\n" +
                        getText(R.string.text12) + formattedDiscount + "\n" +
                        getText(R.string.text13) + formattedMembershipDiscount + "\n" +
                        getText(R.string.text14) + formattedTotalPriceAfterDiscount;

                resultTextView.setText(welcomeMessage + memberTypeMessage + transactionDetails + "\n\n" + getText(R.string.text15));

                chooseItemLayout.setVisibility(View.GONE);
                showResultLayout.setVisibility(View.VISIBLE);
            }
        });

        itemCodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String itemCode = itemCodeEditText.getText().toString().trim();
                    double price = getItemPrice(itemCode);
                    if (price != 0) {
                        DecimalFormat decimalFormat = new DecimalFormat("Rp #,###");
                        String formattedPrice = decimalFormat.format(price);
                        itemPriceTextView.setText(getText(R.string.text16) + formattedPrice);
                    } else {
                        itemPriceTextView.setText(getText(R.string.text16) + " Tidak Diketahui");
                    }
                }
            }
        });
    }

    private double calculateTotalPrice(String itemCode, int quantity) {
        double unitPrice = getItemPrice(itemCode);
        double totalPrice = unitPrice * quantity;
        return totalPrice;
    }

    private double calculateDiscount(double totalPrice) {
        double discount = 0;
        if (totalPrice > 10000000) {
            discount += 100000; // diskon tambahan
        }
        return discount;
    }

    private double calculateMembershipDiscount(double totalPrice, String membership) {
        double membershipDiscount = 0;
        double originalTotalPrice = totalPrice + calculateDiscount(totalPrice); // Tambahkan diskon umum
        switch (membership) {
            case "Gold":
                membershipDiscount = originalTotalPrice * 0.1;
                Log.d("MembershipDiscount", "Gold membership discount: " + membershipDiscount);
                break;
            case "Silver":
                membershipDiscount = originalTotalPrice * 0.05;
                Log.d("MembershipDiscount", "Silver membership discount: " + membershipDiscount);
                break;
            case "Biasa":
                membershipDiscount = originalTotalPrice * 0.02;
                Log.d("MembershipDiscount", "Regular membership discount: " + membershipDiscount);
                break;
        }
        return membershipDiscount;
    }


    private double getItemPrice(String itemCode) {
        double price = 0;
        switch (itemCode) {
            case "SGS":
                price = 12999999;
                break;
            case "O17":
                price = 2500999;
                break;
            case "AV4":
                price = 9150999;
                break;
        }
        return price;
    }

    private String getProductName(String itemCode) {
        String productName = "";
        switch (itemCode) {
            case "SGS":
                productName = "Samsung Galaxy S";
                break;
            case "O17":
                productName = "Oppo A17";
                break;
            case "AV4":
                productName = "Asus Vivobook 14";
                break;
        }
        return productName;
    }
    public void shareTransaction(View view) {
        String transactionDetails = resultTextView.getText().toString();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, transactionDetails);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}