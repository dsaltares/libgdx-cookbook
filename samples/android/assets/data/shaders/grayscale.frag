#ifdef GL_ES
precision mediump float;
precision mediump int;
#else
#define highp;
#endif

uniform sampler2D u_texture;

varying vec4 v_color;
varying vec2 v_texCoord;

const vec3 grayScaleMultiplier = vec3(0.299, 0.587, 0.114);

void main() {
	vec4 texColor = texture2D(u_texture, v_texCoord);
	vec3 gray = vec3(dot(texColor.rgb, grayScaleMultiplier));
    gl_FragColor = vec4(gray.r, gray.g, gray.b, texColor.a);
}