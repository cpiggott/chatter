package io.bigbang.chatter.chatter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import io.bigbang.client.Action;
import io.bigbang.client.Action2;
import io.bigbang.client.AndroidBigBangClient;
import io.bigbang.client.BigBangClient;
import io.bigbang.client.Channel;
import io.bigbang.client.ChannelError;
import io.bigbang.client.ChannelMessage;
import io.bigbang.client.ConnectionError;
import io.bigbang.protocol.JsonObject;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ChatFragment extends Fragment implements AbsListView.OnItemClickListener {


    private List<Message> mMessages;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private MessageAdapter mAdapter;

    private static String mUsername;
    private static int mColor;
    private static double lat;
    private static double lng;

    private String message;
    private EditText messageEditText;
    private Button sendButton;

    private Channel chatChannel;
    private BigBangClient client;

    private Vibrator vibrator;

    // TODO: Rename and change types of parameters
    public static ChatFragment newInstance(String username, int color, Location loc) {
        ChatFragment fragment = new ChatFragment();
        mUsername = username;
        mColor = color;
        lat = loc.getLatitude();
        lng = loc.getLongitude();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessages = new ArrayList<Message>();
        Resources resources = getResources();

        // TODO: Change Adapter to display your content
        mAdapter = new MessageAdapter(getActivity(), mMessages, mUsername);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);


        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        messageEditText = (EditText) view.findViewById(R.id.etChatText);
        sendButton = (Button) view.findViewById(R.id.buttonSend);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
           // mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeChat();
    }

    @Override
    public void onPause() {
        super.onPause();
        client.disconnect();

    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    private void SendMessage(){
        message = messageEditText.getText().toString();
        if(message.equals("") || message == null){
            Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_SHORT ).show();
        } else {

            messageEditText.setText("");//Sets the editText to empty for a new message

            JsonObject json = new JsonObject();
            json.putString("message", message);
            json.putString("sender", mUsername);
            json.putNumber("color", mColor);

            chatChannel.publish(json);
        }
    }

    //Sends a message that a specific user has connected to everyone.
    private void SendConnect(){
        JsonObject json = new JsonObject();
        json.putString("message", mUsername + " has joined the chat.");
        json.putString("sender", "BobBot");
        json.putNumber("color", Color.BLACK);


        chatChannel.publish(json);
    }

    private void updateChatWindow(String message, String username, int color){
        mAdapter.add(new Message(message, username, color));
        mAdapter.notifyDataSetChanged();
        vibrator.vibrate(500);//TODO: Might want to move this
    }

    private void initializeChat() {

        //getActivity().getActionBar().setTitle("Connecting...");
        messageEditText.setEnabled(false);
        sendButton.setEnabled(false);
        Toast.makeText(getActivity(), "Connecting...", Toast.LENGTH_SHORT).show();


        final Handler bigBangHandler = new Handler(getActivity().getMainLooper());
        client = new AndroidBigBangClient(new Action<Runnable>() {
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
                    //getActivity().getActionBar().setTitle("Connected!");

                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                    messageEditText.setEnabled(true);
                    sendButton.setEnabled(true);
                    final String geoHash = GeoHash.encode(lat, lng);
                    client.subscribe(geoHash, new Action2<ChannelError, Channel>() {
                        @Override
                        public void result(ChannelError channelError, Channel channel) {
                            chatChannel = channel;
                            Log.i("location", geoHash);
                            //Toast.makeText(getActivity(), geoHash, Toast.LENGTH_LONG ).show();
                            SendConnect();
                            channel.onMessage(new Action<ChannelMessage>() {
                                @Override
                                public void result(ChannelMessage result) {
                                    JsonObject json = result.getPayload().getBytesAsJSON().asObject();
                                    updateChatWindow(json.getString("message"), json.getString("sender"), json.getInteger("color"));
                                }
                            });
                        }
                    });
                }
            }
        });

        client.disconnected(new Action<Void>() {
            @Override
            public void result(Void result) {
                messageEditText.setEnabled(false);
                sendButton.setEnabled(false);
                //Toast.makeText(getActivity(), "Diconnected", Toast.LENGTH_LONG).show();

            }
        });
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String username);
    }



}
