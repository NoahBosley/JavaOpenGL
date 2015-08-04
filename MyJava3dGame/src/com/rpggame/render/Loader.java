package com.rpggame.render;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import com.rpggame.models.RawModel;
import com.rpggame.render.textures.TextureData;

import de.matthiasmann.twl.utils.PNGDecoder;
import de.matthiasmann.twl.utils.PNGDecoder.Format;

public class Loader {

	// Note: if you ever want to do anything to a VAO you need to bind it

	private List<Integer> vaos = new ArrayList<Integer>(); // I create this list
															// so that I can
															// store VAOs thus
															// letting us delete
															// them later
	private List<Integer> vbos = new ArrayList<Integer>(); // I create this list
															// so that I can
															// store VBOS thus
															// letting us delete
															// them later
	private List<Integer> textures = new ArrayList<Integer>();

	// what ever I want to load into the VAO put in this method
	public RawModel loadToVao(float[] positions, float[] textureCoords,
			float[] normals, int[] indices) {
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new RawModel(vaoID, indices.length);
	}

	public int loadTexture(String fileName) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG", new FileInputStream(
					"res/" + fileName + ".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D,
					GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS,
					-0.4f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID(); // creates a new texture
		textures.add(textureID);
		return textureID;
	}

	public void cleanUp() {
		for (int vao : vaos) {
			GL30.glDeleteVertexArrays(vao);
		}
		for (int vbo : vbos) {
			GL15.glDeleteBuffers(vbo);
		}
		for (int texture : textures) {
			GL11.glDeleteTextures(texture);
		}
	}

	private int loadCubeMap(String[] textureFiles) {
		int texID = GL11.glGenTextures(); // Generates an empty texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0); // Binds the texture to texture
												// unit 0
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);

		for (int i = 0; i < textureFiles.length; i++) {
			TextureData data = decodeTextureFile("res/" + textureFiles[i]
					+ ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0,
					GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		textures.add(texID);
		return texID;
	}

	private TextureData decodeTextureFile(String fileName) {
		int width = 0;
		int height = 0;
		ByteBuffer buffer = null;
		try {
			FileInputStream in = new FileInputStream(fileName);
			PNGDecoder decoder = new PNGDecoder(in);
			width = decoder.getWidth();
			height = decoder.getHeight();
			buffer = ByteBuffer.allocateDirect(4 * width * height);
			decoder.decode(buffer, width * 4, Format.RGBA);
			buffer.flip();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Tried to load texture " + fileName
					+ ", didn't work");
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}

	private int createVAO() {
		int vaoID = GL30.glGenVertexArrays(); // Creates the VAO
		vaos.add(vaoID); // Adds the VAO to the Array List
		GL30.glBindVertexArray(vaoID); // Binds the VAO
		return vaoID;
	}

	// This method is here because I need to store the VBOs in the VAO
	private void storeDataInAttributeList(int attributeNumber,
			int coordinateSize, float[] data) {
		int vboID = GL15.glGenBuffers(); // Creates an empty VBO
		vbos.add(vboID); // Adds the VBO to the VBO list
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID); // Binds the buffer, so
														// we can do things with
														// it. I use
														// GL_ARRAY_BUFFER to
														// specify the type of
														// VBO
		FloatBuffer buffer = storeDataInFloatBuffer(data); // Here is where I
															// convert my data
															// into a float
															// buffer
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW); // here
																				// is
																				// where
																				// I
																				// store
																				// data
																				// into
																				// the
																				// VBO
		GL20.glVertexAttribPointer(attributeNumber, coordinateSize,
				GL11.GL_FLOAT, false, 0, 0); // This puts the VBO(Vertex Array
												// Buffer) into the VAO(Vertex
												// Array Object). The number
												// three is how many vectors we
												// want which is 3 because we
												// are using x, y, and z
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0); // Since I am done setting
													// the data of the VBO I can
													// now unbind it letting
													// opengl know we are done.
	}

	private void unbindVAO() {
		GL30.glBindVertexArray(0); // Unbinds the currently bound VAO
	}

	public void bindIndicesBuffer(int[] indices) {
		int vboID = GL15.glGenBuffers(); // This creates a new empty VBO
		vbos.add(vboID); // This adds the VBO into the VBO list
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer,
				GL15.GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data); // We do this so it is ready to be read from
		buffer.flip(); // We do this so the int buffer is ready to be read from
		return buffer;
	}

	// In order to store data into a VBO we have to call this method
	private FloatBuffer storeDataInFloatBuffer(float[] data) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length); // Creates
																			// an
																			// empty
																			// float
																			// buffer
		buffer.put(data); // I call this OpenGL functions so I can put data in
							// the empty float buffer
		buffer.flip(); // I call this method so that this float buffer is ready
						// to be read from
		return buffer;
	}

}
