package com.cookbook.samples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.SplitPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


public class WidgetsSample extends GdxSample {
	private static final String TAG = "WidgetsSample";
	private static final int SCENE_WIDTH = 1280;
	private static final int SCENE_HEIGHT = 720;

	private Viewport viewport;
	private Label label;
	private BitmapFont font;
	private Image image;
	private Button button;
	private TextButton textButton;
	private ImageButton imageButton;
	private ImageTextButton imageTextButton;
	private CheckBox checkbox, checkbox2, checkbox3;
	private ButtonGroup bg;
	private TextField tf;
	private Stack stack;
	private List<String> list;
	private SelectBox<String> selectBox;
	private ProgressBar pb;
	private Slider slider;
	private ScrollPane scrollPane;
	private SplitPane splitPane;
	private Window window;
	private Touchpad touchpad;
	private Tree tree;
	private Dialog dialog;

	private Stage stage;
	
	Texture logo, actor, accept, checkBoxOn, checkBoxOff, tfSelection, tfBackground, 
	tfCursor, scroll_horizontal, knob_scroll, plus, minus, touchpad_background, 
	touchpad_knob, dialog_background, caveman, divider, progress_bar, knob_progress_bar, slider_background, slider_knob;

	@Override
	public void create () {
		super.create();

		viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
		stage = new Stage(viewport);
		Gdx.input.setInputProcessor(stage);

		font = new BitmapFont(Gdx.files.internal("data/font.fnt"));
		
		int middlepointX = SCENE_WIDTH>>1;
		
		// Text label
		Label.LabelStyle ls = new Label.LabelStyle(font, Color.WHITE);
		label = new Label("This is a label", ls);
		label.setPosition(middlepointX - (label.getWidth()*0.5f), SCENE_HEIGHT - label.getHeight());

		// Flat image
		logo = new Texture(Gdx.files.internal("data/loading_screen/logo.png"));
		image = new Image(new TextureRegionDrawable(new TextureRegion(logo)));
		image.setPosition(middlepointX-image.getWidth()*0.5f, label.getY()-image.getHeight() - 10); // 10 for margin

		float firstRowY = image.getY() - 50;

		// Button with background
		actor = new Texture(Gdx.files.internal("data/scene2d/myactor.png"));
		Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
		buttonStyle.up = new TextureRegionDrawable(new TextureRegion(actor));
		button = new Button(buttonStyle);
		button.setPosition(75, firstRowY-button.getHeight());
		button.addListener( new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "Button clicked");
			};
		});

		// Button with background and text
		TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();
		tbs.font = font;
		tbs.up = new TextureRegionDrawable(new TextureRegion(actor));
		textButton = new TextButton("TextButton", tbs);
		textButton.setPosition(button.getX() + button.getWidth() + 10, firstRowY-textButton.getHeight());
		textButton.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "TextButton clicked");
			};
		});

		//Button with background and icon
		accept = new Texture(Gdx.files.internal("data/scene2d/accept.png"));
		ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
		ibs.up = new TextureRegionDrawable(new TextureRegion(actor));
		ibs.imageUp = new TextureRegionDrawable(new TextureRegion(accept));
		imageButton = new ImageButton(ibs);
		imageButton.setPosition(textButton.getX() + textButton.getWidth() + 10, firstRowY-imageButton.getHeight());
		imageButton.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "ImageButton clicked");
			};
		});

		//Button with background, text and icon
		ImageTextButton.ImageTextButtonStyle itbs = new ImageTextButton.ImageTextButtonStyle();
		itbs.font = font;
		itbs.fontColor = Color.WHITE;
		itbs.up = new TextureRegionDrawable(new TextureRegion(actor));
		itbs.imageUp = new TextureRegionDrawable(new TextureRegion(accept));
		imageTextButton = new ImageTextButton("ImgTextButton", itbs);
		imageTextButton.setPosition(imageButton.getX() + imageButton.getWidth() + 10, firstRowY-imageTextButton.getHeight());
		imageTextButton.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "ImageTextButton clicked");
			};
		});

		//Grouped checkbox buttons
		checkBoxOn = new Texture(Gdx.files.internal("data/scene2d/checkBoxOn.png"));
		checkBoxOff = new Texture(Gdx.files.internal("data/scene2d/checkBoxOff.png"));
		CheckBox.CheckBoxStyle cbs = new CheckBox.CheckBoxStyle();
		cbs.checkboxOn = new TextureRegionDrawable(new TextureRegion(checkBoxOn));
		cbs.checkboxOff = new TextureRegionDrawable(new TextureRegion(checkBoxOff));
		cbs.font = font;
		cbs.fontColor = Color.WHITE;
		checkbox = new CheckBox("Checkbox", cbs);
		checkbox2 = new CheckBox("Checkbox2", cbs);
		checkbox2.setPosition(checkbox.getX(), checkbox.getY()+checkbox.getHeight());
		checkbox2.setPosition(checkbox2.getX(), checkbox2.getY()+checkbox2.getHeight());
		checkbox3 = new CheckBox("Checkbox3", cbs);
		bg = new ButtonGroup();
		bg.add(checkbox, checkbox2, checkbox3);
		bg.setMaxCheckCount(1);
		bg.setMinCheckCount(0);
		float checkBoxX = imageTextButton.getX() + imageTextButton.getWidth() + 10;
		checkbox.setPosition(checkBoxX, firstRowY-checkbox.getHeight()+2);
		checkbox2.setPosition(checkBoxX, checkbox.getY()-checkbox2.getHeight());
		checkbox3.setPosition(checkBoxX, checkbox2.getY()-checkbox3.getHeight()); 
		//Checkbox - interaction
		checkbox.addListener( new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "CheckBox: " + !checkbox.isChecked());
				return true;
			};
		});

		//Single line of text input for passwords
		TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
		tfs.font = font;
		tfs.fontColor = Color.BLACK;
		tfSelection = new Texture(Gdx.files.internal("data/scene2d/tfSelection.png"));
		tfBackground = new Texture(Gdx.files.internal("data/scene2d/tfbackground.png"));
		tfCursor = new Texture(Gdx.files.internal("data/scene2d/cursor.png"));
		tfs.selection = new TextureRegionDrawable(new TextureRegion(tfSelection));
		tfs.background = new TextureRegionDrawable(new TextureRegion(tfBackground));
		tfs.cursor = new TextureRegionDrawable(new TextureRegion(tfCursor));
		tf = new TextField("", tfs);
		tf.setMessageText("Enter password...");
		tf.setPasswordCharacter('*');
		tf.setPasswordMode(true);
		tf.setTextFieldListener(new TextFieldListener() {
			public void keyTyped (TextField textField, char key) {
				if (key == '\n') textField.getOnscreenKeyboard().show(false);
			}
		});
		tf.setPosition(checkbox3.getX() + checkbox3.getWidth() + 10, firstRowY-tf.getHeight());

		//Stack
		stack = new Stack();
		Button button1 = new Button(new TextureRegionDrawable(new TextureRegion(actor)));
		Label label1 = new Label("Stack Label", ls);

		stack.add(button1);
		stack.pack();
		stack.addActorAfter(button1,label1);
		stack.setPosition(tf.getX() + tf.getWidth() + 10, firstRowY-stack.getHeight());

		float secondRowY = firstRowY -130;

		//List
		List.ListStyle listS = new List.ListStyle();
		listS.font = font;
		listS.fontColorSelected = Color.BLACK;
		listS.fontColorUnselected = Color.GRAY;
		listS.selection = new TextureRegionDrawable(new TextureRegion(tfBackground));
		list = new List<String>(listS);
		Array<String> items = new Array<String>();
		items.add("item1");
		items.addAll("item2", "item3", "item4");
		list.setItems(items);
		list.pack(); // To get the actual size
		list.setPosition(75, secondRowY-list.getHeight());

		//SelectBox
		SelectBox.SelectBoxStyle sbs = new SelectBox.SelectBoxStyle();
		sbs.listStyle = listS;
		ScrollPane.ScrollPaneStyle sps = new ScrollPane.ScrollPaneStyle();
		
		scroll_horizontal = new Texture(Gdx.files.internal("data/scene2d/scroll_horizontal.png"));
		knob_scroll = new Texture(Gdx.files.internal("data/scene2d/knob_scroll.png"));
		sps.background = new TextureRegionDrawable(new TextureRegion(tfBackground));
		sps.vScroll = new TextureRegionDrawable(new TextureRegion(scroll_horizontal));
		sps.vScrollKnob = new TextureRegionDrawable(new TextureRegion(knob_scroll));
		sbs.background = new TextureRegionDrawable(new TextureRegion(tfBackground));
		sbs.scrollStyle = sps;
		sbs.font = font;
		sbs.fontColor.set(Color.RED);
		selectBox = new SelectBox<String>(sbs);
		selectBox.setItems(items);
		selectBox.pack(); // To get the actual size
		selectBox.setPosition(list.getX() + list.getWidth() + 10, secondRowY-selectBox.getHeight());


		//ProgressBar
		progress_bar = new Texture(Gdx.files.internal("data/loading_screen/progress_bar.png"));
		knob_progress_bar = new Texture(Gdx.files.internal("data/scene2d/knob.png"));
		ProgressBar.ProgressBarStyle pbs = new ProgressBar.ProgressBarStyle();
		pbs.background = new TextureRegionDrawable(new TextureRegion(progress_bar));
		pbs.knob = new TextureRegionDrawable(new TextureRegion(knob_progress_bar));
		// ProgressBar constructor needs Min value, max value, step-size, horizontal orientation, style
		pb = new ProgressBar(0f, 100f, 1f, false, pbs);
		pb.setPosition(selectBox.getX() + selectBox.getWidth() + 10, secondRowY-pb.getHeight());
		pb.setValue(50f);

		//Slider
		slider_background = new Texture(Gdx.files.internal("data/scene2d/slider_background.png"));
		slider_knob = new Texture(Gdx.files.internal("data/scene2d/slider_knob.png"));
		Slider.SliderStyle ss = new Slider.SliderStyle();
		ss.background = new TextureRegionDrawable(new TextureRegion(slider_background));
		ss.knob = new TextureRegionDrawable(new TextureRegion(slider_knob));
		slider = new Slider(0f, 100f, 1f, false, ss);
		slider.setPosition(pb.getX() + pb.getWidth() + 10, secondRowY-slider.getHeight());
		slider.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				Gdx.app.log(TAG, "slider changed to: " + slider.getValue());
			}
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			};
		});

		//ScrollPane
		List list2 = new List(listS);
		items.addAll("item5", "item6", "item7", "item8");
		list2.setItems(items);
		list2.pack();
		scrollPane = new ScrollPane(list2, sps);
		scrollPane.setWidth(scrollPane.getWidth()*0.4f);
		scrollPane.setHeight(scrollPane.getHeight()*0.4f);
		scrollPane.setPosition(slider.getX() + slider.getWidth() + 10, secondRowY-scrollPane.getHeight());

		//Tree
		plus = new Texture(Gdx.files.internal("data/scene2d/plus.png"));
		minus = new Texture(Gdx.files.internal("data/scene2d/minus.png"));
		Label node1 = new Label("Root-node", ls);
		Label node2 = new Label("Child-node1", ls);
		Label node3 = new Label("Child-node2", ls);
		Tree.TreeStyle treeS = new Tree.TreeStyle();
		treeS.plus = new TextureRegionDrawable(new TextureRegion(plus));
		treeS.minus = new TextureRegionDrawable(new TextureRegion(minus));
		tree = new Tree(treeS);
		tree.add(new Tree.Node(node1));
		tree.getNodes().get(0).add(new Tree.Node(node2));
		tree.getNodes().get(0).add(new Tree.Node(node3));
		tree.expandAll();
		tree.pack();
		tree.setPosition(scrollPane.getX() + scrollPane.getWidth() + 10, secondRowY-tree.getHeight());

		//Touchpad
		touchpad_background = new Texture(Gdx.files.internal("data/scene2d/touchpad_background.png"));
		touchpad_knob = new Texture(Gdx.files.internal("data/scene2d/touchpad_knob.png"));
		Touchpad.TouchpadStyle ts = new Touchpad.TouchpadStyle();
		ts.background = new TextureRegionDrawable(new TextureRegion(touchpad_background));
		ts.knob = new TextureRegionDrawable(new TextureRegion(touchpad_knob));
		touchpad = new Touchpad(10f, ts);
		touchpad.setPosition(tree.getX() + tree.getWidth() + 30, secondRowY-touchpad.getHeight());

		//Dialog
		Label message = new Label("Dialog: Exit?", ls);
		TextButton tb1 = new TextButton("Yes", tbs);
		tb1.addListener( new ClickListener() {             
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.log(TAG, "Dialog, Exit");
				Gdx.app.exit();
			};
		});
		TextButton tb2 = new TextButton("No", tbs);

		Window.WindowStyle ws2 = new Window.WindowStyle();
		ws2.titleFont = font;
		ws2.titleFontColor = Color.WHITE;
		dialog_background = new Texture(Gdx.files.internal("data/scene2d/dialog_background.png"));
		TextureRegionDrawable trd = new TextureRegionDrawable(new TextureRegion(dialog_background));

		dialog = new Dialog("", ws2);
		dialog.setKeepWithinStage(false);
		dialog.getContentTable().row().colspan(1).center();
		dialog.getContentTable().add(message);
		dialog.row().colspan(2);
		dialog.button(tb1);
		dialog.button(tb2);
		dialog.setModal(false);
		dialog.setBackground(trd);
		dialog.pack();
		dialog.setPosition(75, secondRowY-130 - dialog.getHeight());

		//SplitPane
		caveman = new Texture(Gdx.files.internal("data/caveman.png"));
		divider = new Texture(Gdx.files.internal("data/scene2d/divider.png"));
		Image caveman1 = new Image(new TextureRegion(caveman));
		
		List list3 = new List(listS);
		list3.setItems(items);
		list3.pack();
		
		SplitPane.SplitPaneStyle splitPaneS = new SplitPane.SplitPaneStyle();
		splitPaneS.handle = new TextureRegionDrawable(new TextureRegion(divider));
		splitPane = new SplitPane(caveman1, list3, false, splitPaneS);
		splitPane.setWidth(splitPane.getWidth() * 1.4f);
		splitPane.setHeight(splitPane.getHeight() * 0.6f);
		splitPane.setPosition(touchpad.getX() + touchpad.getWidth() + 50, secondRowY-splitPane.getHeight());

		//Window
		Window.WindowStyle ws = new Window.WindowStyle();
		ws.titleFont = font;
		ws.titleFontColor = Color.WHITE;

		Label gameTitle = new Label("WindowLabel", ls);
		Button firstButton = new Button(new TextureRegionDrawable(new TextureRegion(actor)));
		Button secondButton = new Button(new TextureRegionDrawable(new TextureRegion(actor)));
		Button thirdButton = new Button(new TextureRegionDrawable(new TextureRegion(actor)));

		window = new Window("This is the title", ws);
		window.debug();
		window.setKeepWithinStage(false);
		window.padTop(50f);
		window.setPosition(dialog.getX() + dialog.getWidth() + 10, secondRowY-window.getHeight()-200);
		window.row().colspan(1);
		window.add(gameTitle);
		window.row().colspan(1);
		window.add(firstButton);
		window.row().colspan(1);
		window.add(secondButton);
		window.row().colspan(1);
		window.add(thirdButton);
		window.pack();

		//Adds actors to scene
		stage.addActor(image);
		stage.addActor(label);
		stage.addActor(button);
		stage.addActor(textButton);
		stage.addActor(imageButton);
		stage.addActor(imageTextButton);
		stage.addActor(checkbox);
		stage.addActor(checkbox2);
		stage.addActor(checkbox3);
		stage.addActor(tf);
		stage.addActor(stack);
		stage.addActor(list);
		stage.addActor(selectBox);
		stage.addActor(pb);
		stage.addActor(slider);
		stage.addActor(scrollPane);
		stage.addActor(splitPane);
		stage.addActor(window);
		stage.addActor(touchpad);
		stage.addActor(tree);
		stage.addActor(dialog);
		
		//stage.setDebugAll(true);

	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void dispose() {
		logo.dispose();
		actor.dispose();
		accept.dispose();
		checkBoxOn.dispose();
		checkBoxOff.dispose();
		tfSelection.dispose();
		tfBackground.dispose();
		tfCursor.dispose();
		scroll_horizontal.dispose();
		knob_scroll.dispose();
		progress_bar.dispose();
		knob_progress_bar.dispose();
		plus.dispose();
		minus.dispose();
		touchpad_background.dispose();
		touchpad_knob.dispose();
		dialog_background.dispose();
		caveman.dispose(); 
		divider.dispose();
		slider_background.dispose();
		slider_knob.dispose();
		
		font.dispose();
		stage.dispose();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		stage.draw();
	}
}
