Shader "geronimo" {
	Properties {
		
	}
	SubShader {
		Tags {
			"Queue" = "Transparent"
			
		}
		Pass {
			ZWrite Off
			
			Blend SrcAlpha OneMinusSrcAlpha
			
			GLSLPROGRAM
			
			#ifdef VERTEX
			
			uniform mat4 _Object2World;
			uniform vec4 _Time;
			varying vec4 position_in_world_space;
			varying vec4 vtime;
			void main(void){
				(position_in_world_space = (_Object2World * gl_Vertex));
				(vtime = _Time);
				(gl_Position = (gl_ModelViewProjectionMatrix * gl_Vertex));
				
			}
			
			#endif
			
			
			#ifdef FRAGMENT
			
			varying vec4 vtime;
			varying vec4 position_in_world_space;
			void main(void){
				vec2 v4011;
				float v3971;
				float v4015;
				vec2 v4017;
				vec2 v4013;
				float v4016;
				vec2 v4014;
				vec2 v4012;
				float v3983;
				(v3971 = distance(position_in_world_space,vec4(0.0,0.0,0.0,1.0)));
				(v3983 = (((2.0 * 3.141593) * v3971) * 0.008333334));
				(v4011 = (0.9 * vec2(position_in_world_space[int(0.0)],position_in_world_space[int(1.0)])));
				(v4012 = fract(v4011));
				(v4013 = vec2((2.0 * v4012[int(0.0)]),(2.0 * v4012[int(1.0)])));
				(v4014 = ((v4012 * v4012) * vec2((3.0 - v4013[int(0.0)]),(3.0 - v4013[int(1.0)]))));
				(v4015 = v4014[int(1.0)]);
				(v4016 = v4014[int(0.0)]);
				(v4017 = floor(v4011));
				(gl_FragColor = vec4(0.0,0.0,0.0,((vtime[int(1.0)] / 100.0) * ((mod(v3971,13.0) / 13.0) - smoothstep((1.0 - (1.0 / v3983)),1.0,(abs((mod((((((((((fract((sin(dot((vec2(0.0) + vec2(v4017)),vec2(127.1,311.7))) * 43758.545312)) * (1.0 - v4016)) + (fract((sin(dot((vec2(1.0,0.0) + vec2(v4017)),vec2(127.1,311.7))) * 43758.545312)) * v4016)) * (1.0 - v4015)) + (((fract((sin(dot((vec2(0.0,1.0) + vec2(v4017)),vec2(127.1,311.7))) * 43758.545312)) * (1.0 - v4016)) + (fract((sin(dot((vec2(1.0) + vec2(v4017)),vec2(127.1,311.7))) * 43758.545312)) * v4016)) * v4015)) * 2.0) - 1.0) / v3983) * 2.0) + (mod(((atan(position_in_world_space[int(0.0)],position_in_world_space[int(1.0)]) + 3.141593) / (3.141593 * 2.0)),0.008333334) / 0.008333334)),1.0) - 0.5)) * 2.0))))));
				
			}
			
			#endif
			
			ENDGLSL
		}
		
	}
	
}

