package com.rpggame.models;

import com.rpggame.render.textures.ModelTexture;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture texture;
	
	public TexturedModel(RawModel model, ModelTexture texture) { //takes in a model and a texture
		this.rawModel = model;
		this.texture = texture;
	}

	public RawModel getRawModel() { //gets the model
		return rawModel;
	}

	public ModelTexture getTexture() { //gets the texture
		return texture;
	}
	
}
