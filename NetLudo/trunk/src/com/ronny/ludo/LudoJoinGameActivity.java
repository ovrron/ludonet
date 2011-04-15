package com.ronny.ludo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

/**
 * 
 * @author ovrron
 *
 */
public class LudoJoinGameActivity extends Activity 
{
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joingame);
    	initButtonListeners();
    }

//    /**
//     * 
//     */
//    private void initEditTextListener()
//    {
//    	EditText editTextIP = (EditText) findViewById(R.id.editTextIP);
//    	editTextIP.setOnKeyListener(new OnKeyListener()
//    	{
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				ipAddressJoin = editTextIP.getText().toString();
//				return false;
//			}
//    		
//    	});
//    }
    
    /**
     * 
     */
    private void initButtonListeners()
    {
    	Button buttonJoin = (Button) findViewById(R.id.buttonIP);
    	buttonJoin.setOnClickListener(new OnClickListener()
    	{
			public void onClick(View v)
			{
				//TODO 
			}
		});
    }
    
}
