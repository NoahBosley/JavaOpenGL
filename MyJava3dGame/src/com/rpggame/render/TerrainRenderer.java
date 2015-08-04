package com.rpggame.render;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.rpggame.models.RawModel;
import com.rpggame.render.textures.TerrainTexturePack;
import com.rpggame.shaders.TerrainShader;
import com.rpggame.terrain.Terrain;
import com.rpggame.tools.Maths;

public class TerrainRenderer {

	private TerrainShader shader;
	
	public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start(); //starts the terrain shader program
		shader.loadProjectionMatrix(projectionMatrix); //loads a projection matrix
		shader.connectTextureUnits(); //
		shader.stop();
	}
	
	public void render(List<Terrain> terrains) {
		for(Terrain terrain : terrains) { //Here is where I set a variable called terrain to hold the terrains in the list
			prepareTerrain(terrain);
			loadModelMatrix(terrain);
			GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //I here am using the draw elements function so that I can draw indices. I am using GL.GL_TRIANGLES because I want to render my quad in triangles. The second argument is there because it needs to know how many vertices there are in the model. I use GL11.GL_UNSIGSIGN_INTS because the indices are ints we use 0 because we do not want to offset the quad
			unbindTexturedModel();
		}
	}
	
	private void prepareTerrain(Terrain terrain) {
		RawModel model = terrain.getModel();
		GL30.glBindVertexArray(model.getVaoID()); //We do this because we want to do stuff to the VAO
		GL20.glEnableVertexAttribArray(0); //We do this because we need to enable this attribute list number. Remember each attribute list in the VAO holds an ID.
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		blendTextures(terrain);
		shader.loadShineVariables(1, 0);
	}
	
	private void blendTextures(Terrain terrain) {
		TerrainTexturePack texturePack = terrain.getTexturePack();
		GL13.glActiveTexture(GL13.GL_TEXTURE0); //This sets the texture to the first texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getBackgroundTexture().getTextureID()); //binds the texture to the first texture bank
		GL13.glActiveTexture(GL13.GL_TEXTURE1); //This sets the texture for the second texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getrTexture().getTextureID()); //binds the texture to the second texture bank
		GL13.glActiveTexture(GL13.GL_TEXTURE2); //This sets the texture for the third texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getgTexture().getTextureID()); //binds the textures to the third texture bank
		GL13.glActiveTexture(GL13.GL_TEXTURE3); //This sets the texture for the fourth texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getbTexture().getTextureID()); //binds the texture to the fourth texture bank
		GL13.glActiveTexture(GL13.GL_TEXTURE4); //This sets the texture for the 5th texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getBlendMap().getTextureID()); //binds the texture to the 5th texture bank
	}
	
	private void unbindTexturedModel() {
		GL20.glDisableVertexAttribArray(0); //Here is where I disable the attribute list number 0.
		GL20.glDisableVertexAttribArray(1); //Here is where I disable the attribute list number 1.
		GL20.glDisableVertexAttribArray(2); //Here is where I disable the attribute list number 2.
		GL30.glBindVertexArray(0); //This line of code disables the VAO
	}
	
	private void loadModelMatrix(Terrain terrain) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
}
