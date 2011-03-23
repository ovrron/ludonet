package com.ronny.ludo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ronny.ludo.helper.IPAddressHelper;

public class LudoStartActivity extends Activity {
	
	private String ipAdress = null;
	private Button buttonStartSpill = null;
	private Button buttonSendSMS = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);
        init();
    }
    
    private void init()
    {
    	TextView textViewIpAddress = (TextView)findViewById(R.id.textIp);
    	ipAdress = new IPAddressHelper().getLocalIpAddress();
    	if(ipAdress==null){
    		textViewIpAddress.setText("Ingen nettverksforbindelse");
    	}
    	else{
    		textViewIpAddress.setText("IP: " + ipAdress);
    	}
    	initButtons();
    }
    
    private void initButtons()
    {
    	buttonStartSpill = (Button) findViewById(R.id.buttonStart);
    	buttonSendSMS = (Button) findViewById(R.id.buttonSendIP);
    	
    	if(ipAdress==null)
    	{
    		buttonStartSpill.setEnabled(false);
    		buttonSendSMS.setEnabled(false);
    	}
    	
    	buttonStartSpill.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(LudoStartActivity.this, LudoActivity.class));
				
			}
		});
    
    	buttonSendSMS.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO legg inn tekst som skal sendes i strings.xml
				//TODO lage mulighet for en venneliste med telefonnr som man kan velge fra
				//TODO kanskje mulighet for å sende epost også
				String msgText = "NN inviterer deg til et spennende slag ludo. IP adresse = " + ipAdress;
		    	//Intent msg = new Intent(Intent.ACTION_SEND);
		    	//msg.setType("text/plain");
		    	//msg.putExtra("sms_body", msgText);
		    	//msg.putExtra(Intent.EXTRA_TEXT, "email body");
		    	//msg.putExtra(Intent.EXTRA_SUBJECT, "email subject");
		        //startActivity(msg);		
		        //startActivity(Intent.createChooser(msg, "Velg sms"));
		        
		        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		        sendIntent.putExtra("sms_body", msgText);
//		        String tlfno = "90177797, 99001155, 1234678";
//		        sendIntent.putExtra("address", tlfno);
		        sendIntent.setType("vnd.android-dir/mms-sms");
		        startActivity(sendIntent);  
			}
		});
    }
}
