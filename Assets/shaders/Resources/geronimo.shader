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
				vec2 v5741;
				vec2 v5742;
				float v5745;
				float v5699;
				float v5744;
				vec2 v5740;
				vec2 v5739;
				vec2 v5743;
				float v5711;
				(v5699 = distance(position_in_world_space,vec4(0.0,0.0,0.0,1.0)));
				(v5711 = (((2.0 * 3.141593) * v5699) * 0.008333334));
				(v5739 = (0.9 * vec2(position_in_world_space[int(0.0)],position_in_world_space[int(1.0)])));
				(v5740 = floor(v5739));
				(v5741 = fract(v5739));
				(v5742 = vec2((2.0 * v5741[int(0.0)]),(2.0 * v5741[int(1.0)])));
				(v5743 = ((v5741 * v5741) * vec2((3.0 - v5742[int(0.0)]),(3.0 - v5742[int(1.0)]))));
				(v5744 = v5743[int(1.0)]);
				(v5745 = v5743[int(0.0)]);
				(gl_FragColor = vec4(0.0,0.0,0.0,((vtime[int(1.0)] / 100.0) * ((mod(v5699,13.0) / 13.0) - smoothstep((1.0 - (1.0 / v5711)),1.0,(abs((mod((((((((((fract((sin(dot((vec2(0.0) + vec2(v5740)),vec2(127.1,311.7))) * 43758.545312)) * (1.0 - v5745)) + (fract((sin(dot((vec2(1.0,0.0) + vec2(v5740)),vec2(127.1,311.7))) * 43758.545312)) * v5745)) * (1.0 - v5744)) + (((fract((sin(dot((vec2(0.0,1.0) + vec2(v5740)),vec2(127.1,311.7))) * 43758.545312)) * (1.0 - v5745)) + (fract((sin(dot((vec2(1.0) + vec2(v5740)),vec2(127.1,311.7))) * 43758.545312)) * v5745)) * v5744)) * 2.0) - 1.0) / v5711) * 2.0) + (mod(((atan(position_in_world_space[int(0.0)],position_in_world_space[int(1.0)]) + 3.141593) / (3.141593 * 2.0)),0.008333334) / 0.008333334)),1.0) - 0.5)) * 2.0))))));
				
			}
			
			#endif
			
			ENDGLSL
		}
		
	}
	
}

