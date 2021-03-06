#version 330 core

struct Light {
    vec3 position;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct Material {
    sampler2D texture;
    sampler2D diffuse;
    sampler2D specular;
    // vec3 ambient;
    // vec3 diffuse;
    //vec3 specular;
    float shininess;
};

uniform Light light;
uniform Material material;
uniform vec3 viewPos;
uniform float shininess;

uniform sampler2D tex;

out vec4 FragColor;

in vec2 TexCoords;
in vec3 Normal;
in vec3 FragPos;

void main(void) {
    //vec3 ambient = light.ambient  * material.ambient;
    vec3 ambient = light.ambient * vec3(texture(material.diffuse, TexCoords));

    vec3 norm = normalize(Normal);
    vec3 lightDir = normalize(light.position - FragPos);

    float diff = max(dot(norm, lightDir), 0.0);
    //vec3 diffuse = diff * light.diffuse * material.diffuse;
    vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, TexCoords));

    vec3 viewDir = normalize(viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
    vec3 specular = spec * light.specular * vec3(texture(material.specular, TexCoords));;

    vec3 result = (ambient + diffuse + specular);

    FragColor = vec4(result, 1.0);
    //FragColor = texture(material.texture, TexCoords);
}