package com.ronny.ludo.model;


public class ICoordinate{
	public int pos; // optional
	public int x;
	public int y;

	public ICoordinate() {
	}
	public ICoordinate(ICoordinate co) {
		this.pos = co.pos;
		this.x = co.x;
		this.y = co.y;
	}

	public ICoordinate(int pos, int x, int y) {
		this.pos = pos;
		this.x = x;
		this.y = y;
	}
	public String toString() {
		return "["+pos+","+x+","+y+"]";
	}
}
