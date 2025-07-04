package org.jh.forum.common.filters;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.web.util.HtmlUtils;

/**
 * Markdown + MathJax + XSS 过滤工具
 * @author SituChengxiang(SK)
 */
public class MarkdownMathJaxHtmlFilter {

    private final Parser parser;
    private final HtmlRenderer renderer;
    private final Safelist safelist;

    public MarkdownMathJaxHtmlFilter() {
        this.parser = Parser.builder().build();
        this.renderer = HtmlRenderer.builder().build();

        // 允许常用HTML和MathJax标签
        this.safelist = Safelist.relaxed()
                .addTags("math", "mi", "mo", "mn", "msup", "msub", "mrow", "span","svg")
                .addAttributes("span", "class", "style")
                .addAttributes("math", "xmlns", "display")
                .addAttributes("img", "src", "alt", "title")
                .addProtocols("a","herf","img", "src", "http", "https", "data","mailto");
    }

    /**
     * 过滤并安全渲染Markdown内容
     */
    public String filterContent(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }
        Node document = parser.parse(markdown);
        String html = renderer.render(document);
        return Jsoup.clean(html, safelist);
    }

    /**
     * 过滤title，去除所有HTML标签
     */
    public String filterTitle(String input) {
        if (input == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(input);
    }

    //TODO: attribute字段的安全性检验
}