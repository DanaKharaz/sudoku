package com.example.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {
    // define everything
    private static RVInterface rvInterface = null;
    private final Context context;
    protected ArrayList<Sudoku> sudokuArray;
    // constructor
    public RVAdapter(Context context, ArrayList<Sudoku> sudokuArray, RVInterface rvInterface){
        this.context = context;
        this.sudokuArray = sudokuArray;
        RVAdapter.rvInterface = rvInterface;
    }
    @NonNull
    @Override
    public RVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // this is where the layout is inflated (giving a specific look to the rows)
        LayoutInflater inflater = LayoutInflater.from(context);
        // use a card view layout
        View view = inflater.inflate(R.layout.rv_card, parent, false);
        return new RVAdapter.MyViewHolder(view, rvInterface);
    }
    @Override
    public void onBindViewHolder(@NonNull RVAdapter.MyViewHolder holder, int position) {
        // assigning values to views created in the row layout based on position of the recycler view
        Sudoku sudoku = sudokuArray.get(position);
        // use bind method instead of setting text directly to allow accurate searching
        holder.bind(sudoku);
    }
    @Override
    public int getItemCount() {
        // recycler view gets the number of items that needs to be displayed
        return sudokuArray.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
        // row layout
        private final EditText txtName;
        private final TextView txtDone;
        private final TextView txtUnknown;
        private final TextView txtHint;
        private Sudoku currSudoku; // allows view holder keep reference to the bound item
        public MyViewHolder(@NonNull View itemView, RVInterface rvInterface) {
            super(itemView);
            // set views and listeners
            txtName = itemView.findViewById(R.id.txtName);
            txtDone = itemView.findViewById(R.id.txtPercent);
            txtUnknown = itemView.findViewById(R.id.txtUnknown);
            txtHint = itemView.findViewById(R.id.txtHint);
            itemView.setOnClickListener(this);
        }
        @SuppressLint("SetTextI18n")
        void bind(Sudoku sudoku) { // allows view holder bind data being displayed
            txtName.setText(sudoku.getName());
            // TODO : set what editing name does
            txtDone.setText(String.valueOf(sudoku.getDone()) + '%');
            txtUnknown.setText("Unknown: " + String.valueOf(sudoku.getUnknown()));
            txtUnknown.setText("Hints: " + String.valueOf(sudoku.getHints()));
            currSudoku = sudoku; // keep reference to the current object
        }
        @Override
        public void onClick(View v) {
            if (rvInterface != null) {
                // pass current object to activity
                rvInterface.onItemClick(currSudoku);
            }
        }
    }
    // change ArrayList objects to filtered list based on user input
    // (filtering happens in ThisActivity activity)
    public void  setFilteredList(ArrayList<Sudoku> filteredList) {
        this.sudokuArray = filteredList;
        notifyDataSetChanged();
    }
}