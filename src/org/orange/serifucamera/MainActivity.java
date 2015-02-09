package org.orange.serifucamera;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	CameraView surfaceView1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		surfaceView1 = (CameraView) findViewById(R.id.surfaceView1);
		View focus = findViewById(R.id.focus);
		View capture = findViewById(R.id.capture);
		focus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				surfaceView1.focus();
			}
		});
		capture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				surfaceView1.capture();
			}
		});
	}

}
