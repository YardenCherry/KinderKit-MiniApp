package dev.netanelbcn.kinderkit.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import dev.netanelbcn.kinderkit.Controllers.DataManager;
import dev.netanelbcn.kinderkit.Interfaces.DelRecordCallback;
import dev.netanelbcn.kinderkit.Models.ImmunizationRecord;
import dev.netanelbcn.kinderkit.Models.Kid;
import dev.netanelbcn.kinderkit.R;

public class IRAdapter extends RecyclerView.Adapter<IRAdapter.RecordViewHolder> {
    private Context context;
    private DelRecordCallback delRecordCallback;
    private ArrayList<ImmunizationRecord> records;
    private DataManager dataManager = DataManager.getInstance();
    private int currentKidPosition;

    public IRAdapter(Context context, ArrayList<ImmunizationRecord> records, int currentKidPosition) {
        this.context = context;
        this.records = records;
        this.currentKidPosition = currentKidPosition;
        setRecords(currentKidPosition);
    }

    public void setRecords(int currentKidPosition) {
        dataManager.loadAllKids(new DataManager.OnKidsLoadedListener() {
            @Override
            public void onKidsLoaded(ArrayList<Kid> kids) {
                IRAdapter.this.records = kids.get(currentKidPosition).getImmunizationRecords();
            }

            @Override
            public void onFailure(Exception exception) {
                Log.e("errr", exception.getMessage());
            }
        });
    }

    public IRAdapter setIRCallback(DelRecordCallback delRecordCallback) {
        this.delRecordCallback = delRecordCallback;
        return this;
    }


    @NonNull
    @Override
    public IRAdapter.RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.immunization_card, parent, false);
        return new RecordViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull IRAdapter.RecordViewHolder holder, int position) {

        ImmunizationRecord record = getItem(position);
        holder.IC_MTV_vaccineName.setText(record.getVaccineName());
        holder.IC_MTV_doseNumber.setText("Dose Num: " + record.getDoseNumber());
        holder.IC_MTV_vaccinatorName.setText("Vaccinator: " + record.getVaccinatorName());
        holder.IC_MTV_HMOName.setText("HMO: " + record.getHMOName());
        if (record.getCreatorName() == null)
            record.setCreatorName("Unknown");
        holder.IC_MTV_companyName.setText("Company: " + record.getCreatorName());
        holder.IC_MTV_date.setText("Vaccination Date: " + record.getvdate().toString());


    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    private ImmunizationRecord getItem(int position) {
        return records.get(position);
    }

    public class RecordViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView IC_MTV_vaccineName;
        private MaterialTextView IC_MTV_doseNumber;
        private MaterialTextView IC_MTV_vaccinatorName;
        private MaterialTextView IC_MTV_HMOName;
        private MaterialTextView IC_MTV_companyName;
        private MaterialTextView IC_MTV_date;
        private MaterialButton IC_MB_delete;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            IC_MTV_vaccineName = itemView.findViewById(R.id.IC_MTV_vaccineName);
            IC_MTV_doseNumber = itemView.findViewById(R.id.IC_MTV_doseNumber);
            IC_MTV_vaccinatorName = itemView.findViewById(R.id.IC_MTV_vaccinatorName);
            IC_MTV_HMOName = itemView.findViewById(R.id.IC_MTV_HMOName);
            IC_MTV_companyName = itemView.findViewById(R.id.IC_MTV_companyName);
            IC_MTV_date = itemView.findViewById(R.id.IC_MTV_date);
            IC_MB_delete = itemView.findViewById(R.id.IC_MB_delete);
            IC_MB_delete.setOnClickListener(v -> {
                if (delRecordCallback != null) {
                    delRecordCallback.deleteClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}



