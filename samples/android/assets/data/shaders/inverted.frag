#ifdef GL_ES
precision mediump float;
precision mediump int;
#else
#define highp;
#endif


uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
	vec4 texColor = texture2D(u_texture, v_texCoord);
	vec3 inverted = 1.0 - texColor.rgb;
    gl_FragColor = vec4(inverted.r, inverted.g, inverted.b, texColor.a);
}