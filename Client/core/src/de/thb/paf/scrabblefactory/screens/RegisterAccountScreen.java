package de.thb.paf.scrabblefactory.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.thb.paf.scrabblefactory.auth.PasswordHashGenerator;
import de.thb.paf.scrabblefactory.gameplay.timer.CountdownTimer;
import de.thb.paf.scrabblefactory.gameplay.timer.ICountdownListener;
import de.thb.paf.scrabblefactory.managers.GameScreenManager;
import de.thb.paf.scrabblefactory.models.assets.FontAsset;
import de.thb.paf.scrabblefactory.models.components.graphics.Alignment;
import de.thb.paf.scrabblefactory.persistence.DataStore;
import de.thb.paf.scrabblefactory.persistence.entities.Gender;
import de.thb.paf.scrabblefactory.persistence.entities.User;
import de.thb.paf.scrabblefactory.settings.Settings;
import de.thb.paf.scrabblefactory.utils.graphics.widgets.UIWidgetBuilder;
import de.thb.paf.scrabblefactory.utils.graphics.widgets.UIWidgetType;

import static de.thb.paf.scrabblefactory.settings.Settings.App.DEVICE_SCREEN_HEIGHT;
import static de.thb.paf.scrabblefactory.settings.Settings.App.DEVICE_SCREEN_WIDTH;

/**
 * Represents the screen where to register new user accounts.
 *
 * @author Dominic Schiller - Technische Hochschule Brandenburg
 * @version 1.0
 * @since 1.0
 */

public class RegisterAccountScreen extends GameScreen implements ICountdownListener {

    /**
     * The text input field where to enter the name
     */
    private TextField nameTextField;

    /**
     * The text input field where to enter the first name
     */
    private TextField firstNameTextField;

    /**
     * The text input field where to enter the nickname
     */
    private TextField nicknameTextField;

    /**
     * The text input field where to enter the password
     */
    private TextField passwordTextField;

    /**
     * The select box where to choose the gender from
     */
    private SelectBox genderSelectBox;

    /**
     * The text button to trigger the account creation process
     */
    private TextButton createUserButton;

    /**
     * Error for displaying a upcoming error message
     */
    private Label errorMessageLabel;

    /**
     * Default Constructor
     */
    public RegisterAccountScreen() {
        super(ScreenState.REGISTER_ACCOUNT);
        this.stage = new Stage(
                new ExtendViewport(DEVICE_SCREEN_WIDTH, DEVICE_SCREEN_HEIGHT, this.camera),
                this.batch
        );
        this.stage.addActor(DEFAULT_BACKGROUND);
        this.setupUIWidgets();
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void show() {
        this.applyProjectionMatrix();
        Gdx.input.setInputProcessor(this.stage);
        this.clearInputs();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1/255f, 8/255f, 15/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.stage.act(delta);
        this.stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }

    @Override
    public void onCountdownStarted(long time) {
        this.createUserButton.setVisible(false);
        this.stage.addActor(this.errorMessageLabel);
    }

    @Override
    public void onCountdownTick(long time) {

    }

    @Override
    public void onCountdownFinished(long time) {
        this.errorMessageLabel.remove();
        this.createUserButton.setVisible(true);
//        this.stage.addActor(this.createUserButton);
    }

    /**
     * Register new user account based on the current user inputs.
     */
    private void registerUser() {
        this.createUserButton.setVisible(false);

        Date date = null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1948");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Gender gender = null;
        String selectedGender = this.genderSelectBox.getSelected().toString();
        switch(selectedGender) {
            case "Male":
                gender = new Gender("m", "male");
                break;
            case "Female":
                gender = new Gender("f", "female");
                break;
        }

        User user = new User(
                this.nameTextField.getText(),
                this.firstNameTextField.getText(),
                this.nicknameTextField.getText(),
                PasswordHashGenerator.md5(this.passwordTextField.getText()),
                date,
                gender
        );

        DataStore dataStore = DataStore.getInstance();
        user = dataStore.createUser(user);

        if(user.getID() > -1) {
            System.out.println("User (" + user.getNickname() + ") has been successfully created");
            this.navigateToLoginScreen();
        } else {
            this.stage.addActor(this.createUserButton);
        }
    }

    /**
     * Navigate back to the login screen.
     */
    private void navigateToLoginScreen() {
        GameScreenManager gsm = GameScreenManager.getInstance();
        gsm.showScreen(new LoginScreen());
        gsm.clearHistory();
        gsm.dismissScreen(ScreenState.REGISTER_ACCOUNT);
    }

    /**
     * Shows an error message.
     */
    private void showErrorMessage() {
        CountdownTimer displayErrorTimer = new CountdownTimer(3000);
        displayErrorTimer.addCountdownListener(this);
        displayErrorTimer.start();
    }

    /**
     * Validate if all input fields contain valid values.
     * @return The validation status
     */
    private boolean areInputsValid() {
        StringBuilder stringBuilder = new StringBuilder("The following input fields must be filled:\n");
        if(this.nicknameTextField.getText().isEmpty()) {
            stringBuilder.append("- Name -");
        }
        if(this.firstNameTextField.getText().isEmpty()) {
            stringBuilder.append("- First Name -");
        }
        if(this.nicknameTextField.getText().isEmpty()) {
            stringBuilder.append("- Nickname -");
        }
        if(this.passwordTextField.getText().isEmpty()) {
            stringBuilder.append("- Password -");
        }

        String errorMsg = stringBuilder.toString();
        if(errorMsg.contains("- ")) {
            this.errorMessageLabel.setText(errorMsg);
            this.errorMessageLabel.setAlignment(Align.center);
            this.stage.addActor(this.errorMessageLabel);
            return false;
        }

        return true;
    }

    /**
     * Setup all UI widgets required to represent a login form.
     */
    private void setupUIWidgets() {
        float scaling = Settings.App.DEVICE_SCREEN_WIDTH/(float)Settings.Game.RESOLUTION.maxWidth;
        int multiplier = (int)Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER;

        // setup the register form table
        Table table = new Table();
        table.setSize(Settings.App.DEVICE_SCREEN_WIDTH * 0.6f, Settings.App.DEVICE_SCREEN_HEIGHT * 0.75f);

        this.firstNameTextField = this.addLabelInputGroup(table,"First Name", 70);
        table.row();
        this.nameTextField = this.addLabelInputGroup(table, "Name", 20);
        table.row();
        this.nicknameTextField = this.addLabelInputGroup(table,"Nickname", 120);
        table.row();
        this.passwordTextField = this.addLabelInputGroup(table,"Password", 170);
        this.passwordTextField.setPasswordMode(true);
        this.passwordTextField.setPasswordCharacter('*');
        table.row();
        this.genderSelectBox = this.addLabelSelectBoxGroup(table,"Gender", 220, "Male", "Female");
        table.row();
        this.errorMessageLabel = (Label)new UIWidgetBuilder(UIWidgetType.TEXT_LABEL)
                .size(DEFAULT_WIDGET_WIDTH, DEFAULT_LABEL_HEIGHT)
                .alignment(Alignment.TOP_CENTER)
                .margins((int)(265*Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER), 0, 0, 0)
                .font(FontAsset.OPEN_SANS, DEFAULT_FONT_SIZE, Color.RED)
                .create();
        table.row();
        this.createUserButton = (TextButton)new UIWidgetBuilder(UIWidgetType.TEXT_BUTTON)
                .identifier("register")
                .title("Create Account")
                .size(DEFAULT_WIDGET_WIDTH, DEFAULT_INPUT_HEIGHT)
                .alignment(Alignment.TOP_CENTER)
                .margins((int)(265*Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER), 0, 0, 0)
                .clickListener(
                        new ClickListener() {
                            @Override
                            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                super.touchUp(event, x, y, pointer, button);
                                onButtonPressed(event.getListenerActor());
                            }
                        }
                )
                .create();
        table.add(this.createUserButton)
                .height(DEFAULT_INPUT_HEIGHT)
                .width(DEFAULT_WIDGET_WIDTH)
                .padTop(20 * Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER)
                .padBottom(100 * Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER);

        Skin skin = new Skin(Gdx.files.internal("ui/glassy-ui.json"));
        ScrollPane scrollPane = new ScrollPane(table, skin);
        scrollPane.setOverscroll(true, true);
        scrollPane.setBounds(0, 0,
                Settings.App.DEVICE_SCREEN_WIDTH, Settings.App.DEVICE_SCREEN_HEIGHT);
//        table.padTop(-(int)(25*Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER));
        table.setFillParent(true);
        this.stage.addActor(scrollPane);

        // setup the back Button
        Texture backTexture = new Texture(Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/buttons/back.png"));
        Texture backPressedTexture = new Texture(Gdx.files.internal("images/" + Settings.Game.RESOLUTION.name + "/buttons/backPressed.png"));
        backTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);
        backPressedTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Nearest);

        ImageButton backBtn = (ImageButton)new UIWidgetBuilder(UIWidgetType.IMAGE_BUTTON)
                .identifier("back")
                .size((int)(backTexture.getWidth() * scaling), (int)(backTexture.getHeight() * scaling))
                .alignment(Alignment.TOP_LEFT)
                .margins((15 * multiplier), 0, 0, (20 * multiplier)
                )
                .imageButtonTextures(backTexture, backPressedTexture)
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
        this.stage.addActor(backBtn);
    }

    /**
     * Setup a UI label-input widget group.
     * @param labelName The label's name to display
     * @param yPos The y-on-screen position of the widget group
     * @return The created input field instance
     */
    private TextField addLabelInputGroup(Table table, String labelName, int yPos) {
        int topPosition = (int)(yPos*Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER);
        Label label = (Label)new UIWidgetBuilder(UIWidgetType.TEXT_LABEL)
                .title(labelName)
                .size(DEFAULT_WIDGET_WIDTH, DEFAULT_LABEL_HEIGHT)
                .alignment(Alignment.TOP_CENTER)
                .margins(topPosition- DEFAULT_LABEL_HEIGHT, 0, 0, 0)
                .font(FontAsset.OPEN_SANS, DEFAULT_FONT_SIZE, Color.BLACK)
                .create();

        TextField textField = (TextField)new UIWidgetBuilder(UIWidgetType.TEXT_FIELD)
                .size(DEFAULT_WIDGET_WIDTH, DEFAULT_INPUT_HEIGHT)
                .alignment(Alignment.TOP_CENTER)
                .margins(topPosition, 0, 0, 0)
                .font(FontAsset.OPEN_SANS, DEFAULT_FONT_SIZE, Color.BLACK)
                .create();

        table.add(label)
                .height(DEFAULT_LABEL_HEIGHT)
                .width(DEFAULT_WIDGET_WIDTH)
                .padTop(5 * Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER)
                .padBottom(5 * Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER);
        table.row();
        table.add(textField)
                .height(DEFAULT_INPUT_HEIGHT)
                .width(DEFAULT_WIDGET_WIDTH);
//        this.stage.addActor(label);
//        this.stage.addActor(textField);

        return textField;
    }

    /**
     * Setup a UI label-select-box widget group.
     * @param labelName The label's name to display
     * @param yPos The y-on-screen position of the widget group
     * @param items List of options the select box will offer
     * @return The created select box instance
     */
    private SelectBox addLabelSelectBoxGroup(Table table, String labelName, int yPos, Object... items) {
        int topPosition = (int)(yPos*Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER);
        Label label = (Label)new UIWidgetBuilder(UIWidgetType.TEXT_LABEL)
                .title(labelName)
                .size(DEFAULT_WIDGET_WIDTH, DEFAULT_LABEL_HEIGHT)
                .alignment(Alignment.TOP_CENTER)
                .margins(topPosition- DEFAULT_LABEL_HEIGHT, 0, 0, 0)
                .font(FontAsset.OPEN_SANS, DEFAULT_FONT_SIZE, Color.BLACK)
                .create();

        SelectBox selectBox = (SelectBox)new UIWidgetBuilder(UIWidgetType.SELECT_BOX)
                .size(DEFAULT_WIDGET_WIDTH, DEFAULT_INPUT_HEIGHT)
                .alignment(Alignment.TOP_CENTER)
                .margins(topPosition, 0, 0, 0)
                .selectBoxItems(items)
                .font(FontAsset.OPEN_SANS, DEFAULT_FONT_SIZE, Color.BLACK)
                .create();

        table.add(label)
                .height(DEFAULT_LABEL_HEIGHT)
                .width(DEFAULT_WIDGET_WIDTH)
                .padTop(5 * Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER)
                .padBottom(5 * Settings.Game.VIRTUAL_PIXEL_DENSITY_MULTIPLIER);
        table.row();
        table.add(selectBox)
                .height(DEFAULT_INPUT_HEIGHT)
                .width(DEFAULT_WIDGET_WIDTH);
//        this.stage.addActor(label);
//        this.stage.addActor(selectBox);

        return selectBox;
    }

    /**
     * Clear all input fields.
     */
    private void clearInputs() {
        this.nameTextField.setText("");
        this.firstNameTextField.setText("");
        this.nicknameTextField.setText("");
        this.passwordTextField.setText("");
    }

    /**
     * Handle a button pressed event.
     * @param sender The triggered button
     */
    private void onButtonPressed(Actor sender) {
        switch(sender.getName()) {
            case "register":
                if(areInputsValid()) {
                    registerUser();
                } else {
                    showErrorMessage();
                }
                break;
            case "back":
                GameScreenManager gsm = GameScreenManager.getInstance();
                gsm.showScreen(new LandingScreen());
                gsm.clearHistory();
                gsm.dismissScreen(ScreenState.REGISTER_ACCOUNT);
                break;
        }
    }
}
