package com.rpggame.entities;

import org.lwjgl.util.vector.Vector3f;

public class Light {

	private Vector3f position; //This just pretty much sets a the position in the 3d world for the light to spawn
	private Vector3f color; //This is also setting the points for the color
	
	public Light(Vector3f position, Vector3f color) {
		this.position = position;
		this.color = color;
	}

	public Vector3f getPosition() {
		return position;
	}
	
	public Vector3f getColor() {
		return color;
	}
	
	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public void setColor(Vector3f color) {
		this.color = color;
	}
	
}
