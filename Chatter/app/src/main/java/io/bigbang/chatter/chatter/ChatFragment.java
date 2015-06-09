package io.bigbang.chatter.chatter;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
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


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    static final String STATE_MESSAGES = "messages";

    private ArrayList<Message> mMessages;

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
    private boolean needInitialized = true;
    private boolean notifyUser = false;
    private String locale = "";

    private SimpleDateFormat sdm = new SimpleDateFormat("h:mm a");

    private Channel chatChannel;
    private BigBangClient client;
    private Vibrator vibrator;
    private NotificationCompat.Builder mBuilder;

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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_MESSAGES, mMessages);
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            mMessages = savedInstanceState.getParcelableArrayList(STATE_MESSAGES);
        } else {
            mMessages = new ArrayList<Message>();
        }

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

        mBuilder = new NotificationCompat.Builder(getActivity());

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
        notifyUser = false;
        if(needInitialized) {
            initializeChat();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
       // client.disconnect();
        notifyUser = true;

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

    /**
    *Sends the message to BigBang for the users
    */
    private void SendMessage(){
        message = messageEditText.getText().toString();
        if(message.equals("") || message == null){
            Toast.makeText(getActivity(), "Message cannot be empty", Toast.LENGTH_SHORT ).show();
        } else if (message.toLowerCase().equals("!location")){
            if(!locale.equals("")) {
                Toast.makeText(getActivity(), "You are currently in the " + locale + " chat room", Toast.LENGTH_SHORT).show();
                messageEditText.setText("");
            } else {
                Toast.makeText(getActivity(), "We weren't able to grab your location enough.", Toast.LENGTH_SHORT).show();
            }
        } else  {

            messageEditText.setText("");//Sets the editText to empty for a new message

            JsonObject json = new JsonObject();
            json.putString("message", message);
            json.putString("sender", mUsername);
            json.putNumber("color", mColor);
            json.putString("date", sdm.format(new Date()));

            chatChannel.publish(json);
        }
    }

    //Sends a message that a specific user has connected to everyone.
    private void SendConnect(){
        JsonObject json = new JsonObject();
        json.putString("message", mUsername + " has joined the " + locale + " chat.");
        json.putString("sender", "BobBot");
        json.putNumber("color", Color.BLACK);
        json.putString("date", sdm.format(new Date()));


        chatChannel.publish(json);
    }

    /**
     * Creates a new message and updates the messageAdapter to add to the Chat View
     *
     * @param message : message that is to be added to list
     * @param username : username of who sent the message
     * @param color : color of the username that sent the messgae
     * @param date : time the message was sent
     */
    private void updateChatWindow(String message, String username, int color, String date){
        mAdapter.add(new Message(message, username, color, date));
        mAdapter.notifyDataSetChanged();

        if(!username.equals(mUsername)){
            vibrator.vibrate(500);//TODO: Might want to move this
        }

        if(notifyUser) {
            Notify(username, message);
        }
    }

    /*
    * This initializes the BigBand Client and subscribes you to a channel.
    *
    * Calls the GeoHash Encode function that puts you in a location based channel.
    *
    *
    */
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
                    needInitialized = false;
                    Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
                    messageEditText.setEnabled(true);
                    sendButton.setEnabled(true);
                    final String geoHash = GeoHash.encode(lat, lng);
                    Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());
                    try{
                        List<Address> list = geoCoder.getFromLocation(lat, lng, 1);
                        if (list != null & list.size() > 0) {
                            Address address = list.get(0);
                            locale = address.getLocality();
                        }

                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
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
                                    updateChatWindow(json.getString("message"), json.getString("sender"), json.getInteger("color"), json.getString("date"));
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
                needInitialized = true;
            }
        });
    }

    /**
     * This is only called if the application is not in the foregraound
     *
     * @param notificationTitle : title of the notification
     * @param notificationMessage : message that was sent
     */
    @SuppressWarnings("depreciated")
    private void Notify(String notificationTitle, String notificationMessage){
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(getActivity(), ChatFragment.class);
        intent.putExtra("chatFragment", true);

        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);



       mBuilder = new NotificationCompat.Builder(getActivity())
                .setSmallIcon(R.drawable.abc_btn_radio_material)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage);

        Notification notification = mBuilder.build();

        notification.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0,notification);


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
