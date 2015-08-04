package com.rpggame.shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.rpggame.entities.Camera;
import com.rpggame.entities.Light;
import com.rpggame.tools.Maths;

public class TerrainShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/com/rpggame/shaders/terrainVertexShader.txt";
	private static final String FRAGMENT_FILE = "src/com/rpggame/shaders/terrainFragmentShader.txt";
	
	private int location_transfromationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_lightPosition;
	private int location_lightColor;
	private int location_shineDamper;
	private int location_reflectivity;
	private int location_backgroundTexture;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_skyColor;
	
	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
		super.bindAttribute(2, "normal");
	}

	//This method just set the variables to the location of the uniform variables
	@Override
	protected void getAllUniformLocations() {
		location_transfromationMatrix = super.getUniformLocation("transfromationMatrix"); //This line of code sets the value of 'location_transfromationMatrix' to the location of the uniform variable 
		location_projectionMatrix = super.getUniformLocation("projectionMatrix"); //This line of code sets the value of 'location_projectionMatrix' to the location of the uniform variable
		location_viewMatrix = super.getUniformLocation("viewMatrix"); //This line of code sets the value of 'location_viewMatrix' to the location of the uniform variable
		location_lightPosition = super.getUniformLocation("lightPosition"); //This line of code sets the value of 'location_lightPosition' to the location of the uniform variable
		location_lightColor = super.getUniformLocation("lightColor"); //This line of code sets the value of 'location_lightColor' to the location of the uniform variable
		location_shineDamper = super.getUniformLocation("shineDamper"); //This line of code sets the value of 'location_shineDamper' to the location of the uniform variable
		location_reflectivity = super.getUniformLocation("reflectivity"); //This line of code sets the value of 'location_reflectivity' to the location of the uniform variable
		location_backgroundTexture = super.getUniformLocation("backgroundTexture"); //This line of code sets the value of 'location_backgroundTexture' to the location of the uniform variable
		location_rTexture = super.getUniformLocation("rTexture"); //This line of code sets the value of 'location_rTexture' to the location of the uniform variable
		location_gTexture = super.getUniformLocation("gTexture"); //This line of code sets the value of 'location_gTexture' to the location of the uniform variable
		location_bTexture = super.getUniformLocation("bTexture"); //This line of code sets the value of 'location_bTexture' to the location of the uniform variable
		location_blendMap = super.getUniformLocation("blendMap"); //This line of code sets the value of 'location_blendMap' to the location of the uniform variable
		location_skyColor = super.getUniformLocation("skyColor");
	}
	
	public void loadSkyColor(float r, float g, float b) {
		super.loadVector(location_skyColor, new Vector3f(r, g, b));
	}
	
	public void connectTextureUnits() {
		super.loadInt(location_backgroundTexture, 0); //loads a value to the 'location_backgroundTexture' uniform
		super.loadInt(location_rTexture, 1);
		super.loadInt(location_gTexture, 2);
		super.loadInt(location_bTexture, 3);
		super.loadInt(location_blendMap, 4);
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	//This loads a transformation matrix to the 'transformationMatrix' variable
	public void loadTransformationMatrix(Matrix4f matrix) {
		super.loadMatrix(location_transfromationMatrix, matrix);
	}
	
	public void loadLight(Light light) {
		super.loadVector(location_lightPosition, light.getPosition());
		super.loadVector(location_lightColor, light.getColor());
	}
	
	public void loadProjectionMatrix(Matrix4f projection) {
		super.loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera) {
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

	
}
