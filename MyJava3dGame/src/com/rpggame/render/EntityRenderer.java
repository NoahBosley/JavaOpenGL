package com.rpggame.render;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import com.rpggame.entities.Entity;
import com.rpggame.models.RawModel;
import com.rpggame.models.TexturedModel;
import com.rpggame.render.textures.ModelTexture;
import com.rpggame.shaders.StaticShader;
import com.rpggame.tools.Maths;

public class EntityRenderer {
	
	private StaticShader shader;
	
	public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
		this.shader = shader;
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	
	public void render(Map<TexturedModel, List<Entity>> entities) {
		for(TexturedModel model : entities.keySet()) {
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity : batch) {
				prepareInstance(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0); //I here am using the draw elements function so that I can draw indices. I am using GL.GL_TRIANGLES because I want to render my quad in triangles. The second argument is there because it needs to know how many vertices there are in the model. I use GL11.GL_UNSIGSIGN_INTS because the indices are ints we use 0 because we do not want to offset the quad
			}
			unbindTexturedModel();
		}
	}
	
	private void prepareTexturedModel(TexturedModel texturedModel) {
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID()); //We do this because we want to do stuff to the VAO
		GL20.glEnableVertexAttribArray(0); //We do this because we need to enable this attribute list number. Remember each attribute list in the VAO holds an ID.
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		ModelTexture texture = texturedModel.getTexture();
		if(texture.hasTransparency()) {
			MasterRenderer.disableCulling();
		}
		shader.loadFakeLighting(texture.isUseFakeLighting());
		shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());
		GL13.glActiveTexture(GL13.GL_TEXTURE0); //This gets the texture from the first texture bank
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID()); //binds the textures as a 2d texture and also takes the texture ID
	}
	
	private void unbindTexturedModel() {
		MasterRenderer.enableCulling();
		GL20.glDisableVertexAttribArray(0); //Here is where I disable the attribute list number 0.
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL30.glBindVertexArray(0); //This line of code lets OpenGL know that we are done using this attribute number in the VAO
	}
	
	private void prepareInstance(Entity entity) {
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
	}
	
	}


