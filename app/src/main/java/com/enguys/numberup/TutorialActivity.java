package com.enguys.numberup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

import com.enguys.numberup.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

@SuppressLint("NewApi")
public class TutorialActivity extends Activity {

	Rect rect1, rect2, rect3, rect4;
	ImageView img1, img2, img3, img4;
	TextView txt1, txt2, txt3, txt4, txtScore, txtSms, txtTitle, txtSkip;
	RelativeLayout item1, item2, item3, item4, layoutRoot, layoutOk;
	ProgressBar progressBar;
	ArrayList<TextView> textViews;
	ArrayList<Integer> numbers;
	ArrayList<Integer> numbersSort;
	HashSet<Integer> values;
	HashSet<Integer> numberChecks;
	HashSet<Integer> plus;

	int[] rainbow;
	int level1 = 6;
	int level2 = 22;
	int mWidth, mHeight, centerViewHeight, itemWidth;

	int score = 0;
	Boolean started = false;
	Boolean result = true;
	Boolean isShowDialog = false;
	Random random;
	Typeface appFont;
	Animation animBlink;
	ImageView imageView;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_tutorial_activity);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		mWidth = size.x;
		mHeight = size.y;
		centerViewHeight = mWidth / 7;
		itemWidth = mWidth / 7 * 3;
		item1 = (RelativeLayout) findViewById(R.id.item1);
		item2 = (RelativeLayout) findViewById(R.id.item2);
		item3 = (RelativeLayout) findViewById(R.id.item3);
		item4 = (RelativeLayout) findViewById(R.id.item4);

		txt1 = (TextView) findViewById(R.id.txt1);
		txt2 = (TextView) findViewById(R.id.txt2);
		txt3 = (TextView) findViewById(R.id.txt3);
		txt4 = (TextView) findViewById(R.id.txt4);

		img1 = (ImageView) findViewById(R.id.img1);
		img2 = (ImageView) findViewById(R.id.img2);
		img3 = (ImageView) findViewById(R.id.img3);
		img4 = (ImageView) findViewById(R.id.img4);
		txtScore = (TextView) findViewById(R.id.txt_score);
		txtTitle = (TextView) findViewById(R.id.txt_title);
		txtSms = (TextView) findViewById(R.id.txt_sms);
		txtSkip = (TextView) findViewById(R.id.txt_skip);

		layoutRoot = (RelativeLayout) findViewById(R.id.layout_root);
		layoutOk = (RelativeLayout) findViewById(R.id.layout_ok);
		progressBar = (ProgressBar) findViewById(R.id.progress);

		textViews = new ArrayList<TextView>();
		textViews.add(txt1);
		textViews.add(txt2);
		textViews.add(txt3);
		textViews.add(txt4);
		rainbow = getResources().getIntArray(R.array.colors);
		FontFactory fontFactory = FontFactory.getInstance(this);
		appFont = fontFactory.getFont("fonts/Lane-Narrow.ttf");
		txtScore.setTypeface(appFont);
		txtTitle.setTypeface(appFont);
		txtSms.setTypeface(appFont);
		txtSkip.setTypeface(appFont);
		for (int i = 0; i < textViews.size(); i++) {
			textViews.get(i).setTypeface(appFont);
		}

		layoutOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		txtSkip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		layoutRoot.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int x = (int) event.getX();
				int y = (int) event.getY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					setup();
					break;
				case MotionEvent.ACTION_UP:
					if (numberChecks.size() == 4 && result) {
						layoutOk.setVisibility(View.VISIBLE);
						txtSkip.setText("Play");
						imageView.setAnimation(null);
						MediaPlayer mp = MediaPlayer.create(
								TutorialActivity.this, R.raw.next);
						mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
						mp.setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO Auto-generated method stub
								mp.release();
							}
						});
						mp.start();
					} else {
						playGame(false);
					}
					break;
				case MotionEvent.ACTION_MOVE:
					checkIndex(x, y);
					break;
				}
				return true;
			}
		});
		random = new Random();
		values = new HashSet<Integer>();
		plus = new HashSet<Integer>();
		numbers = new ArrayList<Integer>();
		numbersSort = new ArrayList<Integer>();
		numberChecks = new HashSet<Integer>();
		playGame(true);
	}

	public void playGame(Boolean status) {
		if (!status) {
			score = 0;
		}
		reset();
		setAnimation(0);
	}

	public void generateNumber() {
		HashSet<Integer> colors = new HashSet<Integer>();
		values.clear();
		numberChecks.clear();
		numbers.clear();
		numbersSort.clear();
		plus.clear();
		if (score >= level1 && score < level2) {
			int tem = random.nextInt(textViews.size());
			plus.add(tem);
		}
		if (score >= level2) {
			int tem = random.nextInt(textViews.size());
			plus.add(tem);
			while (plus.contains(tem)) {
				tem = random.nextInt(textViews.size());
			}
			plus.add(tem);
		}

		numbers.add(3);
		numbers.add(5);
		numbers.add(6);
		numbers.add(8);
		for (int i = 0; i < numbers.size(); i++) {
			int value = numbers.get(i);

			int textColor = random.nextInt(rainbow.length);
			while (colors.contains(textColor)) {
				textColor = random.nextInt(rainbow.length);
			}
			values.add(value);
			colors.add(textColor);
			textViews.get(i).setText(value + "");
			textViews.get(i).setTextColor(rainbow[textColor]);
			if (plus.contains(i)) {
				textViews.get(i).setText(convertValueToMaths(value));
			}

		}
		for (int i = 0; i < numbers.size(); i++) {
			numbersSort.add(numbers.get(i));
		}
		Collections.sort(numbersSort);
	}

	public void setAnimation(int position) {
		animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
				R.anim.blink);
		if (imageView != null) {
			imageView.setAnimation(null);
			imageView = null;
		}

		int index = 0;
		int value = numbersSort.get(position);
		for (int i = 0; i < numbers.size(); i++) {
			if (value == numbers.get(i)) {
				index = i;
			}
		}
		switch (index) {
		case 0:
			imageView = img1;
			break;
		case 1:
			imageView = img2;
			break;
		case 2:
			imageView = img3;
			break;
		case 3:
			imageView = img4;
			break;

		default:
			break;
		}
		if (imageView != null) {
			imageView.setImageResource(R.drawable.amination_gray);
			imageView.startAnimation(animBlink);
		}

	}

	public void checkInGame(int position, ImageView view) {

		if (!numberChecks.contains(position)) {
			numberChecks.add(position);

			int index = numberChecks.size();
			int number1 = numbers.get(position - 1);
			int number2 = numbersSort.get(index - 1);
			if (index < 4) {
				setAnimation(index);
			}

			if (number1 != number2) {
				result = false;
				Log.d("viewLocation", "False: " + number1 + "-" + number2);
				view.setImageResource(R.drawable.amination_red);
			} else {
				Log.d("viewLocation", "True: " + number1 + "-" + number2);
				view.setImageResource(R.drawable.amination_gray);
			}
		}
	}

	public String convertValueToMaths(int value) {
		String result = "" + value;
		int number1 = random.nextInt((value - 2) + 1) + 1;
		;
		int number2 = value - number1;
		result = number1 + "+" + number2;

		return result;
	}

	public void reset() {
		result = true;
		img1.setImageResource(R.drawable.transparent);
		img2.setImageResource(R.drawable.transparent);
		img3.setImageResource(R.drawable.transparent);
		img4.setImageResource(R.drawable.transparent);
		txtScore.setText("" + score);
		generateNumber();

	}

	public void checkIndex(int x, int y) {
		if (rect1.contains(x, y)) {
			checkInGame(1, img1);
		} else if (rect2.contains(x, y)) {
			checkInGame(2, img2);
		} else if (rect3.contains(x, y)) {
			checkInGame(3, img3);
		} else if (rect4.contains(x, y)) {
			checkInGame(4, img4);
		}
	}

	public void setup() {
		if (!started) {
			rect1 = new Rect(item1.getLeft(), item1.getTop(), item1.getRight(),
					item1.getBottom());
			rect2 = new Rect(item2.getLeft(), item2.getTop(), item2.getRight(),
					item2.getBottom());
			rect3 = new Rect(item3.getLeft(), item3.getTop(), item3.getRight(),
					item3.getBottom());
			rect4 = new Rect(item4.getLeft(), item4.getTop(), item4.getRight(),
					item4.getBottom());
			started = true;
		}
	}

}
