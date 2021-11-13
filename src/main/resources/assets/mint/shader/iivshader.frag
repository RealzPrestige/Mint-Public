#extension GL_OES_standard_derivatives : enable

precision mediump float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;
uniform sampler2D texture;

#define PI 3.1415

void main( void ) {
    vec4 centerCol = texture2D(texture, gl_TexCoord[0].xy);
	vec2 p = (gl_FragCoord.xy / resolution.xy);
	vec3 color = vec3(0.2);

	p.y += sin(p.x * 5.0 + time) * 0.1;

	if (distance(vec2(0.0), vec2(0.0, p.y)) < (1.0 / 6.0) * 6.0 && distance(vec2(0.0), vec2(0.0, p.y)) > 0.1)
		color = vec3(1.0, 0.0, 0.0) + clamp(0.0, 0.0,(sin(p.x * 5.0 + time + PI) * 0.1) * 5.0);
	if (distance(vec2(0.0), vec2(0.0, p.y)) < (1.0 / 6.0) * 5.0 && distance(vec2(0.0), vec2(0.0, p.y)) > 0.1)
		color = vec3(1.0, 0.5, 0.0) + clamp(0.0, 0.0,(sin(p.x * 5.0 + time + PI) * 0.1) * 5.0);
	if (distance(vec2(0.0), vec2(0.0, p.y)) < (1.0 / 6.0) * 4.0 && distance(vec2(0.0), vec2(0.0, p.y)) > 0.1)
		color = vec3(1.0, 1.0, 0.0) + clamp(0.0, 0.0,(sin(p.x * 5.0 + time + PI) * 0.1) * 5.0);
	if (distance(vec2(0.0), vec2(0.0, p.y)) < (1.0 / 6.0) * 3.0 && distance(vec2(0.0), vec2(0.0, p.y)) > 0.1)
		color = vec3(0.0, 0.8, 0.0) + clamp(0.0, 0.0,(sin(p.x * 5.0 + time + PI) * 0.1) * 5.0);
	if (distance(vec2(0.0), vec2(0.0, p.y)) < (1.0 / 6.0) * 2.0 && distance(vec2(0.0), vec2(0.0, p.y)) > 0.1)
		color = vec3(0.0, 0.0, 1.0) + clamp(0.0, 0.0,(sin(p.x * 5.0 + time + PI) * 0.1) * 5.0);
	if (distance(vec2(0.0), vec2(0.0, p.y)) < (1.0 / 6.0) * 1.0 && distance(vec2(0.0), vec2(0.0, p.y - 1.0)) < 1.0)
		color = vec3(0.8, 0.0, 1.0) + clamp(0.0, 0.0,(sin(p.x * 5.0 + time + PI) * 0.1) * 5.0);


	gl_FragColor = vec4(color, centerCol.a);

}