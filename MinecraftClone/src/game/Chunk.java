package game;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import maths.Maths;
import maths.Vector2f;
import maths.Vector3f;
import maths.Vector4f;
import renderer.Model;
import renderer.Vertex;

public class Chunk {

	public int[] data;
	public int size;
	public int height;
	public Model model;
	public boolean generated;
	
	public int chunkX, chunkZ;
	
	private PerlinNoise noise;
	
	private World world;
	
	private int[][] blockTexture = {
		{1, 1, 1, 1, 1, 1},
		{2, 2, 2, 2, 2, 2},
		{3, 3, 3, 3, 0, 2},
		{16, 16, 16, 16, 16, 16},
		{4, 4, 4, 4, 4, 4},
		{20, 20, 20, 20, 21, 21},
		{53, 53, 53, 53, 53, 53},
		{17, 17, 17, 17, 17, 17},
		{18, 18, 18, 18, 18, 18}
	};

	public Chunk(int chunkX, int chunkZ, int size, PerlinNoise noise, World world) {
		height = 256;
		data = new int[size * size * height];
		this.size = size;
		model = new Model(0);
		this.noise = noise;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.world = world;
		generated = false;
	}
	
	public void addTree(int x, int y, int z) {
		set(x, y, z, 6);
		set(x, y+1, z, 6);
		set(x, y+2, z, 6);
		set(x-1, y+2, z-1, 7);
		set(x, y+2, z-1, 7);
		set(x+1, y+2, z-1, 7);
		set(x-1, y+2, z, 7);
		set(x+1, y+2, z, 7);
		set(x-1, y+2, z+1, 7);
		set(x, y+2, z+1, 7);
		set(x+1, y+2, z+1, 7);
		set(x-1, y+3, z-1, 7);
		set(x, y+3, z-1, 7);
		set(x+1, y+3, z-1, 7);
		set(x-1, y+3, z, 7);
		set(x, y+3, z, 7);
		set(x+1, y+3, z, 7);
		set(x-1, y+3, z+1, 7);
		set(x, y+3, z+1, 7);
		set(x+1, y+3, z+1, 7);
		set(x-1, y+4, z, 7);
		set(x+1, y+4, z, 7);
		set(x, y+4, z, 7);
		set(x, y+4, z-1, 7);
		set(x, y+4, z+1, 7);
	}

	public void generate() {
		generated = true;
		float p00 = (float) (noise.getHeight(chunkX, chunkZ) * height);
		float p01 = (float) (noise.getHeight(chunkX, chunkZ + 1) * height);
		float p10 = (float) (noise.getHeight(chunkX + 1, chunkZ) * height);
		float p11 = (float) (noise.getHeight(chunkX + 1, chunkZ + 1) * height);

		float b00 = noise.getBiome(chunkX, chunkZ);
		float b01 = noise.getBiome(chunkX, chunkZ + 1);
		float b10 = noise.getBiome(chunkX + 1, chunkZ);
		float b11 = noise.getBiome(chunkX + 1, chunkZ + 1);
		
		for (int z = 0; z < size; z++) {
			float zv = (float) z / size;
			float p0 = Maths.cosint(p00, p01, zv);
			float p1 = Maths.cosint(p10, p11, zv);
			
			float b0 = Maths.cosint(b00, b01, zv);
			float b1 = Maths.cosint(b10, b11, zv);

			for (int x = 0; x < size; x++) {
				float xv = (float) x / size;
				float p = Maths.lerp(p0, p1, xv);
				float b = Maths.lerp(b0, b1, xv);

				float yh = (int) p;

				for (int y = 0; y < yh; y++) {
					if (y == 0) {
						set(x, y, z, 8);
					} else if (y < yh - 3) {
						set(x, y, z, 1);
					} else if (y == yh - 1) {
						if (b > 0.2f) {
							set(x, y, z, 3);
						} else {
							set(x, y, z, 9);
						}
					} else {
						if (b > 0.2f) {
							set(x, y, z, 2);
						} else {
							set(x, y, z, 9);
						}
					}
				}
				if (b > 0.2f) {
					if (noise.getFloat(chunkX * size + x, chunkZ * size + z) < 0.01f) {
						addTree(x, (int) yh, z);
					}
				}
			}
		}
	}

	public void fill(int value) {
		for (int i = 0; i < data.length; i++) {
			data[i] = value;
		}
	}

	public void set(int x, int y, int z, int value) {
		if (x >= 0 && x < size && y >= 0 && y < height && z >= 0 && z < size) {
			data[x + z * size + y * size * size] = value;
		} else if (y < height && y >= 0) {
			world.setBlock(x + chunkX * size, y, z + chunkZ * size, value, false);
		}
	}

	public int get(int x, int y, int z) {
		if (x >= 0 && x < size && y >= 0 && y < height && z >= 0 && z < size) {
			return data[x + z * size + y * size * size];
		} else if (y < height && y >= 0) {
			return world.getBlock(x + chunkX * size, y, z + chunkZ * size);
		}
		return -1;
	}

	public void updateModel() {
		List<Vertex> vertices = new ArrayList<>();

		for (int y = 0; y < height; y++) {
			for (int z = 0; z < size; z++) {
				for (int x = 0; x < size; x++) {
					int blockId = get(x, y, z);
					if (blockId > 0) {
						int xx = chunkX * size + x;
						int yy = y;
						int zz = chunkZ * size + z;
						if (get(x - 1, y, z) <= 0)
							addLeftFace(xx, yy, zz, vertices, blockTexture[blockId - 1][0]);
						if (get(x + 1, y, z) <= 0)
							addRightFace(xx, yy, zz, vertices, blockTexture[blockId - 1][2]);
						if (get(x, y, z - 1) <= 0)
							addBackFace(xx, yy, zz, vertices, blockTexture[blockId - 1][1]);
						if (get(x, y, z + 1) <= 0)
							addFrontFace(xx, yy, zz, vertices, blockTexture[blockId - 1][3]);
						if (get(x, y - 1, z) <= 0)
							addBottomFace(xx, yy, zz, vertices, blockTexture[blockId - 1][5]);
						if (get(x, y + 1, z) <= 0)
							addTopFace(xx, yy, zz, vertices, blockTexture[blockId - 1][4]);
					}
				}
			}
		}

		Vertex[] tmp = new Vertex[vertices.size()];
		vertices.toArray(tmp);

		model.vertices = tmp;
	}

	private void addTopFace(int x, int y, int z, List<Vertex> vertices, int texId) {
		Vertex v0 = new Vertex(1, 1, 0);
		Vertex v1 = new Vertex(1, 1, 0);
		Vertex v2 = new Vertex(1, 1, 0);
		Vertex v3 = new Vertex(1, 1, 0);

		float size = 1f / 16f;
		v0.pos = new Vector4f(x, y + 1, z, 1);
		v1.pos = new Vector4f(x + 1, y + 1, z, 1);
		v2.pos = new Vector4f(x, y + 1, z + 1, 1);
		v3.pos = new Vector4f(x + 1, y + 1, z + 1, 1);

		int txOffset = texId % 16;
		int tyOffset = texId / 16;
		v0.vec2[0] = new Vector2f(size * txOffset, size * tyOffset);
		v1.vec2[0] = new Vector2f(size * (txOffset + 1), size * tyOffset);
		v2.vec2[0] = new Vector2f(size * txOffset, size * (tyOffset + 1));
		v3.vec2[0] = new Vector2f(size * (txOffset + 1), size * (tyOffset + 1));

		Vector3f normal = new Vector3f(0, 1, 0);
		v0.vec3[0] = normal;
		v1.vec3[0] = normal;
		v2.vec3[0] = normal;
		v3.vec3[0] = normal;

		vertices.add(v2);
		vertices.add(v1);
		vertices.add(v0);

		vertices.add(v3);
		vertices.add(v1);
		vertices.add(v2);
	}

	private void addBottomFace(int x, int y, int z, List<Vertex> vertices, int texId) {
		Vertex v0 = new Vertex(1, 1, 0);
		Vertex v1 = new Vertex(1, 1, 0);
		Vertex v2 = new Vertex(1, 1, 0);
		Vertex v3 = new Vertex(1, 1, 0);

		float size = 1f / 16f;
		v0.pos = new Vector4f(x, y, z + 1, 1);
		v1.pos = new Vector4f(x + 1, y, z + 1, 1);
		v2.pos = new Vector4f(x, y, z, 1);
		v3.pos = new Vector4f(x + 1, y, z, 1);

		int txOffset = texId % 16;
		int tyOffset = texId / 16;
		v0.vec2[0] = new Vector2f(size * txOffset, size * tyOffset);
		v1.vec2[0] = new Vector2f(size * (txOffset + 1), size * tyOffset);
		v2.vec2[0] = new Vector2f(size * txOffset, size * (tyOffset + 1));
		v3.vec2[0] = new Vector2f(size * (txOffset + 1), size * (tyOffset + 1));

		Vector3f normal = new Vector3f(0, -1, 0);
		v0.vec3[0] = normal;
		v1.vec3[0] = normal;
		v2.vec3[0] = normal;
		v3.vec3[0] = normal;

		vertices.add(v2);
		vertices.add(v1);
		vertices.add(v0);

		vertices.add(v3);
		vertices.add(v1);
		vertices.add(v2);
	}

	private void addLeftFace(int x, int y, int z, List<Vertex> vertices, int texId) {
		Vertex v0 = new Vertex(1, 1, 0);
		Vertex v1 = new Vertex(1, 1, 0);
		Vertex v2 = new Vertex(1, 1, 0);
		Vertex v3 = new Vertex(1, 1, 0);

		float size = 1f / 16f;
		v0.pos = new Vector4f(x, y + 1, z, 1);
		v1.pos = new Vector4f(x, y + 1, z + 1, 1);
		v2.pos = new Vector4f(x, y, z, 1);
		v3.pos = new Vector4f(x, y, z + 1, 1);

		int txOffset = texId % 16;
		int tyOffset = texId / 16;
		v0.vec2[0] = new Vector2f(size * txOffset, size * tyOffset);
		v1.vec2[0] = new Vector2f(size * (txOffset + 1), size * tyOffset);
		v2.vec2[0] = new Vector2f(size * txOffset, size * (tyOffset + 1));
		v3.vec2[0] = new Vector2f(size * (txOffset + 1), size * (tyOffset + 1));

		Vector3f normal = new Vector3f(-1, 0, 0);
		v0.vec3[0] = normal;
		v1.vec3[0] = normal;
		v2.vec3[0] = normal;
		v3.vec3[0] = normal;

		vertices.add(v2);
		vertices.add(v1);
		vertices.add(v0);

		vertices.add(v3);
		vertices.add(v1);
		vertices.add(v2);
	}

	private void addRightFace(int x, int y, int z, List<Vertex> vertices, int texId) {
		Vertex v0 = new Vertex(1, 1, 0);
		Vertex v1 = new Vertex(1, 1, 0);
		Vertex v2 = new Vertex(1, 1, 0);
		Vertex v3 = new Vertex(1, 1, 0);

		float size = 1f / 16f;
		int txOffset = texId % 16;
		int tyOffset = texId / 16;
		v0.vec2[0] = new Vector2f(size * txOffset, size * tyOffset);
		v1.vec2[0] = new Vector2f(size * (txOffset + 1), size * tyOffset);
		v2.vec2[0] = new Vector2f(size * txOffset, size * (tyOffset + 1));
		v3.vec2[0] = new Vector2f(size * (txOffset + 1), size * (tyOffset + 1));

		v0.pos = new Vector4f(x + 1, y + 1, z + 1, 1);
		v1.pos = new Vector4f(x + 1, y + 1, z, 1);
		v2.pos = new Vector4f(x + 1, y, z + 1, 1);
		v3.pos = new Vector4f(x + 1, y, z, 1);
		
		Vector3f normal = new Vector3f(1, 0, 0);
		v0.vec3[0] = normal;
		v1.vec3[0] = normal;
		v2.vec3[0] = normal;
		v3.vec3[0] = normal;

		vertices.add(v2);
		vertices.add(v1);
		vertices.add(v0);

		vertices.add(v3);
		vertices.add(v1);
		vertices.add(v2);
	}

	private void addFrontFace(int x, int y, int z, List<Vertex> vertices, int texId) {
		Vertex v0 = new Vertex(1, 1, 0);
		Vertex v1 = new Vertex(1, 1, 0);
		Vertex v2 = new Vertex(1, 1, 0);
		Vertex v3 = new Vertex(1, 1, 0);

		float size = 1f / 16f;
		int txOffset = texId % 16;
		int tyOffset = texId / 16;
		v0.vec2[0] = new Vector2f(size * txOffset, size * tyOffset);
		v1.vec2[0] = new Vector2f(size * (txOffset + 1), size * tyOffset);
		v2.vec2[0] = new Vector2f(size * txOffset, size * (tyOffset + 1));
		v3.vec2[0] = new Vector2f(size * (txOffset + 1), size * (tyOffset + 1));

		v0.pos = new Vector4f(x, y + 1, z + 1, 1);
		v1.pos = new Vector4f(x + 1, y + 1, z + 1, 1);
		v2.pos = new Vector4f(x, y, z + 1, 1);
		v3.pos = new Vector4f(x + 1, y, z + 1, 1);

		Vector3f normal = new Vector3f(0, 0, 1);
		v0.vec3[0] = normal;
		v1.vec3[0] = normal;
		v2.vec3[0] = normal;
		v3.vec3[0] = normal;

		vertices.add(v2);
		vertices.add(v1);
		vertices.add(v0);

		vertices.add(v3);
		vertices.add(v1);
		vertices.add(v2);
	}

	private void addBackFace(int x, int y, int z, List<Vertex> vertices, int texId) {
		Vertex v0 = new Vertex(1, 1, 0);
		Vertex v1 = new Vertex(1, 1, 0);
		Vertex v2 = new Vertex(1, 1, 0);
		Vertex v3 = new Vertex(1, 1, 0);

		float size = 1f / 16f;
		int txOffset = texId % 16;
		int tyOffset = texId / 16;
		v0.vec2[0] = new Vector2f(size * txOffset, size * tyOffset);
		v1.vec2[0] = new Vector2f(size * (txOffset + 1), size * tyOffset);
		v2.vec2[0] = new Vector2f(size * txOffset, size * (tyOffset + 1));
		v3.vec2[0] = new Vector2f(size * (txOffset + 1), size * (tyOffset + 1));

		v0.pos = new Vector4f(x + 1, y + 1, z, 1);
		v1.pos = new Vector4f(x, y + 1, z, 1);
		v2.pos = new Vector4f(x + 1, y, z, 1);
		v3.pos = new Vector4f(x, y, z, 1);

		Vector3f normal = new Vector3f(0, 0, -1);
		v0.vec3[0] = normal;
		v1.vec3[0] = normal;
		v2.vec3[0] = normal;
		v3.vec3[0] = normal;

		vertices.add(v2);
		vertices.add(v1);
		vertices.add(v0);

		vertices.add(v3);
		vertices.add(v1);
		vertices.add(v2);
	}
	
	public boolean load() {
		byte[] byteArray;
		File f = new File("res/world/"+chunkX+"_"+chunkZ+".dat");
		if (f.exists()) {
			try {
				byteArray = Files.readAllBytes(f.toPath());
				int index = 0;
				for (int i = 0; i < byteArray.length; i += 5) {
					int blockCount = (((((int) byteArray[i]) & 0xFF) << 24) | ((((int) byteArray[i + 1]) & 0xFF) << 16) | ((((int) byteArray[i + 2]) & 0xFF) << 8) | ((int) byteArray[i + 3]) & 0xFF);
					int blockId = byteArray[i + 4];
					for (int j = 0; j < blockCount; j++) {
						data[index] = blockId;
						index++;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public void save() {
		List<Byte> byteData = new ArrayList<>();
		int prevBlockId = 0;
		int blockCount = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] != prevBlockId) {
				if (blockCount > 0) {
					byteData.add((byte) ((blockCount >> 24) & 0xFF));
					byteData.add((byte) ((blockCount >> 16) & 0xFF));
					byteData.add((byte) ((blockCount >> 8) & 0xFF));
					byteData.add((byte) (blockCount & 0xFF));
					byteData.add((byte) (prevBlockId & 0xFF));
				}
				blockCount = 0;
			}
			blockCount++;
			prevBlockId = data[i];
		}
		if (blockCount > 0) {
			byteData.add((byte) ((blockCount >> 24) & 0xFF));
			byteData.add((byte) ((blockCount >> 16) & 0xFF));
			byteData.add((byte) ((blockCount >> 8) & 0xFF));
			byteData.add((byte) (blockCount & 0xFF));
			byteData.add((byte) (prevBlockId & 0xFF));
		}
		
		byte[] byteArray = new byte[byteData.size()];
		for (int i = 0; i < byteData.size(); i++) byteArray[i] = byteData.get(i);
		
		try {
			File f = new File("res/world/"+chunkX+"_"+chunkZ+".dat");
			if (f.exists()) {
				f.delete();
			}
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(byteArray);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
