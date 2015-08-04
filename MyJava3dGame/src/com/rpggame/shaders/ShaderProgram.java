package com.rpggame.shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShaderProgram {

	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	public ShaderProgram(String vertexFile, String fragmentFile) {
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER); //This is basically just initializing the 'vertexShaderID' variable to load a vertex shader file
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER); //This is basically just initializing the 'fragmentShaderID' variable to load a fragment shader file
		programID = GL20.glCreateProgram(); //This just initializes the programID variable to create a new shader program
		GL20.glAttachShader(programID, vertexShaderID); //This is basically just attaching my vertex shader file to the program I just created
		GL20.glAttachShader(programID, fragmentShaderID); //This is basically just attaching my fragment shader file to the program I just created
		bindAttributes(); //calls the bind attributes method
		GL20.glLinkProgram(programID); //Links all of the shader files together
		GL20.glValidateProgram(programID); //Just validates the program
		getAllUniformLocations();
	}
	
	protected abstract void getAllUniformLocations();
	
	//Gets the uniform variable's location
	protected int getUniformLocation(String uniformName) {
		return GL20.glGetUniformLocation(programID, uniformName); //This line of code tells OpenGL that we need to find a uniform variable by reading the uniform name I put in
	}
	
	public void start() {
		GL20.glUseProgram(programID); //Basically just starts the program taking the program id
	}
	
	public void stop() {
		GL20.glUseProgram(0); //Basically just stops the shader program with a argument 0
	}
	
	//This just basically just cleans up every thing dealing with shaders. This method includes self explanatory methods 
	public void cleanUp() {
		stop(); //calls the stop method
		GL20.glDetachShader(programID, vertexShaderID); //Detaches the vertex shader file from the shader program
		GL20.glDetachShader(programID, fragmentShaderID); //Detaches the fragment shader file from the shader program
		GL20.glDeleteShader(vertexShaderID); //Deletes the vertex shader file from the program
		GL20.glDeleteShader(fragmentShaderID); //Deletes the fragment shader file from the program
		GL20.glDeleteProgram(programID); //Finally just deletes the shader program
	}
	
	protected abstract void bindAttributes();
	
	protected void bindAttribute(int attribute, String variableName) {
		GL20.glBindAttribLocation(programID, attribute, variableName); //This is basically just binding a attribute in the VAO to a variable in the shader code
	}
	
	//loads values to the uniform variables
	protected void loadFloat(int location, float value) {
		GL20.glUniform1f(location, value); //Needs the location of the uniform variable and loads a value to the uniform variable
	}
	
	//loads values to the uniform variables
	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value); //Needs the location of the uniform variable and loads a value to the uniform variable
	}
	
	//loads values to the uniform variables
	protected void loadVector(int location, Vector3f vector) {
		GL20.glUniform3f(location, vector.x, vector.y, vector.z); //Needs the location of the Vector variable in the shader code
	}
	
	//loads values to the uniform variables
	protected void loadBoolean(int location, boolean value) {
		float toLoad = 0;
		if(value) {
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}
	
	//loads values to the uniform variables
	protected void loadMatrix(int location, Matrix4f matrix) {
		matrix.store(matrixBuffer); //we need to store the float buffer
		matrixBuffer.flip(); //we need to get the matrix to be read from
		GL20.glUniformMatrix4(location, false, matrixBuffer); //we load the matrix to the location of the uniform variable
	}
	
	//This method loads the shader program files
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine()) != null) {
				shaderSource.append(line).append("\n"); 
			}
			reader.close();
			} catch (IOException e) {
				System.err.println("Could not read file!");
				e.printStackTrace();
				System.exit(-1);
			}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader.");
			System.exit(-1);
		}
		return shaderID;
	}
}
	
