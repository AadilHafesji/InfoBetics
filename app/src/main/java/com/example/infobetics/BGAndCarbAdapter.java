package com.example.infobetics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class BGAndCarbAdapter extends FirestoreRecyclerAdapter<BGAndCarbData, BGAndCarbAdapter.BGAndCarbHolder> {
    private onItemClickListener clickListener;

    public BGAndCarbAdapter(@NonNull FirestoreRecyclerOptions<BGAndCarbData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull BGAndCarbHolder bgAndCarbHolder, int i, @NonNull BGAndCarbData bgAndCarbData) {
        //What to show in all the cards
        bgAndCarbHolder.textViewGlucoseAmount.setText(String.valueOf(bgAndCarbData.getGlucoseAmount()));
        bgAndCarbHolder.textViewCarbohydrateAmount.setText(String.valueOf(bgAndCarbData.getCarbohydrateAmount()));
        bgAndCarbHolder.textViewInsulinAmount.setText(String.valueOf(bgAndCarbData.getInsulinAmount()));
    }

    @NonNull
    @Override
    public BGAndCarbHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Which layout to inflate
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bg_carb_item, parent, false);
        return new BGAndCarbHolder(view);
    }

    class BGAndCarbHolder extends RecyclerView.ViewHolder {
        TextView textViewGlucoseAmount, textViewCarbohydrateAmount, textViewInsulinAmount;

        public BGAndCarbHolder(@NonNull View itemView) {
            super(itemView);
            textViewGlucoseAmount = itemView.findViewById(R.id.retrieveBGAmount);
            textViewCarbohydrateAmount = itemView.findViewById(R.id.retrieveCarbAmount);
            textViewInsulinAmount = itemView.findViewById(R.id.retrieveInsulinAmount);

            //Click Listener when clicking on card
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && clickListener != null) {
                        clickListener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }

                    //Call the interface method
                }
            });
        }
    }

    public interface onItemClickListener {
        //Define interface methods
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(onItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
