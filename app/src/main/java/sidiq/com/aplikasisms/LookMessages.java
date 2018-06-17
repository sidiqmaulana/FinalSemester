package sidiq.com.aplikasisms;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LookMessages extends Activity {
	TextView number, date, msg;
	Button forward, hapus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lihatpesan);
		number = (TextView) findViewById(R.id.tvNumber);
		date = (TextView) findViewById(R.id.tvDate);
		msg = (TextView) findViewById(R.id.tvMsg);
		forward = (Button) findViewById(R.id.btFrd);
		hapus = (Button) findViewById(R.id.hapus);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent i = getIntent();
		number.setText(i.getStringExtra("no"));
		date.setText(i.getStringExtra("date"));
		msg.setText(i.getStringExtra("msg"));

		forward.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent click = new Intent(LookMessages.this, BuatPesan.class);
				click.putExtra("message", msg.getText());
				startActivity(click);

			}
		});
		hapus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Dialogs.showConfirmation(LookMessages.this,
						R.string.hapuspesan_dialog,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = getIntent();
								String id_pesan_hapus = i
										.getStringExtra("idpesan");
								String id_thread_hapus = i
										.getStringExtra("idthread");

								// hapus pesan
								Uri deleteUri = Uri.parse("content://sms");

								getContentResolver()
										.delete(deleteUri,
												"thread_id=? and _id=?",
												new String[] {
														String.valueOf(id_thread_hapus),
														String.valueOf(id_pesan_hapus) });

								finish();
								Toast.makeText(LookMessages.this,
										"Messages deleted", Toast.LENGTH_SHORT)
										.show();


								onBackPressed();
							}
						});
			}
		});

	}

	@Override
	public void onBackPressed() {
		Intent link = new Intent(LookMessages.this, DataPesan.class);
		Intent i = getIntent();
		link.putExtra("tipepesan", i.getStringExtra("asal"));
		startActivity(link);
	}

}
