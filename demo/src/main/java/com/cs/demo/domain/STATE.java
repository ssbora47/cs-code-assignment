package com.cs.demo.domain;

public enum STATE {
	STARTED("STARTED"),
	FINISHED("FINISHED");
	private String value;

	STATE(String value){
	    this.value = value;
	}
	@Override
    public String toString() {
        return value;
    }
}
