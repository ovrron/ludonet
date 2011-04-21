/**
 * StedDialog.java
 * @author Ronny �vereng
 * Opprettet 2011.02.22
 * LN350D Applikasjonstvikling for mobile enheter
 * �ving4
 *
 */
package com.ronny.ludo.helper;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.Vector;
import com.ronny.ludo.R;

/**
 * Klassen arver fra Dialog og implementerer OnClickListener
 */
public class BoardDialog extends Dialog implements OnClickListener 
{
	//Tekst for valgt sted
	private String chosenBoard;
	private Vector<String> boards = null;
	
	/**
	 * Konstruktør
	 * @param context
	 * @param stedsnavn
	 * @param chosenBoard
	 */
    public BoardDialog(Context context, String[] stedsnavn, int defaultBoard) 
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radiochoice);

        RadioGroup rg = (RadioGroup)findViewById(R.id.radioGroupSted);
		for(int i=0;i<stedsnavn.length;i++)
		{
			RadioButton rb = (RadioButton)rg.getChildAt(i);
			rb.setText(stedsnavn[i]);
			if (i==defaultBoard)
			{
				rb.setChecked(true);
				this.chosenBoard = stedsnavn[i];
			}
			rb.setOnClickListener(this);
		}
    }
 
    @Override
    public void onClick(View v) 
    {
    	RadioButton rb = (RadioButton)v;
    	chosenBoard = (String)rb.getText();
    	dismiss();
    } 
    
    /**
     * @return valgt sted
     */
    public String valgtSted()
    {
    	return chosenBoard;
    }
}
