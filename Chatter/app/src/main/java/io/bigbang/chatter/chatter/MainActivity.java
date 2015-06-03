package io.bigbang.chatter.chatter;

import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.bigbang.client.Action;
import io.bigbang.client.AndroidBigBangClient;
import io.bigbang.client.BigBangClient;
import io.bigbang.client.ConnectionError;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }


        EditText editTextName;
        Button buttonEnterChat;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            editTextName = (EditText) rootView.findViewById(R.id.etName);
            buttonEnterChat = (Button) rootView.findViewById(R.id.buttonEnterChat);

            buttonEnterChat.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   OnEnterChatClick();
               }
           });

            return rootView;
        }

        /*
        * @Name: OnEnterChatClick
        *
        * @params: None
        * @return: None
        *
        * @Description: Called when the Enter Chat button is clicked. Opens a new fragment with the chat.
        */
        private void OnEnterChatClick(){
            if(editTextName.equals("") || editTextName.length() < 1){
                Toast.makeText(getActivity(), "Username must be longer than one character", Toast.LENGTH_SHORT).show();
            } else {
                Fragment newFragment = ChatFragment.newInstance(editTextName.getText().toString());
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack("enter");
                manager.popBackStack();
                transaction.commit();

            }

        }

        private void initializeChat() {
            final Handler bigBangHandler = new Handler(getActivity().getMainLooper());
            BigBangClient client = new AndroidBigBangClient(new Action<Runnable>() {
                @Override
                public void result(Runnable result){
                    bigBangHandler.post(result);
                }
            });

            client.connect("https://chatter.bigbang.io", new Action<ConnectionError>() {
                @Override
                public void result(ConnectionError error) {
                    if (error != null) {
                        Log.i("bigbang", error.getMessage());
                    } else {
                        Log.i("bigbang", "Connected!");
                    }
                }
            });
        }
    }
}
