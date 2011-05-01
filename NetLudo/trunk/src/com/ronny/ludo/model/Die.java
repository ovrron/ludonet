package com.ronny.ludo.model;

/** 
* Die.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

public class Die implements IDie {
	int eyes;
	
	public Die(){
		eyes = 0;
	}
	
	public int roll(){
		eyes = (int) Math.ceil(Math.random() * 6);
        return eyes;
	}

	public int getEyes(){
		return eyes;
	}
}
