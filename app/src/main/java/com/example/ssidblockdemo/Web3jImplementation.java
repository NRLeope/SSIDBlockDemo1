package com.example.ssidblockdemo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import java.io.File;
import java.math.BigDecimal;
import java.security.Provider;
import java.security.Security;

public class Web3jImplementation extends AppCompatActivity {

    Web3j web3;
    File file;
    String Walletname;
    Credentials credentials;
    TextView txtaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.block_info);
        txtaddress=findViewById(R.id.text_address);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        web3 = Web3j.build(new HttpService("https://mainnet.infura.io/v3/d129003ed5b44220b3732d1b396e6c88"));

        setupBouncyCastle();

        //  this is the pathname for the file that will be created and stores the wallet details
        EditText Edtpath = findViewById(R.id.walletpath);
        final String etheriumwalletPath = Edtpath.getText().toString();

        file = new File(getFilesDir() + etheriumwalletPath);// the etherium wallet location
        //create the directory if it does not exist
        if (!file.mkdirs() ) {
            file.mkdirs();
        }
        else {
            Toast.makeText(getApplicationContext(), "Directory already created",
                    Toast.LENGTH_LONG).show();

        }

    }

    public void createWallet(View v)  {

        EditText Edtpassword = findViewById(R.id.password);
        final String password = Edtpassword.getText().toString();  // this will be your etherium password
        try {
            // generating the etherium wallet
            Walletname = WalletUtils.generateLightNewWalletFile(password, file);
            ShowToast("Wallet generated wallet name is ");
            credentials = WalletUtils.loadCredentials(password, file + "/" + Walletname);
            txtaddress.setText(getString(R.string.your_address) + credentials.getAddress());

        }
        catch(Exception e){
            ShowToast("failed");

        }
    }

    public void connectToEthNetwork(View v) {

        ShowToast(" Now Connecting to Ethereum network");
        try {
            //if the client version has an error the user will not gain access if successful the user will get connnected
            Web3ClientVersion clientVersion = web3.web3ClientVersion().sendAsync().get();
            if (!clientVersion.hasError()) {
                ShowToast("Connected!");
            } else {
                ShowToast(clientVersion.getError().getMessage());
            }
        } catch (Exception e) {
            ShowToast(e.getMessage());
        }
    }

    public void ShowToast(String message) {
        //this method generates toasts
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up a provider  when it's used for the first time.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            return;
        }
        //There is a possibility  the bouncy castle registered by android may not have all ciphers
        //so we  substitute with the one bundled in the app.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}