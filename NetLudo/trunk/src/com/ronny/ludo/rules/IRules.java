package com.ronny.ludo.rules;

import java.util.ArrayList;
import com.ronny.ludo.model.IPiece;

public interface IRules
{
	public boolean isLegalMove(IPiece piece, int eyes);
	public boolean handleMove(IPiece piece, ArrayList<IPiece> pieces);
}

