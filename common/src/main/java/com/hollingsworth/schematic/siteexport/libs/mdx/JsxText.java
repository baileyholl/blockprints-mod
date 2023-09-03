package com.hollingsworth.schematic.siteexport.libs.mdx;


import com.hollingsworth.schematic.siteexport.libs.micromark.Construct;
import com.hollingsworth.schematic.siteexport.libs.micromark.TokenizeContext;
import com.hollingsworth.schematic.siteexport.libs.micromark.Tokenizer;

final class JsxText {

    public static final Construct INSTANCE = new Construct();

    static {
        INSTANCE.tokenize = JsxText::tokenize;
    }

    private static State tokenize(TokenizeContext context, Tokenizer.Effects effects, State ok, State nok) {
        return FactoryTag.create(
                context,
                effects,
                ok,
                nok,
                true,
                "mdxJsxTextTag",
                "mdxJsxTextTagMarker",
                "mdxJsxTextTagClosingMarker",
                "mdxJsxTextTagSelfClosingMarker",
                "mdxJsxTextTagName",
                "mdxJsxTextTagNamePrimary",
                "mdxJsxTextTagNameMemberMarker",
                "mdxJsxTextTagNameMember",
                "mdxJsxTextTagNamePrefixMarker",
                "mdxJsxTextTagNameLocal",
                "mdxJsxTextTagExpressionAttribute",
                "mdxJsxTextTagExpressionAttributeMarker",
                "mdxJsxTextTagExpressionAttributeValue",
                "mdxJsxTextTagAttribute",
                "mdxJsxTextTagAttributeName",
                "mdxJsxTextTagAttributeNamePrimary",
                "mdxJsxTextTagAttributeNamePrefixMarker",
                "mdxJsxTextTagAttributeNameLocal",
                "mdxJsxTextTagAttributeInitializerMarker",
                "mdxJsxTextTagAttributeValueLiteral",
                "mdxJsxTextTagAttributeValueLiteralMarker",
                "mdxJsxTextTagAttributeValueLiteralValue",
                "mdxJsxTextTagAttributeValueExpression",
                "mdxJsxTextTagAttributeValueExpressionMarker",
                "mdxJsxTextTagAttributeValueExpressionValue");
    }

}
