package de.thb.paf.scrabblefactory.factories;

import com.badlogic.gdx.math.Vector2;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import de.thb.paf.scrabblefactory.io.AssetLoader;
import de.thb.paf.scrabblefactory.models.assets.AssetTargetType;
import de.thb.paf.scrabblefactory.models.components.IComponent;
import de.thb.paf.scrabblefactory.models.components.graphics.IGraphicsComponent;
import de.thb.paf.scrabblefactory.models.hud.HUDComponent;
import de.thb.paf.scrabblefactory.models.hud.HUDSystem;
import de.thb.paf.scrabblefactory.models.hud.HUDSystemType;
import de.thb.paf.scrabblefactory.models.hud.HealthHUD;
import de.thb.paf.scrabblefactory.models.hud.IHUDComponent;
import de.thb.paf.scrabblefactory.models.hud.SearchWordHUD;
import de.thb.paf.scrabblefactory.models.hud.TimerHUD;
import de.thb.paf.scrabblefactory.utils.ScrabbleFactoryClassLoader;
import de.thb.paf.scrabblefactory.utils.graphics.AlignmentHelper;

import static de.thb.paf.scrabblefactory.settings.Constants.Json.JSON_KEY_COMPONENTS;
import static de.thb.paf.scrabblefactory.settings.Constants.Json.JSON_KEY_JAVA_PACKAGE;
import static de.thb.paf.scrabblefactory.settings.Constants.Json.JSON_KEY_NAME;
import static de.thb.paf.scrabblefactory.settings.Constants.Json.JSON_KEY_TYPE;
import static de.thb.paf.scrabblefactory.settings.Settings.Game.PPM;
import static de.thb.paf.scrabblefactory.settings.Settings.Game.VIRTUAL_HEIGHT;
import static de.thb.paf.scrabblefactory.settings.Settings.Game.VIRTUAL_WIDTH;

/**
 * Factory class dedicated to create and assemble HUD systems.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class HUDSystemFactory {

    /**
     * The asset loader instance to load required files from the games asset's directory
     */
    private AssetLoader assetLoader;

    /**
     * Get HUD system instance defined by it's unique type.
     * @param hudSystemType The HUD system type
     * @return The requested HUD system instance
     */
    public HUDSystem getHUDSystem(HUDSystemType hudSystemType) {
        if(this.assetLoader == null) {
            this.assetLoader = new AssetLoader();
        }

        ActionFactory actionFactory = new ActionFactory();
        HUDSystem hudSystem = new HUDSystem(hudSystemType);
        JsonObject hudSystemConfig = this.assetLoader.loadInitConfiguration(AssetTargetType.HUD, hudSystemType.id);

        JsonArray hudComponentConfigs = hudSystemConfig.get(JSON_KEY_COMPONENTS).getAsJsonArray();
        for(JsonElement hudComponentConfig : hudComponentConfigs) {
            String hudComponentType = hudComponentConfig.getAsJsonObject().get(JSON_KEY_TYPE).getAsString();
            IHUDComponent hudComponent = this.initHUDComponent(hudComponentType, hudComponentConfig.getAsJsonObject());

            if(hudComponent != null) {
                hudComponent.setHUDSystem(hudSystem);
                actionFactory.registerToEvents(hudComponent);
                this.initAssociatedComponents(hudComponent, hudComponentConfig.getAsJsonObject());
                hudSystem.addHUDComponent(hudComponent);
            }
        }

        return hudSystem;
    }

    /**
     * Initializes a HUD component given by it's type and JSON configuration.
     * @param hudComponentType The HUD component type
     * @param hudComponentConfig the HUD component's JSON config
     * @return The requested HUD component instance
     */
    private IHUDComponent initHUDComponent(String hudComponentType, JsonObject hudComponentConfig) {

        IHUDComponent hudComponent;
        switch(hudComponentType) {
            case "health":
                hudComponent = this.initHealthHUDComponent(hudComponentConfig.getAsJsonObject());
                break;
            case "timer":
                hudComponent = this.initTimerHUDComponent(hudComponentConfig.getAsJsonObject());
                break;
            case "searchWord":
                hudComponent = this.initSearchWordHUDComponent(hudComponentConfig.getAsJsonObject());
                break;
            default:
                hudComponent = null;
                // we do nothing here
                break;
        }

        return hudComponent;
    }

    /**
     * Initializes a health HUD Component.
     * @param hudComponentConfig The HUD components JSON configuration
     * @return The requested health HUD component instance
     */
    private IHUDComponent initHealthHUDComponent(JsonObject hudComponentConfig) {
        Gson gson = new GsonBuilder().create();
        HealthHUD hudComponent = gson.fromJson(hudComponentConfig, HealthHUD.class);

        hudComponent.setPosition(this.getRelativePosition(hudComponent));
        return hudComponent;
    }

    /**
     * Initializes a timer HUD Component.
     * @param hudComponentConfig The HUD components JSON configuration
     * @return The requested timer HUD component instance
     */
    private IHUDComponent initTimerHUDComponent(JsonObject hudComponentConfig) {
        Gson gson = new GsonBuilder().create();
        TimerHUD hudComponent = gson.fromJson(hudComponentConfig, TimerHUD.class);

        hudComponent.setPosition(this.getRelativePosition(hudComponent));
        return hudComponent;
    }

    /**
     * Initializes a search word HUD Component.
     * @param hudComponentConfig The HUD components JSON configuration
     * @return The requested search word HUD component instance
     */
    private IHUDComponent initSearchWordHUDComponent(JsonObject hudComponentConfig) {
        Gson gson = new GsonBuilder().create();
        SearchWordHUD hudComponent = gson.fromJson(hudComponentConfig, SearchWordHUD.class);

        hudComponent.setPosition(this.getRelativePosition(hudComponent));
        return hudComponent;
    }

    /**
     * Calculate the HUD component's relative on screen position as a function on it's defined
     * alignment and margin options.
     * @param hudComponent The HUD component to calculate the relate position for
     * @return The relative on screen position
     */
    private Vector2 getRelativePosition(HUDComponent hudComponent) {
        Vector2 position = AlignmentHelper.getRelativePosition(
                new Vector2 (
                        hudComponent.getSize().x / PPM,
                        hudComponent.getSize().y / PPM
                ),
                new Vector2(VIRTUAL_WIDTH, VIRTUAL_HEIGHT),
                hudComponent.alignment,
                hudComponent.margin
        );

        return position;
    }

    /**
     * Initializes all components associated with the HUD component.
     * @param hudComponent The HUD component to init all components for
     * @param hudComponentConfig The HUD component's JSON config.
     */
    private void initAssociatedComponents(IHUDComponent hudComponent, JsonObject hudComponentConfig) {
        JsonArray associatedComponentConfigs = hudComponentConfig.getAsJsonObject()
                .get(JSON_KEY_COMPONENTS).getAsJsonArray();

        for(JsonElement componentConfig: associatedComponentConfigs) {
            String componentName = componentConfig.getAsJsonObject().get(JSON_KEY_NAME).getAsString();
            String javaPackageName = componentConfig.getAsJsonObject().get(JSON_KEY_JAVA_PACKAGE).getAsString();
            Class<?> componentType = ScrabbleFactoryClassLoader.getClassForName(javaPackageName, componentName);

            IComponent component = null;
            if(IGraphicsComponent.class.isAssignableFrom(componentType)) {
                component = new GraphicsComponentFactory(this.assetLoader)
                        .getGfxComponent(componentType, componentConfig.getAsJsonObject(), hudComponent);
            }

            if(component != null) {
                hudComponent.addComponent(component);
            }
        }
    }
}
