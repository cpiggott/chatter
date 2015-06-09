package io.bigbang.chatter.chatter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chris on 6/3/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    private static String mUsername;

    public MessageAdapter(Context context, List<Message> messages, String username) {
        super(context, R.layout.list_row, messages);
        mUsername = username;
    }
//TODO: Clean this up!
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;
        Message message = getItem(position);


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row, parent, false);

            //initialize viewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.tvSender = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(message.getSender().equals(mUsername)){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row_me, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.tvSender = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);

            convertView.setTag(viewHolder);
        } else {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row, parent, false);

            //initialize viewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.tvSender = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        }

        viewHolder.tvSender.setTextColor(message.getColor());
        viewHolder.tvBody.setText(message.getBody());
        viewHolder.tvSender.setText(message.getSender());
        viewHolder.tvDate.setText(message.getDate());

        return convertView;


    }


    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see //developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        TextView tvBody;
        TextView tvSender;
        TextView tvDate;
    }


}


