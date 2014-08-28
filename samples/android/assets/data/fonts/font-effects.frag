#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D u_texture;
uniform vec4 u_outlineColor;
uniform vec4 u_glowColor;
uniform int u_enableOutline;
uniform int u_enableGlow;
uniform vec2 u_outline;
uniform vec2 u_glow;

varying vec4 v_color;
varying vec2 v_texCoord;

const float smoothing = 1.0/32.0;
const float threshold = 0.5;

void main() {
    float distance = texture2D(u_texture, v_texCoord).a;	
	float alpha = 1.0;

	if (u_enableOutline > 0 && distance >= u_outline.x && distance <= u_outline.y) {
		
		if (distance <= u_outline.x + smoothing) {
			alpha = smoothstep(u_outline.x - smoothing, u_outline.x + smoothing, distance);
		}
		else {
			alpha = smoothstep(u_outline.y + smoothing, u_outline.y - smoothing, distance);
		}
		
		gl_FragColor = vec4(u_outlineColor.rgb, alpha * v_color.a * u_outlineColor.a);
	}
	else {
		vec2 glow = u_glow;
		
		glow.y = max(u_outline.y, glow.y);
		
		if (u_enableGlow > 0 && distance >= glow.x && distance < glow.y) {
			alpha = smoothstep(glow.x - smoothing, glow.y + smoothing, distance);
			gl_FragColor = vec4(u_glowColor.rgb, alpha * u_glowColor.a);
		}
		else {
			alpha = smoothstep(threshold - smoothing, threshold + smoothing, distance);
			gl_FragColor = vec4(v_color.rgb, alpha * v_color.a);
		}		
	}
}