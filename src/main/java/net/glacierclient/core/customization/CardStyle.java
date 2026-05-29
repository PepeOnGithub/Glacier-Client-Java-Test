package net.glacierclient.core.customization;

/**
 * Per-module card appearance. Fully customizable: background colour, secondary (gradient) colour,
 * accent colour, render style and corner radius. Colours are ARGB so the alpha channel doubles as
 * opacity.
 */
public class CardStyle {

    public enum Style { SOLID, GRADIENT, GLASS, OUTLINE }

    public int bgColor     = 0xFF202327;
    public int bgColor2    = 0xFF2A2E33;
    public int accentColor = 0xFF7289DA;
    public Style style     = Style.SOLID;
    public int radius      = 8;

    public CardStyle copy() {
        CardStyle c = new CardStyle();
        c.bgColor = bgColor;
        c.bgColor2 = bgColor2;
        c.accentColor = accentColor;
        c.style = style;
        c.radius = radius;
        return c;
    }
}
