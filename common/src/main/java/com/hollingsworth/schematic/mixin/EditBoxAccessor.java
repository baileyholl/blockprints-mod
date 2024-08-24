package com.hollingsworth.schematic.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiFunction;

@Mixin(EditBox.class)
public interface EditBoxAccessor {

    @Accessor
    Font getFont();

    @Accessor
    String getValue();

    @Accessor
    long getFocusedTime();

    @Accessor
    boolean isBordered();

    @Accessor
    boolean isCanLoseFocus();

    @Accessor
    boolean isIsEditable();

    @Accessor
    int getDisplayPos();

    @Accessor
    int getCursorPos();

    @Accessor
    int getHighlightPos();

    @Accessor
    int getTextColor();

    @Accessor
    int getTextColorUneditable();

    @Accessor
    String getSuggestion();


    @Accessor
    BiFunction<String, Integer, FormattedCharSequence> getFormatter();

}
