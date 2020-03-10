package me.benfah.simpledrawers.block.entity.renderer;

import java.util.function.Consumer;

import com.mojang.blaze3d.systems.RenderSystem;

import me.benfah.simpledrawers.block.entity.BlockEntityBasicDrawer;
import me.benfah.simpledrawers.init.SDBlockEntities;
import me.benfah.simpledrawers.init.SDItems;
import net.fabricmc.loom.util.MinecraftVersionInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.SignBlockEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelRotation;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.world.LightType;

public class BlockEntityBasicDrawerRenderer extends BlockEntityRenderer<BlockEntityBasicDrawer>
{

	public BlockEntityBasicDrawerRenderer(BlockEntityRenderDispatcher dispatcher)
	{
		super(dispatcher);
	}
	
	
	
	
	public void transformToFace(MatrixStack stack, Direction d)
	{
		stack.translate(.5f, .5f, .5f);
		stack.multiply(d.getRotationQuaternion());
		stack.multiply(new Quaternion(Vector3f.POSITIVE_X, 90, true));
		stack.translate(-.5f, -.5f, -.5f);
	}
	
	// render method gives a light argument with 0, so i have to get it somewhere else
	private int calcLight(BlockEntityBasicDrawer blockEntity)
	{
		Direction d = blockEntity.getCachedState().get(Properties.FACING);
		BlockPos pos = blockEntity.getPos().add(d.getVector());
		int skyLight = blockEntity.getWorld().getLightLevel(LightType.SKY, pos);
		int blockLight = blockEntity.getWorld().getLightLevel(LightType.BLOCK, pos);
		
		return skyLight << 20 | blockLight << 4;
	}
	
	@Override
	public void render(BlockEntityBasicDrawer blockEntity, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, int overlay)
	{
		light = calcLight(blockEntity);
		Direction d = blockEntity.getCachedState().get(Properties.FACING);
		
		if(blockEntity.getHolder() != null && !blockEntity.getHolder().isEmpty())
		{
			drawCenteredText(11, blockEntity.getHolder().getDisplayAmount(), matrices, vertexConsumers, d);
			drawItem(6, new ItemStack(blockEntity.getHolder().getItemType()), matrices, vertexConsumers, light, overlay, d);
			
			matrices.pop();
			RenderSystem.setupLevelDiffuseLighting(matrices.peek().getModel());
			matrices.push();
		}
		
		
		
	}
	
	public void drawItem(double y, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing)
	{
		matrices.push();
//		matrices.translate(0, 1, 0);
		transformToFace(matrices, facing);
		transformToCenteredPosition(y, matrices);
		matrices.translate(0, 0, -0.01);
		matrices.multiply(new Quaternion(Vector3f.NEGATIVE_Z, 180, true));
		matrices.multiply(new Quaternion(Vector3f.NEGATIVE_Y, 180, true));
		matrices.scale(0.4f, 0.4f, 0.0001f);
		if(vertexConsumers instanceof VertexConsumerProvider.Immediate)
		((VertexConsumerProvider.Immediate)vertexConsumers).draw();
		
		BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(stack);
		if(model.hasDepth())
		RenderSystem.setupGui3DDiffuseLighting();
		else
		RenderSystem.setupGuiFlatDiffuseLighting();
        matrices.peek().getNormal().load(Matrix3f.scale(1, -1, 1));
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, Mode.GUI, light, overlay, matrices, vertexConsumers);
		if(vertexConsumers instanceof VertexConsumerProvider.Immediate)
		((VertexConsumerProvider.Immediate)vertexConsumers).draw();
		matrices.pop();
	}
	
	public void drawCenteredText(double y, String s, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Direction facing)
	{
		matrices.push();
		
		double translateZ = 1d;
		
		
		transformToFace(matrices, facing);
		transformToCenteredPosition(y, matrices);
		matrices.scale(0.01f, 0.01f, 0.01f);
		int width = dispatcher.getTextRenderer().getStringWidth(s);
		dispatcher.getTextRenderer().draw(s, -width / 2, 3, 0, false, matrices.peek().getModel(), vertexConsumers, false, 0, 15728880);
		matrices.pop();
	}
	
	public void transformToPosition(double x, double y, MatrixStack matrices)
	{
		matrices.translate(x / 16d, y / 16d, 1d / 16d);
	}
	
	public void transformToCenteredPosition(double y, MatrixStack matrices)
	{
		transformToPosition(8d, y, matrices);
	}
	
}