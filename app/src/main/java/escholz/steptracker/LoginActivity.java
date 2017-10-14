package escholz.steptracker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A login screen that offers login via email/password, google sign-in and facebook sign-in.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_EMAIL_ADDRESS = "emailAddress";
    private static final String LOG_TAG = "LoginActivity";
    private static final int RESULT_GOOGLE_SIGN_IN = 5;

    private FirebaseAuth firebaseAuth;
    private EditText emailAddressView;
    private EditText passwordView;
    private CallbackManager callbackManager;

    private final FacebookCallback<LoginResult> facebookLoginResultCallback
            = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            /* On Successful login extract a Firebase AuthCredential from the FacebookAuthProvider,
             * passing in the token returned by the Facebook Auth API
             */
            final AuthCredential credential = FacebookAuthProvider
                    .getCredential(loginResult.getAccessToken().getToken());
            // Log in to Firebase with the AuthCredential
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(LoginActivity.this, firebaseAuthCallback);
        }

        @Override
        public void onCancel() {
            // If the user cancelled/denied the authentication request do nothing
        }

        @Override
        public void onError(FacebookException error) {
            // If there was an error with the Facebook API display to user
            Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private final OnCompleteListener<AuthResult> firebaseAuthCallback
            = new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                launchMainActivity();
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final GoogleApiClient.OnConnectionFailedListener googleApiCallback
            = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            /* If there is a connection failure with Google, display the error */
            Toast.makeText(LoginActivity.this, "Connection failed.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Assign FirebaseAuth instance
        firebaseAuth = FirebaseAuth.getInstance();
        // Assign form input views
        emailAddressView = findViewById(R.id.email);
        passwordView = findViewById(R.id.password);

        // Setup Click Listener for Sign In Button
        Button signInButton = findViewById(R.id.email_sign_in_button);
        signInButton.setOnClickListener(this);
        // Setup Click Listener for Register Button
        Button registerButton = findViewById(R.id.email_register_button);
        registerButton.setOnClickListener(this);
        // Setup Click Listener for Google Sign In Button
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setOnClickListener(this);
        // Setup Click Listener for Facebook Sign In Button
        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookSignInButton = findViewById(R.id.facebook_sign_in_button);
        facebookSignInButton.setReadPermissions("email", "public_profile");
        facebookSignInButton.registerCallback(callbackManager, facebookLoginResultCallback);

        // Restore contents of e-mail address field if it's in the saved state
        if (savedInstanceState != null) {
            emailAddressView.setText(savedInstanceState.getString(EXTRA_EMAIL_ADDRESS));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* Check if a user is logged in.
         * If there are not, display the sign-in views
         * If there is a user logged in move on to the MainActivity
         */
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            findViewById(R.id.main_layout).setVisibility(View.VISIBLE);
        } else {
            launchMainActivity();
        }
    }

    /**
     * Close this Activity, to remove it from the task stack.
     * Launch the MainActivity.
     */
    private void launchMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == null)
            return;

        final String emailAddress = emailAddressView.getText().toString();
        final String password = passwordView.getText().toString();
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                /* After clicking the sign-in button, call the email and password signIn on
                 * firebase and listen with our common auth success listener.
                 */
                if (TextUtils.isEmpty(emailAddress))
                    return;

                firebaseAuth.signInWithEmailAndPassword(emailAddress, password)
                    .addOnCompleteListener(this, firebaseAuthCallback);
                break;
            case R.id.email_register_button:
                /* After clicking the register button, create a user on firebase and listen with
                *  our common auth success listener
                */
                if (TextUtils.isEmpty(emailAddress))
                    return;

                firebaseAuth.createUserWithEmailAndPassword(emailAddress, password)
                        .addOnCompleteListener(LoginActivity.this, firebaseAuthCallback);
                break;
            case R.id.google_sign_in_button:
                /* Set up the Google API client to request the
                 * user's e-mail address and an authentication token that Firebase can use to
                 * verify the login integrity.
                 */
                final GoogleSignInOptions googleSignInOptions =
                        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build();
                final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, googleApiCallback)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                        .build();
                // Construct Intent to launch the Google Sign In dialog
                final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RESULT_GOOGLE_SIGN_IN);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_EMAIL_ADDRESS, emailAddressView.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RESULT_GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                final GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    firebaseAuth.signInWithCredential(credential)
                            .addOnCompleteListener(this, firebaseAuthCallback);
                }
            } else {
                Toast.makeText(LoginActivity.this, "Google Sign In failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Pass the activity result back to the Facebook SDK
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}

