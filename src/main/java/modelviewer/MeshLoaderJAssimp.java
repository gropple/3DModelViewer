package modelviewer;

import jassimp.AiMesh;
import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import jassimp.Jassimp;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIVector3D;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

interface MeshLoader {
    MeshData[] load(URI resourcePath, String texturesDir, int flags) throws IOException;
}

// Loads a mesh (model) with JAssimp lib
public class MeshLoaderJAssimp implements MeshLoader {
    public MeshData[] load(URI resourcePath, String texturesDir, int flags) throws IOException {
        // https://learnopengl.com/#!Model-Loading/Assimp great guide to Assimp

//        URI uri = URI.create(resourcePath);
//        String fullPath = "file://" + resourcePath.getPath();
        String fullPath = resourcePath.getPath().substring(1);

        // Assimp general:
        // Gives 24 vertices for a simple cube, rather than 8.
        // https://sourceforge.net/p/assimp/discussion/817654/thread/026e9640/
        // Basically, because of UV texture co-ords, it needs to duplicate vertices.

        // Hmmm... Issues with Kotlin Assimp
        // 1. Can't import everything e.g. IronMan fails mysteriously
        // 2. Doesn't seem to import well e.g. cube produces 6 quads rather than the requested triangles.
//        AiScene aiScene = new Importer().readFile(resourcePath, flags);

        // But LGJWL Assimp issues:
        // 1. Can't make heads nor tails out of how to parse the materials
        // 2. Also not always clean imports, e.g. weird glitches on Lego.
//        AIScene aiScene2 = aiImportFile(resourcePath.getPath().substring(1), flags);

        // Make sure the libs are preloaded in reverse order so there's no lookup fails
        System.loadLibrary("assimp-vc140-mt");
        System.loadLibrary("jassimp");


        Set<AiPostProcessSteps> steps = new HashSet<AiPostProcessSteps>();
        steps.add(AiPostProcessSteps.TRIANGULATE);
        steps.add(AiPostProcessSteps.JOIN_IDENTICAL_VERTICES);

//        AiScene scene = Jassimp.importFile(fullPath, new HashSet<AiPostProcessSteps>(AiPostProcessSteps.TRIANGULATE, AiPostProcessSteps.JOIN_IDENTICAL_VERTICES));
        AiScene scene = Jassimp.importFile(fullPath, steps);
        scene.getMaterials();

//        if (aiScene == null || aiScene2 == null) {
//            System.out.println("Error loading model " + resourcePath);
//        }

//                MeshData[] meshes = new MeshData[numMeshes];
//        MeshData[] meshes = new MeshData[0];

//        ArrayList<AiMaterial> materials2 = aiScene.getMaterials();
//        materials.forEach(mat -> {
//            mat.
//        });
//        PointerBuffer aiMaterials = aiScene.mMaterials();
//        List<Material> materials = new ArrayList<>();
//        for (int i = 0; i < materials2.size(); i++) {
//            AiMaterial mat = materials2.get(i);
////            processMaterial(aiMaterial, materials, texturesDir);
////            aiMaterial.mProperties().get(AI_MATKEY_COLOR_DIFFUSE);
//
//
////            mat.getShininess();
////            Material m = new Material(mat.getName(), mat.get);
//
//            int x=1;
//        }


//        List<AiMaterial> materials = new ArrayList<>();

        int numMeshes = scene.getNumMeshes();
        List<AiMesh> aiMeshes = scene.getMeshes();
        MeshData[] meshes = new MeshData[numMeshes];
//        Mesh[] meshes = new Mesh[numMeshes];
        for (int i = 0; i < numMeshes; i++) {
            AiMesh aiMesh = aiMeshes.get(i);
            int x=1;
            MeshData mesh = processMesh(aiMesh, scene);
            meshes[i] = mesh;
        }
//        int numMeshes = aiScene.getNumMeshes();
//        ArrayList<AiMesh> aiMeshes = aiScene.getMeshes();
//        for (int i = 0; i < numMeshes; i++) {
//            AiMesh aiMesh = aiMeshes.get(i);
//            int x=1;
//            MeshData mesh = processMesh(aiMesh, materials);
//            meshes[i] = mesh;
//        }


        return meshes;

    }

    private MeshData processMesh(AiMesh aiMesh, AiScene scene) {
        int numVertices = aiMesh.getNumVertices();
        float[] vertices = new float[numVertices * 3];
        for(int vertex = 0; vertex < numVertices; vertex ++) {
            vertices[vertex] = aiMesh.getPositionX(vertex);
            vertices[vertex + 1] = aiMesh.getPositionY(vertex);
            vertices[vertex + 2] = aiMesh.getPositionZ(vertex);
        }

        float[] normals = new float[numVertices * 3];
        for(int normal = 0; normal < numVertices; normal ++) {
            normals[normal] = aiMesh.getPositionX(normal);
            normals[normal + 1] = aiMesh.getPositionY(normal);
            normals[normal + 2] = aiMesh.getPositionZ(normal);
        }

        ArrayList<Integer> indices = new ArrayList<Integer>();
//        float[] normals = new float[numVertices * 3];
        int numFaces = aiMesh.getNumFaces();
        for(int face = 0; face < numFaces; face ++) {
            int numIndicesForFace = aiMesh.getFaceNumIndices(face);
            for(int index = 0; index < numIndicesForFace; index ++) {
                int vertex = aiMesh.getFaceVertex(face, index);
                indices.add(vertex);
            }
        }
        int[] indicesRaw = new int[indices.size()];
        for(int index = 0; index < indices.size(); index ++) {
            indicesRaw[index] = indices.get(index);
        }

        return new MeshData(vertices, normals, indicesRaw, null);
    }
}