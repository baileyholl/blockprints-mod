package com.hollingsworth.schematic.siteexport.libs.micromark;

import appeng.libs.micromark.commonmark.*;
import appeng.libs.micromark.symbol.Codes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultExtension {
    private DefaultExtension() {
    }

    public static Extension create() {
        var extension = new Extension();

        extension.document = new HashMap<>();
        extension.document.put(Codes.asterisk, List.of(ListConstruct.list));
        extension.document.put(Codes.plusSign, List.of(ListConstruct.list));
        extension.document.put(Codes.dash, List.of(ListConstruct.list));
        extension.document.put(Codes.digit0, List.of(ListConstruct.list));
        extension.document.put(Codes.digit1, List.of(ListConstruct.list));
        extension.document.put(Codes.digit2, List.of(ListConstruct.list));
        extension.document.put(Codes.digit3, List.of(ListConstruct.list));
        extension.document.put(Codes.digit4, List.of(ListConstruct.list));
        extension.document.put(Codes.digit5, List.of(ListConstruct.list));
        extension.document.put(Codes.digit6, List.of(ListConstruct.list));
        extension.document.put(Codes.digit7, List.of(ListConstruct.list));
        extension.document.put(Codes.digit8, List.of(ListConstruct.list));
        extension.document.put(Codes.digit9, List.of(ListConstruct.list));
        extension.document.put(Codes.greaterThan, List.of(BlockQuote.blockQuote));

        extension.contentInitial = Map.of(
                Codes.leftSquareBracket, List.of(Definition.definition));

        extension.flowInitial = Map.of(
                Codes.horizontalTab, List.of(CodeIndented.codeIndented),
                Codes.virtualSpace, List.of(CodeIndented.codeIndented),
                Codes.space, List.of(CodeIndented.codeIndented));

        extension.flow = Map.of(
                Codes.numberSign, List.of(HeadingAtx.headingAtx),
                Codes.asterisk, List.of(ThematicBreak.thematicBreak),
                Codes.dash, List.of(SetextUnderline.setextUnderline, ThematicBreak.thematicBreak),
                Codes.lessThan, List.of(HtmlFlow.htmlFlow),
                Codes.equalsTo, List.of(SetextUnderline.setextUnderline),
                Codes.underscore, List.of(ThematicBreak.thematicBreak),
                Codes.graveAccent, List.of(CodeFenced.codeFenced),
                Codes.tilde, List.of(CodeFenced.codeFenced));

        extension.string = Map.of(
                Codes.ampersand, List.of(CharacterReference.characterReference),
                Codes.backslash, List.of(CharacterEscape.characterEscape));

        extension.text = new HashMap<>();
        extension.text.put(Codes.carriageReturn, List.of(appeng.libs.micromark.commonmark.LineEnding.lineEnding));
        extension.text.put(Codes.lineFeed, List.of(appeng.libs.micromark.commonmark.LineEnding.lineEnding));
        extension.text.put(Codes.carriageReturnLineFeed,
                List.of(appeng.libs.micromark.commonmark.LineEnding.lineEnding));
        extension.text.put(Codes.exclamationMark, List.of(LabelStartImage.labelStartImage));
        extension.text.put(Codes.ampersand, List.of(CharacterReference.characterReference));
        extension.text.put(Codes.asterisk, List.of(Attention.attention));
        extension.text.put(Codes.lessThan, List.of(AutoLink.autolink, HtmlText.htmlText));
        extension.text.put(Codes.leftSquareBracket, List.of(LabelStartLink.labelStartLink));
        extension.text.put(Codes.backslash, List.of(HardBreakEscape.hardBreakEscape, CharacterEscape.characterEscape));
        extension.text.put(Codes.rightSquareBracket, List.of(LabelEnd.labelEnd));
        extension.text.put(Codes.underscore, List.of(Attention.attention));
        extension.text.put(Codes.graveAccent, List.of(CodeText.codeText));

        extension.nullInsideSpan = List.of(Attention.attention.resolveAll, InitializeText.resolver);

        extension.nullAttentionMarkers = List.of(Codes.asterisk, Codes.underscore);

        extension.nullDisable = List.of();

        return extension;
    }

}
