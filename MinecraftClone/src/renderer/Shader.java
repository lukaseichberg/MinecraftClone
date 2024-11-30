package renderer;

public abstract class Shader implements ShaderInterface {
	
	public Vertex in;
	public Vertex out;
	public Uniform uniform;
	
	public Shader() {
		uniform = new Uniform();
	}

}
