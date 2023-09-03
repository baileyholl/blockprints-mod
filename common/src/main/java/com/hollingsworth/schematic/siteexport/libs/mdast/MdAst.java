package com.hollingsworth.schematic.siteexport.libs.mdast;


import com.hollingsworth.schematic.siteexport.libs.mdast.model.MdAstRoot;
import com.hollingsworth.schematic.siteexport.libs.micromark.Micromark;

public final class MdAst {
    private MdAst() {
    }

    public static MdAstRoot fromMarkdown(String markdown, MdastOptions options) {
        var evts = Micromark.parseAndPostprocess(markdown, options);
        return new MdastCompiler(options).compile(evts);
    }
}
