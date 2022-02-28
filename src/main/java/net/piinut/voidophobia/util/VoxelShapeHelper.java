package net.piinut.voidophobia.util;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;

public class VoxelShapeHelper {

    public static Box rotateX(Box box){
        Vec3d pMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d pMax = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d c = new Vec3d(0, 0.5, 0.5);
        Vec3d diffMin = c.subtract(pMin);
        Vec3d diffMax = c.subtract(pMax);
        Vec3d newMin = c.add(-diffMin.x, diffMin.z, -diffMin.y);
        Vec3d newMax = c.add(-diffMax.x, diffMax.z, -diffMax.y);
        return new Box(newMin, newMax);
    }

    public static Box rotateY(Box box){
        Vec3d pMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d pMax = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d c = new Vec3d(0.5, 0, 0.5);
        Vec3d diffMin = c.subtract(pMin);
        Vec3d diffMax = c.subtract(pMax);
        Vec3d newMin = c.add(diffMin.z, -diffMin.y, -diffMin.x);
        Vec3d newMax = c.add(diffMax.z, -diffMax.y, -diffMax.x);
        return new Box(newMin, newMax);
    }

    public static VoxelShape rotateX(VoxelShape voxelShape){
        List<Box> boxes = voxelShape.getBoundingBoxes();
        VoxelShape voxelShape1 = VoxelShapes.empty();
        for(Box box : boxes){
            voxelShape1 = VoxelShapes.union(voxelShape1, VoxelShapes.cuboid(rotateX(box)));
        }
        return voxelShape1;
    }

    public static VoxelShape rotateY(VoxelShape voxelShape){
        List<Box> boxes = voxelShape.getBoundingBoxes();
        VoxelShape voxelShape1 = VoxelShapes.empty();
        for(Box box : boxes){
            voxelShape1 = VoxelShapes.union(voxelShape1, VoxelShapes.cuboid(rotateY(box)));
        }
        return voxelShape1;
    }

}
