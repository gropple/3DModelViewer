package BasicLighting;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;

import java.util.ArrayList;

class World {
    private final ArrayList<Cube> models = new ArrayList<>();

    World() {
        Shader lightingShader = new Shader("../shaders/basic_lighting_vertex.glsl", "../shaders/basic_lighting_fragment.glsl");
        Shader lampShader = new Shader("../shaders/basic_lighting_vertex.glsl", "../shaders/basic_lighting_lamp_fragment.glsl");

        lightingShader.setVec3("objectColour", new Vector3(1.0f, 0.5f, 0.31f));
        lightingShader.setVec3("lightColour",  new Vector3(1.0f, 1.0f, 1.0f));

        // Scale, translate, then rotate.
        {
            // The cube
            Matrix4x4 transform = Matrix4x4.identity();
            models.add(new Cube(transform, lightingShader));
        }

        {
            // The lamp cube
            Matrix4x4 transform = Matrix4x4.translate(0.3f, 0, 0).$times(Matrix4x4.scale(-0.5f));
            models.add(new Cube(transform, lampShader));
        }
    }

    public void draw(Matrix4x4 projectionMatrix, Matrix4x4 cameraTranslate) {
        models.forEach(model -> model.draw(projectionMatrix, cameraTranslate));
    }

}
