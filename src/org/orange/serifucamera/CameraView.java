package org.orange.serifucamera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

/**
 * CameraView
 * 
 * 相机预览view
 * 
 * @author Liang
 *
 */
public class CameraView extends SurfaceView implements Callback {
	private Camera camera;

	public CameraView(Context context) {
		super(context);
		init();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	Paint editModePaint = new Paint();
	Rect editModeRect;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isInEditMode()) {
			editModePaint.setColor(Color.WHITE);
			String text = "CameraView\nCamera preview will be shown here.";
			canvas.drawText(text, getWidth() / 2 - editModePaint.measureText(text) / 2, getHeight() / 2, editModePaint);
		}
	}

	private void init() {
		SurfaceHolder mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		editModePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		editModeRect = new Rect(100, 200, 400, 600);
		startCameraPreview();
	}

	public void startCameraPreview() {
		camera = Camera.open();
	}

	private void destroy() {
		camera.stopPreview();
		camera.release();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		// 已经获得Surface的width和height，设置Camera的参数
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(width, height);
		List<Size> vSizeList = parameters.getSupportedPictureSizes();
		int twidth = 1280;
		int theight = 720;
		for (int num = 0; num < vSizeList.size(); num++) {
			Size vSize = vSizeList.get(num);
			Log.d("support_size", vSize.width + "x" + vSize.height);
			if (vSize.width > 1000 && vSize.width < 2000) {
				theight = vSize.height;
				twidth = vSize.width;
			}
		}
		parameters.setPictureSize(twidth, theight);
		camera.setParameters(parameters);
		setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				focus();
			}
		});
		try {
			// 设置显示
			camera.setPreviewDisplay(holder);
			camera.startPreview();

		} catch (IOException exception) {
			camera.release();
			camera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		destroy();
	}

	public void capture() {
		camera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// data是一个原始的JPEG图像数据，
				// 在这里我们可以存储图片，很显然可以采用MediaStore
				// 注意保存图片后，再次调用startPreview()回到预览
				Uri imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
				try {
					Bitmap preparedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(data.length);
					preparedBitmap.compress(CompressFormat.JPEG, 50, byteArrayOutputStream);
					preparedBitmap.recycle();

					System.out.println("uri : " + imageUri.toString());
					OutputStream os = getContext().getContentResolver().openOutputStream(imageUri);
					os.write(byteArrayOutputStream.toByteArray());
					os.flush();
					os.close();

					Cursor cr = getContext().getContentResolver().query(imageUri, null, null, null, null);
					int columnIndex = cr.getColumnIndex(MediaStore.Images.Media.DATA);
					String path = null;
					if (cr.moveToFirst()) {
						path = cr.getString(columnIndex);
					}
					Log.d("save", "saved in : " + path);
				} catch (Exception e) {
					e.printStackTrace();
				}
				camera.startPreview();
			}
		});
	}

	public void focus() {
		camera.autoFocus(new AutoFocusCallback() {
			@Override
			public void onAutoFocus(boolean success, Camera camera) {
				if (success) {
					
				}
			}
		});
	}

}
