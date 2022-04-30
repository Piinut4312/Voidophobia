package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import static net.minecraft.client.render.WorldRenderer.DIRECTIONS;

public class AirVuxGeneratorBlockEntity extends AbstractVuxGeneratorBlockEntity {

    public static final int DEFAULT_VUX_CAPACITY = 4800;
    public static final int DEFAULT_VUX_GEN_RATE = 12;
    public static final int DEFAULT_VUX_OUTPUT_RATE = 120;

    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index){
                case 0 -> {
                    return AirVuxGeneratorBlockEntity.this.getVuxStored();
                }
                case 1 -> {
                    return AirVuxGeneratorBlockEntity.this.getVuxGenRate();
                }
                case 2 -> {
                    return AirVuxGeneratorBlockEntity.this.getVuxCapacity();
                }
                case 3 -> {
                    return AirVuxGeneratorBlockEntity.this.getVuxOutputRate();
                }
                default -> {
                    return 0;
                }
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index){
                case 0 -> AirVuxGeneratorBlockEntity.this.setVuxStored(value);
                case 1 -> AirVuxGeneratorBlockEntity.this.setVuxGenRate(value);
                case 2 -> AirVuxGeneratorBlockEntity.this.setVuxCapacity(value);
                case 3 -> AirVuxGeneratorBlockEntity.this.setVuxOutputRate(value);
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public AirVuxGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.AIR_VUX_GENERATOR, pos, state);
        this.setVuxGenRate(DEFAULT_VUX_GEN_RATE);
        this.setVuxCapacity(DEFAULT_VUX_CAPACITY);
        this.setVuxOutputRate(DEFAULT_VUX_OUTPUT_RATE);
    }

    @Override
    public int getVuxOutput() {
        return Math.min(this.getVuxStored(), this.getVuxOutputRate());
    }

    public void generateVux(){
        this.addVux(this.getVuxGenRate());
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, AirVuxGeneratorBlockEntity blockEntity) {
        boolean canGenerate = false;
        for(Direction dir : DIRECTIONS){
            BlockPos testPos = blockPos.add(dir.getVector());
            BlockState testState = world.getBlockState(testPos);
            if(testState.isAir()){
                canGenerate = true;
                break;
            }
        }
        if(canGenerate){
            blockEntity.generateVux();
        }
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, AirVuxGeneratorBlockEntity blockEntity) {
    }

}
