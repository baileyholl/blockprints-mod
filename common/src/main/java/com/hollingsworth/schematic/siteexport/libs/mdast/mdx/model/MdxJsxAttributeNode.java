package com.hollingsworth.schematic.siteexport.libs.mdast.mdx.model;

import com.google.gson.stream.JsonWriter;
import com.hollingsworth.schematic.siteexport.libs.unist.UnistNode;

import java.io.IOException;

/**
 * Potential attributes of {@link MdxJsxElementFields}
 */
public interface MdxJsxAttributeNode extends UnistNode {
    void toJson(JsonWriter writer) throws IOException;
}
