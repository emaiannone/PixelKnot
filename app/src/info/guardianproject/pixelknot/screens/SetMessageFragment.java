package info.guardianproject.pixelknot.screens;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.app.SherlockFragment;

import info.guardianproject.pixelknot.Constants;
import info.guardianproject.pixelknot.Constants.PixelKnot.Keys;
import info.guardianproject.pixelknot.R;
import info.guardianproject.pixelknot.utils.ActivityListener;
import info.guardianproject.pixelknot.utils.PassphraseDialogListener;
import info.guardianproject.pixelknot.utils.PixelKnotListener;

import org.json.JSONException;

public class SetMessageFragment extends SherlockFragment implements Constants, ActivityListener, PassphraseDialogListener {
	Activity a;
	View root_view;

	EditText secret_message_holder;
	TextWatcher secret_message_watcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {}

		@Override
		public void afterTextChanged(Editable s) {
			((PixelKnotListener) a).getPixelKnot().setSecretMessage(secret_message_holder.getText().toString());
		}
		
	};
	
	@Override
	public View onCreateView(LayoutInflater li, ViewGroup container, Bundle savedInstanceState) {
		root_view = li.inflate(R.layout.set_message_fragment, container, false);
		
		secret_message_holder = (EditText) root_view.findViewById(R.id.secret_message_holder);
		secret_message_holder.addTextChangedListener(secret_message_watcher);
		return root_view;
	}

	@Override
	public void onAttach(Activity a) {
		super.onAttach(a);		
		this.a = a;
	}

	private void setPassphrase() {
		setPassphrase(null);
	}

	private void setPassphrase(String passphrase) {
		if(passphrase == null) {
			try {
				if(((PixelKnotListener) a).getPixelKnot().has(Keys.PASSWORD)) {
					passphrase = ((PixelKnotListener) a).getPixelKnot().getString(Keys.PASSWORD);
				}
			} catch (JSONException e) {}
		}

		SetPassphraseDialog.getDialog(this, passphrase).show();
	}
	
	private void confirmPassphraseOverride() {
		ConfirmPassphraseOverrideDialog.getDialog(this).show();
	}

	@Override
	public void initButtons() {
		ImageButton share_unprotected = new ImageButton(a);
		share_unprotected.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		share_unprotected.setPadding(0, 0, 0, 0);
		share_unprotected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirmPassphraseOverride();
			}

		});
		share_unprotected.setImageResource(R.drawable.share_selector);

		ImageButton passphrase_protect = new ImageButton(a);
		passphrase_protect.setBackgroundColor(getResources().getColor(android.R.color.transparent));
		passphrase_protect.setPadding(0, 0, 0, 0);
		passphrase_protect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setPassphrase();
			}
		});
		passphrase_protect.setImageResource(R.drawable.password_selector);
		
		((PixelKnotListener) a).setButtonOptions(new ImageButton[] {passphrase_protect, share_unprotected});
	}

	@Override
	public void updateUi() {
		try {
			String secret_message = ((PixelKnotListener) a).getPixelKnot().has(Keys.SECRET_MESSAGE) ? ((PixelKnotListener) a).getPixelKnot().getString(Keys.SECRET_MESSAGE) : null;
			if(secret_message == null)
				secret_message_holder.setText("");

			((PixelKnotListener) a).showKeyboard(secret_message_holder);
		} catch (JSONException e) {}
	}

	@Override
	public void onPassphraseSuccessfullySet(String passphrase) {
		((PixelKnotListener) a).getPixelKnot().setPassphrase(passphrase);
		((PixelKnotListener) a).setCanAutoAdvance(true);
		((PixelKnotListener) a).autoAdvance();
	}

	@Override
	public void onRandomPassphraseRequested() {
		String random_passphrase = ((PixelKnotListener) a).getPixelKnot().generateRandomPassword();
		SetPassphraseDialog.getDialog(this, random_passphrase).show();
	}
}
