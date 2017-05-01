package eyeq.jatakarabbit;

import eyeq.util.client.model.UModelCreator;
import eyeq.util.client.model.UModelLoader;
import eyeq.util.client.model.gson.ItemmodelJsonFactory;
import eyeq.util.client.renderer.ResourceLocationFactory;
import eyeq.util.client.resource.ULanguageCreator;
import eyeq.util.client.resource.lang.LanguageResourceManager;
import eyeq.util.oredict.CategoryTypes;
import eyeq.util.oredict.UOreDictionary;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import eyeq.jatakarabbit.block.BlockMortar;
import eyeq.jatakarabbit.item.ItemMallet;
import eyeq.jatakarabbit.item.ItemMochi;

import java.io.File;

import static eyeq.jatakarabbit.JatakaRabbit.MOD_ID;

@Mod(modid = MOD_ID, version = "1.0", dependencies = "after:eyeq_util")
@Mod.EventBusSubscriber
public class JatakaRabbit {
    public static final String MOD_ID = "eyeq_jatakarabbit";

    @Mod.Instance(MOD_ID)
    public static JatakaRabbit instance;

    private static final ResourceLocationFactory resource = new ResourceLocationFactory(MOD_ID);

    public static Block mortar;

    public static Item mallet;
    public static Item mochi;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        addRecipes();
        if(event.getSide().isServer()) {
            return;
        }
        renderItemModels();
        createFiles();
    }

    @SubscribeEvent
    protected static void registerBlocks(RegistryEvent.Register<Block> event) {
        mortar = new BlockMortar().setHardness(2.0F).setUnlocalizedName("mortar");

        GameRegistry.register(mortar, resource.createResourceLocation("mortar"));
    }

    @SubscribeEvent
    protected static void registerItems(RegistryEvent.Register<Item> event) {
        mallet = new ItemMallet().setUnlocalizedName("mallet");
        mochi = new ItemMochi(2, 1.2F, false).setUnlocalizedName("mochi");

        GameRegistry.register(new ItemBlock(mortar), mortar.getRegistryName());

        GameRegistry.register(mallet, resource.createResourceLocation("mallet"));
        GameRegistry.register(mochi, resource.createResourceLocation("mochi"));

        UOreDictionary.registerOre(CategoryTypes.PREFIX_FOOD_GOLD, "mochi", mochi);
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(mortar), "X X", "X X", "XXX",
                'X', "plankWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(mallet), "X", "Y", "X",
                'X', "plankWood", 'Y', "stickWood"));
    }

    @SideOnly(Side.CLIENT)
    public static void renderItemModels() {
        UModelLoader.setCustomModelResourceLocation(mortar);

        UModelLoader.setCustomModelResourceLocation(mallet);
        UModelLoader.setCustomModelResourceLocation(mochi);
    }

    public static void createFiles() {
        File project = new File("../1.11.2-JatakaRabbit");

        LanguageResourceManager language = new LanguageResourceManager();

        language.register(LanguageResourceManager.EN_US, mortar, "Wooden Mortar");
        language.register(LanguageResourceManager.JA_JP, mortar, "木臼");

        language.register(LanguageResourceManager.EN_US, mallet, "Wodden Mallet");
        language.register(LanguageResourceManager.JA_JP, mallet, "杵");
        language.register(LanguageResourceManager.EN_US, mochi, "Rice Cake");
        language.register(LanguageResourceManager.JA_JP, mochi, "餅");

        ULanguageCreator.createLanguage(project, MOD_ID, language);

        UModelCreator.createItemJson(project, Item.getItemFromBlock(mortar), ItemmodelJsonFactory.ItemmodelParent.GENERATED);

        UModelCreator.createItemJson(project, mallet, ItemmodelJsonFactory.ItemmodelParent.HANDHELD);
        UModelCreator.createItemJson(project, mochi, ItemmodelJsonFactory.ItemmodelParent.GENERATED);
    }
}
