package com.example.clockit.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.clockit.app.model.Client;

import java.util.List;

public class ClientAdapter extends ArrayAdapter<Client> {

    private final Context context;
    private final List<Client> clients;

    public ClientAdapter(Context context, List<Client> clients) {
        super(context, R.layout.client_row, clients);
        this.context = context;
        this.clients = clients;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.client_row, parent, false);

        TextView tvClientName = (TextView) rowView.findViewById(R.id.tvClientName);

        Client client = clients.get(position);

        tvClientName.setText(client.getName());

        return rowView;
    }
}
