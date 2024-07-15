package org.kaustav.majorproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryItem> historyItemList;

    public HistoryAdapter(List<HistoryItem> historyItemList) {
        this.historyItemList = historyItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem historyItem = historyItemList.get(position);
        holder.timestampTextView.setText(formatTimestamp(historyItem.getTimestamp()));
        holder.inputTextView.setText(historyItem.getInput());
        holder.outputTextView.setText(historyItem.getOutput());


        // Set click listener for the card view
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility of input and output TextViews
                int inputVisibility = holder.inputTextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                int outputVisibility = holder.outputTextView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                int inputLabelVisibility = holder.inputLabel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                int outputLabelVisibility = holder.outputLabel.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                int clipboardBillVisibility=holder.clipboardBill.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                int clipboardSummaryVisibility=holder.clipboardSummary.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;


                holder.inputTextView.setVisibility(inputVisibility);
                holder.outputTextView.setVisibility(outputVisibility);
                holder.inputLabel.setVisibility(inputLabelVisibility);
                holder.outputLabel.setVisibility(outputLabelVisibility);
                holder.clipboardBill.setVisibility(clipboardBillVisibility);
                holder.clipboardSummary.setVisibility(clipboardSummaryVisibility);




            }
        });
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timestampTextView;
        TextView inputTextView;
        TextView outputTextView;
        TextView inputLabel;
        TextView outputLabel;
        CardView cardView;
        ImageButton clipboardBill;
        ImageButton clipboardSummary;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            inputTextView = itemView.findViewById(R.id.inputTextView);
            outputTextView = itemView.findViewById(R.id.outputTextView);
            inputLabel=itemView.findViewById(R.id.inputLabel);
            outputLabel=itemView.findViewById(R.id.outputLabel);
            cardView = itemView.findViewById(R.id.historyCard);
            clipboardBill=itemView.findViewById(R.id.clipboardBill);
            clipboardSummary=itemView.findViewById(R.id.clipboardSummary);

            clipboardBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("EditText", inputTextView.getText().toString());
                    clipboardManager.setPrimaryClip(clip);
                    Toast.makeText(itemView.getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            clipboardSummary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("EditText", outputTextView.getText().toString());
                    clipboardManager.setPrimaryClip(clip);
                    Toast.makeText(itemView.getContext(), "Copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private String formatTimestamp(long timestamp) {
        // Format the timestamp here according to your requirements
        // Example: Convert timestamp to date and time string
        Date date = new Date(timestamp);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }
}


