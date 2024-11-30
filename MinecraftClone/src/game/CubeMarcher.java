package game;

import java.util.ArrayList;

import maths.Vector3f;

public class CubeMarcher {
	
	public int width;
	public int height;
	public int depth;
	public int x, z;
	public int[] map = new int[width * height * depth];
	public ArrayList<Vector3f> triangles;
	
	public CubeMarcher(int width, int height, int depth, int[] data, int x, int z) {
		this.width = width;
		this.height = height;
		this.depth = depth;
		this.x = x;
		this.z = z;
	}

	public Vector3f[] march() {
		triangles = new ArrayList<>();
		for (int z = 0; z < depth - 1; z++) {
		    for (int y = 0; y < height - 1; y++) {
		        for (int x = 0; x < width - 1; x++) {
		            triangulateCell(x, y, z);
		        }
		    }
		}
		Vector3f[] data = new Vector3f[triangles.size()];
		for (int i = 0; i < triangles.size(); i++) {
			data[i] = triangles.get(i);
		}
		return data;
	}

	void triangulateCell(int x, int y, int z) {
	    boolean[] points = new boolean[8];
	    ArrayList<Vector3f> vertices = new ArrayList<>();
	    if (check000(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check001(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check010(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check011(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check100(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check101(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check110(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	    if (check111(x, y, z, points, vertices)) {
	        addToTriangles(vertices);
	        vertices.clear();
	    }
	}

	void addToTriangles(ArrayList<Vector3f> vertices) {
	    for (int i = 1; i < vertices.size() - 1; i++) {
	        triangles.add(vertices.get(0));
	        triangles.add(vertices.get(i));
	        triangles.add(vertices.get(i + 1));
	    }
	}

	boolean check000(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x, y, z) != 0) {
	        if (!points[0]) {
	            points[0] = true;
	            if (!check100(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y, (float) z + this.z * depth));
	            }
	            if (!check001(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check010(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y + 0.5f, (float) z + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check010(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x, y + 1, z) != 0) {
	        if (!points[2]) {
	            points[2] = true;
	            if (!check011(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y + 1, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check000(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y + 0.5f, (float) z + this.z * depth));
	            }
	            if (!check110(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y + 1, (float) z + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check110(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x + 1, y + 1, z) != 0) {
	        if (!points[6]) {
	            points[6] = true;
	            if (!check010(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y + 1, (float) z + this.z * depth));
	            }
	            if (!check111(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y + 1, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check100(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y + 0.5f, (float) z + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check100(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x + 1, y, z) != 0) {
	        if (!points[4]) {
	            points[4] = true;
	            if (!check110(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y + 0.5f, (float) z + this.z * depth));
	            }
	            if (!check101(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check000(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y, (float) z + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check001(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x, y, z + 1) != 0) {
	        if (!points[1]) {
	            points[1] = true;
	            if (!check000(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check101(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y, (float) z + 1 + this.z * depth));
	            }
	            if (!check011(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y + 0.5f, (float) z + 1 + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check011(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x, y + 1, z + 1) != 0) {
	        if (!points[3]) {
	            points[3] = true;
	            if (!check010(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y + 1, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check001(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + this.x * width, (float) y + 0.5f, (float) z + 1 + this.z * depth));
	            }
	            if (!check111(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y + 1, (float) z + 1 + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check111(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x + 1, y + 1, z + 1) != 0) {
	        if (!points[7]) {
	            points[7] = true;
	            if (!check110(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y + 1, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check011(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y + 1, (float) z + 1 + this.z * depth));
	            }
	            if (!check101(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y + 0.5f, (float) z + 1 + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	boolean check101(int x, int y, int z, boolean[] points, ArrayList<Vector3f> vertices) {
	    if (getPoint(x + 1, y, z + 1) != 0) {
	        if (!points[5]) {
	            points[5] = true;
	            if (!check100(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y, (float) z + 0.5f + this.z * depth));
	            }
	            if (!check111(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 1 + this.x * width, (float) y + 0.5f, (float) z + 1 + this.z * depth));
	            }
	            if (!check001(x, y, z, points, vertices)) {
	                vertices.add(new Vector3f((float) x + 0.5f + this.x * width, (float) y, (float) z + 1 + this.z * depth));
	            }
	        }
	        return true;
	    }
	    return false;
	}

	void clearCheck(boolean[] points) {
	    for (int i = 0; i < points.length; i++) {
	        points[i] = false;
	    }
	}

	int getPoint(int x, int y, int z) {
	    return map[x + y * width + z * width * height];
	}

}
