package de.thb.paf.scrabblefactory.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.Date;

import de.thb.paf.scrabblefactory.auth.AuthenticationManager;
import de.thb.paf.scrabblefactory.gameplay.timer.CountdownTimer;
import de.thb.paf.scrabblefactory.gameplay.timer.ICountdownListener;
import de.thb.paf.scrabblefactory.managers.GameScreenManager;
import de.thb.paf.scrabblefactory.models.assets.FontAsset;
import de.thb.paf.scrabblefactory.models.components.graphics.Alignment;
import de.thb.paf.scrabblefactory.persistence.DataStore;
import de.thb.paf.scrabblefactory.persistence.entities.Score;
import de.thb.paf.scrabblefactory.persistence.entities.UserScore;
import de.thb.paf.scrabblefactory.settings.Settings;
import de.thb.paf.scrabblefactory.utils.graphics.AlignmentHelper;
import de.thb.paf.scrabblefactory.utils.graphics.widgets.UIWidgetBuilder;
import de.thb.paf.scrabblefactory.utils.graphics.widgets.UIWidgetType;

import static de.thb.paf.scrabblefactory.settings.Settings.App.DEVICE_SCREEN_HEIGHT;
import static de.thb.paf.scrabblefactory.settings.Settings.App.DEVICE_SCREEN_WIDTH;

/**
 * Represents the game dialog screen showing the earned score
 * from a mastered scrabble challenge.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class ChallengeScoreDialogScreen extends GameScreen implements ICountdownListener {

    /**
     * The dialog's static background image
     */
    private Image dialogBackground;

    /**
     * The label displaying the earned score
     */
    private volatile Label scoreLabel;

    /**
     * The earned score
     */
    private volatile int score;

    /**
     * The current counted score
     */
    private volatile int countedScore;

    /**
     * The score count timer
     */
    private CountdownTimer timer;

    /**
     * Default Constructor
     */
    public ChallengeScoreDialogScreen() {
        super(ScreenState.CHALLENGE_WON);
        this.stage = new Stage(
                new ExtendViewport(DEVICE_SCREEN_WIDTH, DEVICE_SCREEN_HEIGHT, this.camera),
                this.batch
        );
    }

    /**
     * Set the score to display and count up.
     * @param score The score to display and count up
     */
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void update(float deltaTime) {
        // TODO: Implement here...
    }

    @Override
    public void show() {
        this.applyProjectionMatrix();
        Gdx.input.setInputProcessor(this.stage);

        this.saveScoreAsync();

        if(!this.isInitialized) {
            float scaling = this.initBackgroundScene();
            this.setupWidgets(scaling);
            this.isInitialized = true;
        }

        this.timer = new CountdownTimer(this.score, 10);
        this.timer.addCountdownListener(this);
        this.timer.start();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        // TODO: Implement here...
    }

    @Override
    public void hide() {
        // TODO: Implement here...
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

    @Override
    public void onCountdownStarted(long time) {
        this.countedScore = 0;
    }

    @Override
    public void onCountdownTick(long time) {
        this.countedScore++;
        this.scoreLabel.setText(this.countedScore + "");
        //System.out.println(this.countedScore);
    }

    @Override
    public void onCountdownFinished(long time) {
        this.scoreLabel.setText(this.score + "");
    }

    /**
     * Initialize and setup the screen's background.
     */
    private float initBackgroundScene() {
        float scaling = (Settings.App.DEVICE_SCREEN_WIDTH / (float)Settings.Game.RESOLUTION.maxWidth);

        Texture dialogTexture = new Texture(
                Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/backgrounds/dialog_bg.png")
        );
        dialogTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        this.dialogBackground = new Image(dialogTexture);
        this.dialogBackground.setScale(scaling);

        Vector2 dialogPosition = AlignmentHelper.getRelativePosition(
                new Vector2(this.dialogBackground.getWidth() * scaling, this.dialogBackground.getHeight() * scaling),
                new Vector2(Settings.App.DEVICE_SCREEN_WIDTH, Settings.App.DEVICE_SCREEN_HEIGHT),
                Alignment.MIDDLE,
                new int[] {0, 0, 0, 0}
        );
        this.dialogBackground.setPosition(dialogPosition.x, dialogPosition.y);

        this.stage.addActor(this.dialogBackground);
        return scaling;
    }

    /**
     * Setup all UI widgets required to represent the main menu.
     */
    private void setupWidgets(float scaling) {
        Texture stopTexture = new Texture(Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/buttons/finish.png"));
        Texture stopPressedTexture = new Texture(Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/buttons/finishPressed.png"));
        stopTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        stopPressedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        Texture retryTexture = new Texture(Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/buttons/retry.png"));
        Texture retryPressedTexture = new Texture(Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/buttons/retryPressed.png"));
        retryTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        retryPressedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        int multiplier = (int)Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER;

        Label headline = (Label)new UIWidgetBuilder(UIWidgetType.TEXT_LABEL)
                .title("You won!")
                .alignment(Alignment.MIDDLE)
                .margins(
                        0, 0,
                        (int)((this.dialogBackground.getHeight() * scaling)/2) - (26 * multiplier),
                        0
                )
                .font(FontAsset.PORKY, 28 * multiplier, Color.BLACK)
                .create();
        headline.setAlignment(Align.center);

        Label description = (Label)new UIWidgetBuilder(UIWidgetType.TEXT_LABEL)
                .title("You have earned the following score:")
                .alignment(Alignment.MIDDLE)
                .size(
                        (int)(this.dialogBackground.getWidth() * scaling) - (20 * multiplier),
                        50 * multiplier
                )
                .margins(
                        0, 0,
                        (int)((this.dialogBackground.getHeight() * scaling)/2)
                                - (15 * multiplier) - (int)headline.getHeight(),
                        0
                )
                .create();
        description.setAlignment(Align.center);

        this.scoreLabel = (Label)new UIWidgetBuilder(UIWidgetType.TEXT_LABEL)
                .identifier("skipCounting")
                .title("50264")
                .alignment(Alignment.MIDDLE)
                .size(
                        (int)(this.dialogBackground.getWidth() * scaling) - (20 * multiplier),
                        50 * multiplier
                )
                .margins(
                        0, 0,
                        (int)((this.dialogBackground.getHeight() * scaling)/2)
                                - (20 * multiplier) - (int)headline.getHeight(),
                        0
                )
                .font(FontAsset.PORKY, 32 * multiplier, Color.WHITE, Color.BLACK)
                .clickListener(new ClickListener() {
                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                        super.touchUp(event, x, y, pointer, button);
                        onButtonPressed(event.getListenerActor());
                    }
                })
                .create();
        scoreLabel.setAlignment(Align.center);

        ImageButton stopGameBtn = (ImageButton)new UIWidgetBuilder(UIWidgetType.IMAGE_BUTTON)
                .identifier("stopGame")
                .alignment(Alignment.MIDDLE)
                .margins(
                        (int)((this.dialogBackground.getHeight() * scaling)/2)
                                - stopTexture.getHeight() - (10 * multiplier),
                        (int)((this.dialogBackground.getWidth() * scaling)/2)
                                - (stopTexture.getWidth()/2) - (50 * multiplier),
                        0, 0)
                .imageButtonTextures(stopTexture, stopPressedTexture)
                .actorGestureListener(
                        new ActorGestureListener() {
                            @Override
                            public void tap(InputEvent event, float x, float y, int count, int button) {
                                super.tap(event, x, y, count, button);
                                onButtonPressed(event.getListenerActor());
                            }
                        }
                )
                .create();

        ImageButton retryGameBtn = (ImageButton)new UIWidgetBuilder(UIWidgetType.IMAGE_BUTTON)
                .identifier("retryGame")
                .alignment(Alignment.MIDDLE)
                .margins(
                        (int)((this.dialogBackground.getHeight() * scaling)/2)
                                - (int)stopTexture.getHeight() - (10 * multiplier),
                        0, 0,
                        (int)((this.dialogBackground.getWidth() * scaling)/2)
                                - (int)(stopTexture.getWidth()/2) - (50 * multiplier))
                .imageButtonTextures(retryTexture, retryPressedTexture)
                .actorGestureListener(
                        new ActorGestureListener() {
                            @Override
                            public void tap(InputEvent event, float x, float y, int count, int button) {
                                super.tap(event, x, y, count, button);
                                onButtonPressed(event.getListenerActor());
                            }
                        }
                )
                .create();

        this.stage.addActor(headline);
        this.stage.addActor(description);
        this.stage.addActor(this.scoreLabel);
        this.stage.addActor(stopGameBtn);
        this.stage.addActor(retryGameBtn);
    }

    /**
     * Handle a button pressed event.
     * @param sender The triggered button
     */
    private void onButtonPressed(Actor sender) {
        switch(sender.getName()) {
            case "stopGame":
                Gdx.app.postRunnable(() -> {
                    GameScreenManager gsm = GameScreenManager.getInstance();
                    gsm.clearHistory();
                    this.goToScreen(ScreenState.MAIN_MENU);
                });
                break;
            case "retryGame":
                Gdx.app.postRunnable(() -> {
                    GameScreenManager gsm = GameScreenManager.getInstance();
                    IGameScreen screen = gsm.getScreen(ScreenState.PLAY);
                    if(screen != null) {
                        ((PlayScreen)screen).resetLevel();
                        gsm.showLastScreen();
                    }
                });
                break;
            case "skipCounting":
                this.timer.stopTimer();
                break;
        }
    }

    /**
     * Update the score label
     * @param points the updated score to display
     */
    private void updatePoints(String points) {
        // update only on rendering thread
        Gdx.app.postRunnable(() -> {
            scoreLabel.setText(points);
        });
    }

    /**
     * Navigate to a specific screen.
     * @param screenState The screen's state to navigate to
     */
    private void goToScreen(ScreenState screenState) {
        GameScreenManager gsm = GameScreenManager.getInstance();
        IGameScreen screen = gsm.getScreen(screenState);

        if(screen == null) {
            switch(screenState) {
                case MAIN_MENU:
                    screen = new MainMenuScreen();
                    break;
            }
        }
        gsm.showScreen(screen);
        gsm.clearHistory();
        gsm.dismissScreen(ScreenState.CHALLENGE_WON);
    }

    /**
     * Asynchronously save the earned score to database.
     */
    private void saveScoreAsync() {
        new Thread(() -> {
            UserScore userScore = new UserScore(
                    AuthenticationManager.getInstance().getCurrentUser(),
                    new Score(this.score),
                    new Date()
            );

            DataStore.getInstance().createUserScore(userScore);
        }).start();
    }
}
