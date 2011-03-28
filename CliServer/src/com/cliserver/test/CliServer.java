package com.cliserver.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class CliServer extends Activity {
	private Button btnClient = null;
	private Button btnServer = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnServer = (Button)findViewById(R.id.btnserver);
        btnServer.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(CliServer.this, Server.class));
				CliServer.this.finish();
			}
		});
        btnClient = (Button)findViewById(R.id.btnclient);
        btnClient.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(CliServer.this, Client.class));
				CliServer.this.finish();
			}
		});
    }

	
}