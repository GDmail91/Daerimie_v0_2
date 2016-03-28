package org.daelimie.test.daelimie;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by YS on 2016-03-28.
 */
public class InstantAlram extends Activity implements
        View.OnClickListener {

    private Button mConfirm, mCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.instant_alram_page);

        setContent();
    }

    private void setContent() {
        mConfirm = (Button) findViewById(R.id.alram_confirm);

        mConfirm.setOnClickListener(this);
    }

    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.btnConfirm:
                this.finish();
                break;
            case R.id.btnCancel:
                this.finish();
                break;
            default:
                break;
        }*/
    }
}
