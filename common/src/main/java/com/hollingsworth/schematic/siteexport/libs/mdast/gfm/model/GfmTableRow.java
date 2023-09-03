package com.hollingsworth.schematic.siteexport.libs.mdast.gfm.model;


import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstAnyContent;
import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstParent;

public class GfmTableRow extends MdAstParent<GfmTableCell> implements MdAstAnyContent {
    public GfmTableRow() {
        super("tableRow");
    }

    @Override
    protected Class<GfmTableCell> childClass() {
        return GfmTableCell.class;
    }
}
