package de.thb.paf.scrabblefactory.utils.graphics.widgets;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.thb.paf.scrabblefactory.io.AssetLoader;
import de.thb.paf.scrabblefactory.models.assets.FontAsset;
import de.thb.paf.scrabblefactory.models.components.graphics.Alignment;
import de.thb.paf.scrabblefactory.settings.Settings;
import de.thb.paf.scrabblefactory.utils.graphics.AlignmentHelper;

/**
 * A basic builder for creating several UI widgets.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class UIWidgetBuilder {

    /**
     * The ui element's style definition
     */
    private Skin uiSkin;

    /**
     * The widget to create
     */
    private Actor widget;

    /**
     * The associated widget type
     */
    private UIWidgetType widgetType;

    /**
     * The widget's title text
     */
    private String title;

    /**
     * The widget's on-screen alignment
     */
    private Alignment alignment;

    /**
     * The widget's on-screen margins
     */
    private int[] margins;

    /**
     * The widget's size: width and height
     */
    private int[] size;

    /**
     * The widget's custom font
     */
    private BitmapFont font;

    /**
     * The widget's on-click listener
     */
    private ClickListener clickListener;

    /**
     * A text input widget's text listener
     */
    private TextField.TextFieldListener textFieldListener;

    /**
     * Constructor
     * @param widgetType The widget's type to create
     */
    public UIWidgetBuilder(UIWidgetType widgetType) {
        this.widgetType = widgetType;

        this.alignment = Alignment.TOP_LEFT;
        this.margins = new int[] {0, 0, 0, 0};
        this.size = null;
        this.font = null;
        this.clickListener = null;
        this.textFieldListener = null;

        this.widget = null;
        this.uiSkin = new Skin(Gdx.files.internal("ui/glassy-ui.json"));
    }

    /**
     * Set the widget's title.
     * @param title The widget's title.
     * @return The current builder instance
     */
    public UIWidgetBuilder title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Set the widget's on-screen alignment.
     * @param alignment The widget's on-screen alignment
     * @return The current builder instance
     */
    public UIWidgetBuilder alignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Set the widget's on-screen margins.
     * @param margins The widget's on-screen margins
     * @return The current builder instance
     */
    public UIWidgetBuilder margins(int... margins) {
        this.margins = margins;
        return this;
    }

    /**
     * Set the widget's size: width and height.
     * @param sizes The widget's size: width and height
     * @return The current builder instance
     */
    public UIWidgetBuilder size(int... sizes) {
        this.size = sizes;
        return this;
    }

    /**
     * Set the the widget's custom font.
     * @param font The font to apply
     * @param fontSize The font's size to apply
     * @param textColor The font's color to apply
     * @return The current builder instance
     */
    public UIWidgetBuilder font(FontAsset font, int fontSize, Color textColor) {
        this.font = new AssetLoader().loadFont(
                font,
                fontSize,
                0,
                textColor,
                Color.BLACK
        );
        return this;
    }

    /**
     * Set The text input widget's text listener.
     * @param textFieldListener The text input widget's text listener to apply
     * @return The current builder instance
     */
    public UIWidgetBuilder textFieldListener(TextField.TextFieldListener textFieldListener) {
        this.textFieldListener = textFieldListener;
        return this;
    }

    /**
     * Set the widget's on-click listener.
     * @param clickListener The widget's on-click listener to apply
     * @return The current builder instance
     */
    public UIWidgetBuilder clickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
        return this;
    }

    /**
     * Create the requested UI widget with all applied configurations.
     * @return The requested UI widget
     */
    public Actor create() {
        this.applyStyles();

        switch(this.widgetType) {
            case TEXT_BUTTON:
                this.widget = new TextButton(this.title, this.uiSkin);
                break;
            case TEXT_FIELD:
                this.widget = new TextField(this.title, this.uiSkin);
                break;
            case TEXT_LABEL:
                this.widget = new Label(this.title, this.uiSkin);
                break;
        }

        this.applyBoundsAndPosition();
        this.applyListeners();

        return this.widget;
    }

    /**
     * Apply all custom style configurations.
     */
    private void applyStyles() {
        if(this.font != null) {
            switch(this.widgetType) {
                case TEXT_FIELD:
                    TextField.TextFieldStyle textFieldStyle = this.uiSkin.get(TextField.TextFieldStyle.class);
                    textFieldStyle.font = this.font;
                    break;
                case TEXT_LABEL:
                    Label.LabelStyle labelStyle = this.uiSkin.get(Label.LabelStyle.class);
                    labelStyle.font = this.font;
                    break;
            }
        }
    }

    /**
     * Apply all position and size configurations.
     */
    private void applyBoundsAndPosition() {
        if(this.size != null) {
            switch(this.size.length) {
                case 2:
                    this.widget.setHeight(this.size[1]);
                case 1:
                    this.widget.setWidth(this.size[0]);
                    break;
            }
        }

        Vector2 screenPosition = AlignmentHelper.getRelativePosition(
                new Vector2(this.widget.getWidth(), this.widget.getHeight()),
                new Vector2(Settings.App.DEVICE_SCREEN_WIDTH, Settings.App.DEVICE_SCREEN_HEIGHT),
                alignment,
                new int[] {0, 0, 0 ,0}
        );

        // apply margins
        screenPosition.x += this.margins[3];
        screenPosition.x -= this.margins[1];
        screenPosition.y -= this.margins[0];
        screenPosition.y += this.margins[2];

        this.widget.setPosition(screenPosition.x, screenPosition.y);
    }

    /**
     * Associate and apply all configured action and event listeners.
     */
    private void applyListeners() {
        if(this.widgetType == UIWidgetType.TEXT_FIELD && this.textFieldListener !=  null) {
            ((TextField)this.widget).setTextFieldListener(this.textFieldListener);
        }

        if(this.clickListener != null) {
            this.widget.addListener(this.clickListener);
        }
    }

}