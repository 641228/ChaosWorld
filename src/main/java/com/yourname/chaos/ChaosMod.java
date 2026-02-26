package com.yourname.chaos;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("chaos")
public class ChaosMod {
    public static final String MOD_ID = "chaos";

    // 方块注册
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);

    // 混乱石 —— 硬度和石头完全一样
    public static final RegistryObject<Block> CHAOS_STONE = BLOCKS.register("chaos_stone",
        () -> new Block(BlockBehaviour.Properties.of(Material.STONE)
            .strength(1.5F, 6.0F) // 和石头相同硬度
            .requiresCorrectToolForDrops()
        )
    );

    // 维度
    public static final ResourceKey<Level> CHAOS_DIM = ResourceKey.create(
        Registry.DIMENSION_REGISTRY,
        new ResourceLocation(MOD_ID, "chaos")
    );

    public ChaosMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    // ————————————————————————————————
    // 主世界生成混乱石（概率 = 煤矿）
    // ————————————————————————————————
    public static class OreGen {
        public static void registerOreGen() {
            // 只会在石头里生成
            OreConfiguration config = new OreConfiguration(
                new BlockMatchTest(Blocks.STONE),
                CHAOS_STONE.get().defaultBlockState(),
                17 // 煤矿矿石脉大小
            );

            // 放置规则：完全 = 煤矿
            PlacedFeature placed = Feature.ORE.configured(config)
                .placed(
                    CountPlacement.of(15),
                    HeightRangePlacement.uniform(
                        VerticalAnchor.bottom(),
                        VerticalAnchor.absolute(128)
                    ),
                    InSquarePlacement.spread(),
                    BiomeFilter.biome()
                );
        }
    }

    // ————————————————————————————————
    // 混乱维度怪物：主世界 + 地狱 + 末地
    // ————————————————————————————————
    public static MobSpawnSettings.Builder chaosMobs() {
        MobSpawnSettings.Builder builder = new MobSpawnSettings.Builder();

        // 主世界
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ZOMBIE, 100, 2, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 90, 2, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.CREEPER, 80, 1, 3));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.DROWNED, 40, 1, 4));

        // 地狱
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 60, 1, 2));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 50, 1, 2));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 40, 2, 4));

        // 末地
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 100, 1, 4));
        builder.addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(EntityType.SHULKER, 15, 1, 1));

        return builder;
    }
}
