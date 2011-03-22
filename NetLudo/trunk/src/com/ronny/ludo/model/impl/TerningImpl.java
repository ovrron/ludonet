package com.ronny.ludo.model.impl;

import com.ronny.ludo.model.Terning;

public class TerningImpl implements Terning {
	int eyes;
	
	public TerningImpl(){
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
