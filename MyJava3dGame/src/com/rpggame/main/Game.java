package com.rpggame.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import com.rpggame.entities.Camera;
import com.rpggame.entities.Entity;
import com.rpggame.entities.Light;
import com.rpggame.models.RawModel;
import com.rpggame.models.TexturedModel;
import com.rpggame.render.Loader;
import com.rpggame.render.MasterRenderer;
import com.rpggame.render.OBJLoader;
import com.rpggame.render.Window;
import com.rpggame.render.textures.ModelTexture;
import com.rpggame.render.textures.TerrainTexture;
import com.rpggame.render.textures.TerrainTexturePack;
import com.rpggame.terrain.Terrain;

public class Game {
	

	public static void main(String[] args) {
		render();	
		Loader loader = new Loader();
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("GameGrass"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("Dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("flowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("Marble"));
		
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
		
		
		RawModel model = OBJLoader.loadObjModel("grassModel", loader);
		TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("grassModel_T")));
		staticModel.getTexture().setTransparency(true);
		staticModel.getTexture().setUseFakeLighting(true);
		Light light = new Light(new Vector3f(0, 40, -20), new Vector3f(1, 1, 1));
		
		Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap, "HeightMap");
		
		Camera camera = new Camera();
		MasterRenderer renderer = new MasterRenderer();
		
		
		List<Entity> grasses = new ArrayList<Entity>();
		Random random = new Random();
		
		for(int i = 0; i < 60; i++) {
			if(i % 20 == 0) {
			float z = random.nextFloat() * -500;
			float x = random.nextFloat() * -500;
			float y = terrain.getHeightOfTerrain(x, z);
			
			grasses.add(new Entity(staticModel, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.9f));
		}
		}
		
		while(!Display.isCloseRequested()) {
			camera.move();
			renderer.processTerrain(terrain);
			for(Entity grass : grasses) {
				renderer.processEntity(grass);
			}
			renderer.render(light, camera);
			update();
		}
		renderer.cleanUp();
		loader.cleanUp();
		Window.closeDisplay();
	}
	
	public static void update() {
		Window.updateDisplay();
	}
	
	public static void render() {
		Window.createDisplay();
	}
	
}
