package net.piinut.voidophobia.world.feture;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.piinut.voidophobia.world.config.GodelCrystalPileFeatureConfig;


public class GodelCrystalPileFeature extends Feature<GodelCrystalPileFeatureConfig> {

    public GodelCrystalPileFeature(Codec<GodelCrystalPileFeatureConfig> configCodec) {
        super(configCodec);
    }

    private static boolean canGenerate(BlockState state){
        return !(state.isAir() || state.isOf(Blocks.WATER) || state.isOf(Blocks.LAVA));
    }

    private static boolean canReplace(BlockState state){
        return state.isAir() || state.isOf(Blocks.WATER);
    }

    @Override
    public boolean generate(FeatureContext<GodelCrystalPileFeatureConfig> context) {
        BlockPos blockPos = context.getOrigin();
        StructureWorldAccess structureWorldAccess = context.getWorld();

        for(int l = 0; l < 10; l++){
            if(!structureWorldAccess.testBlockState(blockPos.down(l), GodelCrystalPileFeature::canGenerate)){
                continue;
            }

            boolean test = true;

            for(int k = 1; k < context.getConfig().height().getMax(); k++){
                BlockPos pos = context.getOrigin().up(k).down(l);
                if(!structureWorldAccess.testBlockState(pos, GodelCrystalPileFeature::canReplace)){
                    test = false;
                    break;
                }
            }

            if(!test) return false;

            for(int i = -1; i < 2; i++){
                for(int j = -1; j < 2; j++){
                    int extra = (1+i*j)*(1-i*j)*2;
                    for(int k = 0; k < context.getConfig().height().get(context.getRandom())+extra; k++){
                        BlockPos pos = context.getOrigin().add(i, k-l, j);
                        BlockState state = context.getConfig().block().getBlockState(context.getRandom(), pos);
                        context.getWorld().setBlockState(pos, state, 3);
                    }
                }
            }
            return true;
        }

        return false;
    }

}
