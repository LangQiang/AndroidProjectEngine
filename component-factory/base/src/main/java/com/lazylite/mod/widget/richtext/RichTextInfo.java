package com.lazylite.mod.widget.richtext;

import java.io.Serializable;

/**
 * @author DongJr
 * @date 2020/2/20
 */
public class RichTextInfo implements Serializable {

    private static final long serialVersionUID = 8165409311975946179L;

    private String content;
    private String type;
    private String url;
    private Style style;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public static class Style implements Serializable{

        private static final long serialVersionUID = 3540943505860295160L;
        private int marginBottom;
        private int width;
        private int height;

        public int getMarginBottom() {
            return marginBottom;
        }

        public void setMarginBottom(int marginBottom) {
            this.marginBottom = marginBottom;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

}
