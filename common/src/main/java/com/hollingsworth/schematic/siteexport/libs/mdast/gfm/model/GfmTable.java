package com.hollingsworth.schematic.siteexport.libs.mdast.gfm.model;


import com.google.gson.stream.JsonWriter;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstFlowContent;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstParent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

public class GfmTable extends MdAstParent<GfmTableRow> implements MdAstFlowContent {
    @Nullable
    public List<Align> align = null;

    public GfmTable() {
        super("table");
    }

    @Override
    protected Class<GfmTableRow> childClass() {
        return GfmTableRow.class;
    }

    @Override
    protected void writeJson(JsonWriter writer) throws IOException {
        if (align != null) {
            writer.name("align").beginArray();
            for (var value : align) {
                switch (value) {
                    case LEFT -> writer.value("left");
                    case CENTER -> writer.value("center");
                    case RIGHT -> writer.value("right");
                    case NONE -> writer.nullValue();
                }
            }
            writer.endArray();
        }

        super.writeJson(writer);
    }
}
