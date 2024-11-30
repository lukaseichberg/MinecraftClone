package game;

import java.util.HashMap;
import java.util.Map;

import renderer.Renderer;

public class World {
	
	private Map<Integer, Map<Integer, Chunk>> chunks;
	public PerlinNoise noise;
	private int chunkSize;
	
	public World(long seed) {
		chunks = new HashMap<>();
		noise = new PerlinNoise(seed);
		chunkSize = 16;
	}
	
	public void setBlock(int x, int y, int z, int value, boolean update) {
		int cz = Math.floorDiv(z, chunkSize);
		int cx = Math.floorDiv(x, chunkSize);
		if (!chunks.containsKey(cz)) {
			chunks.put(cz, new HashMap<>());
		}
		if (!chunks.get(cz).containsKey(cx)) {
			Chunk c = new Chunk(cx, cz, chunkSize, noise, this);
			chunks.get(cz).put(cx, c);
		}
		Chunk c = chunks.get(cz).get(cx);
		c.set(Math.floorMod(x, chunkSize), y, Math.floorMod(z, chunkSize), value);
		c.updateModel();
		updateNeigborChunkModel(cx, cz);
	}
	
	public int getBlock(int x, int y, int z) {
		int cz = Math.floorDiv(z, chunkSize);
		int cx = Math.floorDiv(x, chunkSize);

		if (!chunks.containsKey(cz)) {
			return -1;
		}
		if (!chunks.get(cz).containsKey(cx)) {
			return -1;
		}
		return chunks.get(cz).get(cx).get(Math.floorMod(x, chunkSize), y, Math.floorMod(z, chunkSize));
	}
	
	public void generateChunk(int x, int z) {
		if (!chunks.containsKey(z)) {
			chunks.put(z, new HashMap<>());
		}
		if (!chunks.get(z).containsKey(x)) {
			Chunk c = new Chunk(x, z, chunkSize, noise, this);
			if (c.load()) {
				c.generated = true;
			}
			chunks.get(z).put(x, c);
		}
		Chunk c = chunks.get(z).get(x);
		if (!c.generated) {
			c.generate();
			c.updateModel();
		}
		updateNeigborChunkModel(x, z);
	}
	
	public void save() {
		for (Map.Entry<Integer, Map<Integer, Chunk>> cz:chunks.entrySet()) {
			for (Map.Entry<Integer, Chunk> cx:cz.getValue().entrySet()) {
				cx.getValue().save();
			}
		}
	}
	
	public void updateNeigborChunkModel(int x, int z) {
		updateChunkModel(x - 1, z);
		updateChunkModel(x + 1, z);
		updateChunkModel(x, z - 1);
		updateChunkModel(x, z + 1);
	}
	
	public void updateChunkModel(int x, int z) {
		if (chunks.containsKey(z)) {
			if (chunks.get(z).containsKey(x)) {
				chunks.get(z).get(x).updateModel();
			}
		}
	}
	
	public void render(int x, int z, int range, Renderer r) {
		for (int zz = z - range; zz <= z + range; zz++) {
			for (int xx = x - range; xx <= x + range; xx++) {
				if (chunks.containsKey(zz)) {
					if (chunks.get(zz).containsKey(xx)) {
						Chunk c = chunks.get(zz).get(xx);
						if (c.generated) {
							r.renderModel(c.model);
							continue;
						}
						if (c.load()) {
							c.generated = true;
							continue;
						}
					}
				}
				generateChunk(xx, zz);
			}
		}
	}
	
	public int countChunks() {
		int i = 0;
		for (Map.Entry<Integer, Map<Integer, Chunk>> cz:chunks.entrySet()) {
			i += cz.getValue().size();
		}
		return i;
	}

}
