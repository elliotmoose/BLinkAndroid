package javanesecoffee.com.blink;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class BlinkActivity extends AppCompatActivity {
    private ProgressDialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        initProgressDialog();
    }

    public void initProgressDialog()
    {
        if(dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage("Registering...");
        }
    }

    public void showProgressDialog()
    {
        initProgressDialog();
        dialog.show();
    }

    public void showProgressDialog(String message)
    {
        initProgressDialog();
        dialog.setMessage(message);
        dialog.show();
    }

    public void hideProgressDialog() {
        initProgressDialog();
        if(dialog.isShowing()) {
            dialog.hide();
        }
    }
}
