package com.hollingsworth.schematic.client.gui;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public interface NestedRenderables {

    List<AbstractWidget> addBeforeParent();

    default void addAfterParent(List<AbstractWidget> widgets){

    }


}
