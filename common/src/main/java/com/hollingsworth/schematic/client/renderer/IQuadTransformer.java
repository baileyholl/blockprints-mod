package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.mixin.VertexFormatAccessor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.Arrays;
import java.util.List;

/**
 * Transformer for {@link BakedQuad baked quads}.
 *
 */
public interface IQuadTransformer {
    int STRIDE = DefaultVertexFormat.BLOCK.getIntegerSize();
    int POSITION = findOffset(DefaultVertexFormat.ELEMENT_POSITION);
    int COLOR = findOffset(DefaultVertexFormat.ELEMENT_COLOR);
    int UV0 = findOffset(DefaultVertexFormat.ELEMENT_UV0);
    int UV1 = findOffset(DefaultVertexFormat.ELEMENT_UV1);
    int UV2 = findOffset(DefaultVertexFormat.ELEMENT_UV2);
    int NORMAL = findOffset(DefaultVertexFormat.ELEMENT_NORMAL);

    void processInPlace(BakedQuad quad);

    default void processInPlace(List<BakedQuad> quads) {
        for (BakedQuad quad : quads)
            processInPlace(quad);
    }

    default BakedQuad process(BakedQuad quad) {
        var copy = copy(quad);
        processInPlace(copy);
        return copy;
    }

    default List<BakedQuad> process(List<BakedQuad> inputs) {
        return inputs.stream().map(IQuadTransformer::copy).peek(this::processInPlace).toList();
    }

    default IQuadTransformer andThen(IQuadTransformer other) {
        return quad -> {
            processInPlace(quad);
            other.processInPlace(quad);
        };
    }

    private static BakedQuad copy(BakedQuad quad) {
        var vertices = quad.getVertices();
        return new BakedQuad(Arrays.copyOf(vertices, vertices.length), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    private static int findOffset(VertexFormatElement element) {
        var index = DefaultVertexFormat.BLOCK.getElements().indexOf(element);
        return index < 0 ? -1 : ((VertexFormatAccessor)DefaultVertexFormat.BLOCK).getOffsets().getInt(index) / 4;
    }
}
