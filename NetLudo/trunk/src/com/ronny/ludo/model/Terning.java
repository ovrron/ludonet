package com.ronny.ludo.model;


public class Terning implements ITerning {
	int eyes;
	
	public Terning(){
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
