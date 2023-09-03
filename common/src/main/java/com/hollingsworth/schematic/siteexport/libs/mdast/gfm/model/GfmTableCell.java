package com.hollingsworth.schematic.siteexport.libs.mdast.gfm.model;


import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstAnyContent;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstParent;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstPhrasingContent;

public class GfmTableCell extends MdAstParent<MdAstPhrasingContent> implements MdAstAnyContent {
    public GfmTableCell() {
        super("tableCell");
    }

    @Override
    protected Class<MdAstPhrasingContent> childClass() {
        return MdAstPhrasingContent.class;
    }
}
