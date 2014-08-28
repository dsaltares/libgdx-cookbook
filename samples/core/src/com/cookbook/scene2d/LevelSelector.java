package com.cookbook.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

public class LevelSelector extends Table {
	private LevelSelectorStyle style;
	private Image buttonLeft, buttonRight;
	private TextButton buttonGo;
	private int currentLevelIndex = 0;
	private Array<Level> levels;
	public float imageWidth = 400;
	public float imageHeight = 195;
	
	public LevelSelector() {
		initialize();
	}

	public LevelSelector(Skin skin) {
		super(skin);
		setStyle(skin.get(LevelSelectorStyle.class));
		initialize();
		setSize(getPrefWidth(), getPrefHeight());
	}

	public LevelSelector(Skin skin, String styleName) {
		super(skin);
		setStyle(skin.get(styleName, LevelSelectorStyle.class));
		initialize();
		setSize(getPrefWidth(), getPrefHeight());
	}

	public LevelSelector(LevelSelectorStyle style){
		setStyle(style);
		initialize();
		setSize(getPrefWidth(), getPrefHeight());
	}
	
	public LevelSelector(Drawable leftArrow, Drawable rightArrow, TextButtonStyle textButtonStyle) {
		this(new LevelSelector.LevelSelectorStyle(leftArrow, rightArrow, textButtonStyle));
	}

	public LevelSelector(Drawable leftArrow, Drawable rightArrow, Drawable background, TextButtonStyle textButtonStyle) {
		this(new LevelSelector.LevelSelectorStyle(leftArrow, rightArrow, background, textButtonStyle));
	}
	
	public LevelSelector(Array<Level> array, Skin skin) {
		super(skin);
		setStyle(skin.get(LevelSelectorStyle.class));
		initialize();
		setSize(getPrefWidth(), getPrefHeight());
		this.levels = new Array<Level>(array);
	}
	
	public LevelSelector(Array<Level> array, Skin skin, String styleName) {
		super(skin);
		setStyle(skin.get(LevelSelectorStyle.class));
		initialize();
		setSize(getPrefWidth(), getPrefHeight());
		this.levels = new Array<Level>(array);
	}
	
	public LevelSelector(Array<Level> array, LevelSelectorStyle style) {
		setStyle(style);
		initialize();
		setSize(getPrefWidth(), getPrefHeight());
		this.levels = new Array<Level>(array);
	}
	
	public void addLevel(Level level) {
		if(level != null && !levels.contains(level,false))
			levels.add(level);
		update();
	}
	
	public void addLevels (Array<Level> array) {
		for(Level l : array)
			levels.add(l);
		update();
	}
	
	public void addLevels(Level...levelsvar) {
		for(Level level : levelsvar) {
			if(level != null && !levels.contains(level,false))
				levels.add(level);
		}
		update();
	}
	
	private void initialize() {
		debug();
		setTouchable(Touchable.enabled);
		
		levels = new Array<Level>();

		buttonLeft.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				showPreviousLevel();
			}
		});
		buttonRight.addListener(new ClickListener() {
			public void clicked (InputEvent event, float x, float y) {
				showNextLevel();
			}
		});
	}

	private void showPreviousLevel() {
		if(currentLevelIndex > 0) {
			currentLevelIndex--;
			update();
		}
	}

	private void showNextLevel() {
		if(currentLevelIndex+1 < levels.size) {
			currentLevelIndex++;
			update();
		}
	}
	
	private void update() {
		if(levels.size != 0) {
			clearChildren();
			Level currentLevel = levels.get(currentLevelIndex);
			row();
			add(currentLevel.getTitle()).colspan(3);
			row();
			add(buttonLeft).colspan(1).padRight(10f);
			add(currentLevel.getImage()).colspan(1).size(imageWidth, imageHeight);
			add(buttonRight).colspan(1).padLeft(10f);
			row();
			add(buttonGo).colspan(3).padTop(10f).fillX();
			row();
			pad(20f);
			pack();
		}
	}

	public void setImageSize(float width, float height) {
		this.imageWidth = width;
		this.imageHeight = height;
	}
	
	public TextButton getButton() {
		return buttonGo;
	}
	
	public int getCurrentLevel() {
		return currentLevelIndex+1;
	}
	
	public void draw(Batch batch, float parentAlpha) {
		validate();

		super.draw(batch, parentAlpha);
	}

	public LevelSelectorStyle getStyle() {
		return style;
	}

	public void setStyle(LevelSelectorStyle style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		this.buttonLeft = new Image(style.leftArrow);
		this.buttonRight = new Image(style.rightArrow);
		this.buttonGo = new TextButton("GO", style.textButtonStyle);
		setBackground(style.background);
		invalidateHierarchy();
	}

	public float getPrefWidth () {
		float width = super.getPrefWidth();
		if (style.background != null) width = Math.max(width, style.background.getMinWidth());
		return width;
	}

	public float getPrefHeight () {
		float height = super.getPrefHeight();
		if (style.background != null) height = Math.max(height, style.background.getMinHeight());
		return height;
	}

	public float getMinWidth () {
		return getPrefWidth();
	}

	public float getMinHeight () {
		return getPrefHeight();
	}

	public static class Level {
		private Label title;
		private Image image;

		public Level(CharSequence level_name, Skin skin) {
			title = new Label(level_name, skin);
		}

		public Level(CharSequence level_name, LabelStyle labelStyle) {
			title = new Label(level_name, labelStyle);
		}

		public Level(CharSequence level_name, Image img, Skin skin) {
			title = new Label(level_name, skin);
			image = img;
		}

		public Level(CharSequence level_name, Image img, LabelStyle labelStyle) {
			title = new Label(level_name, labelStyle);
			image = img;
		}

		public Label getTitle() {
			return title;
		}

		public void setTitle(Label title) {
			this.title = title;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image img) {
			this.image = img;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((title.getText() == null) ? 0 : title.getText().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Level other = (Level) obj;
			if (title == null) {
				if (other.title.getText() != null)
					return false;
			} else if (!title.getText().equals(other.title.getText()))
				return false;
			return true;
		}

	}

	static public class LevelSelectorStyle {
		/* Optional */
		public Drawable background;
		
		/* Must be defined */
		public Drawable leftArrow, rightArrow;
		public TextButtonStyle textButtonStyle;
		
		public LevelSelectorStyle() {
		}

		public LevelSelectorStyle(Drawable leftArrowImg, Drawable rightArrowImg, TextButtonStyle textButtonStyle) {
			this.leftArrow = leftArrowImg;
			this.rightArrow = rightArrowImg;
			this.textButtonStyle = textButtonStyle;
		}

		public LevelSelectorStyle(Drawable leftArrowImg, Drawable rightArrowImg, Drawable background, TextButtonStyle textButtonStyle) {
			this.leftArrow = leftArrowImg;
			this.rightArrow = rightArrowImg;
			this.background = background;
			this.textButtonStyle = textButtonStyle;
		}

		public LevelSelectorStyle( LevelSelectorStyle style) {
			this.background = style.background;
			this.leftArrow = style.leftArrow;
			this.rightArrow = style.rightArrow;
		}

	}

}
