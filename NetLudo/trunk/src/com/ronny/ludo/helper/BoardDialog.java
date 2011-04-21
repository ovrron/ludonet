/**
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
	private String separator = null;
	
	/**
	 * Konstruktør
	 * @param context
	 * @param boards
	 * @param chosenBoard
	 */
    public BoardDialog(Context context, Vector<String> boards, String separator, String defaultBoardName) 
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.radiochoice);

        this.boards = boards;
        this.separator = separator;
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

        for(int i=0;i<this.boards.size();i++)
        {
			RadioButton radioButton = new RadioButton(radioGroup.getContext());
			String[] board = boards.get(i).split(this.separator);
			radioButton.setText(board[0]);
			radioButton.setId(i);
			if(defaultBoardName != null && board[0].equals(defaultBoardName))
			{
				radioButton.setChecked(true);
			}
			radioButton.setOnClickListener(this);
			radioGroup.addView(radioButton);
        }
        
//		for(int i=0;i<boards.length;i++)
//		{
//			RadioButton rb = (RadioButton)rg.getChildAt(i);
//			rb.setText(boards[i]);
//			if (i==defaultBoard)
//			{
//				rb.setChecked(true);
//				this.chosenBoard = boards[i];
//			}
//			rb.setOnClickListener(this);
//		}
    }
 
    @Override
    public void onClick(View v) 
    {
    	RadioButton rb = (RadioButton)v;
    	//chosenBoard = (String)rb.getText();
    	chosenBoard = boards.get(rb.getId());
    	dismiss();
    } 
    
    /**
     * @return valgt brett
     */
    public String getChosenBoard()
    {
    	return chosenBoard;
    }
}
