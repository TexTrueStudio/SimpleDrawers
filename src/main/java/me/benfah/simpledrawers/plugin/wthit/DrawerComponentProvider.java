package me.benfah.simpledrawers.plugin.wthit;

import mcp.mobius.waila.api.*;
import me.benfah.simpledrawers.api.drawer.BlockAbstractDrawer;
import me.benfah.simpledrawers.api.drawer.blockentity.BlockEntityAbstractDrawer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class DrawerComponentProvider implements IBlockComponentProvider
{

    public static DrawerComponentProvider INSTANCE = new DrawerComponentProvider();

    DrawerComponentProvider()
    {
    }

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config)
    {
        if(accessor.getBlock() instanceof BlockAbstractDrawer)
        {
            return BlockAbstractDrawer.getStack((BlockAbstractDrawer) accessor.getBlock(),
                    accessor.getBlockState().get(BlockAbstractDrawer.BORDER_TYPE));
        }
        return ItemStack.EMPTY;
    }


    @Override
    public void appendBody(List<Text> tooltip, IBlockAccessor accessor, IPluginConfig config)
    {
        BlockEntityAbstractDrawer drawer = (BlockEntityAbstractDrawer) accessor.getBlockEntity();

        RenderableTextComponent[] renderables = (RenderableTextComponent[]) drawer.getItemHolders().stream()
                .map(holder -> getRenderable(holder.generateStack(holder.getAmount()))).toArray(RenderableTextComponent[]::new);
        tooltip.add(new RenderableTextComponent(renderables));

    }

    private static RenderableTextComponent getRenderable(ItemStack stack)
    {
        if(!stack.isEmpty())
        {
            NbtCompound tag = new NbtCompound();
            tag.putString("id", Registry.ITEM.getId(stack.getItem()).toString());
            tag.putInt("count", stack.getCount());
            if(stack.hasTag())
                tag.putString("nbt", stack.getTag().toString());
            return new RenderableTextComponent(new Identifier("item"), tag);
        } else
        {
            NbtCompound spacerTag = new NbtCompound();
            spacerTag.putInt("width", 18);
            return new RenderableTextComponent(new Identifier("spacer"), spacerTag);
        }
    }

}