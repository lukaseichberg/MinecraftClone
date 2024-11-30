package game;

import renderer.Shader;
import renderer.Vertex;

public class VS extends Shader {

	@Override
	public void main() {
		out = new Vertex(1, 2, 0);
		out.vec2[0] = in.vec2[0].clone();
		out.vec3[0] = in.vec3[0].clone();
		out.vec3[1] = in.pos.getVector3f();
//		out.vec4[0] = in.pos.mul(uniform.mat4f[1]);
		out.pos = in.pos.mul(uniform.mat4f[0]);
	}

}
