package renderer;

public class ShaderProgram {

	private Shader vertexShader;
	private Shader fragmentShader;
	public Uniform uniform;
	
	public ShaderProgram(Shader vs, Shader fs, Uniform uniform) {
		vertexShader = vs;
		fragmentShader = fs;
		this.uniform = uniform;
	}
	
	public Vertex processVertex(Vertex v0) {
		vertexShader.in = v0;
		vertexShader.out = null;
		vertexShader.uniform = uniform;
		vertexShader.main();
		return vertexShader.out;
	}
	
	public Vertex processFragment(Vertex v0) {
		fragmentShader.in = v0;
		fragmentShader.out = null;
		fragmentShader.uniform = uniform;
		fragmentShader.main();
		return fragmentShader.out;
	}

}
