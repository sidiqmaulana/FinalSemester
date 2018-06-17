package sidiq.com.aplikasisms;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class BuatPesan extends Activity {
	EditText nomorKontak, text;

	// contact picker
	private static final int CONTACT_PICKER_RESULT = 1001;

	// phonecontact
	public void doLaunchContactPicker(View view) {
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, uri);
		startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String phone = "";
		Cursor contacts = null;
		try {
			if (resultCode == RESULT_OK) {
				switch (requestCode) {
				case CONTACT_PICKER_RESULT:

					// gets the uri of selected contact
					Uri result = data.getData();
					// get the contact id from the Uri (last part is contact
					// id)
					String id = result.getLastPathSegment();
					// queries the contacts DB for phone no
					contacts = getContentResolver().query(
							ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
							null,
							ContactsContract.CommonDataKinds.Phone._ID + "=?",
							new String[] { id }, null);
					// gets index of phone no
					int phoneIdx = contacts.getColumnIndex(Phone.DATA);
					if (contacts.moveToFirst()) {
						// gets the phone no
						phone = contacts.getString(phoneIdx);
						EditText phoneTxt = (EditText) findViewById(R.id.nomorHp);
						// assigns phone no to EditText field phoneno
						phoneTxt.setText(phone);
					} else {
						Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
					}

					break;

				}

			} else {
				// gracefully handle failure
				Toast.makeText(BuatPesan.this, R.string.belumdipilih,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			if (contacts != null) {
				contacts.close();
			}
		}
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.buatpesan);

		final ImageButton send = (ImageButton) findViewById(R.id.send);

		text = (EditText) findViewById(R.id.smsBox);
		nomorKontak = (EditText) findViewById(R.id.nomorHp);

		// fungsi untuk menampilkan isi pesan saat akan diteruskan
		Intent i = getIntent();
		if (i.getStringExtra("message") != null) {
			text.setText(i.getStringExtra("message"));
		}

		send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String pesan = text.getText().toString();
				String nomor = nomorKontak.getText().toString();
				if (pesan.length() > 0 && nomor.length() > 0) {
					try {
						// proses kirim sms
						SmsManager sms = SmsManager.getDefault();
						sms.sendTextMessage(nomor, null, pesan, null, null);

						// proses simpan sms yang terkirim
						ContentValues values = new ContentValues();
						values.put("address", nomor);
						values.put("body", pesan);
						getContentResolver().insert(
								Uri.parse("content://sms/sent"), values);

						Toast.makeText(BuatPesan.this,
								"Pesan berhasil dikirim", Toast.LENGTH_SHORT)
								.show();
						finish();
					} catch (Exception e) {
						Toast.makeText(BuatPesan.this, "Pesan gagal dikirim",
								Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}

				} else {
					Toast.makeText(BuatPesan.this,
							"Nomor atau Isi Pesan Masing Kosong",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

}
