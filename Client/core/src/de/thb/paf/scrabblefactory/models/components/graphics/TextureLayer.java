package de.thb.paf.scrabblefactory.models.components.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a basic texture layer  which can be used by the LayeredTexturesGraphicsComponent.
 * 
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 * @see LayeredTexturesGraphicsComponent
 */
public class TextureLayer {

    /**
     * The texture to render
     */
    public Sprite texture;

    /**
     * The texture's file name
     */
    @SerializedName("textureName")
    public final String textureName;

    /**
     * The texture's alignment
     */
    @SerializedName("alignment")
    public final Alignment alignment;

    /**
     * The layer's margins
     */
    @SerializedName("margin")
    public final int[] margin;

    /**
     * The z-index representing the order in the layer-stack
     */
    @SerializedName("zIndex")
    public final int zIndex;

    /**
     * Constructor
     * @param texture The texture to render
     * @param zIndex The texture's z-order on the render stack
     */
    public TextureLayer(Sprite texture, int zIndex) {
        this.texture = texture;
        this.textureName = "";
        this.margin = null;
        this.alignment = null;
        this.zIndex = zIndex;
    }
}