uniform float time;
uniform vec2 resolution;
const float scale = 3.0f;
const float intensivity = 0.05f;
const int iteration = 3;
const float speed = 85.0; // range 1 to 100

const mat2 m = mat2( 1.6,  1.2, -1.2,  1.6 );

vec2 hash( vec2 p ) {
    p = vec2(dot(p,vec2(127.1,311.7)), dot(p,vec2(269.5,183.3)));
    return -1.0 + 2.0*fract(sin(p)*43758.5453123);
}

float noise( in vec2 p ) {
    const float K1 = 0.366025404;
    const float K2 = 0.211324865; ;
    vec2 i = floor(p + (p.x+p.y)*K1);
    vec2 a = p - i + (i.x+i.y)*K2;
    vec2 o = (a.x>a.y) ? vec2(1.0,0.0) : vec2(0.0,1.0);
    vec2 b = a - o + K2;
    vec2 c = a - 1.0 + 2.0*K2;
    vec3 h = max(0.5-vec3(dot(a,a), dot(b,b), dot(c,c) ), 0.0 );
    vec3 n = h*h*h*h*vec3( dot(a,hash(i+0.0)), dot(b,hash(i+o)), dot(c,hash(i+1.0)));
    return dot(n, vec3(70.0));
}

float fbm(vec2 n) {
    float total = 0.0, amplitude = 0.1;
    for (int i = 0; i < 7; i++) {
        total += noise(n) * amplitude;
        n = m * n;
        amplitude *= 0.4;
    }
    return total;
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 p = fragCoord.xy / resolution.xy;
    vec2 uv = p*vec2(resolution.x/resolution.y,1.0);
    float time = time*0.03f;
    float q = fbm(uv  * 0.5);

    uv *= scale;

    float f = 0.0;
    float weight = 0.7;
    for (int i=0; i<iteration; i++){
        f += weight*noise( uv );
        uv = m*uv + time;
        weight *= 0.6;
    }

    f = fract(f+time/(101.-speed));

    float coeff = intensivity/abs(.5 - f);
    vec3 result = vec3(.9,.2,.1)*coeff;

    fragColor = vec4(result,1.0);
}
void main(void)
{
    iTime = time;
    iResolution = vec3(resolution, 0.0);
    mainImage(gl_FragColor, gl_FragCoord.xy);
}