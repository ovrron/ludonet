package com.ronny.ludo.model;

/** 
* Coordinate.java 
* Copyright: (c) 2011 Ronny Heitmann Andersen, Ronny Øvereng and Karl-Erik Moberg
*/

public class Coordinate{
	public int pos; // optional
	public int x;
	public int y;

	
    /**
     * Konstruktør  
     */ 
	public Coordinate() {
	}

    /**
     * Konstruktør  
     */ 
	public Coordinate(Coordinate co) {
		this.pos = co.pos;
		this.x = co.x;
		this.y = co.y;
	}

    /**
     * Konstruktør  
     */ 
	public Coordinate(int pos, int x, int y) {
		this.pos = pos;
		this.x = x;
		this.y = y;
	}
	
	public String toString() {
		return "["+pos+","+x+","+y+"]";
	}

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coordinate other = (Coordinate) obj;
        if (pos != other.pos)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }
}
