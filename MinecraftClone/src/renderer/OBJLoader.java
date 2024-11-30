package renderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import maths.Vector2f;
import maths.Vector3f;
import maths.Vector4f;

public class OBJLoader {
	
	public static Model loadObjModel(String fileName) {
		FileReader fr = null;
		try {
			fr = new FileReader(new File("res/" + fileName + ".obj"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't load file!");
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(fr);
		String line;
		List<Vector3f> vertices = new ArrayList<>();
		List<Vector2f> textures = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Integer> vertexIndices = new ArrayList<>();
		List<Integer> textureIndices = new ArrayList<>();
		List<Integer> normalIndices = new ArrayList<>();
		
		try {
			while ((line = reader.readLine()) != null) {
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f vertex = new Vector3f(
							Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3])
					);
					vertices.add(vertex);
				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(
							Float.parseFloat(currentLine[1]),
							1-Float.parseFloat(currentLine[2])
					);
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(
							Float.parseFloat(currentLine[1]),
							Float.parseFloat(currentLine[2]),
							Float.parseFloat(currentLine[3])
					);
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					String[] vertex0 = currentLine[1].split("/");
					String[] vertex1 = currentLine[2].split("/");
					String[] vertex2 = currentLine[3].split("/");
					vertexIndices.add(Integer.parseInt(vertex0[0]) - 1);
					vertexIndices.add(Integer.parseInt(vertex1[0]) - 1);
					vertexIndices.add(Integer.parseInt(vertex2[0]) - 1);
					textureIndices.add(Integer.parseInt(vertex0[1]) - 1);
					textureIndices.add(Integer.parseInt(vertex1[1]) - 1);
					textureIndices.add(Integer.parseInt(vertex2[1]) - 1);
					normalIndices.add(Integer.parseInt(vertex0[2]) - 1);
					normalIndices.add(Integer.parseInt(vertex1[2]) - 1);
					normalIndices.add(Integer.parseInt(vertex2[2]) - 1);
				}
			}
			reader.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Model model = new Model(vertexIndices.size());
		
		for (int i = 0; i < vertexIndices.size() / 3; i++) {
			Vertex v0 = new Vertex(1, 2, 0);
			Vertex v1 = new Vertex(1, 2, 0);
			Vertex v2 = new Vertex(1, 2, 0);

			Vector3f pos0 = vertices.get(vertexIndices.get(i * 3));
			Vector3f pos1 = vertices.get(vertexIndices.get(i * 3 + 1));
			Vector3f pos2 = vertices.get(vertexIndices.get(i * 3 + 2));

			Vector2f texture0 = textures.get(textureIndices.get(i * 3));
			Vector2f texture1 = textures.get(textureIndices.get(i * 3 + 1));
			Vector2f texture2 = textures.get(textureIndices.get(i * 3 + 2));

			Vector3f normal0 = normals.get(normalIndices.get(i * 3));
			Vector3f normal1 = normals.get(normalIndices.get(i * 3 + 1));
			Vector3f normal2 = normals.get(normalIndices.get(i * 3 + 2));

			v0.pos = new Vector4f(pos0, 1);
			v0.vec2[0] = texture0;
			v0.vec3[0] = normal0;

			v1.pos = new Vector4f(pos1, 1);
			v1.vec2[0] = texture1;
			v1.vec3[0] = normal1;

			v2.pos = new Vector4f(pos2, 1);
			v2.vec2[0] = texture2;
			v2.vec3[0] = normal2;
			
			Vector3f d0 = pos1.sub(pos0);
			Vector3f d1 = pos2.sub(pos0);
			
			Vector2f deltaUv1 = texture1.sub(texture0);
			Vector2f deltaUv2 = texture2.sub(texture0);

			float r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv2.x * deltaUv1.y);
			d0._mul(deltaUv2.y);
			d1._mul(deltaUv1.y);

			Vector3f tangent = d0.sub(d1);
			tangent._mul(r);

			v0.vec3[1] = tangent;
			v1.vec3[1] = tangent;
			v2.vec3[1] = tangent;
				

			model.vertices[i * 3] = v0;
			model.vertices[i * 3 + 1] = v1;
			model.vertices[i * 3 + 2] = v2;
		}
		
		return model;
	}

}
