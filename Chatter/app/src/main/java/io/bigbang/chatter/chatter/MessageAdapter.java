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

    public MessageAdapter(Context context, List<Message> messages) {
        super(context, R.layout.list_row, messages);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_row, parent, false);

            //initialize viewHolder
            viewHolder = new ViewHolder();
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.tvSender = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message message = getItem(position);
        viewHolder.tvBody.setText(message.getBody());
        viewHolder.tvSender.setText(message.getSender());

        return convertView;


    }


    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * @see http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        TextView tvBody;
        TextView tvSender;
    }


}


