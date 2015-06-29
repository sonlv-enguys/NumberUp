package com.game.numberup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends FragmentActivity  {

	Rect rect1, rect2, rect3, rect4;
	ImageView img1, img2, img3, img4;
	TextView txt1, txt2, txt3, txt4, txtScore, txtNewScore, txtTitle;
	Dialog dialog;
	RelativeLayout item1, item2, item3, item4, layoutRoot;
	ProgressBar progressBar;
	ArrayList<TextView> textViews;
	ArrayList<Integer> numbers;
	ArrayList<Integer> numbersSort;
	HashSet<Integer> values;
	HashSet<Integer> numberChecks;
	HashSet<Integer> plus;

	int[] rainbow;

	int mWidth, mHeight, centerViewHeight, itemWidth;
	int best_score = 3;
	int level1 = 6;
	int level2 = 22;
	int totalTime = 2000;
	int max = 10;
	int plush = 0;
	int score = 0;
	private static final int REQUEST_RESOLVE_ERROR = 1001;
	private static final int REQUEST_SHARE_FACEBOOK = 1002;
	private static final String DIALOG_ERROR = "dialog_error";
	private boolean mResolvingError = false;
	Boolean started = false;
	Boolean result = true;
	Boolean isShowDialog = false;
	SharedPreferences preferences;
	CountDownTimer countDownTimer;
	Random random;
	Typeface appFont;
	AudioManager audio;
	ProgressBarHandler progressBarHandler;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this, TutorialActivity.class);
		startActivity(intent);
		setContentView(R.layout.game_main_activity);
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

		layoutRoot = (RelativeLayout) findViewById(R.id.layout_root);
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
		for (int i = 0; i < textViews.size(); i++) {
			textViews.get(i).setTypeface(appFont);
		}

		int[] viewLocation = new int[2];
		item4.getLocationInWindow(viewLocation);
		Log.d("viewLocation", viewLocation[0] + " : " + viewLocation[1]);

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
						score = score + 1;
						reset();
						playMedia(R.raw.next);
					} else {
						showDialogPlayGame();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					checkIndex(x, y);
					break;
				}
				return true;
			}
		});
		progressBarHandler = new ProgressBarHandler(this);
		preferences = getSharedPreferences(getPackageName(),
				Activity.MODE_PRIVATE);
		best_score = preferences.getInt("best_score", best_score);
		random = new Random();
		values = new HashSet<Integer>();
		plus = new HashSet<Integer>();
		numbers = new ArrayList<Integer>();
		numbersSort = new ArrayList<Integer>();
		numberChecks = new HashSet<Integer>();

		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		showDialogPlayGame();

	}

	public void playMedia(int resId) {
		MediaPlayer mp = MediaPlayer.create(this, resId);
		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mp.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.release();
			}
		});
		mp.start();
	}

	public void updateVolume() {
		audio.setStreamVolume(AudioManager.STREAM_MUSIC,
				audio.getStreamVolume(AudioManager.STREAM_RING),
				AudioManager.FLAG_VIBRATE);
	}

	public void generateNumber() {
		HashSet<Integer> colors = new HashSet<Integer>();
		values.clear();
		numberChecks.clear();
		numbers.clear();
		numbersSort.clear();
		plus.clear();
		if (score >= level1 && score < level2) {
			max = 14;
			int tem = random.nextInt(textViews.size());
			plus.add(tem);
		}
		if (score >= level2) {
			max = 14;
			int tem = random.nextInt(textViews.size());
			plus.add(tem);
			while (plus.contains(tem)) {
				tem = random.nextInt(textViews.size());
			}
			plus.add(tem);
		}

		for (int i = 0; i < textViews.size(); i++) {
			int value = random.nextInt((max - 1) + 1) + 1;
			while (values.contains(value)) {
				value = random.nextInt((max - 1) + 1) + 1;
			}
			if (plus.contains(i)) {
				value = random.nextInt((max - 4) + 1) + 4;
				while (values.contains(value)) {
					value = random.nextInt((max - 4) + 1) + 4;
				}
			}
			int textColor = random.nextInt(rainbow.length);
			while (colors.contains(textColor)) {
				textColor = random.nextInt(rainbow.length);
			}
			values.add(value);
			numbers.add(value);
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

	public void checkInGame(int position, ImageView view) {

		if (!numberChecks.contains(position)) {
			numberChecks.add(position);
			int index = numberChecks.size() - 1;
			int number1 = numbers.get(position - 1);
			int number2 = numbersSort.get(index);
			Log.d("viewLocation", index + "-" + position + "-" + numbers.size()
					+ "-" + numbersSort.size());

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

	public void startTime() {
		// if (score > 0) {
		// progressBar.setProgress(100);
		// if (countDownTimer != null) {
		// countDownTimer.cancel();
		// }
		// countDownTimer = new CountDownTimer(totalTime, 1) {
		// public void onTick(long millisUntilFinished) {
		// progressBar.setProgress(Math.round(100.0f
		// * millisUntilFinished / totalTime));
		// }
		//
		// public void onFinish() {
		// progressBar.setProgress(0);
		// showDialogPlayGame();
		//
		// }
		// }.start();
		// }
	}

	public void showDialogPlayGame() {
		updateVolume();
		if (dialog == null) {
			dialog = new Dialog(this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setCancelable(false);
			dialog.setContentView(R.layout.dialog_game_over_activity);
			dialog.getWindow().setBackgroundDrawableResource(
					R.drawable.transparent);
			dialog.getWindow().setLayout(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT);
			txtTitle = (TextView) dialog.findViewById(R.id.txt_dialog_title);
			txtNewScore = (TextView) dialog.findViewById(R.id.txt_dialog_score);
			TextView txtShare = (TextView) dialog
					.findViewById(R.id.txt_dialog_share);
			txtTitle.setTypeface(appFont);
			txtNewScore.setTypeface(appFont);
			txtShare.setTypeface(appFont);
			ImageView play = (ImageView) dialog
					.findViewById(R.id.img_dialog_play);
			play.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					reset();
					dialog.cancel();
					isShowDialog = false;
				}
			});
			txtShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					shareFacebook();
				}
			});
			dialog.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method stub
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						finish();
						dialog.dismiss();
					}
					if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
						audio.adjustStreamVolume(AudioManager.STREAM_RING,
								AudioManager.ADJUST_RAISE,
								AudioManager.FLAG_SHOW_UI);
						updateVolume();
					}
					if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
						audio.adjustStreamVolume(AudioManager.STREAM_RING,
								AudioManager.ADJUST_LOWER,
								AudioManager.FLAG_SHOW_UI);
						updateVolume();
					}
					return true;
				}
			});
		}

		if (!isShowDialog) {
			txtTitle.setText("Game Over");
			txtNewScore.setText(getString(R.string.score, score, best_score));
			if (!started) {
				txtNewScore.setText("");
				txtTitle.setText("Number Up");
			} else {
				playMedia(R.raw.error);
			}
			if (score > best_score) {
				upateHeightScore();
			}
			try {
				dialog.show();
			} catch (Exception e) {
				// TODO: handle exception
			}

			isShowDialog = true;
		}
		score = 0;
	}

	private void upateHeightScore() {
		// TODO Auto-generated method stub
		preferences.edit().putInt("best_score", score).commit();
		best_score = score;
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

		startTime();

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

	public void shareFacebook() {
		progressBarHandler.show();
		String imagePath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/a.png";
		Bitmap bitmap = takeScreenshot();
		saveBitmap(bitmap, imagePath);
		dialog.cancel();
		Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
		shareIntent.setType("image/jpeg");
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sample Photo");
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				"This photo is created by App Name");
		shareIntent.putExtra(Intent.EXTRA_STREAM,
				Uri.fromFile(new File(imagePath)));
		PackageManager pm = getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent,
				0);
		for (final ResolveInfo app : activityList) {
			if ((app.activityInfo.name).contains("facebook")) {
				final ActivityInfo activity = app.activityInfo;
				final ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				// shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				// shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				// | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				shareIntent.setComponent(name);
				startActivityForResult(shareIntent, REQUEST_SHARE_FACEBOOK);
				break;
			}
		}
		progressBarHandler.hide();
	}

	public Bitmap takeScreenshot() {
		View view = dialog.findViewById(R.id.layout_root);
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
				view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.WHITE);
		view.draw(canvas);
		return returnedBitmap;
	}

	public void saveBitmap(Bitmap bitmap, String path) {
		File imagePath = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("shareFacebook", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("shareFacebook", e.getMessage(), e);
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_SHARE_FACEBOOK) {
			dialog.show();
		}
	}

}
